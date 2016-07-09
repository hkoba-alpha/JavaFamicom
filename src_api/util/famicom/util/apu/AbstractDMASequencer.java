package famicom.util.apu;

import famicom.api.apu.IDMASound;
import famicom.api.apu.IFamicomAPU;

public abstract class AbstractDMASequencer implements SoundSequencer {

	protected AbstractDMASequencer() {
	}

	@Override
	public ChannelType[] getChannels() {
		return new ChannelType[] { ChannelType.DMA };
	}

	@Override
	public boolean soundStep(IFamicomAPU apu, ChannelType channel, int frame) {
		return soundStep(apu.getDMA(), frame);
	}

	@Override
	public void start(IFamicomAPU apu, ChannelType channel, int frame) {
		start(apu.getDMA(), frame);
	}

	@Override
	public void pause(IFamicomAPU apu, ChannelType channel, int frame) {
		pause(apu.getDMA(), frame);
	}

	protected void start(IDMASound sound, int frame) {
	}

	protected void pause(IDMASound sound, int frame) {
	}

	abstract protected boolean soundStep(IDMASound sound, int frame);
}
