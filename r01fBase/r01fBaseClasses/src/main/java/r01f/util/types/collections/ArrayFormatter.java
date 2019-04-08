package r01f.util.types.collections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import r01f.util.types.Strings;

public class ArrayFormatter {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final String NULL = "null";
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static String format(final Object o) {
		if (o == null) return null;
		if (!CollectionUtils.isArray(o.getClass())) return null;
		if (CollectionUtils.isObjectsArray(o.getClass())) return _formatObjectsArray((Object[])o);
		return _formatPrimitivesArray(o);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String _formatObjectsArray(final Object[] objsArray) {
		int arrayLength = objsArray.length;
		if (arrayLength == 0) return "[]";
		StringBuilder buffer = new StringBuilder((20 * (arrayLength - 1)));
		_deepToString(objsArray,buffer,new HashSet<Object[]>());
		return buffer.toString();
	}
	private static void _deepToString(final Object[] objsArray,
									  final StringBuilder buffer,
									  final Set<Object[]> alreadyFormatted) {
		if (objsArray == null) {
			buffer.append(NULL);
			return;
		}
		alreadyFormatted.add(objsArray);
		buffer.append('[');
		int length = objsArray.length;
		for (int i = 0; i < length; i++) {
			if (i != 0) buffer.append(", ");
			Object element = objsArray[i];
			if (element == null) {
				buffer.append(NULL);
				continue;
			}
			if (!CollectionUtils.isArray(element.getClass())) {				// Objetos normales
				buffer.append(Strings.quote(element.toString()));
				continue;
			}
			// this is an array for sure BUT it can be an objects array or a primitives array
			if (!CollectionUtils.isObjectsArray(element.getClass())) {		// primitives array
				buffer.append(_formatPrimitivesArray(element));
				continue;
			}
			if (alreadyFormatted.contains(element)) {
				buffer.append("[...]");
				continue;
			}
			// this is an objects array for sure 
			_deepToString((Object[])element,buffer,alreadyFormatted);
		}
		buffer.append(']');
		alreadyFormatted.remove(objsArray);
	}
	private static String _formatPrimitivesArray(Object primitivesArray) {
		Class<?> elementType = primitivesArray.getClass().getComponentType();
		if (elementType.equals(boolean.class)) return Arrays.toString((boolean[]) primitivesArray);
		if (elementType.equals(char.class)) return Arrays.toString((char[]) primitivesArray);
		if (elementType.equals(byte.class)) return Arrays.toString((byte[]) primitivesArray);
		if (elementType.equals(short.class)) return Arrays.toString((short[]) primitivesArray);
		if (elementType.equals(int.class)) return Arrays.toString((int[]) primitivesArray);
		if (elementType.equals(long.class)) return Arrays.toString((long[]) primitivesArray);
		if (elementType.equals(float.class)) return Arrays.toString((float[]) primitivesArray);
		if (elementType.equals(double.class)) return Arrays.toString((double[]) primitivesArray);
		throw new IllegalArgumentException(Strings.customized("<{}> is not an array of primitives",primitivesArray.toString()));
	}
}
