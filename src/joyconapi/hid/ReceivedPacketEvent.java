package joyconapi.hid;

/** HIDデバイスからレポートがあったことを表すイベント */
public class ReceivedPacketEvent extends HIDEvent<Packet> {
	public ReceivedPacketEvent(HIDDevice target, byte ids, byte[] data, int length) {
		this(target, new Packet(ids, data, length));
	}

	public ReceivedPacketEvent(HIDDevice target, Packet packet) {
		super(target, packet);
	}
}
