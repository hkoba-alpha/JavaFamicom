package famicom.api.ppu;

public interface IAccessPPU<T> {
	/**
	 * PPUに1バイト書き込む
	 * @param addr アドレス
	 * @param data データ
	 * @return
	 */
	T writePPU(int addr, int data);

	/**
	 * PPUにまとめて書き込む
	 * @param addr 開始アドレス
	 * @param data データ
	 * @param offset dataの開始オフセット
	 * @param length 書き込みサイズ
	 * @return
	 */
	T writePPU(int addr, byte[] data, int offset, int length);

	/**
	 * PPUから1バイオ読み込む
	 * @param addr 読み込む
	 * @return
	 */
	int readPPU(int addr);
	
	/**
	 * PPUからまとめて読み込む
	 * @param addr 開始アドレス
	 * @param data データ
	 * @param offset dataの開始オフセット
	 * @param length 読み込むサイズ
	 * @return
	 */
	T readPPU(int addr, byte[] data, int offset, int length);
}
