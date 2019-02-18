package r01f.util.enums;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumWithCode;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Enum utilities
 * Usage:
 * Provided an enum:
 * <pre class='brush:java'>
 * 		public enum MyEnum {
 * 			A,B,C;
 * 		}
 * </pre>
 * The util methods can be accessed:
 * <pre class='brush:java'>
 * 		boolean isIn = Enums.of(MyEnum.class).isIn(MyEnum.A,
 *											   	   new MyEnum[] {MyEnum.A,MyEnum.B,MyEnum.C});
 * </pre>
 */
public class Enums {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Tries to guess the code type of a {@link EnumWithCode} implementing type
	 * @param enumType
	 * @return
	 */
	public static Class<?> guessEnumWithCodeCodeType(final Class<? extends Enum<?>> enumType) {
		// trick: get an enum element and call it's getCodeType() method
		return ((EnumWithCode<?,?>)enumType.getEnumConstants()[0]).getCodeType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds an enum wrapper that offers some util with enum
	 * @param theEnumType the enum type
	 * @return an enum wrapper offering some useful methods
	 */
	public static <E extends Enum<E>> EnumWrapper<E> of(final Class<E> theEnumType) {
		return new EnumWrapper<E>(theEnumType.getEnumConstants(),true);
	}
	/**
	 * Builds an enum wrapper that offers some util with enum
	 * @param theEnumType the enum type
	 * @return an enum wrapper offering some useful methods
	 */
	public static <E extends Enum<E>> EnumWrapper<E> wrap(final Class<E> theEnumType) {
		return new EnumWrapper<E>(theEnumType.getEnumConstants(),true);
	}
	/**
	 * Builds an enum wrapper that offers some utilities with enum
	 * @param theEnumType the enum type
	 * @param strict if true an IllegalArgumentException if the element is not found when invoking 'fromName()' method
	 * @return an enum wrapper offering some useful methods
	 */
	public static <E extends Enum<E>> EnumWrapper<E> of(final Class<E> theEnumType,
														final boolean strict) {
		return new EnumWrapper<E>(theEnumType.getEnumConstants(),strict);		
	}
	/**
	 * Builds an enum wrapper that offers some utilities with enum
	 * @param theEnumType the enum type
	 * @param strict if true an IllegalArgumentException if the element is not found when invoking 'fromName()' method
	 * @return an enum wrapper offering some useful methods
	 */
	public static <E extends Enum<E>> EnumWrapper<E> wrap(final Class<E> theEnumType,
														  final boolean strict) {
		return new EnumWrapper<E>(theEnumType.getEnumConstants(),strict);		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static class EnumWrapper<T extends Enum<T>> {
		@Getter private final T[] _values;		// enum values
		@Getter private final boolean _strict;	// is an IllegalArgumentException thrown if the element is not found
												// when invoking fromName method??
		
		public static <T extends Enum<T>> EnumWrapper<T> create(final Class<T> enumType) {
			return new EnumWrapper<T>(enumType.getEnumConstants(),
									  true);
		}
		/**
		 * Returns the enum element from it's name
		 * @param name element's name
		 * @return the enum's element
		 */	
		public T fromName(final String name) {
			if (_strict && name == null) throw new IllegalArgumentException("It does NOT exist an element of enum whith name = '" + name + "' in values [" + CollectionUtils.of(_values).toStringCommaSeparated() + "] of " + _values.getClass().getComponentType());
			if (name == null) return null;
			
			T outT = null;
			for (T ty : _values) {
				if (ty.name().equals(name)) {
					outT = ty;
					break;
				}
			}
			if (_strict && outT == null) throw new IllegalArgumentException("It does NOT exist an element of enum whith name = '" + name + "' in values [" + CollectionUtils.of(_values).toStringCommaSeparated() + "] of " + _values.getClass().getComponentType());
			return outT;
		}
		/**
		 * Returns the enum element from it's name
		 * @param name element's name
		 * @return the enum's element
		 */	
		public T fromNameIgnoringCase(final String name) {
			if (_strict && name == null) throw new IllegalArgumentException("It does NOT exist an element of enum whith name = '" + name + "' in values [" + CollectionUtils.of(_values).toStringCommaSeparated() + "] of " + _values.getClass().getComponentType());
			if (name == null) return null;
			
			T outT = null;
			for (T ty : _values) {
				if (ty.name().equalsIgnoreCase(name)) {
					outT = ty;
					break;
				}
			}
			if (_strict && outT == null) throw new IllegalArgumentException("It does NOT exist an element of enum whith name = '" + name + "' in values [" + CollectionUtils.of(_values).toStringCommaSeparated() + "] of " + _values.getClass().getComponentType());
			return outT;
		}
		/**
		 * Returns the enum element from it's code
		 * WARNING: the enum MUST be an instance of {@link EnumWithCode}
		 * @param code
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <C> T fromCode(final C code) {
			T outT = null;
			for (T ty : _values) {
				EnumWithCode<C,T> enumWithCode = (EnumWithCode<C,T>)ty;
				if (enumWithCode.getCode().toString().equals(code.toString())) {		// toString() is used because equals() may return false when using primitives
					outT = ty;
					break;
				}
			}
			if (_strict && outT == null) throw new IllegalArgumentException("It does NOT exist an element of enum whith code = '" + code + "' in values [" + CollectionUtils.of(_values).toStringCommaSeparated() + "] of " + _values.getClass().getComponentType());
			return outT;
		}
		/**
		 * Returns true if the provided param can be one of the enum elements
		 * @param code the code to check
		 * @return true if one enum element's code matches the param
		 */
		public <C> boolean canBeCode(final C code) {
			T outT = null;
			try {
				outT = this.fromCode(code);
			} catch(IllegalArgumentException illArgEx) {
				/* nothing to do */
			}
			return outT != null;	// if it's different from null, the enum element has been found
		}
		/**
		 * Returns true if the provided param can be one of the enum elements
		 * @param name the name to check
		 * @return true if one enum element's name matches the param
		 */
		public boolean canBe(final String name) {
			T outT = null;
			try {
				outT = this.fromName(name);
			} catch(IllegalArgumentException illArgEx) {
				/* nothing to do */
			}
			return outT != null;	// if it's different from null, the enum element has been found
		}
		/**
		 * Returns true if the element is among the provided other elements
		 * @param el the element to check if is included
		 * @param els the set of elements
		 * @return true if el is included in els
		 */
		public boolean isIn(final T el,final T... els) {
			boolean isIn = false;
			for (T currE : els) {
				if (currE == el) {
					isIn = true;
					break;
				}
			}		
			return isIn;
		}
		/**
		 * Returns true if the element is the other element
		 * @param el one element of the enum
		 * @param other other element of the enum
		 * @return true if both elements are the same
		 */
		public boolean is(final T el,final T other) {
			return el == other;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the enum names quouted
	 * @param vals
	 * @return
	 */
	public static <E extends EnumExtended<E>> Collection<String> namesQuoted(final E... vals) {
		return Enums.namesQuoted(Lists.newArrayList(vals));
	}
	/**
	 * Returns the enum names quouted
	 * @param vals
	 * @return
	 */
	public static <E extends EnumExtended<E>> Collection<String> namesQuoted(final Collection<E> vals) {
		return FluentIterable.from(vals)
	 			   .transform(new Function<E,String>() {
									@Override
									public String apply(final E val) {
										return Strings.customized("'{}'",
															 	  val.name());
									}
	 			   			  })
	 			   .toList();
	}

}
