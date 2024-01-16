package joyconapi.util;

import java.util.*;

public class ArrayUtil {
	private ArrayUtil() {}

	public static <T> T[] slice(T[] array, int from, int to) {
		return Arrays.copyOfRange(array, from, to);
	}
}
