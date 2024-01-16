package joyconapi.hid;

public class Packet {
	public final byte ids;
	public final byte[] data;
	public final int length;

	Packet(byte ids, byte[] data, int length) {
		this.ids = ids;
		this.data = data;
		this.length = length;
	}
}
