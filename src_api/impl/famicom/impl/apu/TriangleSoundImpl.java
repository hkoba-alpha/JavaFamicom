package famicom.impl.apu;

import famicom.api.apu.ITriangleSound;

public class TriangleSoundImpl extends SoundBase implements ITriangleSound {
	/*
	private static final byte[] sampleData = { 15, 14, 13, 12, 11, 10, 9, 8, 7,
		6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
		14, 15 };*/
	
	private static final byte[] sampleData = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
		14, 15, 15, 14, 13, 12, 11, 10, 9, 8, 7,
		6, 5, 4, 3, 2, 1, 0, 0 };

	private int lengthCounter;
	private int lineCounter;
	private int lineCounterData;
	private boolean loopFlag;

	public TriangleSoundImpl() {
		super((byte)0);
	}

	@Override
	public ITriangleSound setLinear(boolean loopFlag, int lineCount) {
		this.loopFlag = loopFlag;
		this.lineCounterData = lineCount;
		this.lineCounter = lineCount;
		
		return this;
	}

	@Override
	public ITriangleSound setTimer(int lengthIndex, int timerCount) {
		lineCounter = lineCounterData;
		lengthCounter = lengthIndexData[lengthIndex];
		super.setTimerCount(timerCount + 1);
		return this;
	}

	@Override
	protected byte getOutput(int index) {
		return sampleData[index % sampleData.length];
	}

	@Override
	public void setFrameSample(byte[] data, int offset) {
		if (lengthCounter == 0 || lineCounter == 0) {
			fillNullSound(data, offset);
			return;
		}
		super.setFrameSample(data, offset);
	}

	public void stepLength() {
		if (!loopFlag) {
			if (lineCounter > 0) {
				lineCounter--;
			}
			if (lengthCounter > 0) {
				lengthCounter--;
			}
		}
	}

	@Override
	public void stepFrame(int index, int stepNum) {
		if (index < 4) {
			stepLength();
		}
	}

	@Override
	public boolean isPlaying() {
		return lengthCounter > 0;
	}

	@Override
	public ITriangleSound stop() {
		lengthCounter = 0;
		return this;
	}
}
