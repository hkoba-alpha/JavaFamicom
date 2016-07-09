package famicom.api;

/**
 * メモリへアクセスするインタフェース
 * 0x0000-0x07ff:RAM
 * 0x6000-0x7fff:バッテリーバックアップ
 * @author hkoba
 *
 * @param <T>
 */
public interface IAccessMemory<T> {
	/**
	 * メモリへ書き込む
	 * @param addr
	 * @param data
	 * @return
	 */
	T write(int addr, int data);

	/**
	 * メモリから読み込む
	 * @param addr
	 * @return
	 */
	int read(int addr);

	/**
	 * メモリへまとめて書き込む
	 * @param addr
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	T write(int addr, byte[] data, int offset, int length);

	/**
	 * メモリからまとめて読み込む
	 * @param addr
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	T read(int addr, byte[] data, int offset, int length);
}
