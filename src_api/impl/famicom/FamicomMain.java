package famicom;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import sample.SampleROM;
import famicom.api.rom.FamicomROM;
import famicom.impl.FamicomImpl;

/**
 * 
 */

/**
 * @author hkoba
 *
 */
public class FamicomMain extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FamicomImpl famicom;

	public FamicomMain(FamicomROM rom) {
		super("ファミコン");
		famicom = new FamicomImpl(rom);
		rom.initRom(famicom);
		String name = famicom.getRomName();
		if (name != null) {
			super.setTitle(name);
		}
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(famicom, BorderLayout.CENTER);
		pack();
		setResizable(false);
		addWindowListener(this);
	}

	public void mainLoop() {
		famicom.mainLoop();
	}

	public static void startROM(FamicomROM rom) {
		FamicomMain frm = new FamicomMain(rom);
		frm.setVisible(true);
		frm.mainLoop();
	}

	public static void main(String[] args) {
		SampleROM rom = new SampleROM();
		startROM(rom);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		famicom.requestFocus();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
