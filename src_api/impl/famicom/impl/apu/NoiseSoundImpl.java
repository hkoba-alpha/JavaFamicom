package famicom.impl.apu;

import famicom.api.apu.INoiseSound;

class NoiseSoundImpl extends SoundBase implements INoiseSound {

	static final int[] noizeTimerIndex = { 4, 8, 16, 32, 64, 96, 128, 160, 202,
			254, 380, 508, 762, 1016, 034, 4068 };

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

	private int lengthCounter;
	private boolean loopFlag;
	private int volumeValue;
	private EnvelopeData envData;
	private EnvelopeData nextEnv;
	private int shiftRegister;
	private boolean shortFlag;
	private int lastIndex;

	@Override
	public INoiseSound setVolume(boolean stopFlag, int volume) {
		volumeValue = volume;
		loopFlag = stopFlag;
		envData = null;
		nextEnv = null;
		return this;
	}

	@Override
	public INoiseSound setEnvelope(boolean loopFlag, int period) {
		this.loopFlag = loopFlag;
		nextEnv = new EnvelopeData(period + 1);
		volumeValue = 0;
		return this;
	}

	@Override
	public INoiseSound setRandomMode(boolean shortFlag, int timerIndex) {
		this.shortFlag = shortFlag;
		super.setTimerCount(noizeTimerIndex[timerIndex]);
		lastIndex = -1;
		return this;
	}

	@Override
	public INoiseSound setLength(int lengthIndex) {
		if (nextEnv != null) {
			envData = nextEnv;
			nextEnv = null;
		}
		if (envData != null) {
			envData.count = envData.period;
			volumeValue = 15;
		}
		lengthCounter = lengthIndexData[lengthIndex];
		shiftRegister = 0x4000;
		return this;
	}

	@Override
	protected byte getOutput(int index) {
		while (index > lastIndex) {
			if (shortFlag) {
				shiftRegister = (shiftRegister << 1)
						| (((shiftRegister >> 14) & 1) ^ ((shiftRegister >> 8) & 1));
			} else {
				shiftRegister = (shiftRegister << 1)
						| (((shiftRegister >> 14) & 1) ^ ((shiftRegister >> 13) & 1));
			}
			lastIndex++;
		}
		return ((shiftRegister & 0x8000) == 0) ? (byte) volumeValue : 0;
	}

	private void stepLength() {
		if (!loopFlag && lengthCounter > 0) {
			lengthCounter--;
		}
	}

	private void stepEnvelope() {
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
	public INoiseSound stop() {
		lengthCounter = 0;
		return this;
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}
}
