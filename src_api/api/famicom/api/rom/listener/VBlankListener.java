package famicom.api.rom.listener;

import famicom.api.IFamicom;
import famicom.api.ppu.IFamicomPPU;

public interface VBlankListener {
	/**
	 * VBlank
	 * @param famicom
	 * @param ppu
	 */
	void vBlank(IFamicom famicom, IFamicomPPU ppu);
}
