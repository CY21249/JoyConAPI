package joyconapi.util;

public class DoubleUtil {
	public static void main(String[] args) {
		DoubleUtil.toFixed(-1024.567890, 10, 2);
	}

	public static String toFixed(double value, int intDigitLength, int fracDigitLength) {
		if (Double.isNaN(value))
			return StringUtil.padStart("NaN", intDigitLength + 1 + fracDigitLength, ' ');

		int intPart = (int) Math.abs(value);
		double fracPart = Math.abs(value) - intPart;

		String intPartStr = StringUtil.padStart((value < 0 ? "-" : "") + intPart + "", intDigitLength, ' ');
		String fracPartStr = StringUtil.slice(fracPart + StringUtil.repeat("9", fracDigitLength), 2, fracDigitLength + 2);
		return intPartStr + "." + fracPartStr;
	}
}
