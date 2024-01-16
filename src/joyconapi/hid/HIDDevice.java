package joyconapi.hid;

import java.io.*;
import java.util.*;

import purejavahidapi.*;
import joyconapi.hid.util.*;
import joyconapi.util.*;

/** HID (Human Interface Device) のデバイスを表すオブジェクト */
public class HIDDevice {
	private static final Map<String, HIDDevice> instances = new HashMap<>();

	public final String id;
	public final HIDDeviceInfo info;
	private final HidDevice pure;
	private final Set<HIDEventListener> eventListeners = new HashSet<>();

	/** レポートを送信する */
	public void sendOutputReport(byte reportIDs, byte[] data) {
		this.pure.setOutputReport(reportIDs, data, data.length);
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {}
	}

	public void sendCommand(byte ids, byte command, byte subcommand, byte[] argument) {
		byte[] headers = new byte[] {
			command,
			// (byte)((++this.lastPacketCount)&0xF),
			0x00, 0x010, 0x40, 0x40, 0x00, 0x01, 0x40, 0x40,
			subcommand
		};
		byte[] buff = BitUtil.concat(headers, argument);
		this.sendOutputReport(ids, buff);
	}

	/** イベントリスナーを登録 */
	public void addEventListener(HIDEventListener listener) {
		this.eventListeners.add(listener);
	}

	/** コンストラクタ */
	public HIDDevice(String id, HidDevice pure, HIDDeviceInfo info) {
		this.id = id;
		this.pure = pure;
		this.info = info;
		this.init();
	}

	/** 初期化 */
	private void init() {
		// イベントリスナを登録する
		this.pure.setInputReportListener(new InputReportListener() {
			@Override
			public void onInputReport(HidDevice tgt, byte ids, byte[] data, int length) {
				HIDDevice target = HIDDevice.from(tgt);
				// イベントを作成する
				PacketReceivedEvent event = new PacketReceivedEvent(target, ids, data, length);
				// 登録している全てのリスナについて
				for (HIDEventListener listener : target.eventListeners) {
					// イベントを発行する
					listener.dispatchEvent(event);
				}
			}
		});
	}

	/** pure (PureJavaHidApi の HidDevice) から HIDDevice インスタンスを作成する */
	public static HIDDevice from(HidDevice pure) {
		String id = pure.getHidDeviceInfo().getDeviceId();
		HIDDevice instance = instances.get(id);
		if (instance != null)
			return instance;

		HIDDevice device = new HIDDevice(id, pure, HIDDeviceInfo.from(pure.getHidDeviceInfo()));
		instances.put(id, device);

		return device;
	}

	/** デバイスを検索する */
	public static HIDDeviceInfo search(short vendorID, short productID) {
		final List<HidDeviceInfo> list = PureJavaHidApi.enumerateDevices();
		Debugger.common.log("HIDDevice",
			"HIDデバイス " 
				+ "0x" + StringUtil.padStart(HexUtil.toString(vendorID), 4, '0')
				+ "/0x" + StringUtil.padStart(HexUtil.toString(productID), 4, '0')
				+ " を探しています...\n");
		
		HidDeviceInfo deviceInfo = null;
		for (HidDeviceInfo info : list) {
			if (info.getVendorId() == vendorID && info.getProductId() == productID) {
				deviceInfo = info;
				break;
			}
		}

		if (deviceInfo != null) {
			Debugger.common.log("HIDDevice", "デバイスが見つかりました:\n", 
				PureHidApiUtil.toString(deviceInfo), "\n");
			return HIDDeviceInfo.from(deviceInfo);
		} else {
			Debugger.common.log("HIDDevice", "デバイスが見つかりませんでした\n");
			return null;
		}
	}

	/** デバイスに接続する */
	public static HIDDevice connect(HIDDeviceInfo info) {
		try {
			// throws IOException
			HidDevice device = PureJavaHidApi.openDevice(info.pure);
			Debugger.common.log("HIDDevice", "デバイス " + info.productName + " に接続しました");

			return HIDDevice.from(device);
		} catch (IOException ioe) {
			Debugger.common.log("HIDDevice", "デバイスに接続できませんでした");
			return null;
		}
	}
}
