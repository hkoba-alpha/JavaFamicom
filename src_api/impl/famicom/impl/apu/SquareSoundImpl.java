package famicom.impl.apu;

import famicom.api.apu.ISquareSound;

public class SquareSoundImpl extends SoundBase implements ISquareSound {

	private static final byte[][] sampleDataList = { { 0, 1, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 1, 0, 0, 0, 0, 0 }, { 0, 1, 1, 1, 1, 0, 0, 0 },
			{ 1, 0, 0, 1, 1, 1, 1, 1 } };

	/**
	 * サンプルデータ
	 */
	private byte[] sampleData;

	/**
	 * ボリューム
	 */
	private int volumeValue;

	/**
	 * カウンタ
	 */
	private int lengthCounter;

	/**
	 * ２番目のチャネル
	 */
	private boolean secondFlag;
	private boolean loopFlag;

	private class EnvelopeData {
		int period;
		int count;

		EnvelopeData(int period) {
			this.period = period;
			count = period;
		}

		boolean step() {
			count--;
			if (count == 0) {
				if (volumeValue > 0) {
					volumeValue--;
				} else if (loopFlag) {
					volumeValue = 15;
				}
				count = period;
			}
			return false;
		}
	}
	private class SweepData {
		int period;
		boolean upFlag;
		int count;
		int value;
		
		SweepData(boolean upFlag, int period, int val) {
			this.upFlag = upFlag;
			this.period = period;
			this.value = val;
			count = period;
		}

		void step() {
			if (lengthCounter == 0) {
				// 処理不要
				return;
			}
			count--;
			if (count == 0) {
				int tm = getTimerCount();
				if (upFlag) {
					tm -= (tm >> value);
					if (secondFlag) {
						tm--;
					}
				} else {
					tm += (tm >> value);
				}
				if (tm < 8 || tm > 0x7ff) {
					// 無効化する
					lengthCounter = 0;
				} else {
					setTimerCount(tm);
				}
				count = period;
			}
		}
	}

	private EnvelopeData envData;
	private EnvelopeData nextEnv;
	private SweepData sweepData;
	private SweepData nextSweep;

	public SquareSoundImpl(boolean secondFlag) {
		sampleData = sampleDataList[0];
		this.secondFlag = secondFlag;
	}

	@Override
	public ISquareSound setVolume(int duty, boolean stopFlag, int volume) {
		volumeValue = volume;
		loopFlag = stopFlag;
		sampleData = sampleDataList[duty];
		envData = null;
		nextEnv = null;
		return this;
	}

	@Override
	public ISquareSound setEnvelope(int duty, boolean loopFlag, int period) {
		this.loopFlag = loopFlag;
		nextEnv = new EnvelopeData(period + 1);
		volumeValue = 0;
		sampleData = sampleDataList[duty];
		return this;
	}

	@Override
	public ISquareSound setTimer(int lengthIndex, int timerCount) {
		if (nextEnv != null) {
			envData = nextEnv;
			nextEnv = null;
		}
		if (nextSweep != null) {
			sweepData = nextSweep;
			nextSweep = null;
		}
		if (envData != null) {
			envData.count = envData.period;
			volumeValue = 15;
		}
		if (sweepData != null) {
			sweepData.count = sweepData.period;
		}
		if (timerCount < 7 || timerCount > 0x7fe) {
			// 無効
			lengthCounter = 0;
		} else {
			lengthCounter = lengthIndexData[lengthIndex];
			setTimerCount(timerCount + 1);
		}
		return this;
	}

	@Override
	public ISquareSound setSweep(boolean enableFlag, int period,
			boolean upMode, int value) {
		if (enableFlag && value > 0) {
			nextSweep = new SweepData(upMode, period + 1, value);
		} else {
			nextSweep = null;
			sweepData= null;
		}
		return this;
	}

	@Override
	public boolean isPlaying() {
		return lengthCounter > 0;
	}

	@Override
	protected byte getOutput(int index) {
		int ix = (index / 2) % sampleData.length;
		return (byte)(sampleData[ix] * volumeValue);
	}

	@Override
	public void setFrameSample(byte[] data, int offset) {
		if (lengthCounter == 0 || volumeValue == 0) {
			fillNullSound(data, offset);
			return;
		}
		super.setFrameSample(data, offset);
	}

	public void stepLength() {
		if (sweepData != null) {
			sweepData.step();
		}
		if (!loopFlag && lengthCounter > 0) {
			lengthCounter--;
		}
	}
	public void stepEnvelope() {
		if (envData != null) {
			envData.step();
		}
	}

	@Override
	public void stepFrame(int index, int stepNum) {
		if (index < 4) {
			stepEnvelope();
			if (((index + stepNum) & 1) == 0) {
				stepLength();
			}
		}
	}

	@Override
	public ISquareSound stop() {
		lengthCounter = 0;
		return this;
	}
}
