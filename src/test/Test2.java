package test;

import joyconapi.joycon.*;

public class Test2 {
	public static void main(String[] args) {
		JoyCon joyCon = JoyCon.searchLeft();
		if (joyCon == null)
			joyCon = JoyCon.searchRight();
		if (joyCon == null)
			return;
		
		joyCon.addEventListener(new JoyConEventListener() {
			@Override
			protected void onGyroInput(MoveInputEvent event) {
				System.out.print("\r" + event.data.orientation.y + "      ");
			}
		});
	}
}
