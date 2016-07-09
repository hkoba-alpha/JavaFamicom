package sample.lrunner.play;

import java.util.Arrays;

import sample.SampleROM;
import sample.lrunner.data.StageConfig;
import sample.lrunner.data.StageData;
import famicom.api.IFamicom;
import famicom.api.pad.IJoyPad;
import famicom.api.ppu.IFamicomPPU;
import famicom.util.apu.PsgSoundData;

public class SelectPlay implements PlayBase {
	private StageData stageData;

	private PsgSoundData soundData;

	private boolean pushFlag;

	public SelectPlay(StageData data) {
		stageData = data;
		SampleROM.soundManager.removeSequencer(0).removeSequencer(5).removeSequencer(10);
	}

	@Override
	public PlayBase stepFrame(IFamicom famicom, IFamicomPPU ppu) {
		if (soundData == null) {
			soundData = new PsgSoundData("     0:-- -- -- --|80 00 a0 10|");
			ppu.setScroll(0, 0);
			byte[] blank = new byte[32 * 30];
			Arrays.fill(blank, (byte) 0x60);
			ppu.getNameTable(IFamicomPPU.ADDR_NAMETABLE_0).writePPU(0, blank,
					0, blank.length);
			for (int i = 0; i < 64; i++) {
				ppu.getSprite(i).setY(240);
			}
			StageData.drawString(ppu, 8, 14, String.format("STAGE %2d", stageData.getStageNum()));
		}
		int button = famicom.getPad(IFamicom.PAD_1).getButton();
		if (pushFlag) {
			if (button == 0) {
				pushFlag = false;
			}
		} else if (button > 0) {
			StageConfig config = stageData.getStageConfig();
			if ((button & IJoyPad.BUTTON_A) > 0) {
				// 追加
				int num = stageData.getStageNum() + 1;
				if (num > config.getStageMax()) {
					num = 1;
				}
				stageData = config.loadStage(num);
				SampleROM.soundManager.addSequencer(0, soundData, false);
			} else if ((button & IJoyPad.BUTTON_B) > 0) {
				int num = stageData.getStageNum() - 1;
				if (num < 1) {
					num = config.getStageMax();
				}
				stageData = config.loadStage(num);				
				SampleROM.soundManager.addSequencer(0, soundData, false);
			} else if ((button & IJoyPad.BUTTON_START) > 0) {
				// 開始
				return new StartPlay(stageData);
			}
			if (stageData == null) {
				// ステージ１にする
				stageData = config.loadStage(1);
			}
			StageData.drawString(ppu, 8, 14, String.format("STAGE %2d", stageData.getStageNum()));
			pushFlag = true;
		}
		return this;
	}

}
