package hid;

import joycon.JoyCon;

public class Test {
	public static void main(String[] args) {
		HIDDeviceInfo leftInfo = HIDDevice.search(JoyCon.VENDOR_ID, JoyCon.JOYCON_LEFT);
		HIDDevice left = HIDDevice.connect(leftInfo);
		
		HIDDeviceInfo rightInfo = HIDDevice.search(JoyCon.VENDOR_ID, JoyCon.JOYCON_RIGHT);
		HIDDevice right = HIDDevice.connect(rightInfo);
	}
}
