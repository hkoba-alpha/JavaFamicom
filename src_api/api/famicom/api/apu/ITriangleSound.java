package famicom.api.apu;

public interface ITriangleSound {
	ITriangleSound setLinear(boolean loopFlag, int lineCount);

	ITriangleSound setTimer(int lengthIndex, int timerCount);

	/**
	 * 長さカウンタがゼロではないかを返す
	 * 
	 * @return
	 */
	boolean isPlaying();

	ITriangleSound stop();
}
