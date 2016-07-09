package famicom.util.apu;

import famicom.api.apu.IFamicomAPU;
import famicom.api.apu.INoiseSound;

public abstract class AbstractNoiseSequencer implements SoundSequencer {

	protected AbstractNoiseSequencer() {
	}

	@Override
	public ChannelType[] getChannels() {
		return new ChannelType[] { ChannelType.Noise };
	}

	@Override
	public boolean soundStep(IFamicomAPU apu, ChannelType channel, int frame) {
		return soundStep(apu.getNoise(), frame);
	}

	@Override
	public void start(IFamicomAPU apu, ChannelType channel, int frame) {
		start(apu.getNoise(), frame);
	}

	@Override
	public void pause(IFamicomAPU apu, ChannelType channel, int frame) {
		pause(apu.getNoise(), frame);
	}

	protected void start(INoiseSound sound, int frame) {
	}

	protected void pause(INoiseSound sound, int frame) {
		sound.stop();
	}

	abstract protected boolean soundStep(INoiseSound sound, int frame);
}
