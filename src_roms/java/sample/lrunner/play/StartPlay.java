package sample.lrunner.play;

import java.util.Arrays;

import sample.SampleROM;
import sample.lrunner.SpriteData.IconType;
import sample.lrunner.data.StageData;
import famicom.api.IFamicom;
import famicom.api.pad.IJoyPad;
import famicom.api.ppu.IFamicomPPU;
import famicom.util.apu.PsgSoundData;

public class StartPlay implements PlayBase {
	private StageData stageData;

	private PsgSoundData startSound;

	public StartPlay(StageData data) {
		stageData = data;
		SampleROM.soundManager.removeSequencer(0).removeSequencer(5)
				.removeSequencer(10);
	}

	@Override
	public PlayBase stepFrame(IFamicom famicom, IFamicomPPU ppu) {
		if (startSound == null) {
			startSound = new PsgSoundData(
					StartPlay.class.getResourceAsStream("/start_stage.txt"));
			SampleROM.soundManager.addSequencer(0, startSound, false);
			ppu.setScroll(0, 0);
			byte[] blank = new byte[32 * 30];
			Arrays.fill(blank, (byte) 0x60);
			for (int i = 0; i < blank.length; i++) {
				//blank[i] = (byte)(i & 255);
			}
			ppu.getNameTable(IFamicomPPU.ADDR_NAMETABLE_0).writePPU(0, blank,
					0, blank.length);
			for (int i = 0; i < 64; i++) {
				ppu.getSprite(i).setY(240);
			}
			StageData.drawString(ppu, 12, 8, "PLAYER 1");
			StageData.drawString(ppu, 8, 14, String.format("STAGE %2d LEFT 5", stageData.getStageNum()));
			StageData.drawString(ppu, 8, 18, "SCORE    00000000");
			StageData.drawString(ppu, 8, 20, "HISCORE  00000000");
			stageData.initStage();
		} else if (!SampleROM.soundManager.isPlaying(startSound)) {
			return new StageStart(stageData, new PlayData(stageData));
		}
		if ((famicom.getPad(IFamicom.PAD_1).getButton() & IJoyPad.BUTTON_SELECT) > 0) {
			return new SelectPlay(stageData);
		}
		return this;
	}

}
