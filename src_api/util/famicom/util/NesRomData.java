package famicom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NesRomData {
	private File nesFile;

	private int prgUnitSize;
	private int chrUnitSize;

	public NesRomData(File file) throws IOException {
		nesFile = file;
		FileInputStream is = new FileInputStream(file);
		byte[] head = new byte[16];
		is.read(head);
		is.close();
		if (head[0] != 'N' || head[1] != 'E' || head[2] != 'S' || head[3] != 0x1a) {
			throw new IOException("Invalid file header.");
		}
		prgUnitSize = head[4];
		chrUnitSize = head[5];
	}

	public int getPrgUnitSize() {
		return prgUnitSize;
	}

	public int getChrUnitSize() {
		return chrUnitSize;
	}

}
