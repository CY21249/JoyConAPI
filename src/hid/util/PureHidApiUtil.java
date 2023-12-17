package hid.util;

import purejavahidapi.HidDeviceInfo;

public class PureHidApiUtil {
	public static void printDeviceInfo(HidDeviceInfo info) {
		System.out.println("Device " + info.getDeviceId() + " [" + info.getPath() + "] (" + info.getUsagePage() + ")");
		System.out.println("Manufacture: " + info.getManufacturerString() + " (Vendor: " + info.getVendorId() + ")");
		System.out.println("Product    : " + info.getProductString() + " (" + info.getProductId() + ")");
		System.out.println("ReleaseNum : " + info.getReleaseNumber());
		System.out.println("SerialNum  : " + info.getSerialNumberString());	
	}
}
