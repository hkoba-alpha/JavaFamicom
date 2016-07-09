package famicom.api.apu;

/**
 * 矩形波インタフェース
 * 
 * @author hkoba
 *
 */
public interface ISquareSound {
	/**
	 * ボリュームを設定する. エンベロープは無効となる.
	 * 
	 * @param duty
	 *            デューティー比:[0-3]
	 * @param stopFlag
	 *            長さカウンタを止めるかどうかのフラグ.
	 * @param volume
	 *            ボリューム:[0-15]
	 * @return
	 */
	ISquareSound setVolume(int duty, boolean stopFlag, int volume);

	/**
	 * エンベロープを設定する. ボリュームは無効となる.
	 * 
	 * @param duty
	 *            デューティー比:[0-3]
	 * @param loopFlag
	 *            ループして続けるかのフラグ
	 * @param period
	 *            周期:[0-15]
	 * @return
	 */
	ISquareSound setEnvelope(int duty, boolean loopFlag, int period);

	/**
	 * 周波数と長さを設定する.
	 * <table>
	 * <tr>
	 * <tr>
	 * <th></th>
	 * <th>0</th>
	 * <th>1</th>
	 * <th>2</th>
	 * </tr>
	 * </tr>
	 * </table>
	 * 
	 * @param lengthIndex
	 *            長さカウンタへのインデックス
	 * @param timerCount
	 *            周波数の周期カウンタ
	 * @return
	 */
	ISquareSound setTimer(int lengthIndex, int timerCount);

	/**
	 * スイープを設定する
	 * 
	 * @param enableFlag
	 *            有効フラグ
	 * @param period
	 *            周期:[0-7]
	 * @param upMode
	 *            方向: false=低くなっていく,true=高くなっていく
	 * @param value
	 *            スィープ量:[0-7]
	 * @return
	 */
	ISquareSound setSweep(boolean enableFlag, int period, boolean upMode,
						  int value);

	/**
	 * 長さカウンタがゼロではないかを返す
	 * 
	 * @return
	 */
	boolean isPlaying();

	ISquareSound stop();
}
