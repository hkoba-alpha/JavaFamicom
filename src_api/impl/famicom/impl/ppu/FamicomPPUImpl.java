package famicom.impl.ppu;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.util.Arrays;

import famicom.api.IFamicom;
import famicom.api.ppu.IFamicomPPU;
import famicom.api.ppu.INameTable;
import famicom.api.ppu.IPalette;
import famicom.api.ppu.IPatternTable;
import famicom.api.ppu.ISprite;
import famicom.api.rom.annotation.MirrorMode;
import famicom.api.rom.listener.HBlankListener;
import famicom.api.rom.listener.VBlankListener;

public class FamicomPPUImpl implements IFamicomPPU {
	private static final int SPRITE_MAX = 8;

	private PatternTableImpl[] patternData;
	private SpriteImpl[] spriteData;
	private PaletteImpl paletteData;
	private NameTableImpl[] nameTableData;

	private BufferedImage bufImage;

	private int[] bufScreen;

	private int scrollX;
	private int scrollY;
	private boolean bgEnableFlag;
	private boolean spriteEnableFlag;
	private boolean bgLeftView;
	private boolean spriteLeftView;
	private NameTableImpl[] screenData;
	private PatternTableImpl spritePattern;
	private PatternTableImpl bgPattern;
	private int spriteSize;
	private HBlankListener hblankListener;
	private VBlankListener vblankListener;
	private int nameIndex;
	private MirrorMode mirrorMode;
	/**
	 * 背景色の色
	 */
	private int bgColorFlag;

	public FamicomPPUImpl(MirrorMode mode) {
		mirrorMode = mode;
		patternData = new PatternTableImpl[2];
		patternData[0] = new PatternTableImpl();
		patternData[1] = new PatternTableImpl();
		paletteData = new PaletteImpl();
		spriteData = new SpriteImpl[64];
		spriteSize = 8;
		for (int i = 0; i < spriteData.length; i++) {
			spriteData[i] = new SpriteImpl();
		}
		nameTableData = new NameTableImpl[4];
		for (int i = 0; i < nameTableData.length; i++) {
			nameTableData[i] = new NameTableImpl();
		}
		spritePattern = bgPattern = patternData[0];
		screenData = new NameTableImpl[4];
		screenData[0] = nameTableData[0];
		bufImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
		bufScreen = ((DataBufferInt) bufImage.getRaster().getDataBuffer())
				.getData();
		setNameIndex(0);
	}

	/**
	 * 
	 * @param nmix
	 */
	private void setNameIndex(int nmix) {
		nameIndex = nmix;
		switch (mirrorMode) {
		case HORIZONTAL:
			nameTableData[2] = nameTableData[0];
			nameTableData[3] = nameTableData[1];
			screenData[1] = nameTableData[nameIndex ^ 2];
			screenData[2] = nameTableData[nameIndex ^ 1];
			break;
		case VERTICAL:
			nameTableData[2] = nameTableData[0];
			nameTableData[3] = nameTableData[1];
		default:
			screenData[1] = nameTableData[nameIndex ^ 1];
			screenData[2] = nameTableData[nameIndex ^ 2];
			break;
		}
		screenData[0] = nameTableData[nameIndex];
		screenData[3] = nameTableData[nameIndex ^ 3];
	}

