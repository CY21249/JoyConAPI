package joyconapi.util;

public class HexUtil {
	public static void main(String[] args) {
		System.out.println(HexUtil.toString(100));
	}

	private static char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private HexUtil() {
	}

	public static String toString(int num) {
		if (num == 0)
			return "0";
		
		String str = "";
		while (num > 0) {
			str = String.valueOf(chars[num % 16]) + str;
			num /= 16;
		}
		return str;
	}

	public static String toString(byte num) {
		int unsigned = num < 0 ? num + 256 : num;

		String str = toString((int) unsigned);
		return str.length() == 1 ? "0" + str : str;
	}

	public static String toString(byte[] array, boolean hasSpace) {
		String str = "";
		for (byte b : array)
			str += toString(b) + " ";

		return StringUtil.slice(str, 0, -1);
	}

	public static String toString(byte[] array) {
		return toString(array, true);
	}
}
