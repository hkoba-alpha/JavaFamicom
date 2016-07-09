package famicom.util.apu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import famicom.api.apu.IDMASound;
import famicom.api.apu.IFamicomAPU;
import famicom.api.apu.INoiseSound;
import famicom.api.apu.ISquareSound;
import famicom.api.apu.ITriangleSound;
import famicom.api.rom.listener.SoundListener;

public class SoundManager implements SoundListener {
	private static class DummySquare implements ISquareSound {
		@Override
		public ISquareSound setVolume(int duty, boolean stopFlag, int volume) {
			return this;
		}

		@Override
		public ISquareSound setEnvelope(int duty, boolean loopFlag, int period) {
			return this;
		}

		@Override
		public ISquareSound setTimer(int lengthIndex, int timerCount) {
			return this;
		}

		@Override
		public ISquareSound setSweep(boolean enableFlag, int period,
				boolean upMode, int value) {
			return this;
		}

		@Override
		public boolean isPlaying() {
			return true;
		}

		@Override
		public ISquareSound stop() {
			return this;
		}
	}

	private static class DummyTriangle implements ITriangleSound {
		@Override
		public ITriangleSound setLinear(boolean loopFlag, int lineCount) {
			return this;
		}

		@Override
		public ITriangleSound setTimer(int lengthIndex, int timerCount) {
			return this;
		}

		@Override
		public boolean isPlaying() {
			return true;
		}

		@Override
		public ITriangleSound stop() {
			return this;
		}
	}

	private static class DummyNoise implements INoiseSound {
		@Override
		public INoiseSound setVolume(boolean stopFlag, int volume) {
			return this;
		}

		@Override
		public INoiseSound setEnvelope(boolean loopFlag, int period) {
			return this;
		}

		@Override
		public INoiseSound setRandomMode(boolean shortFlag, int timerIndex) {
			return this;
		}

		@Override
		public INoiseSound setLength(int lengthIndex) {
			return this;
		}

		@Override
		public INoiseSound stop() {
			return this;
		}

		@Override
		public boolean isPlaying() {
			return true;
		}
	}

	private static class DummyDMA implements IDMASound {
		@Override
		public IDMASound setPeriod(boolean loopFlag, int periodIndex) {
			return this;
		}

		@Override
		public IDMASound setDelta(int delta) {
			return this;
		}

		@Override
		public IDMASound setSample(byte[] data, int length) {
			return this;
		}		
	}

	private static class DummyAPU implements IFamicomAPU {
		private ISquareSound square = new DummySquare();
		private ITriangleSound triangle = new DummyTriangle();
		private INoiseSound noise = new DummyNoise();
		private IDMASound dma = new DummyDMA();

		@Override
		public IFamicomAPU write(int addr, int data) {
			return this;
		}

		@Override
		public int read(int addr) {
			return 0;
		}

		@Override
		public IFamicomAPU write(int addr, byte[] data, int offset, int length) {
			return this;
		}

		@Override
		public IFamicomAPU read(int addr, byte[] data, int offset, int length) {
			return this;
		}

		@Override
		public ISquareSound getSquare1() {
			return square;
		}

		@Override
		public ISquareSound getSquare2() {
			return square;
		}

		@Override
		public ITriangleSound getTriangle() {
			return triangle;
		}

		@Override
		public INoiseSound getNoise() {
			return noise;
		}

		@Override
		public IDMASound getDMA() {
			return dma;
		}

		@Override
		public void setSoundListener(SoundListener listener) {
		}
	}

	private static IFamicomAPU dummyApu = new DummyAPU();

	private class SequenceData {
		private int priority;
		private SoundSequencer sequencer;
		private int startFlag;
		private int pauseFlag;
		private int playingFlag;
		private int channelFlag;
		private int frameIndex;
		private boolean loopFlag;
		/**
		 * 音を停止するフラグ
		 */
		private boolean silentFlag;

		private SequenceData(int pri, SoundSequencer seq, boolean loop) {
			priority = pri;
			sequencer = seq;
			loopFlag = loop;
			setChannelFlag();
		}

		private void setChannelFlag() {
			SoundSequencer.ChannelType[] allList = SoundSequencer.ChannelType
					.values();
			SoundSequencer.ChannelType[] chlst = sequencer.getChannels();
			for (int i = 0; i < allList.length; i++) {
				boolean flg = false;
				for (SoundSequencer.ChannelType ch : chlst) {
					if (ch == allList[i]) {
						flg = true;
						break;
					}
				}
				if (flg) {
					channelFlag |= (1 << i);
				}
			}
		}

		private void pause(IFamicomAPU apu) {
			SoundSequencer.ChannelType[] allList = SoundSequencer.ChannelType
					.values();
			for (int i = 0; i < allList.length; i++) {
				if ((pauseFlag & (1 << i)) > 0) {
					sequencer.pause(apu, allList[i], frameIndex);
					playingFlag &= ~(1 << i);
				}
			}
			pauseFlag = 0;
		}

