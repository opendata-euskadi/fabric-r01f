package com.google.inject;

import java.util.Map;

import com.google.inject.internal.MoreTypes;

public class EvenMoreTypes {
	/**
	 * Builds a TypeLiteral of a Map using the key and value types
	 * <pre>
	 * SIDE NOTE: 	This is necessary since the {@link TypeLiteral} type's public API does NOT expose
	 * 				the TypeLiteral(type) constructor
	 * </pre>
	 * @param keyType map's key type
	 * @param valueType map's value type
	 * @return the {@link TypeLiteral}
	 */
	public static <K,V> TypeLiteral<Map<K,V>> mapOf(final Class<K> keyType,final Class<V> valueType) {
		TypeLiteral<Map<K,V>> typeLiteral = new TypeLiteral<Map<K,V>>(new MoreTypes.ParameterizedTypeImpl(null,
																										  Map.class,
																										  keyType,valueType));
		return typeLiteral;
		
	}
	/**
	 * Builds a TypeLiteral of a Map using the key and value types
	 * (this method is used when the key or value types are generics 
	 * <pre>
	 * SIDE NOTE: 	This is necessary since the {@link TypeLiteral} type's public API does NOT expose
	 * 				the TypeLiteral(type) constructor
	 * </pre>
	 * @param keyType map's key type
	 * @param valueType map's value type
	 * @return the {@link TypeLiteral}
	 */
	public static <K,V> TypeLiteral<Map<K,V>> mapOf(final TypeLiteral<K> keyType,final TypeLiteral<V> valueType) {
		TypeLiteral<Map<K,V>> typeLiteral = new TypeLiteral<Map<K,V>>(new MoreTypes.ParameterizedTypeImpl(null,
																										  Map.class,
																										  keyType.getType(),valueType.getType()));
		return typeLiteral;
	}
}
