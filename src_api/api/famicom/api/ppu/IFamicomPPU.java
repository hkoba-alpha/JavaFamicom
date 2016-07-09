package famicom.api.ppu;

import famicom.api.IAccessMemory;
import famicom.api.rom.listener.HBlankListener;
import famicom.api.rom.listener.VBlankListener;

/**
 * ファミコンのPPUインタフェース
 * @author hkoba
 *
 */
public interface IFamicomPPU extends IAccessPPU32<IFamicomPPU>, IAccessMemory<IFamicomPPU> {
	static final int ADDR_PATTERN_0 = 0x0000;
	static final int ADDR_PATTERN_1 = 0x1000;
	static final int ADDR_NAMETABLE_0 = 0x2000;
	static final int ADDR_NAMETABLE_1 = 0x2400;
	static final int ADDR_NAMETABLE_2 = 0x2800;
	static final int ADDR_NAMETABLE_3 = 0x2c00;

	/**
	 * スプライトを取得する
	 * @param index 番号[0-63]
	 * @return
	 */
	ISprite getSprite(int index);

	/**
	 * PPUコントロール１
	 * @param bigSpriteFlag スプライトサイズ[false:8x8, true:8x16]
	 * @param bgAddr BGパターンアドレス[false:0x0000, true:0x1000]
	 * @param spriteAddr スプライトパターンアドレス[false:0x0000, true:0x1000]
	 * @param nameIndex ネームテーブルアドレス[0:0x2000, 1:0x2400, 2:0x2800, 3:0x2c00]
	 * @return
	 */
	IFamicomPPU controlPPU1(boolean bigSpriteFlag, boolean bgAddr, boolean spriteAddr, int nameIndex);

	/**
	 * PPUコントロール２
	 * @param bgColor 背景色[0-7:RredBlueGreen]
	 * @param spriteFlag スプライト有効フラグ
	 * @param bgFlag BG有効フラグ
	 * @param spriteMask スプライト左端描画フラグ
	 * @param bgMask BG左端描画フラグ
	 * @return
	 */
	IFamicomPPU controlPPU2(int bgColor, boolean spriteFlag, boolean bgFlag, boolean spriteMask, boolean bgMask);

	/**
	 * スクロール設定
	 * @param horizontal 水平スクロール値
	 * @param vertical 垂直スクロール値
	 * @return
	 */
	IFamicomPPU setScroll(int horizontal, int vertical);

	/**
	 * 
	 * @param addr [ADDR_NAMETABLE_0 - ADDR_NAMETABLE_3]
	 * @return
	 */
	INameTable getNameTable(int addr);

	/**
	 * 
	 * @param addr [ADDR_PATTEN_0, ADDR_PATTERN_1]
	 * @return
	 */
	IPatternTable getPattenTable(int addr);

	/**
	 * パレット情報を取得する
	 * @return
	 */
	IPalette getPalette();

	IFamicomPPU setHBlankListener(HBlankListener listener);
	IFamicomPPU setVBlankListener(VBlankListener listener);
}
