package famicom.api.ppu;

public interface ISprite {
	/**
	 * Y座標を設定する
	 * @param y
	 * @return
	 */
	ISprite setY(int y);

	/**
	 * X座標を設定する
	 * @param x
	 * @return
	 */
	ISprite setX(int x);

	/**
	 * 属性を設定する
	 * @param verRevFlag 垂直反転フラグ
	 * @param horRevFlag 水平反転フラグ
	 * @param frontBgFlag BG全面フラグ
	 * @param colorIndex 色[0-3]
	 * @return スプライト自身
	 */
	ISprite setAttribute(boolean verRevFlag, boolean horRevFlag, boolean frontBgFlag, int colorIndex);

	/**
	 * パターンを設定する
	 * @param patIndex
	 * @return
	 */
	ISprite setPattern(int patIndex);
}
