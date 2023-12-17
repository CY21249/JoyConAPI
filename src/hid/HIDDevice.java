package hid;

import java.io.*;
import java.util.*;

import hid.util.PureHidApiUtil;
import purejavahidapi.*;

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
				ReceivedPacketEvent event = new ReceivedPacketEvent(target, ids, data, length);
				// 登録している全てのリスナについて
				for (HIDEventListener listener : target.eventListeners) {
					// イベントを発行する
					listener.dispatchEvent(event);
				}
			}
		});
	}

	/** pure (PureJavaHidApi の HidDevice) から HIDDevice インスタンスを作成する */
	static HIDDevice from(HidDevice pure) {
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
		System.out.println("\nHIDデバイスを探しています...\n");
		
		HidDeviceInfo deviceInfo = null;
		for (HidDeviceInfo info : list) {
			if (info.getVendorId() == vendorID && info.getProductId() == productID) {
				deviceInfo = info;
				break;
			}
		}

		if (deviceInfo != null) {
			System.out.println("デバイスが見つかりました:");
			PureHidApiUtil.printDeviceInfo(deviceInfo);
			System.out.println();
			
			return HIDDeviceInfo.from(deviceInfo);
		} else {
			System.out.println("デバイスが見つかりませんでした");
			return null;
		}
	}

	/** デバイスに接続する */
	public static HIDDevice connect(HIDDeviceInfo info) {
		try {
			// throws IOException
			HidDevice device = PureJavaHidApi.openDevice(info.pure);
			System.out.println("デバイス " + info.productName + " に接続しました");

			return HIDDevice.from(device);
		} catch (IOException ioe) {
			System.out.println("デバイスに接続できませんでした");
			return null;
		}
	}

}
