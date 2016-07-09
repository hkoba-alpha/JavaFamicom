package famicom.api.ppu;

public interface IPalette extends IAccessPPU<IPalette> {
	/**
	 * パレットを設定する
	 * @param spFlag スプライトかどうか[false:BG, true:Sprite]
	 * @param ix インデックス[0-3]
	 * @param color1 色１
	 * @param color2 色２
	 * @param color3 色３
	 * @return
	 */
	IPalette setPalette(boolean spFlag, int ix, int color1, int color2, int color3);

	/**
	 * 背景色を設定する
	 * @param color
	 * @return
	 */
	IPalette setBgColor(int color);
}
