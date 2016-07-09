package famicom.api.pad;

public interface IJoyPad {
	static final int STICK_RIGHT = 1;
	static final int STICK_LEFT = 2;
	static final int STICK_DOWN = 4;
	static final int STICK_UP = 8;

	static final int BUTTON_START = 1;
	static final int BUTTON_SELECT = 2;
	static final int BUTTON_B = 4;
	static final int BUTTON_A = 8;

	int getStick();
	int getButton();
}
