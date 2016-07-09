package famicom.api;

import famicom.api.apu.IFamicomAPU;
import famicom.api.pad.IJoyPad;
import famicom.api.ppu.IFamicomPPU;

public interface IFamicom extends IAccessMemory<IFamicom> {
	static final int PAD_1 = 0;
	static final int PAD_2 = 1;

	IFamicomPPU getPPU();
	IFamicomAPU getAPU();
	IJoyPad getPad(int num);
}
