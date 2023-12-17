package joycon.test;

import joycon.*;

public class Test {
	public static void main(String[] args) {
		JoyCon left = JoyCon.searchLeft();

		left.addEventListener(new JoyConEventListener() {
			@Override
			protected void onGyroInput(MoveInputEvent event) {
				MoveInfoInputReport data = event.data;

				System.out.print("\r" +  data.orientation.x + " / " + data.orientation.y + " / " + data.orientation.z);
			}
		});
	}
}
