package joyconapi.joycon;


public class Button {
	static {
		final Button[] buttons = {
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
			new Button("MINUS"),
		};
	}
	public static final Button UP = new Button("BUTTON_UP");

	public final String name;
	public boolean isON;

	private Button(String name) {
		this.name = name;
		this.isON = false;
	}

	@Override
	public boolean equals(Object obj) {
		Button button = (Button)obj;
		return obj instanceof Button && obj != null && this.name.equals(button.name);
	}
}

class ButtonInfo {
	public final String name;
	public final int bitIndex;

	ButtonInfo(String name, int bitIndex) {
		this.name = name;
		this.bitIndex = bitIndex;
	}
}