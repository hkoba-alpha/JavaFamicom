package famicom.impl.ppu;

import famicom.api.ppu.INameTable;

public class NameTableImpl implements INameTable {
	private byte[] charaData = new byte[0x3c0];
	private byte[] colorData = new byte[256];

	private AccessPPUHelper<NameTableImpl> helper;

	public NameTableImpl() {
		helper = new AccessPPUHelper<NameTableImpl>(this);
	}

	@Override
	public INameTable writePPU32(int addr, byte[] data, int offset, int length,
			boolean add32Flag) {
		return helper.writePPU32(addr, data, offset, length, add32Flag);
	}

	@Override
	public INameTable readPPU32(int addr, byte[] data, int offset, int length,
			boolean add32Flag) {
		return helper.readPPU32(addr, data, offset, length, add32Flag);
	}

	@Override
	public INameTable writePPU(int addr, int data) {
		addr &= 0x3ff;
		if (addr < 0x3c0) {
			charaData[addr] = (byte)data;
		} else {
			int ix = ((addr & 7) << 1) | ((addr & 0x38) << 2);
			colorData[ix] = (byte)((data & 3) << 2);
			colorData[ix + 1] = (byte)(data & 0xc);
			colorData[ix + 16] = (byte)((data & 0x30) >> 2);
			colorData[ix + 17] = (byte)((data & 0xc0) >> 4);
		}
		return this;
	}

	@Override
	public INameTable writePPU(int addr, byte[] data, int offset, int length) {
		return helper.writePPU(addr, data, offset, length);
	}

	@Override
	public int readPPU(int addr) {
		addr &= 0x3ff;
		if (addr < 0x3c0) {
			return charaData[addr] & 255;
		} else {
			int ix = ((addr & 7) << 1) | ((addr & 0x38) << 2);
			return (colorData[ix] >> 2)|colorData[ix + 1]|(colorData[ix + 16] << 2)|(colorData[ix + 17] << 4);
		}
	}

	@Override
	public INameTable readPPU(int addr, byte[] data, int offset, int length) {
		return helper.readPPU(addr, data, offset, length);
	}

	@Override
	public INameTable setColor(int x, int y, int colIndex) {
		colorData[((y & 0x1e) << 3) | (x >> 1)] = (byte)((colIndex & 3) << 2);
		return this;
	}

	@Override
	public INameTable print(int x, int y, byte[] data, int width, int height,
			int offset, int lineSize) {
		for (int dy = 0; dy < height; dy++) {
			int yy = (y + dy) % 30;
			for (int dx = 0; dx < width; dx++) {
				int xx = (x + dx) & 31;
				charaData[(yy << 5) + xx] = data[offset + dy * lineSize + dx];
			}
		}
		return this;
	}

	@Override
	public INameTable print(int x, int y, int data) {
		charaData[(y << 5) + x] = (byte)data;
		return this;
	}

	public void fetchLine(int sx, int sy, int[] scanLine, int offset, int[] color, int width, PatternTableImpl pattern) {
		int iy = sy & 7;
		int charaAddr = (sx >> 3) | ((sy & 0xf8) << 2);
		int colorAddr = (sx >> 4) | (sy & 0xf0);
		byte[] pat = pattern.getPatternData();
		int chix = (charaData[charaAddr] & 255) << 4;
		byte ch1 = pat[chix + iy];
		byte ch2 = pat[chix + iy + 8];
		byte col = colorData[colorAddr];
		int bit = 0x80 >> (sx & 7);
		while (true) {
			if (scanLine[offset] < 0x2000000) {
				int ch = 0;
				if ((ch2 & bit) > 0) {
					ch = 2;
				}
				if ((ch1 & bit) > 0) {
					ch++;
				}
				if (ch > 0) {
					scanLine[offset] = color[col + ch];
				}
			}
			width--;
			if (width == 0) {
				break;
			}
			offset++;
			if (bit == 1) {
				bit = 0x80;
				charaAddr++;
				chix = (charaData[charaAddr] & 255) << 4;
				ch1 = pat[chix + iy];
				ch2 = pat[chix + iy + 8];
				if ((charaAddr & 1) == 0) {
					colorAddr++;
					col = colorData[colorAddr];
				}
			} else {
				bit >>= 1;
			}
		}
	}
}
