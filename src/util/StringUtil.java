package util;

public class StringUtil {
	private StringUtil() {}

	public static String slice(String str, int from, int to) {
		if (from < 0)
			from = str.length() + from;
		if (to < 0)
			to = str.length() + to;

		String res = "";
		for (int i = from; i < to; i++) {
			res += str.charAt(i);
		}
		return res;
	}

	public static String slice(String str, int from) {
		return slice(str, from, str.length());
	}

	public static String padStart(String str, int length, char fill) {
		String res = "";
		for (int i = 0; i < length - str.length(); i++) {
			res += fill;
		}
		res += str;

		return res;
	}
}
