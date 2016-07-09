package famicom.api.ppu;

public interface IAccessPPU32<T> extends IAccessPPU<T> {

	/**
	 * PPUにまとめて書き込む.
	 * addrは32バイトずつずれる
	 * @param addr アドレス
	 * @param data データ
	 * @param offset dataの開始オフセット
	 * @param length 書き込みサイズ
	 * @param add32Flag dataも32バイトずつずらすかのフラグ
	 * @return
	 */
	T writePPU32(int addr, byte[] data, int offset, int length, boolean add32Flag);

	/**
	 * PPUからまとめて読み込む.
	 * addrは32バイトずつずれる
	 * @param addr アドレス
	 * @param data データ
	 * @param offset dataの開始オフセット
	 * @param length 読み込みサイズ
	 * @param add32Flag dataも32バイトずつずらすかのフラグ
	 * @return
	 */
	T readPPU32(int addr, byte[] data, int offset, int length, boolean add32Flag);
}
