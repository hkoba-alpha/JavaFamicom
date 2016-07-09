package famicom.api.apu;

import famicom.api.IAccessMemory;
import famicom.api.rom.listener.SoundListener;

public interface IFamicomAPU extends IAccessMemory<IFamicomAPU> {
	ISquareSound getSquare1();
	ISquareSound getSquare2();

	ITriangleSound getTriangle();

	INoiseSound getNoise();

	IDMASound getDMA();

	void setSoundListener(SoundListener listener);
}
