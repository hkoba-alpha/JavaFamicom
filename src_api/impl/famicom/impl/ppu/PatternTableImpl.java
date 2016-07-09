package famicom.impl.ppu;

import famicom.api.ppu.IPatternTable;

public class PatternTableImpl implements IPatternTable {
	private byte[] patternData = new byte[0x1000];

	private AccessPPUHelper<PatternTableImpl> helper;
	
	public PatternTableImpl() {
		helper = new AccessPPUHelper<PatternTableImpl>(this);
		for (int i = 0; i < patternData.length; i++) {
			//patternData[i] = (byte)((i >> 2) & 255);
			if ((i & 8) == 0) {
				patternData[i] = 15;
			} else if ((i & 4) > 0){
				patternData[i] = (byte)255;
			}
		}
	}

	@Override
	public IPatternTable writePPU(int addr, int data) {
		patternData[addr & 0xfff] = (byte)data;
		return this;
	}

	@Override
	public IPatternTable writePPU(int addr, byte[] data, int offset, int length) {
		while (length > 0) {
			addr &= 0xfff;
			int len = length;
			if (addr + len >= 0x1000) {
				len = 0x1000 - addr;
			}
			System.arraycopy(data, offset, patternData, addr, len);
			length -= len;
			addr += len;
			offset += len;
		}
		return this;
	}

	@Override
	public int readPPU(int addr) {
		return patternData[addr & 0xfff] & 255;
	}

	@Override
	public IPatternTable readPPU(int addr, byte[] data, int offset, int length) {
		while (length > 0) {
			addr &= 0xfff;
			int len = length;
			if (addr + len >= 0x1000) {
				len = 0x1000 - addr;
			}
			System.arraycopy(patternData, addr, data, offset, len);
			length -= len;
			addr += len;
			offset += len;
		}
		return this;
	}

	@Override
	public IPatternTable setChracter(int index, byte[] data) {
		return writePPU((index & 255) << 4, data, 0, 16);
	}

	@Override
	public byte[] getCharacter(int index) {
		byte[] ret = new byte[16];
		System.arraycopy(patternData, (index & 255)<<4, ret, 0, 16);
		return ret;
	}

	public byte[] getPatternData() {
		return patternData;
	}
}
