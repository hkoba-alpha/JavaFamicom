package famicom.api.ppu;

public interface INameTable extends IAccessPPU32<INameTable> {
	/**
	 * パレットを設定する
	 * @param x X座標
	 * @param y Y座標
	 * @param colIndex カラーインデックス[0-3]
	 * @return
	 */
	INameTable setColor(int x, int y, int colIndex);

	INameTable print(int x, int y, byte[] data, int width, int height, int offset, int lineSize);
	INameTable print(int x, int y, int data);
}
