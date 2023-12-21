package util;

public class BitUtil {
	public static void main(String[] args) {
		System.out.println(BitUtil.toInt16(new byte[] { -54, -11 }, 0, true));
	}

	private BitUtil() {}

	public static boolean isBitON(int flags, int index) {
		return (flags & (1 << index)) != 0;
	}

	public static boolean isBitOn(byte flags, int index) {
		return isBitON((int) flags, index);
	}

	public static boolean isBitON(byte[] flags, int index) {
		return isBitON((int) flags[index / 8], index % 8);
	}

	public static int toInt16(byte[] data, int index, boolean isLittleEndian) {
		return toNumber(data, index, index + 2, isLittleEndian);
	}

	public static int toNumber(byte[] data, int from, int to, boolean isLittleEndian) {
		byte[] dataSliced = slice(data, from, to);
		if (isLittleEndian)
			dataSliced = reverse(dataSliced);

		byte first = dataSliced[0];
		// 先頭 bit が 1 だったら負、全bitを反転する
		int sign = (first & 0x80) != 0 ? -1 : 1;
		int res = sign < 0 ? ~first : first;

		for (int i = 1; i < dataSliced.length; i++) {
			int val = sign < 0 ? ~dataSliced[i] : dataSliced[i];
			res *= 256;
			res += val < 0 ? val + 256 : val;
		}

		return sign < 0 ? ~res : res;
	}

	private static byte[] reverse(byte[] arr) {
		byte[] res = new byte[arr.length];
		
		for (int i = 0; i < arr.length; i++)
			res[arr.length - 1 - i] = arr[i];

		return res;
	}

	public static byte[] concat(byte[] arr1, byte[] arr2) {
		byte[] res = new byte[arr1.length + arr2.length];

		for (int i = 0; i < arr1.length; i++)
			res[i] = arr1[i];
		for (int i = 0; i < arr2.length; i++)
			res[i + arr1.length] = arr2[i];
		return res;
	}

	private static byte[] slice(byte[] arr, int from, int to) {
		int length = to - from;
		byte[] res = new byte[length];

		for (int i = 0; i < length; i++)
			res[i] = arr[i + from];
		
		return res;
	}
}
