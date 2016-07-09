package famicom.util.apu;

import famicom.api.apu.IFamicomAPU;
import famicom.api.apu.ISquareSound;

public abstract class AbstractSquareSequencer implements SoundSequencer {

	private ChannelType channelType;

	protected AbstractSquareSequencer(ChannelType channel) {
		channelType = channel;
	}

	@Override
	public ChannelType[] getChannels() {
		return new ChannelType[] { channelType };
	}

	@Override
	public boolean soundStep(IFamicomAPU apu, ChannelType channel, int frame) {
		return soundStep(getSquare(apu, channel), frame);
	}

	@Override
	public void start(IFamicomAPU apu, ChannelType channel, int frame) {
		start(getSquare(apu, channel), frame);
	}

	@Override
	public void pause(IFamicomAPU apu, ChannelType channel, int frame) {
		pause(getSquare(apu, channel), frame);
	}

	protected ISquareSound getSquare(IFamicomAPU apu, ChannelType type) {
		return type == ChannelType.Square1 ? apu.getSquare1(): apu.getSquare2();
	}

	protected void start(ISquareSound sound, int frame) {
	}

	protected void pause(ISquareSound sound, int frame) {
		sound.stop();
	}

	abstract protected boolean soundStep(ISquareSound sound, int frame);
}
