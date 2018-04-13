package com.siemens.primecult.utils;

public class ArithmeticUtil {

	static float thresholdValue = 0.00001f;

	public static boolean isFloatValueEqual(float value1, float value2) {

		if (Math.abs(value1 - value2) < thresholdValue)
			return true;
		return false;
	}

}
