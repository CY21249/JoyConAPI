package joyconapi.joycon;


import joyconapi.event.EventListener;

/** JoyCon からのイベントを監視するリスナ */
public abstract class JoyConEventListener implements EventListener<JoyConEvent> {
	@Override
	public void dispatchEvent(JoyConEvent event) {
		if (event instanceof MoveInputEvent)
			this.onGyroInput((MoveInputEvent) event);
		// else
		// 	System.out.println(event);
	}

	/** ジャイロ値が入力されたときに実行されるハンドラ */
	protected abstract void onGyroInput(MoveInputEvent event);
}