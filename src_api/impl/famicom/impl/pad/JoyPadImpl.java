package famicom.impl.pad;

import java.awt.event.KeyEvent;

import famicom.api.pad.IJoyPad;
import famicom.api.rom.annotation.PadConfig;

public class JoyPadImpl implements IJoyPad {
	private int keyUp;
	private int keyDown;
	private int keyRight;
	private int keyLeft;
	private int keySelect;
	private int keyStart;
	private int keyA;
	private int keyB;

	private int stickFlag;
	private int buttonFlag;

	public JoyPadImpl(PadConfig config) {
		keyUp = config.up();
		keyDown = config.down();
		keyLeft = config.left();
		keyRight = config.right();
		keyStart = config.start();
		keySelect = config.select();
		keyA = config.a();
		keyB = config.b();
	}

	public void processEvent(KeyEvent key, boolean pressFlag) {
		int code = key.getKeyCode();
		int flag = 0;
		if (code == keyUp) {
			flag = STICK_UP;
		}
		if (code == keyDown) {
			flag |= STICK_DOWN;
		}
		if (code == keyLeft) {
			flag |= STICK_LEFT;
		}
		if (code == keyRight) {
			flag |= STICK_RIGHT;
		}
		if (flag != 0) {
			if (pressFlag) {
				stickFlag |= flag;
			} else {
				stickFlag &= (~flag);
			}
			//System.out.println("stick=" + stickFlag);
		}
		flag = 0;
		if (code == keyStart) {
			flag = BUTTON_START;
		}
		if (code == keySelect) {
			flag |= BUTTON_SELECT;
		}
		if (code == keyA) {
			flag |= BUTTON_A;
		}
		if (code == keyB) {
			flag |= BUTTON_B;
		}
		if (flag != 0) {
			if (pressFlag) {
				buttonFlag |= flag;
			} else {
				buttonFlag &= (~flag);
			}
			//System.out.println("button=" + buttonFlag);
		}
	}

	@Override
	public int getStick() {
		return stickFlag;
	}

	@Override
	public int getButton() {
		return buttonFlag;
	}

}
