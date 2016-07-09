package famicom.api.rom.listener;

import famicom.api.apu.IFamicomAPU;

public interface SoundListener {
	/**
	 * 240Hzで呼ばれる処理
	 * @param apu
	 * @param irqFlag
	 */
	void soundStep(IFamicomAPU apu, boolean irqFlag);
}
