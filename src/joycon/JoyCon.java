package joycon;

import java.util.*;

import hid.*;

public class JoyCon {
	public static final Map<String, JoyCon> instances = new HashMap<>();

	public static final short VENDOR_ID = 0x057E;
	public static final short JOYCON_LEFT = 0x2006;
	public static final short JOYCON_RIGHT = 0x2007;

	public final String deviceID;
	public final int hand;
	private final HIDDevice hid;
	private final Set<JoyConEventListener> eventListeners = new HashSet<>();
	// public final Button buttons = new Buttons();

	JoyCon(String deviceID, HIDDevice hid) {
		this.deviceID = deviceID;
		this.hid = hid;
		this.hand = hid.info.productID == 0x2006 ? -1 : hid.info.productID == 0x2007 ? 1 : 0;
		if (this.hand == 0)
			throw new RuntimeException("Bad deviceID: " + hid.info.productID);
		this.init();
	}

	private void init() {
		// ジャイロ入力等の設定をする
		byte ids = 1;
		byte[] datat = new byte[16];
		datat[9] = 0x03;
		datat[10] = 0x3F;
		this.hid.sendOutputReport(ids, datat);

		// Subcommand 0x40: Enable IMU (6-Axis sensor)
		// One argument of x00 Disable or x01 Enable.
		this.hid.sendCommand((byte) 0x01, (byte) 0x40, new byte[] { 0x01 });

		// Subcommand 0x03: Set input report mode
		// Argument 30: Standard full mode. Pushes current state @60Hz
		this.hid.sendCommand((byte) 0x01, (byte) 0x03, new byte[] { 0x30 });

		this.hid.addEventListener(new HIDEventListener() {
			@Override
			public void onInputReport(ReceivedPacketEvent irEvent) {
				JoyCon target = JoyCon.from(irEvent.target);
				// イベントを解釈
				Packet packet = irEvent.data;

				JoyConInputReport inputReport = new PacketDecorder().decode(target, packet);

				JoyConEvent event = null;
				if (inputReport instanceof MoveInfoInputReport) {
					event = new MoveInputEvent(target, (MoveInfoInputReport) inputReport);
				}
				
				for (JoyConEventListener listener : target.eventListeners) {
					listener.dispatchEvent(event);
				}

				// 登録されているリスナに発送

			}
		});
	}

	public void addEventListener(JoyConEventListener listener) {
		this.eventListeners.add(listener);
	}

	public static JoyCon from(HIDDevice device) {
		String id = device.id;
		JoyCon instance = instances.get(id);
		if (instance != null)
			return instance;

		JoyCon joyCon = new JoyCon(id, device);
		instances.put(id, joyCon);

		return joyCon;
	}

	public static JoyCon searchLeft() {
		HIDDeviceInfo info = HIDDevice.search(JoyCon.VENDOR_ID, JoyCon.JOYCON_LEFT);
		if (info == null)
			return null;
		return JoyCon.from(HIDDevice.connect(info));
	}

	public static JoyCon searchRight() {
		HIDDeviceInfo info = HIDDevice.search(JoyCon.VENDOR_ID, JoyCon.JOYCON_RIGHT);
		if (info == null)
			return null;
		return JoyCon.from(HIDDevice.connect(info));
	}
}
