package famicom.api.rom.listener;

import famicom.api.IFamicom;
import famicom.api.ppu.IFamicomPPU;

public interface HBlankListener {
	/**
	 * HBlankイベント.
	 * @param famicom
	 * @param ppu
	 * @param line 0-239
	 */
	void hBlank(IFamicom famicom, IFamicomPPU ppu, int line);
}
