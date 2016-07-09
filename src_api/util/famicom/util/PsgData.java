package famicom.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import famicom.api.apu.IFamicomAPU;
import famicom.api.rom.listener.SoundListener;

public class PsgData implements SoundListener {
	private static class LineData {
		int waitNum;
		int flag;
		byte[] data;

		LineData(String txt) {
			String[] dt = txt.trim().split("[ :|*]+");
			data = new byte[dt.length - 1];
			waitNum = Integer.parseInt(dt[0]);
			for (int i = 0; i < data.length; i++) {
				if (!"--".equals(dt[i + 1])) {
					data[i] = (byte)Integer.parseInt(dt[i + 1],  16);
					flag |= (1 << i);
				}
			}
		}
		
		void writeData(IFamicomAPU sound) {
			for (int i = 0; i < data.length; i++) {
				if ((flag & (1 << i)) > 0) {
					sound.write(0x4000 + i,  data[i] & 255);
				}
			}
		}
	}

	private ArrayList<LineData> dataList;

	private int waitNum;

	private int nextIndex = 0;

	public PsgData(InputStream is) {
		dataList = new ArrayList<LineData>();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			String lnstr;
			while ((lnstr = rd.readLine()) != null) {
				dataList.add(new LineData(lnstr));
			}
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void soundStep(IFamicomAPU apu, boolean irqFlag) {
		if (waitNum > 0) {
			waitNum--;
			return;
		}
		if (nextIndex < dataList.size()) {
			dataList.get(nextIndex).writeData(apu);
			nextIndex++;
			if (nextIndex < dataList.size()) {
				waitNum = dataList.get(nextIndex).waitNum - 1;
			}
			return;
		}
		apu.setSoundListener(null);
	}
}
