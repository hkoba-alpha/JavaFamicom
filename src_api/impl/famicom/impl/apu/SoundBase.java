package famicom.impl.apu;

import java.util.Arrays;

/**
 * サウンドの規程クラス
 * 
 * @author hkoba
 *
 */
public abstract class SoundBase {
	/**
	 * 240Hzの1シーケンスでのクロック数
	 */
	public static final int SEQUENCE_CLOCK = 7457;

	/**
	 * 1シーケンスのサンプルレート
	 */
	public static final int SAMPLE_RATE = 200;

	/**
	 * サンプリングインデックスごとに進めるクロック数
	 */
	private static byte[] addClock = new byte[SAMPLE_RATE];

	static {
		for (int i = 0; i < SAMPLE_RATE; i++) {
			addClock[i] = (byte) ((i + 1) * SEQUENCE_CLOCK / SAMPLE_RATE - i
					* SEQUENCE_CLOCK / SAMPLE_RATE);
		}
	}

	/**
	 * 長さインデックス用
	 */
	public static final int[] lengthIndexData = { 0x0a, 0xfe, 0x14, 0x02, 0x28,
			0x04, 0x50, 0x06, 0xa0, 0x08, 0x3c, 0x0a, 0x0e, 0x0c, 0x1a, 0x0e,
			0x0c, 0x10, 0x18, 0x12, 0x30, 0x14, 0x60, 0x16, 0xc0, 0x18, 0x48,
			0x1a, 0x10, 0x1c, 0x20, 0x1e };

	/**
	 * 分周期のタイマ値
	 */
	private int timerCount;

	/**
	 * クロック数
	 */
	private int curClock;

	/**
	 * 現在の音でのサンプリングの位置
	 */
	private int sampleIndex;

	/**
	 * 最後に出力したタイマ
	 */
	private int lastTimerCount;

	/**
	 * サンプルデータを返す.
	 * 
	 * @param index
	 * @return
	 */
	abstract protected byte getOutput(int index);

	/**
	 * ステップ実行する
	 * 
	 * @param index
	 * @param stepNum
	 */
	public abstract void stepFrame(int index, int stepNum);

	/**
	 * 音無のあたい
	 */
	private byte nullValue;

	protected SoundBase() {
		this((byte) 0);
	}

	protected SoundBase(byte val) {
		timerCount = 0;
		nullValue = val;
	}

	/**
	 * 無音のデータを書き込む
	 * 
	 * @param data
	 * @param offset
	 */
	public void fillNullSound(byte[] data, int offset) {
		int ix = 0;
		if (lastTimerCount > 0 && sampleIndex > 0) {
			// ノイズのないように終了する
			for (int i = 0; i < SAMPLE_RATE; i++) {
				data[offset + i] = getOutput(sampleIndex);
				ix++;
				if (data[offset + i] == nullValue) {
					break;
				}
				curClock += addClock[i];
				sampleIndex += (curClock / lastTimerCount);
				curClock %= lastTimerCount;
			}
		}
		if (ix < SAMPLE_RATE) {
			Arrays.fill(data, offset + ix, offset + SAMPLE_RATE, nullValue);
			curClock = 0;
			sampleIndex = 0;
			lastTimerCount = 0;
		}
	}

	protected void setTimerCount(int count) {
		timerCount = count;
	}

	protected int getTimerCount() {
		return timerCount;
	}

	public void setFrameSample(byte[] data, int offset) {
		if (timerCount < 1) {
			fillNullSound(data, offset);
			return;
		}
		if (timerCount != lastTimerCount) {
			if (lastTimerCount > 0) {
				curClock = curClock * timerCount / lastTimerCount;
			}
			lastTimerCount = timerCount;
		}
		for (int i = 0; i < SAMPLE_RATE; i++) {
			data[offset + i] = getOutput(sampleIndex);
			curClock += addClock[i];
			sampleIndex += (curClock / timerCount);
			curClock %= timerCount;
		}
	}
}