	@Override
	public IFamicomPPU writePPU32(int addr, byte[] data, int offset,
			int length, boolean add32Flag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFamicomPPU readPPU32(int addr, byte[] data, int offset, int length,
			boolean add32Flag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFamicomPPU writePPU(int addr, int data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFamicomPPU writePPU(int addr, byte[] data, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int readPPU(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFamicomPPU readPPU(int addr, byte[] data, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISprite getSprite(int index) {
		return spriteData[index & 255];
	}

	@Override
	public IFamicomPPU controlPPU1(boolean bigSpriteFlag, boolean bgAddr,
			boolean spriteAddr, int nameIndex) {
		spriteSize = bigSpriteFlag ? 16 : 8;
		bgPattern = patternData[bgAddr ? 1 : 0];
		spritePattern = patternData[spriteAddr ? 1 : 0];
		setNameIndex(nameIndex & 3);
		return this;
	}

	@Override
	public IFamicomPPU controlPPU2(int bgColor, boolean spriteFlag,
			boolean bgFlag, boolean spriteMask, boolean bgMask) {
		bgColorFlag = bgColor & 7;
		spriteEnableFlag = spriteFlag;
		bgEnableFlag = bgFlag;
		spriteLeftView = spriteMask;
		bgLeftView = bgMask;
		return this;
	}

	@Override
	public IFamicomPPU setScroll(int horizontal, int vertical) {
		scrollX = horizontal & 255;
		scrollY = vertical & 255;
		return this;
	}

	@Override
	public INameTable getNameTable(int addr) {
		return nameTableData[(addr >> 10) & 3];
	}

	@Override
	public IPatternTable getPattenTable(int addr) {
		return patternData[(addr >> 12) & 1];
	}

	@Override
	public IPalette getPalette() {
		return paletteData;
	}

	@Override
	public IFamicomPPU setHBlankListener(HBlankListener listener) {
		this.hblankListener = listener;
		return this;
	}

	@Override
	public IFamicomPPU setVBlankListener(VBlankListener listener) {
		this.vblankListener = listener;
		return this;
	}

	@Override
	public IFamicomPPU write(int addr, int data) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public int read(int addr) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFamicomPPU write(int addr, byte[] data, int offset, int length) {
		while (length > 0) {
			write(addr, data[offset] & 255);
			offset++;
			length--;
			addr++;
		}
		return this;
	}

	@Override
	public IFamicomPPU read(int addr, byte[] data, int offset, int length) {
		while (length > 0) {
			data[offset] = (byte) read(addr);
			offset++;
			length--;
			addr++;
		}
		return this;
	}

	/**
	 * 
	 * @param line
	 * @param imageFlag
	 */
	public void stepFrame(IFamicom famicom, int line, boolean imageFlag) {
		int[] spPalette = paletteData.getSpritePalette();
		int[] bgPalette = paletteData.getBgPalette();
		if (line == 0 && imageFlag) {
			Arrays.fill(bufScreen, spPalette[0] & 0xffffff);
		}
		if (imageFlag && line >= 8 && line < 232) {
			// sprite
			if (spriteEnableFlag) {
				int count = 0;
				for (SpriteImpl sp : spriteData) {
					if (sp.fetchLine(line - 1, bufScreen, line << 8,
							spPalette, spriteSize, spriteLeftView,
							spritePattern)) {
						count++;
						if (count == SPRITE_MAX) {
							break;
						}
					}
				}
			}
			// hlistener
			if (hblankListener != null) {
				hblankListener.hBlank(famicom, this, line);
			}
			// bg
			if (bgEnableFlag) {
				int nmix = 0;
				int iy = (line + scrollY) % 480;
				if (iy >= 240) {
					nmix = 2;
					iy -= 240;
				}
				int wd = 256;
				int sx = scrollX;
				int offset = line << 8;
				if (!bgLeftView) {
					wd -= 8;
					sx += 8;
					offset += 8;
				}
				if (sx < 256) {
					int len = 256 - sx;
					screenData[nmix].fetchLine(sx, iy, bufScreen, offset,
							bgPalette, len, bgPattern);
					wd -= len;
					offset += len;
				}
				if (wd > 0) {
					screenData[nmix + 1].fetchLine(0, iy, bufScreen, offset,
							bgPalette, wd, bgPattern);
				}
			}
		} else {
			if (line == 240) {
				if (bgColorFlag > 0) {
					int flag = 0;
					if ((bgColorFlag & 1) > 0) {
						// 緑
						flag = 0x00c000;
					}
					if ((bgColorFlag & 2) > 0) {
						// 青
						flag |= 0x0000c0;
					}
					if ((bgColorFlag & 4) > 0) {
						// 赤
						flag |= 0xc00000;
					}
					for (int i = 0; i < bufScreen.length; i++) {
						bufScreen[i] |= flag;
					}
				}
				// vlistener
				if (vblankListener != null) {
					vblankListener.vBlank(famicom, this);
				}
			}
			// hlistener
			if (hblankListener != null) {
				hblankListener.hBlank(famicom, this, line);
			}
		}
	}

	public BufferedImage getImage() {
		return bufImage;
	}
}
