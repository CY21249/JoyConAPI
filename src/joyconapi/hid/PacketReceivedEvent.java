package joyconapi.hid;

/** HIDデバイスからレポートがあったことを表すイベント */
public class PacketReceivedEvent extends HIDEvent<Packet> {
	public PacketReceivedEvent(HIDDevice target, byte ids, byte[] data, int length) {
		this(target, new Packet(ids, data, length));
	}

	public PacketReceivedEvent(HIDDevice target, Packet packet) {
		super(target, packet);
	}
}
