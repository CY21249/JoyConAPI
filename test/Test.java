package test;

import util.DoubleUtil;

public class Test {
	public static void main(String[] args) {
		JoyCon left = JoyCon.searchLeft();

		left.addEventListener(new JoyConEventListener() {
			@Override
			protected void onGyroInput(MoveInputEvent event) {
				MoveInfoInputReport data = event.data;

				System.out.print("\r"
						+ DoubleUtil.toFixed(data.orientation.x, 3, 5) + ", "
						+ DoubleUtil.toFixed(data.orientation.y, 3, 5) + ", "
						+ DoubleUtil.toFixed(data.orientation.z, 3, 5) + " / "
						+ DoubleUtil.toFixed(data.angleVelocity.x, 3, 5) + ", "
						+ DoubleUtil.toFixed(data.angleVelocity.y, 3, 5) + ", "
						+ DoubleUtil.toFixed(data.angleVelocity.z, 3, 5) + " / "
						+ DoubleUtil.toFixed(data.acceleration.x, 3, 5) + ", "
						+ DoubleUtil.toFixed(data.acceleration.y, 3, 5) + ", "
						+ DoubleUtil.toFixed(data.acceleration.z, 3, 5));
			}
		});
	}
}
