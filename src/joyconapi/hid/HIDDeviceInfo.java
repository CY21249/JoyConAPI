package joyconapi.hid;

import purejavahidapi.HidDeviceInfo;

public class HIDDeviceInfo {
	final HidDeviceInfo pure;
	public final String id;
	public final String path;
	public final short vendorID;
	public final short productID;
	public final String productName;
	public final short releaseNumber;
	public final short usagePage;
	public final String serialNumber;
	public final String manufactureName;

	public HIDDeviceInfo(HidDeviceInfo pure, String id, String path, short vendorID, short productID, String productName,
			short releaseNumber, short usagePage, String serialNumber, String manufactureName) {
		this.pure = pure;
		this.id = id;
		this.path = path;
		this.vendorID = vendorID;
		this.productID = productID;
		this.productName = productName;
		this.releaseNumber = releaseNumber;
		this.usagePage = usagePage;
		this.serialNumber = serialNumber;
		this.manufactureName = manufactureName;
	}

	static HIDDeviceInfo from(HidDeviceInfo pure) {
		return new HIDDeviceInfo(
			pure, 
			pure.getDeviceId(), 
			pure.getPath(), 
			pure.getVendorId(), 
			pure.getProductId(), pure.getProductString(),
			pure.getReleaseNumber(), 
			pure.getUsagePage(), 
			pure.getSerialNumberString(),
			pure.getManufacturerString()
		);
	}
}