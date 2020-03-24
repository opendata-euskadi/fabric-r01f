package r01f.enums;

import java.util.Collection;

import r01f.util.enums.Enums;

/**
 * Encapsulates basic operations with an {@link Enum} 
 * Usage:
 * <pre class='brush:java'>
 * public enum MyEnum 
 *   implements EnumExtended<MyEnum> {
 * 		VALUE1,
 * 		VALUE2,
 * 		VALUE3;
 * 
 *		// Static wrapper that implements basic Enum operations
 *		private static EnumExtendedWrapper<MyEnum> _enums = new EnumExtendedWrapper<MyEnum>(MyEnum.values());
 *
 *		@Override
 *		public boolean isIn(MyEnum... other) {
 *			return _enums.isIn(this,other);
 *		}
 *		@Override
 *		public boolean is(R01MPublishRequestType other) {
 *			return _enums.is(this,other);
 *		}
 *		// Static factory
 *		public static MyEnum fromName(String name) {
 *			return _enums.fromName(name);
 *		}
 * }
 * </pre>
 */
public class EnumExtendedWrapper<E extends EnumExtended<E>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final E[] _values;	// Enum values
	
	boolean _strict = false;		// true if an IllegalArgumentException must be thrown if the element is not found
									// when calling fromXXX methods
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor from {@link Enum} elements
	 * @param values 
	 */
	public EnumExtendedWrapper(final E[] values) {
		_values = values;
	}
	/**
	 * Constructor from {@link Enum} elements and strict value
	 * @param values
	 * @param strict
	 */
	public EnumExtendedWrapper(final E[] values,
							   final boolean strict) {
		this(values);
		_strict = strict;
	}
	/**
	 * Factory
	 * @param enumType
	 * @return
	 */
	public static <T extends EnumExtended<T>> EnumExtendedWrapper<T> wrapEnumExtended(final Class<T> enumType) {
		return new EnumExtendedWrapper<T>(enumType.getEnumConstants());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the "strict" behavior that throws an {@link IllegalArgumentException} if the element
	 * is NOT found when calling fromXXX static factory methods
	 */
	public EnumExtendedWrapper<E> strict() {
		_strict = true;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the enum element from it's name
	 * @param name 
	 * @return 
	 */	
	public E fromName(final String name) {
		E outT = null;
		for (E ty : _values) {
			if (ty.name().equals(name)) {
				outT = ty;
				break;
			}
		}
		if (_strict && outT == null) throw new IllegalArgumentException("It does NOT exist an element of enum whith name = " + name);
		return outT;
	}
	/**
	 * Returns true if the provided String can be an Enunm element
	 * @param name
	 * @return
	 */
	public boolean canBe(final String name) {
		E outT = null;
		try {
			outT = this.fromName(name);
		} catch (IllegalArgumentException illArgEx) {
			/* nothing to do */
		}
		return outT != null;	// found if not null
	}
	/**
	 * Returns true if the element provided in the first parameter is within the provided ones in the second parameter 
	 * @param el 
	 * @param els 
	 * @return true 
	 */
	public boolean isIn(final E el,final E... els) {
		boolean isIn = false;
		for (E currE : els) {
			if (currE == el) {
				isIn = true;
				break;
			}
		}		
		return isIn;
	}
	/**
	 * Returns true if the element provided in the first parameter is NOT within the provided ones in the second parameter 
	 * @param el 
	 * @param els 
	 * @return true 
	 */
	public boolean isNOTIn(final E el,final E... els) {
		boolean isNOTIn = true;
		for (E currE : els) {
			if (currE == el) {
				isNOTIn = false;
				break;
			}
		}		
		return isNOTIn;
	}
	/**
	 * Returns true if the element provided in the first parameter is within the provided ones in the second parameter 
	 * @param el 
	 * @param els 
	 * @return true 
	 */
	public boolean isIn(final E el,final Collection<E> els) {
		boolean isIn = false;
		for (E currE : els) {
			if (currE == el) {
				isIn = true;
				break;
			}
		}		
		return isIn;
	}
	/**
	 * Returns if the element provided in the first parameter is the provided one in the second parameter
	 * @param el 
	 * @param other 
	 * @return true 
	 */
	public boolean is(final E el,final E other) {
		return el == other;
	}
	/**
	 * Returns if the element provided in the first parameter is NOT the provided one in the second parameter
	 * @param el
	 * @param other
	 * @return
	 */
	public boolean isNOT(final E el,final E other) {
		return !this.is(el,other);
	}
	/**
	 * Returns the enum names quouted
	 * @return
	 */
	public Collection<String> namesQuoted() {
		return Enums.namesQuoted(_values);
	}	
}
