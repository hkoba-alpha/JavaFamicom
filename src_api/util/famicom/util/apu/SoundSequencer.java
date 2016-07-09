package famicom.util.apu;

import famicom.api.apu.IFamicomAPU;

public interface SoundSequencer {
	public enum ChannelType {
		Square1,
		Square2,
		Triangle,
		Noise,
		DMA
	}

	/**
	 * 使用するチャネル
	 * @return チャネル
	 */
	ChannelType[] getChannels();

	/**
	 * 音の処理をする
	 * @param apu APU
	 * @param channel チャネル
	 * @param frame 開始してからのフレーム(240 frame/s)
	 * @return true:続きがある, false:終了
	 */
	boolean soundStep(IFamicomAPU apu, ChannelType channel, int frame);

	void start(IFamicomAPU apu, ChannelType channel, int frame);
	void pause(IFamicomAPU apu, ChannelType channel, int frame);
}
