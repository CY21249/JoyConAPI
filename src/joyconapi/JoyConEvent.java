package joyconapi;


import joyconapi.event.*;

public class JoyConEvent<D> extends Event<JoyCon, D> {
	public JoyConEvent(JoyCon joyCon, D data) {
		super(joyCon, data);
	}
}


class ButtonEventData {

}

/** ボタンが押されたことを表すイベント */
class ButtonPushedEvent extends JoyConEvent<ButtonEventData> {
	public ButtonPushedEvent(JoyCon joyCon, ButtonEventData data) {
		super(joyCon, data);
	}
}


/** ボタンが離されたことを表すイベント */
class ButtonReleasedEvent extends JoyConEvent<ButtonEventData> {
	public ButtonReleasedEvent(JoyCon joyCon, ButtonEventData data) {
		super(joyCon, data);
	}
}
