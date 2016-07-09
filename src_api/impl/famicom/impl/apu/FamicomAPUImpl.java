package famicom.impl.apu;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import famicom.api.apu.IDMASound;
import famicom.api.apu.IFamicomAPU;
import famicom.api.apu.INoiseSound;
import famicom.api.apu.ISquareSound;
import famicom.api.apu.ITriangleSound;
import famicom.api.rom.listener.SoundListener;

public class FamicomAPUImpl implements IFamicomAPU {
	public static final int SAMPLE_RATE = 48000;
	private ArrayList<SoundBase> soundList;
	private int[] squareTimer = new int[2];
	private int triangleTimer;
	private byte[][] sampleData;
	private SoundMixer mixData;
	private int frameIndex;
	private int stepSize;
	private SourceDataLine dataLine;
	private byte[] sampleBuffer = new byte[SAMPLE_RATE];
	private int bufferSize;
	private int maxAvailable;
	private SoundListener soundListener;

	public FamicomAPUImpl() {
		soundList = new ArrayList<SoundBase>();
		soundList.add(new SquareSoundImpl(false));
		soundList.add(new SquareSoundImpl(true));
		soundList.add(new TriangleSoundImpl());
		soundList.add(new NoiseSoundImpl());
		sampleData = new byte[5][SoundBase.SAMPLE_RATE];
		mixData = new SoundMixer();
		stepSize = 4;
		AudioFormat fmt = new AudioFormat(SAMPLE_RATE, 8, 1, false, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open();
			dataLine.start();
			maxAvailable = dataLine.available();
			dataLine.write(new byte[1], 0, 1);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 240Hzのフレームを進める
	 */
	public void stepFrame() {
		if (soundListener != null) {
			soundListener.soundStep(this, frameIndex == 0 && stepSize == 4);
		}
		for (int i = 0; i < soundList.size(); i++) {
			soundList.get(i).setFrameSample(sampleData[i], 0);
			soundList.get(i).stepFrame(frameIndex, stepSize);
		}
		frameIndex++;
		if (frameIndex >= stepSize) {
			frameIndex = 0;
		}
		mixData.mixData(sampleData[0], sampleData[1], sampleData[2],
				sampleData[3], sampleData[4], sampleBuffer, bufferSize);
		// dump
		/*
		int last = -1;
		int len = 0;
		for (int i = 0; i < sampleData[2].length; i++) {
			if (sampleData[2][i] != last) {
				if (len > 0) {
					System.out.print("(" + len + ")");
				}
				System.out.print(sampleData[2][i]);
				len = 1;
				last = sampleData[2][i];
			} else {
				len++;
			}
		}
		System.out.println("(" + len + ")");
		*/
		bufferSize += SoundBase.SAMPLE_RATE;
	}

	/**
	 * 60Hz分のサウンドをフラッシュする
	 */
	public void flushSound() {
		int tm = maxAvailable - dataLine.available();
		if (tm > 2000) {
			System.out.println("stock=" + tm + ", add=" + bufferSize);
		}
		dataLine.write(sampleBuffer, 0, bufferSize);
		bufferSize = 0;
	}

	@Override
	public IFamicomAPU write(int addr, int val) {
		if (addr < 0x4000) {
			return this;
		}
		if (addr < 0x4008) {
			// 矩形波
			int ix = (addr >> 2) & 1;
			SquareSoundImpl sq = (SquareSoundImpl) soundList.get(ix);
			switch (addr & 3) {
			case 0:
				if ((val & 0x10) > 0) {
					// エンベロープ無効
					sq.setVolume(val >> 6, (val & 0x20) > 0, val & 15);
				} else {
					sq.setEnvelope(val >> 6, (val & 0x20) > 0, val & 15);
				}
				break;
			case 1:
				sq.setSweep((val & 0x80) > 0, (val >> 4) & 7, (val & 8) > 0,
						val & 7);
				break;
			case 2:
				squareTimer[ix] = ((squareTimer[ix] & 0x700) | val);
				break;
			case 3:
				squareTimer[ix] = ((squareTimer[ix] & 0xff) | ((val & 7) << 8));
				sq.setTimer((val >> 3) & 0x1f, squareTimer[ix]);
				break;
			}
		} else if (addr < 0x400c) {
			// 三角波
			TriangleSoundImpl triangleData = (TriangleSoundImpl) soundList
					.get(2);
			switch (addr & 3) {
			case 0:
				triangleData.setLinear((val & 0x80) > 0, val & 0x7f);
				break;
			case 2:
				triangleTimer = ((triangleTimer & 0x700) | val);
				break;
			case 3:
				triangleTimer = ((triangleTimer & 0xff) | ((val & 7) << 8));
				triangleData.setTimer((val >> 3) & 0x1f, triangleTimer);
				break;
			}
		} else if (addr < 0x4010) {
			// ノイズ
			NoiseSoundImpl noiseData = (NoiseSoundImpl) soundList.get(3);
			switch (addr & 3) {
			case 0:
				if ((val & 0x10) > 0) {
					// ボリューム
					noiseData.setVolume((val & 0x20) > 0, val & 15);
				} else {
					noiseData.setEnvelope((val & 0x20) > 0, val & 15);
				}
				break;
			case 2:
				noiseData.setRandomMode((val & 0x80) > 0, val & 15);
				break;
			case 3:
				noiseData.setLength((val >> 3) & 0x1f);
				break;
			}
		}
		return this;
	}

	@Override
	public int read(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFamicomAPU write(int addr, byte[] data, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFamicomAPU read(int addr, byte[] data, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISquareSound getSquare1() {
		return (ISquareSound) soundList.get(0);
	}

	@Override
	public ISquareSound getSquare2() {
		return (ISquareSound) soundList.get(1);
	}

	@Override
	public ITriangleSound getTriangle() {
		return (ITriangleSound) soundList.get(2);
	}

	@Override
	public INoiseSound getNoise() {
		return (INoiseSound) soundList.get(3);
	}

	@Override
	public IDMASound getDMA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSoundListener(SoundListener listener) {
		soundListener = listener;
	}
}
