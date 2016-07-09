package famicom.api.apu;

public interface INoiseSound {
	/**
	 * ボリュームを設定する. エンベロープは無効となる.
	 * 
	 * @param stopFlag
	 *            長さカウンタを止めるかどうかのフラグ.
	 * @param volume
	 *            ボリューム:[0-15]
	 * @return
	 */
	INoiseSound setVolume(boolean stopFlag, int volume);

	/**
	 * エンベロープを設定する. ボリュームは無効となる.
	 * 
	 * @param loopFlag
	 *            ループして続けるかのフラグ
	 * @param period
	 *            周期:[0-15]
	 * @return
	 */
	INoiseSound setEnvelope(boolean loopFlag, int period);

	INoiseSound setRandomMode(boolean shortFlag, int timerIndex);

	INoiseSound setLength(int lengthIndex);

	INoiseSound stop();

	/**
	 * 長さカウンタがゼロではないかを返す
	 * 
	 * @return
	 */
	boolean isPlaying();
}
