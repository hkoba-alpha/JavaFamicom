package famicom.api.rom.listener;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import famicom.api.IFamicom;

public interface GameStateListener {
	void saveState(ObjectOutputStream out, IFamicom famicom,
				   VBlankListener vListener, HBlankListener hListener,
				   SoundListener sListener);

	void loadState(ObjectInputStream ins, IFamicom famicom,
				   Class<? extends VBlankListener> vListenerClass,
				   Class<? extends HBlankListener> hListenerClass,
				   Class<? extends SoundListener> sListenerClass);
}
