package util;

public class MathUtil {
	public static double sum(double[] arr) {
		double d = 0;
		for (double val : arr) {
			d += val;
		}
		return d;
	}

	public static double mean(double[] arr) {
		return sum(arr) / arr.length;
	}
	
}
