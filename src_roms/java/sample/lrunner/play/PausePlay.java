package sample.lrunner.play;

import java.awt.Dimension;
import java.awt.Point;

import sample.lrunner.data.StageData;
import famicom.api.IFamicom;
import famicom.api.pad.IJoyPad;
import famicom.api.ppu.IFamicomPPU;

public class PausePlay implements PlayBase {
	private StageData stageData;

	private PlayData playData;

	private boolean replayFlag;

	public PausePlay(StageData data, PlayData play) {
		stageData = data;
		playData = play;
	}

	@Override
	public PlayBase stepFrame(IFamicom famicom, IFamicomPPU ppu) {
		if (replayFlag) {
			if ((famicom.getPad(0).getButton() & IJoyPad.BUTTON_START) > 0) {
				return new StageStart(stageData, playData, StageStart.ScrollType.StartHorizontal);
			}
		} else if ((famicom.getPad(0).getButton() & IJoyPad.BUTTON_START) == 0) {
			replayFlag = true;
		}

		int stick = famicom.getPad(0).getStick();
		Point pos = stageData.getScrollPosition();
		Dimension sz = stageData.getScrollSize();
		if ((stick & IJoyPad.STICK_LEFT) > 0 && pos.x > 0) {
			pos.x -= 2;
		} else if ((stick & IJoyPad.STICK_RIGHT) > 0 && pos.x < sz.width - 256) {
			pos.x += 2;
		}
		if ((stick & IJoyPad.STICK_UP) > 0 && pos.y > 0) {
			pos.y -= 2;
		} else if ((stick & IJoyPad.STICK_DOWN) > 0 && pos.y < sz.height - 240) {
			pos.y += 2;
		}
		stageData.setScrollPosition(ppu, pos.x, pos.y);
		stageData.drawStage(ppu, false);
		return this;
	}

}
