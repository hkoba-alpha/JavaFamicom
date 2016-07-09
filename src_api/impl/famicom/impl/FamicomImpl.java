package famicom.impl;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import famicom.api.IFamicom;
import famicom.api.apu.IFamicomAPU;
import famicom.api.pad.IJoyPad;
import famicom.api.ppu.IFamicomPPU;
import famicom.api.rom.FamicomROM;
import famicom.api.rom.annotation.MirrorMode;
import famicom.api.rom.annotation.PadConfig;
import famicom.api.rom.annotation.RomInformation;
import famicom.impl.apu.FamicomAPUImpl;
import famicom.impl.pad.JoyPadImpl;
import famicom.impl.ppu.FamicomPPUImpl;

@PadConfig(number = 0, up = KeyEvent.VK_UP, down = KeyEvent.VK_DOWN, left = KeyEvent.VK_LEFT, right = KeyEvent.VK_RIGHT, start = KeyEvent.VK_ENTER, select = KeyEvent.VK_SHIFT, a = KeyEvent.VK_X, b = KeyEvent.VK_Z)
@PadConfig(number = 1, up = KeyEvent.VK_U, down = KeyEvent.VK_M, left = KeyEvent.VK_H, right = KeyEvent.VK_K, start = 0, select = 0, a = KeyEvent.VK_B, b = KeyEvent.VK_V)
public class FamicomImpl extends Canvas implements IFamicom, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FamicomPPUImpl famicomPPU;
	private FamicomAPUImpl famicomAPU;
	private JoyPadImpl[] joyPad;

	private int scaleSize;

	private String romName;

	private byte[] mainMemory;

	private ByteBuffer backupMemory;

	public FamicomImpl(FamicomROM rom) {
		try {
			File backFile = new File("state/" + rom.getClass().getName()
					+ "/backup.dat");
			if (!backFile.isFile()) {
				backFile.getParentFile().mkdirs();
				FileOutputStream os = new FileOutputStream(backFile);
				os.write(new byte[0x2000], 0, 0x2000);
				os.close();
			}
			FileChannel channel = new RandomAccessFile(backFile, "rw").getChannel();
			backupMemory = channel.map(MapMode.READ_WRITE, 0, 0x2000);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		joyPad = new JoyPadImpl[4];
		PadConfig[] padlst = FamicomImpl.class
				.getAnnotationsByType(PadConfig.class);
		if (padlst != null) {
			for (PadConfig pad : padlst) {
				joyPad[pad.number()] = new JoyPadImpl(pad);
			}
		}
		MirrorMode mode = MirrorMode.VERTICAL;
		RomInformation info = rom.getClass()
				.getAnnotation(RomInformation.class);
		if (info != null) {
			mode = info.mirror();
			romName = info.name();
		}
		famicomPPU = new FamicomPPUImpl(mode);
		famicomAPU = new FamicomAPUImpl();
		scaleSize = 3;
		addKeyListener(this);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(256 * scaleSize, 224 * scaleSize);
	}

	public String getRomName() {
		return romName;
	}

	public void setScale(int sc) {
		scaleSize = sc;
	}

	@Override
	public IFamicomPPU getPPU() {
		return famicomPPU;
	}

	@Override
	public IFamicom write(int addr, int data) {
		if (addr < 0x2000) {
			// メイン
			mainMemory[addr & 0x7ff] = (byte)data;
		} else if (addr >= 0x6000 && addr < 0x8000) {
			// バックアップ
			backupMemory.position(addr - 0x6000);
			backupMemory.put((byte)data);
		}
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public int read(int addr) {
		if (addr < 0x2000) {
			// メイン
			return mainMemory[addr & 0x7ff];
		} else if (addr >= 0x6000 && addr < 0x8000) {
			// バックアップ
			backupMemory.position(addr - 0x6000);
			return backupMemory.get() & 255;
		}
		return 0;
	}

	@Override
	public IFamicom write(int addr, byte[] data, int offset, int length) {
		if (addr < 0x800) {
			int sz = 0x800 - addr;
			if (sz > length) {
				sz = length;
			}
			System.arraycopy(data, offset, mainMemory, addr, sz);
			addr += sz;
			length -= sz;
			offset += sz;
		}
		if (addr + length > 0x6000 && addr < 0x8000) {
			// バックアップ
			if (addr < 0x6000) {
				length -= (0x6000 - addr);
				offset += (0x6000 - addr);
				addr = 0x6000;
			}
			int sz = 0x8000 - addr;
			if (sz > length) {
				sz = length;
			}
			backupMemory.position(addr - 0x6000);
			backupMemory.put(data, offset, sz);
		}
		return this;
	}

	@Override
	public IFamicom read(int addr, byte[] data, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	public void mainLoop() {
		createBufferStrategy(2);
		BufferStrategy bstr = getBufferStrategy();
		// 17 17 16
		int tmix = 0;
		int[] add = new int[] { 17, 16, 17 };
		long nextTime = System.currentTimeMillis() + add[tmix];
		tmix++;
		try {
			Graphics g = bstr.getDrawGraphics();
			while (true) {
				int sc = scaleSize;
				for (int i = 0; i < 262; i++) {
					famicomPPU.stepFrame(this, i, g != null);
					if ((i % 66) == 0) {
						famicomAPU.stepFrame();
					}
				}
				if (g != null) {
					// g.drawImage(famicomPPU.getImage(), 0, 0, null);
					g.drawImage(famicomPPU.getImage(), 0, 0, 256 * sc,
							224 * sc, 0, 8, 256, 232, null);
					bstr.show();
					g.dispose();
				}
				famicomAPU.flushSound();
				long tm = System.currentTimeMillis();
				if (tm < nextTime) {
					Thread.currentThread().join(nextTime - tm);
					g = bstr.getDrawGraphics();
					// System.out.println("draw:" + (nextTime - tm));
				} else if (g == null) {
					// ２回連続
					System.out.println("skip-reset");
					nextTime = tm;
				} else {
					System.out.println("skip");
					g = null;
				}
				nextTime += add[tmix];
				tmix = (tmix + 1) % 3;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public IFamicomAPU getAPU() {
		return famicomAPU;
	}

	@Override
	public IJoyPad getPad(int num) {
		if (num < 0 || num >= joyPad.length) {
			return null;
		}
		return joyPad[num];
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		for (JoyPadImpl pad : joyPad) {
			if (pad != null) {
				pad.processEvent(e, true);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (JoyPadImpl pad : joyPad) {
			if (pad != null) {
				pad.processEvent(e, false);
			}
		}
	}
}