		private boolean play(IFamicomAPU apu) {
			SoundSequencer.ChannelType[] allList = SoundSequencer.ChannelType
					.values();
			for (int i = 0; i < allList.length; i++) {
				int chflg = 1 << i;
				if ((startFlag & chflg) > 0) {
					sequencer.start(apu, allList[i], frameIndex);
					if (!sequencer.soundStep(apu, allList[i], frameIndex)) {
						channelFlag &= ~chflg;
					}
					playingFlag |= chflg;
				} else if ((playingFlag & chflg) > 0) {
					if (!sequencer.soundStep(apu, allList[i], frameIndex)) {
						channelFlag &= ~chflg;
						playingFlag &= ~chflg;
					}
				} else if ((channelFlag & (1 << i)) > 0) {
					// スキップ
					if (!sequencer.soundStep(dummyApu, allList[i], frameIndex)) {
						channelFlag &= ~chflg;
					}
				}
			}
			startFlag = 0;
			if (channelFlag == 0) {
				if (loopFlag) {
					setChannelFlag();
					frameIndex = 0;
					return true;
				} else {
					return false;
				}
			}
			frameIndex++;
			return true;
		}
	}

	private ArrayList<SequenceData> soundList;
	private ArrayList<SequenceData> removeList;

	public SoundManager() {
		soundList = new ArrayList<SequenceData>();
		removeList = new ArrayList<SequenceData>();
	}

	@Override
	public void soundStep(IFamicomAPU apu, boolean irqFlag) {
		for (SequenceData dt : removeList) {
			dt.pauseFlag = dt.playingFlag;
			dt.pause(apu);
		}
		removeList.clear();
		// フラグの設定
		SoundSequencer.ChannelType[] allList = SoundSequencer.ChannelType
				.values();
		for (int i = 0; i < allList.length; i++) {
			int flg = (1 << i);
			boolean play = false;
			for (SequenceData dt : soundList) {
				if ((dt.channelFlag & flg) > 0) {
					// 対象
					if (play || dt.silentFlag) {
						// すでに出力済み、あるいは休止中
						if ((dt.playingFlag & flg) > 0) {
							// 停止する
							dt.pauseFlag |= flg;
						}
					} else {
						// これから出力する
						if ((dt.playingFlag & flg) == 0) {
							// 再生開始
							dt.startFlag |= flg;
						}
						play = true;
					}
				}
			}
		}
		// play
		for (SequenceData dt : soundList) {
			dt.pause(apu);
		}
		Iterator<SequenceData> it = soundList.iterator();
		while (it.hasNext()) {
			SequenceData dt = it.next();
			if (!dt.play(apu)) {
				// 終了した
				it.remove();
			}
		}
	}

	public SoundManager addSequencer(int priority, SoundSequencer seq, boolean loopFlag) {
		soundList.add(new SequenceData(priority, seq, loopFlag));
		sortSoundList();
		return this;
	}
	public SoundManager setLoopFlag(SoundSequencer seq, boolean loopFlag) {
		for (SequenceData dt: soundList) {
			if (dt.sequencer == seq) {
				dt.loopFlag = loopFlag;
				break;
			}
		}
		return this;
	}
	public boolean isPlaying(SoundSequencer seq) {
		for (SequenceData dt: soundList) {
			if (dt.sequencer == seq) {
				return true;
			}
		}
		return false;
	}

	public SoundManager removeSequencer(int priority) {
		Iterator<SequenceData> it = soundList.iterator();
		while (it.hasNext()) {
			SequenceData dt = it.next();
			if (dt.priority == priority) {
				it.remove();
				removeList.add(dt);
			}
		}
		sortSoundList();
		return this;
	}

	public SoundManager removeSequencer(SoundSequencer seq) {
		Iterator<SequenceData> it = soundList.iterator();
		while (it.hasNext()) {
			SequenceData dt = it.next();
			if (dt.sequencer == seq) {
				it.remove();
				removeList.add(dt);
			}
		}
		sortSoundList();
		return this;
	}

	public SoundManager pauseSequencer(int priority) {
		for (SequenceData dt: soundList) {
			if (dt.priority == priority) {
				dt.silentFlag = true;
			}
		}
		return this;
	}
	public SoundManager pauseSequencer(SoundSequencer seq) {
		for (SequenceData dt: soundList) {
			if (dt.sequencer == seq) {
				dt.silentFlag = true;
			}
		}
		return this;
	}
	public SoundManager resumeSequencer(int priority) {
		for (SequenceData dt: soundList) {
			if (dt.priority == priority) {
				dt.silentFlag = false;
			}
		}
		return this;
	}
	public SoundManager resumeSequencer(SoundSequencer seq) {
		for (SequenceData dt: soundList) {
			if (dt.sequencer == seq) {
				dt.silentFlag = false;
			}
		}
		return this;
	}

	private void sortSoundList() {
		Collections.sort(soundList, new Comparator<SequenceData>() {
			@Override
			public int compare(SequenceData o1, SequenceData o2) {
				if (o1.priority > o2.priority) {
					return -1;
				} else if (o1.priority < o2.priority) {
					return 1;
				}
				return 0;
			}
		});
	}
}
