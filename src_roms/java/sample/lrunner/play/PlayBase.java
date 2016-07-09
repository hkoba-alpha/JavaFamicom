package sample.lrunner.play;

import famicom.api.IFamicom;
import famicom.api.ppu.IFamicomPPU;

public interface PlayBase {
	PlayBase stepFrame(IFamicom famicom, IFamicomPPU ppu);

}
