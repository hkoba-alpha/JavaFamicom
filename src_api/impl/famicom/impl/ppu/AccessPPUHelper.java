package famicom.impl.ppu;

import famicom.api.ppu.IAccessPPU;
import famicom.api.ppu.IAccessPPU32;

public class AccessPPUHelper<T extends IAccessPPU<? super T>> implements IAccessPPU32<T> {
	private T orgPPU;

	public AccessPPUHelper(T ppu) {
		orgPPU = ppu;
	}

	@Override
	public T writePPU(int addr, int data) {
		// no impl
		return orgPPU;
	}

	@Override
	public T writePPU(int addr, byte[] data, int offset, int length) {
		while (length > 0) {
			orgPPU.writePPU(addr, data[offset]);
			addr++;
			offset++;
			length--;
		}
		return orgPPU;
	}

	@Override
	public int readPPU(int addr) {
		// no impl
		return 0;
	}

	@Override
	public T readPPU(int addr, byte[] data, int offset, int length) {
		while (length > 0) {
			data[offset] = (byte)orgPPU.readPPU(addr);
			addr++;
			offset++;
			length--;
		}
		return orgPPU;
	}

	@Override
	public T writePPU32(int addr, byte[] data, int offset, int length,
			boolean add32Flag) {
		while (length > 0) {
			orgPPU.writePPU(addr, data[offset]);
			addr += 32;
			if (add32Flag) {
				offset += 32;
			} else {
				offset++;
			}
			length--;
		}
		return orgPPU;
	}

	@Override
	public T readPPU32(int addr, byte[] data, int offset, int length,
			boolean add32Flag) {
		while (length > 0) {
			data[offset] = (byte)orgPPU.readPPU(addr);
			addr += 32;
			if (add32Flag) {
				offset += 32;
			} else {
				offset++;
			}
			length--;
		}
		return orgPPU;
	}
}
