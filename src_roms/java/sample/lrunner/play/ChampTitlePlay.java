package sample.lrunner.play;

import sample.SampleROM;
import sample.lrunner.data.StageConfig;
import sample.lrunner.data.StageData;
import famicom.api.ppu.IFamicomPPU;
import famicom.api.ppu.INameTable;

public class ChampTitlePlay extends TitlePlayBase {

	public ChampTitlePlay(IFamicomPPU ppu) {
		super(ppu);
		drawTitle(ppu, 4, 12);
		drawMark(ppu, 13, 4);
		byte[] champ = new byte[15 * 2];
		for (int i = 0; i < champ.length; i++) {
			champ[i] = (byte) ((i < 15 ? i + 0xc0 : i - 15 + 0x21) & 255);
		}
		INameTable table = ppu.getNameTable(IFamicomPPU.ADDR_NAMETABLE_0);
		table.print(8, 8, champ, 15, 2, 0, 15);
		StageData.drawString(ppu, 2, 24, "COPYRIGHT 1984  DOUG SMITH");
		StageData.drawString(ppu, 3, 25, "PUBLISHED BY HUDSON SOFT");
		StageData.drawString(ppu, 6, 26, "UNDER LICENSE FROM");
		StageData.drawString(ppu, 3, 27, "BRODERBUND SOFTWARE INC");
		drawCommand(ppu, 7, 17, new String[] { "PRESS START BUTTON", "EDIT MODE",
				"LODE RUNNER" });
	}

	@Override
	protected PlayBase commandSelected(IFamicomPPU ppu, int sel) {
		if (sel == 0) {
			ppu.controlPPU2(0, true, true, false, false);
			StageConfig config = new StageConfig(
					SampleROM.class.getResource("/stage/champ/"));
			StageData stage = config.loadStage(1);
			return new StartPlay(stage);
		} else if (sel == 2) {
			return new NormalTitlePlay(ppu);
		}
		return this;
	}

}
