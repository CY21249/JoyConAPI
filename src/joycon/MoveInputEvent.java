package joycon;

/** ジャイロ値が入力されたことを表すイベント */
public class MoveInputEvent extends JoyConEvent<MoveInfoInputReport> {
	public MoveInputEvent(JoyCon joyCon, MoveInfoInputReport data) {
		super(joyCon, data);
	}
}