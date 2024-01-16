package joyconapi.hid.util;

import purejavahidapi.*;

public class PureHidApiUtil {
	public static String toString(HidDeviceInfo info) {
		String str = "Device " + info.getDeviceId() + " [" + info.getPath() + "] (" + info.getUsagePage() + ")\n"
			+ "Manufacture: " + info.getManufacturerString() + " (Vendor: " + info.getVendorId() + ")\n"
			+ "Product    : " + info.getProductString() + " (" + info.getProductId() + ")\n"
			+ "ReleaseNum : " + info.getReleaseNumber() + "\n"
			+ "SerialNum  : " + info.getSerialNumberString();

		return str;
	}
}
