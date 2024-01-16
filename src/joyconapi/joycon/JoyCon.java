package joyconapi.joycon;

import java.util.*;

import joyconapi.angle.*;
import joyconapi.hid.*;
import joyconapi.util.Debugger;
import joyconapi.vector.*;

public class JoyCon {
	public static final Map<String, JoyCon> instances = new HashMap<>();

	public static final short VENDOR_ID = 0x057E;
	public static final short JOYCON_LEFT = 0x2006;
	public static final short JOYCON_RIGHT = 0x2007;

	// device product info
	/** HIDデバイスの id */
	public final String deviceID;
	/** Joy-Con の左右 (左: -1, 右: 1) */
	public final int hand;
	/** hid デバイスオブジェクト */
	private final HIDDevice hid;
	/** Joy-Con のイベントを監視するイベントリスナ */
	private final Set<JoyConEventListener> eventListeners = new HashSet<>();
	/** パケットをデコードするオブジェクト */
	private final PacketDecorder packetDecorder = new PacketDecorder();
	// public final Button buttons = new Buttons();

	// device state

	/** Joy-Con の加速度 */
	private Vector3D acceleration;
	/** Joy-Con の加速度を返す */
	public Vector3D getAcceleration() { return this.acceleration; }

	/** Joy-Con の各速度 */
	private EularAngleVelocity angleVelocity;
	/** Joy-Con の各速度を返す */
	public EularAngleVelocity getAngleVelocity() { return this.angleVelocity; }

	/** Joy-Con の姿勢 */
	private EularAngle orientation;
	/** Joy-Con の姿勢を返す */
	public EularAngle getOrientation() { return this.orientation; }

	JoyCon(String deviceID, HIDDevice hid) {
		this.deviceID = deviceID;
		this.hid = hid;
		this.hand = hid.info.productID == 0x2006 ? -1 : hid.info.productID == 0x2007 ? 1 : 0;
		if (this.hand == 0)
			throw new RuntimeException("Bad deviceID: " + hid.info.productID);
		this.init();
	}

	private void init() {
		byte ids = 0x01;

		// HIDモードにする
		this.hid.sendCommand(ids, (byte) 0x01, (byte) 0x03, new byte[] { 0x3F });
		
		// サブコマンド 0x40: IMU (6軸センサー) を有効(0x01)にする
		this.hid.sendCommand(ids, (byte)0x01, (byte) 0x40, new byte[] { 0x01 });
		
		// サブコマンド 0x03: Input Report モードにする (0x30: スタンダードフルモード) (@60Hz で状態をプッシュする)
		this.hid.sendCommand(ids, (byte)0x01, (byte) 0x03, new byte[] { 0x30 });

		this.hid.addEventListener(new HIDEventListener() {
			@Override
			public void onInputReport(PacketReceivedEvent irEvent) {
				JoyCon target = JoyCon.from(irEvent.target);
				// パケットのデータをデコード
				Packet packet = irEvent.data;
				JoyConInputReport inputReport = target.packetDecorder.decode(target, packet);

				// イベントを解釈
				JoyConEvent event = null;
				if (inputReport instanceof MoveInfoInputReport) {
					MoveInputEvent e = new MoveInputEvent(target, (MoveInfoInputReport) inputReport);
					event = e;
					target.acceleration = e.data.acceleration;
					target.angleVelocity = e.data.angleVelocity;
					target.orientation = e.data.orientation;
				}
				
				// 登録されているリスナに発送
				for (JoyConEventListener listener : target.eventListeners) {
					listener.dispatchEvent(event);
				}
			}
		});
	}

	/** Joy-Con からの InputReport を listen するハンドラを登録する */
	public void addEventListener(JoyConEventListener listener) {
		this.eventListeners.add(listener);
	}

	/** HIDオブジェクトから Joy-Con を作成する */
	public static JoyCon from(HIDDevice device) {
		String id = device.id;
		JoyCon instance = instances.get(id);
		if (instance != null)
			return instance;

		JoyCon joyCon = new JoyCon(id, device);
		instances.put(id, joyCon);

		return joyCon;
	}

	/** 左の Joy-Con を探し、あれば接続する */
	public static JoyCon searchLeft() {
		Debugger.common.log("JoyCon", "Joy-Con (Left) を探します");
		HIDDeviceInfo info = HIDDevice.search(JoyCon.VENDOR_ID, JoyCon.JOYCON_LEFT);
		if (info == null)
			return null;

		Debugger.common.log("JoyCon", "Joy-Con (Left) と接続します");
		return JoyCon.from(HIDDevice.connect(info));
	}
	
	/** 右の Joy-Con を探し、あれば接続する */
	public static JoyCon searchRight() {
		Debugger.common.log("JoyCon", "Joy-Con (Right) を探します");
		HIDDeviceInfo info = HIDDevice.search(JoyCon.VENDOR_ID, JoyCon.JOYCON_RIGHT);
		if (info == null)
			return null;
			
		Debugger.common.log("JoyCon", "Joy-Con (Right) と接続します");
		return JoyCon.from(HIDDevice.connect(info));
	}
}
