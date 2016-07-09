package famicom.impl.ppu;

import famicom.api.ppu.ISprite;

public class SpriteImpl implements ISprite {
	private int spX;
	private int spY;
	private boolean verRevFlag;
	private boolean horRevFlag;
	/**
	 * 3:手前,1:BGの後ろ
	 */
	private int colorFlag;
	private int colorOffset;
	private int patternOffset;

	public SpriteImpl() {
		colorFlag = 0x3000000;
		colorOffset = 8;
	}

	@Override
	public ISprite setY(int y) {
		spY = y & 255;
		return this;
	}

	@Override
	public ISprite setX(int x) {
		spX = x & 255;
		return this;
	}

	@Override
	public ISprite setAttribute(boolean verRevFlag, boolean horRevFlag,
			boolean frontBgFlag, int colorIndex) {
		this.verRevFlag = verRevFlag;
		this.horRevFlag = horRevFlag;
		colorFlag = frontBgFlag ? 0x1000000: 0x3000000;
		colorOffset = colorIndex * 4;
		return this;
	}

	@Override
	public ISprite setPattern(int patIndex) {
		patternOffset = patIndex * 16;
		return this;
	}

	/**
	 * スキャンライン
	 * @param line ライン[7-239]
	 * @param scanLine
	 * @param offset
	 * @param color
	 * @param spSize
	 * @param leftFlag 左端を描画するか
	 * @param pattern
	 * @return
	 */
	public boolean fetchLine(int line, int[] scanLine, int offset, int[] color, int spSize, boolean leftFlag, PatternTableImpl pattern) {
		int iy = (line - spY) & 255;
		if (iy >= spSize) {
			return false;
		}
		if (verRevFlag) {
			iy = spSize - iy - 1;
		}
		byte ch1 = pattern.getPatternData()[(patternOffset + iy) & 0xfff];
		byte ch2 = pattern.getPatternData()[(patternOffset + iy + 8) & 0xfff];
		int bit = 0x80;
		for (int dx = 0; dx < 8; dx++) {
			int x;
			if (horRevFlag) {
				x = (spX + 7 - dx) & 255;
			} else {
				x = (spX + dx) & 255;
			}
			if (!leftFlag && x < 8) {
				bit >>= 1;
				continue;
			}
			if ((scanLine[offset + x] & 0x1000000) != 0) {
				// すでに他が描画されている
				bit >>= 1;
				continue;
			}
			int ch = 0;
			if ((ch2 & bit) > 0) {
				ch = 2;
			}
			if ((ch1 & bit) > 0) {
				ch++;
			}
			if (ch != 0) {
				scanLine[offset + x] = (color[colorOffset + ch] & 0xffffff) | colorFlag;
			}
			bit >>= 1;
		}
		return true;
	}
}
