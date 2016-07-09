package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import sample.lrunner.data.StageConfig;
import sample.lrunner.data.StageData;
import sample.lrunner.play.NormalTitlePlay;
import sample.lrunner.play.PlayBase;
import sample.lrunner.play.StartPlay;
import famicom.api.IFamicom;
import famicom.api.ppu.IFamicomPPU;
import famicom.api.rom.FamicomROM;
import famicom.api.rom.annotation.MirrorMode;
import famicom.api.rom.annotation.RomInformation;
import famicom.api.rom.listener.VBlankListener;
import famicom.util.apu.SoundManager;

@RomInformation(name = "ロードランナー", mirror = MirrorMode.VERTICAL)
public class SampleROM implements FamicomROM, VBlankListener {

	private int y = 0;

	private byte[] prog;

	private int addr;
	private int[] color1 = new int[] { 0x6, 0x16, 0x26, 0x16 };
	private int[] color2 = new int[] { 0x20, 0x10, 0x00, 0x10 };

	private static byte[][] blockData;

	public static SoundManager soundManager;

	static class CharaData {
		int spriteNum;
		int charaNum;
		int colorNum;
		boolean revFlag;
		int spX;
		int spY;

		CharaData(int num, int cl) {
			spriteNum = num;
			colorNum = cl;
		}

		CharaData setChara(int num, boolean rev) {
			charaNum = num;
			revFlag = rev;
			return this;
		}

		CharaData setPosition(int x, int y) {
			spX = x;
			spY = y;
			return this;
		}

		void setSprite(IFamicomPPU ppu) {
			int spix = spriteNum * 4;
			int dx = 0;
			int chnum = charaNum * 4;
			if (revFlag) {
				dx = 2;
			}
			ppu.getSprite(spix).setAttribute(false, revFlag, false, colorNum)
					.setPattern(chnum + dx).setX(spX).setY(spY);
			ppu.getSprite(spix + 1)
					.setAttribute(false, revFlag, false, colorNum)
					.setPattern(chnum + dx + 1).setX(spX).setY(spY + 8);
			ppu.getSprite(spix + 2)
					.setAttribute(false, revFlag, false, colorNum)
					.setPattern(chnum + (dx ^ 2)).setX(spX + 8).setY(spY);
			ppu.getSprite(spix + 3)
					.setAttribute(false, revFlag, false, colorNum)
					.setPattern(chnum + (dx ^ 2) + 1).setX(spX + 8)
					.setY(spY + 8);
		}
	}

	private static int[] blockColor = {
		0, 1, 1, 2, 2, 1, 2, 3,
		0, 0, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,};

	private PlayBase playData;

	@Override
	public void initRom(IFamicom famicom) {
		blockData = new byte[blockColor.length][5];
		for (int i = 0; i < blockColor.length; i++) {
			for (int j = 0; j < 4; j++) {
				blockData[i][j] = (byte) (0x60 + i * 4 + ((j << 1) & 2) + (j >> 1));
			}
			blockData[i][4] = (byte) blockColor[i];
			int x = (i & 0xf) * 2;
			int y = (i >> 4) * 2 + 6;
			// famicom.getPPU().getNameTable(0x0000).print(x, y, blockData[i],
			// 2, 2, 0, 2).setColor(x, y, 1);
		}

		soundManager = new SoundManager();
		famicom.getAPU().setSoundListener(soundManager);

		/*
		InputStream is = SampleROM.class.getResourceAsStream("/sound6.txt");
		PsgData psg = new PsgData(is);
		famicom.getAPU().setSoundListener(psg);
		*/
		famicom.getPPU().setVBlankListener(this)
				.controlPPU1(false, true, false, 0)
				.controlPPU2(0, true, true, false, false);
		File f = new File("/Users/hkoba/Documents/ROM/LodeRunner.nes");
		// File f = new File("/Users/hkoba/Documents/ROM/DQ1.nes");
		try {
			FileInputStream fis = new FileInputStream(f);
			byte[] data = new byte[16];
			fis.read(data);
			int dx = data[4] * 0x4000;
			prog = new byte[dx];
			if (data[5] > 0) {
				fis.read(prog);
				for (int i = 0; i < 2; i++) {
					byte[] pattern = new byte[0x1000];
					fis.read(pattern);
					famicom.getPPU().getPattenTable(i * 0x1000)
							.writePPU(i * 0x1000, pattern, 0, 0x1000);
				}
			}
			fis.close();
			// 09: 6 ,16,26,16
			// 0f:0, 10,20,10
			famicom.getPPU()
					.getPalette()
					.writePPU(
							0,
							new byte[] { 0x0f, 0x16, 0x30, 0x38, 0x0f, 0x17,
									0x26, 0x07, 0x0f, 0x36, 0x00, 0x30, 0x0f,
									0x38, 0x28, 0x30, 0x0f, 0x16, 0x27, 0x12,
									0x0f, 0x30, 0x2b, 0x16, 0x0f, 0x29, 0x16,
									0x30, 0x0f, 0x30, 0x30, 0x30 }, 0, 32);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StageConfig config = new StageConfig(SampleROM.class.getResource("/sample/data/"));
		StageData stage = config.loadStage(2);
		//stage.initStage();
		//stage.drawStage(famicom.getPPU(), true);
		//playData = new StageStart(stage, new PlayData(stage));
		playData = new StartPlay(stage);
		playData = new NormalTitlePlay(famicom.getPPU());
	}

	@Override
	public void vBlank(IFamicom famicom, IFamicomPPU ppu) {
		addr++;
		ppu.getPalette().writePPU(9, color1[(addr >> 5) & 3])
				.writePPU(15, color2[(addr >> 4) & 3]);
		playData = playData.stepFrame(famicom, ppu);
	}

}
