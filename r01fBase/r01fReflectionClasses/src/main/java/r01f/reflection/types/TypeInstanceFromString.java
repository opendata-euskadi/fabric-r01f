package r01f.reflection.types;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;

import r01f.guids.OID;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


public class TypeInstanceFromString {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an instance of a type from it {@link String} value
	 * @param valueStr
	 * @param dataType
	 * @return
	 */
	@SuppressWarnings({ "unchecked","rawtypes" })
	public static <T> T instanceFrom(final String valueStr,
									 final Class<T> dataType) {
		if (Strings.isNullOrEmpty(valueStr)) {
			return dataType != Boolean.class ? null
											 : (T)Boolean.valueOf(false);
		}

		T value = null;
		if (dataType == Boolean.class) {
			value = (T)Boolean.valueOf(valueStr);

		} else if (dataType == Integer.class) {
			value = (T)Integer.valueOf(valueStr);

		} else if (dataType == Long.class) {
			value = (T)Long.valueOf(valueStr);

		} else if (dataType == Double.class) {
			value = (T)Double.valueOf(valueStr);

		} else if (dataType == Float.class) {
			value = (T)Float.valueOf(valueStr);

		} else if (dataType == String.class) {
			value = (T)valueStr;

		} else if (dataType == Character.class) {
			value = (T)Character.valueOf(valueStr.charAt(0));

		} else if (dataType == Date.class) {
			value = (T)Dates.fromMillis(Long.valueOf(valueStr));	// dates are serialized as milis
			
		} else if (dataType != null && ReflectionUtils.isSubClassOf(dataType,Enum.class)) {
			value = (T)Enum.valueOf((Class<? extends Enum>)dataType,valueStr);

		} else if (dataType != null && ReflectionUtils.isSubClassOf(dataType,OID.class)) {
			value = (T)ReflectionUtils.invokeStaticMethod(dataType,
														  OID.STATIC_FACTORY_METHOD_NAME,new Class<?>[] {String.class},new Object[] {valueStr});

		} else if (dataType != null && ReflectionUtils.isSubClassOf(dataType,CanBeRepresentedAsString.class)) {
			value = (T)ReflectionUtils.createInstanceFromString(dataType,valueStr);
		}
		return value;
	}
	public static <T> T instanceFrom(final String valueStr,
									 final Class<T> dataType,
									 final Function<String,T> ifFail) {
		T value = TypeInstanceFromString.instanceFrom(valueStr,
												 	  dataType);
		if (value == null && ifFail != null) value = ifFail.apply(valueStr);
		return value;
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public static <T> T[] arrayFrom(final String text,
								    final Class<T> dataType) {
		String[] arrayItems = text.split(",");
		T[] spectrum = null;
		if (dataType == Integer.class) {
			spectrum = (T[])new Integer[arrayItems.length];
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Integer.valueOf(arrayItems[i]);

		} else if (dataType == Long.class) {
			spectrum = (T[])new Long[arrayItems.length];
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Long.valueOf(arrayItems[i]);

		} else if (dataType == Double.class) {
			spectrum = (T[])new Double[arrayItems.length];
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Double.valueOf(arrayItems[i]);

		} else if (dataType == Float.class) {
			spectrum = (T[])new Float[arrayItems.length];
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Float.valueOf(arrayItems[i]);

		} else if (dataType == String.class) {
			spectrum = (T[])new String[arrayItems.length];
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)arrayItems[i];

		} else if (dataType == Character.class) {
			spectrum = (T[])new Character[arrayItems.length];
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Character.valueOf(arrayItems[i].charAt(0));

		} else if (dataType == Date.class) {
			spectrum = (T[])new Date[arrayItems.length];
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Dates.fromMillis(Long.valueOf(arrayItems[i]));

		} else if (ReflectionUtils.isSubClassOf(dataType,OID.class)) {
			spectrum = (T[])Array.newInstance(dataType,arrayItems.length);
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)ReflectionUtils.invokeStaticMethod(dataType,
																										   OID.STATIC_FACTORY_METHOD_NAME,new Class<?>[] {String.class},new Object[] {arrayItems[i]});

		} else if (ReflectionUtils.isSubClassOf(dataType,Enum.class)) {
			spectrum = (T[])Array.newInstance(dataType,arrayItems.length);
			for (int i=0; i<arrayItems.length; i++) spectrum[i] = (T)Enum.valueOf((Class<? extends Enum>)dataType,arrayItems[i]);

		} else {
			throw new IllegalArgumentException(dataType.getName());
		}
		return spectrum;
	}
	@SuppressWarnings({ "rawtypes" })
	public static <T> Collection<T> collectionFrom(final String text,
								    	 		   final Class<T> dataType,
								    	 		   final Class<? extends Collection> colType) {
		// Get an array of values
		T[] values = TypeInstanceFromString.arrayFrom(text,
													  dataType);
		// Get the collection type
		Collection<T> outCol = null;
		if (colType == List.class) {
			outCol = ReflectionUtils.createInstanceOf(ArrayList.class);
		} else if (colType == Set.class) {
			outCol = ReflectionUtils.createInstanceOf(HashSet.class);
		} else {
			throw new IllegalArgumentException("Not a valid Collection type");
		}
		// Add all items
		if (CollectionUtils.hasData(values)) {
			for (T val : values) outCol.add(val);
		}
		return outCol;
	}
}
