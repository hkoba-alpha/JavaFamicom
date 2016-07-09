package famicom.util.apu;

import famicom.api.apu.IFamicomAPU;
import famicom.api.apu.ITriangleSound;

public abstract class AbstractTriangleSequencer implements SoundSequencer {

	protected AbstractTriangleSequencer() {
	}

	@Override
	public ChannelType[] getChannels() {
		return new ChannelType[] { ChannelType.Triangle };
	}

	@Override
	public boolean soundStep(IFamicomAPU apu, ChannelType channel, int frame) {
		return soundStep(apu.getTriangle(), frame);
	}

	@Override
	public void start(IFamicomAPU apu, ChannelType channel, int frame) {
		start(apu.getTriangle(), frame);
	}

	@Override
	public void pause(IFamicomAPU apu, ChannelType channel, int frame) {
		pause(apu.getTriangle(), frame);
	}

	protected void start(ITriangleSound sound, int frame) {
	}

	protected void pause(ITriangleSound sound, int frame) {
		sound.stop();
	}

	abstract protected boolean soundStep(ITriangleSound sound, int frame);
}
