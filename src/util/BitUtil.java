package util;

public class BitUtil {
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
		if (isLittleEndian)
			return toNumberLE(data, from, to);

		int res = 0;

		for (int i = from; i < to; i++) {
			byte val = data[i];
			res *= 256;
			res += val < 0 ? val + 256 : val;
		}

		return res;
	}

	private static int toNumberLE(byte[] leData, int from, int to) {
		int res = 0;

		// A_B_C -> [C, B, A]
		// (A * b + B) * b + C
		for (int i = to - 1; i >= from; i--) {
			byte val = leData[i];
			res *= 256;
			res += val < 0 ? val + 256 : val;
		}

		return res;
	}


}
