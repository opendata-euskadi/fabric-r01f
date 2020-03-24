/*
 * MathUtils.java Created on 2 de febrero de 2003, 12:55
 */
package r01f.util.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CharMatcher;

/**
 * Number utilities
 */
public abstract class Numbers {
	/**
	 * Java integer standard size
	 */
	public static final int INTEGER_WIDTH = 4;

/////////////////////////////////////////////////////////////////////////////////////////
//  MISC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the provided type is a numeric type
	 * @param type
	 * @return
	 */
	public static boolean isNumberType(final Class<?> type) {
		return type.equals(Number.class) || type.equals(Integer.class) || type.equals(Long.class)
			|| type.equals(Double.class) || type.equals(Float.class) || type.equals(Short.class) 
			|| type.equals(Byte.class)
			|| type.equals(BigInteger.class) || type.equals(BigDecimal.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EVEN/ODD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Check if the number is an even number
	 * Java Puzzle 1: Oddity
	 * @param integer el entero
	 * @return true si es par
	 */
	public static boolean isEven(final int intNum) {
		return !isOdd(intNum);
	}
	/**
	 * Checks if the number is an odd number
	 * Java Puzzle 1: Oddity
	 * @param intNum el entero
	 * @return true si es impar
	 */
	public static boolean isOdd(final int intNum) {
		return (intNum & 1) != 0;
	}
	/**
	 * Checks if the number is an even number
	 * Java Puzzle 1: Oddity
	 * @param longnum el long
	 * @return true si es par
	 */
	public static boolean isEven(final long longNum) {
		return !isOdd(longNum);
	}
	/**
	 * Checks if the number is an odd number
	 * Java Puzzle 1: Oddity
	 * @param longNum el long
	 * @return true si es impar
	 */
	public static boolean isOdd(final long longNum) {
		return (longNum & 1) != 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if the first number is within the other ones
	 * @param num
	 * @param others
	 * @return
	 */
	public static boolean isWithin(final int num,final int... others) {
		if (others == null || others.length == 0) return false;
		for (int o : others) {
			if (num == o) return true;
		}
		return false;
	}
	/**
	 * Checks if the first number is within the other ones
	 * @param num
	 * @param others
	 * @return
	 */
	public static boolean isWithin(final long num,final long... others) {
		if (others == null || others.length == 0) return false;
		for (long o : others) {
			if (num == o) return true;
		}
		return false;
	}
	/**
	 * Checks if the first number is within the other ones
	 * @param num
	 * @param others
	 * @return
	 */
	public static boolean isWithin(final double num,final double... others) {
		if (others == null || others.length == 0) return false;
		for (double o : others) {
			if (num == o) return true;
		}
		return false;
	}
	/**
	 * Checks if the first number is within the other ones
	 * @param num
	 * @param others
	 * @return
	 */
	public static boolean isWithin(final float num,final float... others) {
		if (others == null || others.length == 0) return false;
		for (float o : others) {
			if (num == o) return true;
		}
		return false;
	}
	/**
	 * Checks if the first number is within the other ones
	 * @param num
	 * @param others
	 * @return
	 */
	public static boolean isWithin(final short num,final short... others) {
		if (others == null || others.length == 0) return false;
		for (short o : others) {
			if (num == o) return true;
		}
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BIT MASKING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a mask to see the value of a bit inside an int
	 * @param bitIndex 
	 * @return 
	 */
	private static int getMask(final int bitIndex) {
		return (1 << bitIndex);
	}
	/**
	 * Sets a bit inside an int and returns the new int
	 * @param originalInt 
	 * @param bitIndex 
	 * @return 
	 */
	public static int setBit(final int originalInt,final int bitIndex) {
		return originalInt | getMask(bitIndex);
	}
	/**
	 * Clears (set to 0) a bit inside an int and returns the new int
	 * @param originalInt
	 * @param bitIndex
	 * @return 
	 */
	public static int clearBit(final int originalInt,final int bitIndex) {
		return originalInt & ~getMask(bitIndex);
	}
	/**
	 * Returns an int's bit value
	 * @param integer 
	 * @param bitIndex 
	 * @return 
	 */
	public static boolean getBit(final int integer,final int bitIndex) {
		return ((integer & getMask(bitIndex)) != 0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Converts an int into an array of bytes
	 * BEWARE: The LSB (LessSignificantByte) is at [0]
	 * @param integer
	 * @return a 4 byte array with LSB(LessSignificantByte) at [0]
	 */
	public static byte[] intToBytes(final int integer) {
		byte[] byteArray = new byte[INTEGER_WIDTH];
		for (int i = 0; i < INTEGER_WIDTH; i++) {
			byteArray[i] = (byte) (integer >> (8 * i) & 0xFF);
		}
		return byteArray;
	}
	/**
	 * Converts a byte array into an int
	 * BEWARE: The LSB (LessSignificantByte) is at [0]
	 * @param integer
	 * @return a int built from the byte array with LSB(LessSignificantByte) at [0]
	 */
	public static int bytesToInt(final byte[] byteArray) {
		int integer = 0;
		for (int i = 0; i < byteArray.length; i++) {
			integer |= byteArray[i] << (8 * i);
		}
		return integer;
	}

	/***************************************************************************************************************************************
	 * toByte ************ byte = 8 bits, range = -128 .. 127
	 **************************************************************************************************************************************/
	/**
	 * double to byte
	 */
	public static byte toByte(final double d) {
		if (d < -128 || d > 127) throw new IllegalArgumentException("Number is too large. Range is -128 to 127");
		return Double.valueOf(d).byteValue();
	}
	/**
	 * float to byte
	 */
	public static byte toByte(final float f) {
		if (f < -128 || f > 127) throw new IllegalArgumentException("Number is too large. Range is -128 to 127");
		return Float.valueOf(f).byteValue();
	}
	/**
	 * int to byte
	 */
	public static byte toByte(final int i) {
		if (i < -128 || i > 127) throw new IllegalArgumentException("Number is too large. Range is -128 to 127");
		return Integer.valueOf(i).byteValue();
	}
	/**
	 * long to byte
	 */
	public static byte toByte(final long l) {
		if (l < -128 || l > 127) throw new IllegalArgumentException("Number is too large. Range is -128 to 127");
		return Long.valueOf(l).byteValue();
	}
	/**
	 * short to byte
	 */
	public static byte toByte(final short sh) {
		if (sh < -128 || sh > 127) throw new IllegalArgumentException("Number is too large. Range is -128 to 127");
		return Short.valueOf(sh).byteValue();
	}
	/**
	 * String to byte
	 */
	public static byte toByte(final String s) {
		long l = toLong(s);
		if (l < -128 || l > 127) throw new IllegalArgumentException("Number is too large. Range is -128 to 127");
		return Long.valueOf(l).byteValue();
	}
	/***************************************************************************************************************************************
	 * toDouble ********************************* double = 64 bits, range = 1.7976931348623157 x 10^308, 4.9406564584124654 x 10^-324
	 **************************************************************************************************************************************/
	/**
	 * byte to double
	 */
	public static double toDouble(final byte b) {
		return Byte.valueOf(b).doubleValue();
	}
	/**
	 * float to double
	 */
	public static double toDouble(final float f) {
		return Float.valueOf(f).doubleValue();
	}
	/**
	 * int to double
	 */
	public static double toDouble(final int i) {
		return Integer.valueOf(i).doubleValue();
	}
	/**
	 * long to double
	 */
	public static double toDouble(final long l) {
		return Long.valueOf(l).doubleValue();
	}
	/**
	 * short to double
	 */
	public static double toDouble(final short s) {
		return Short.valueOf(s).doubleValue();
	}
	/**
	 * String to double
	 */
	public static double toDouble(final String s) {
		return Double.valueOf(s).doubleValue();
	}
	/***************************************************************************************************************************************
	 * toFloat ************************* float = 32 bits, range = 3 1.40239846 x 10^-45 to .40282347 x 10^38
	 **************************************************************************************************************************************/
	/**
	 * byte to float
	 */
	public static float toFloat(final byte b) {
		return Byte.valueOf(b).floatValue();
	}
	/**
	 * double to float
	 */
	public static float toFloat(final double d) {
		if (d < 1.40239846 * Math.pow(10, -45) || d > 3.40282347 * Math.pow(10, 38)) throw new IllegalArgumentException("Number is too large. Range is 1.40239846 x 10^-45 to 3.40282347 x 10^38 ");
		return Double.valueOf(d).floatValue();
	}
	/**
	 * int to float
	 */
	public static float toFloat(final int i) {
		return Integer.valueOf(i).floatValue();
	}
	/**
	 * long to float
	 */
	public static float toFloat(final long l) {
		return Long.valueOf(l).floatValue();
	}
	/**
	 * short to float
	 */
	public static float toFloat(final short sh) {
		return Short.valueOf(sh).floatValue();
	}
	/**
	 * String to float
	 */
	public static float toFloat(final String s) {
		return Float.valueOf(s).floatValue();
	}
	/***************************************************************************************************************************************
	 * toInt ********************** int = 32 bits, range = -2,147,483,648 .. 2,147,483,647
	 **************************************************************************************************************************************/
	/**
	 * byte to int
	 */
	public static int toInt(final byte b) {
		return Byte.valueOf(b).intValue();
	}
	/**
	 * double to int
	 */
	public static int toInt(final double d) {
		if (d < -2147483648 || d > 2147483647) throw new IllegalArgumentException("Number is too large. Range is -2,147,483,648 to 2,147,483,647");
		return Double.valueOf(d).intValue();
	}
	/**
	 * float to int
	 */
	public static int toInt(final float f) {
		if (f < -2147483648 || f > 2147483647) throw new IllegalArgumentException("Number is too large. Range is -2,147,483,648 to 2,147,483,647");
		return Float.valueOf(f).intValue();
	}
	/**
	 * long to int
	 */
	public static int toInt(final long l) {
		return Long.valueOf(l).intValue();
	}
	/**
	 * short to int
	 */
	public static int toInt(final short sh) {
		return Short.valueOf(sh).intValue();
	}
	/**
	 * String to int
	 */
	public static int toInt(final String s) {
		long l = toLong(s);
		if (l < -2147483648 || l > 2147483647) throw new IllegalArgumentException("Number is too large. Range is -2,147,483,648 to 2,147,483,647");
		return Long.valueOf(l).intValue();
	}
	/***************************************************************************************************************************************
	 * toLong ******************************** long = 64 bits, range = -9,223,372,036,854,775,808 .. 9,223,372,036,854,775,807
	 **************************************************************************************************************************************/
	/**
	 * byte to Long
	 */
	public static long toLong(final byte b) {
		return Byte.valueOf(b).longValue();
	}
	/**
	 * double to Long
	 */
	public static long toLong(final double d) {
		return Double.valueOf(d).longValue();
	}
	/**
	 * float to Long
	 */
	public static long toLong(final float f) {
		return Float.valueOf(f).longValue();
	}
	/**
	 * int to Long
	 */
	public static long toLong(final int i) {
		return Long.valueOf(i).longValue();
	}
	/**
	 * short to Long
	 */
	public static long toLong(final short sh) {
		return Short.valueOf(sh).longValue();
	}
	/**
	 * String to Long
	 */
	public static long toLong(final String s) {
		int index = s.indexOf(".");
		String newLong = s;
		if (index != -1) {
			newLong = newLong.substring(0, index);
		}
		double d = toDouble(newLong);
		if (d < -9223372036854775808D || d > 9223372036854775807D)throw new IllegalArgumentException("Number is too large. Range is -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807");
		return Double.valueOf(d).longValue();
	}
	/***************************************************************************************************************************************
	 * toShort ************** short = 16 bits, range = -32,768 .. 32,767
	 **************************************************************************************************************************************/
	/**
	 * byte to Short
	 */
	public static short toShort(final byte b) {
		return Byte.valueOf(b).shortValue();
	}
	/**
	 * double to Short
	 */
	public static short toShort(final double d) {
		if (d < -32768 || d > 32767) throw new IllegalArgumentException("Number is too large.  Range is -32,768 to 32,767");
		return Double.valueOf(d).shortValue();
	}
	/**
	 * float to Short
	 */
	public static short toShort(final float f) {
		if (f < -32768 || f > 32767) throw new IllegalArgumentException("Number is too large.  Range is -32,768 to 32,767");
		return Float.valueOf(f).shortValue();
	}
	/**
	 * int to Short
	 */
	public static short toShort(final int i) {
		if (i < -32768 || i > 32767) throw new IllegalArgumentException("Number is too large.  Range is -32,768 to 32,767");
		return Integer.valueOf(i).shortValue();
	}
	/**
	 * long to Short
	 */
	public static short toShort(final long l) {
		if (l < -32768 || l > 32767) throw new IllegalArgumentException("Number is too large.  Range is -32,768 to 32,767");
		return Long.valueOf(l).shortValue();
	}
	/**
	 * String to Short
	 */
	public static short toShort(final String s) {
		long l = toLong(s);
		if (l < -32768 || l > 32767) throw new IllegalArgumentException("Number is too large. Range is -32,768 to 32,767");
		return Long.valueOf(l).shortValue();
	}
	/** ****************** toString ******************** */
	/**
	 * byte to String
	 */
	public static String toString(final byte d) {
		return Byte.toString(d);
	}
	/**
	 * double to String
	 */
	public static String toString(final double d) {
		return Double.toString(d);
	}
	/**
	 * float to String
	 */
	public static String toString(final float f) {
		return Float.toString(f);
	}
	/**
	 * int to String
	 */
	public static String toString(final int i) {
		return Integer.toString(i);
	}
	/**
	 * long to String
	 */
	public static String toString(final long l) {
		return Long.toString(l);
	}
	/**
	 * short to String
	 */
	public static String toString(final short sh) {
		return Short.toString(sh);
	}
	/**
	 * Checks if an string is a number
	 * @param s
	 * @return true if it's a number; false otherwise
	 */
	@GwtIncompatible(value = "GWT does NOT suppports CharMatcher")
	public static boolean isNumber(final String s) {
		return CharMatcher.javaDigit().matchesAllOf(s);
		// Regex is not supported by GWT
		/*
		if (s == null) {
			return false;
		}
		Pattern p = Pattern.compile("^[0-9]+$");
		Matcher m = p.matcher(s);
		if (m.find()) return true;
		return false;
		*/
	}
	/**
	 * Checks if an object is a Number
	 * @param obj
	 * @return
	 */
	public static <T> boolean isNumber(final T obj) {
		return obj instanceof Number;
	}
///////////////////////////////////////////////////////////////////////////////
// 	ROMAN (LATIN) NUMBERS
///////////////////////////////////////////////////////////////////////////////
	public static int intrFromRoman(final String roman) {
		 // Using roman numbers, a symbol cannot repeat more than 3 times
		 String modRoman = roman.replace("XL", "XXXX").replace("XC", "LXXXX") 
		 						.replace("CD", "CCCC").replace("CM", "DCCCC")
		 						.replace("IV", "IIII").replace("IX", "VIIII"); 
		int outNumber = 0;
		for (int n = 0; n < modRoman.length(); n++) {
			char romanChar = modRoman.charAt(n);
			if (romanChar == 'M') {
				outNumber+=1000; 
			} else if (romanChar == 'D') {
				outNumber+=500; 
			} else if (romanChar == 'C') {
				outNumber+=100; 
			} else if (romanChar == 'L') {
				outNumber+=50; 
			} else if (romanChar == 'X') {
				outNumber+=10; 
			} else if (romanChar == 'V') {
				outNumber+=5; 
			} else if (romanChar == 'I') {
				outNumber+=1; 
			}
		}
		return outNumber;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FORMAT
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> boolean isInteger(final T obj) {
		return obj instanceof Integer;
	}
	public static boolean isInteger(final String strInt,final int radix) {
		boolean isInteger = false;
		if (strInt != null) {
			try {
				Integer.parseInt(strInt,radix);
				isInteger = true;
			} catch (NumberFormatException nfEx) {/* Ignore */}
		}
		return isInteger;
	}
	public static boolean isInteger(final String strInt) {
		return isInteger(strInt,10);
	}
	public static <T> boolean isByte(final T obj) {
		return obj instanceof Byte;
	}
	public static boolean isByte(final String strByte,final int radix) {
		boolean isByte = false;
		if (strByte != null) {
			try {
				Byte.parseByte(strByte,radix);
				isByte = true;
			} catch (NumberFormatException nfEx) {/* Ignore */}
		}
		return isByte;
	}
	public static boolean isByte(final String strByte) {
		return isByte(strByte,10);
	}
	public static <T> boolean isLong(final T obj) {
		return obj instanceof Long;
	}
	public static boolean isLong(final String strLong,final int radix) {
		boolean isLong = false;
		if (strLong != null) {
			try {
				Long.parseLong(strLong,radix);
				isLong = true;
			} catch (NumberFormatException nfEx) {/* Ignore */}
		}
		return isLong;
	}
	public static boolean isLong(final String strLong) {
		return Numbers.isLong(strLong,10);
	}
	public static <T> boolean isFloat(final T obj) {
		return obj instanceof Float;
	}
	public static boolean isFloat(final String strFloat) {
		boolean isFloat = false;
		if (strFloat != null) {
			try {
				Float.parseFloat(strFloat);
				isFloat = true;
			} catch (NumberFormatException nfEx) {/* Ignore */}
		}
		return isFloat;
	}
	public static <T> boolean isShort(final T obj) {
		return obj instanceof Short;
	}
	public static boolean isShort(final String strShort) {
		boolean isShort = false;
		if (strShort != null) {
			try {
				Float.parseFloat(strShort);
				isShort = true;
			} catch (NumberFormatException nfEx) {/* Ignore */}
		}
		return isShort;
	}
	public static <T> boolean isDouble(final T obj) {
		return obj instanceof Double;
	}
	public static boolean isDouble(final String strDouble) {
		boolean isDouble = false;
		if (strDouble != null) {
			try {
				Double.parseDouble(strDouble);
				isDouble = true;
			} catch (NumberFormatException nfEx) {/* Ignore */}
		}
		return isDouble;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ROMAN NUMBERS
/////////////////////////////////////////////////////////////////////////////////////////
	private final static TreeMap<Integer, String> ROIMAN_NUMBER_TRANSLATE_MAP = new TreeMap<Integer, String>();
	static {
		ROIMAN_NUMBER_TRANSLATE_MAP.put(1000,"M");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(900, "CM");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(500, "D");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(400, "CD");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(100, "C");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(90,  "XC");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(50,  "L");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(40,  "XL");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(10,  "X");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(9,   "IX");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(5,   "V");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(4,   "IV");
		ROIMAN_NUMBER_TRANSLATE_MAP.put(1,   "I");
	}	
	public final static String toRoman(final int number) {
		int l = ROIMAN_NUMBER_TRANSLATE_MAP.floorKey(number);
		if ( number == l ) {
			return ROIMAN_NUMBER_TRANSLATE_MAP.get(number);
		}
		return ROIMAN_NUMBER_TRANSLATE_MAP.get(l) + toRoman(number-l);
	}
}
