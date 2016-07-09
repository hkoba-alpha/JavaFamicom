package famicom.api.ppu;

public interface IPatternTable extends IAccessPPU<IPatternTable> {
	/**
	 * キャラクタパターンを設定する
	 * @param index キャラクタインデックス[0-255]
	 * @param data 16バイトのデータ
	 * @return
	 */
	IPatternTable setChracter(int index, byte[] data);

	/**
	 * キャラクタパターンを取得する
	 * @param index　キャラクタインデックス[0-255]
	 * @return 16バイトのデータ
	 */
	byte[] getCharacter(int index);
}
