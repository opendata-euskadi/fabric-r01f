package r01f.exceptions;

import com.google.common.annotations.GwtIncompatible;

import r01f.enums.EnumExtendedWrapper;

/**
 * Wraps operations for a {@link EnrichedThrowableSubType}
 * Usage:
 * <pre class='brush:java'>
 * 		@Accessors(prefix="_")
 * 		public enum MyThrowableSubType
 * 		 implements EnrichedThrowableSubType<MyThrowableSubType> {
 *			TYPE_A(1);
 *			TYPE_B(2);
 *			TYPE_C(3);
 *			
 * 			@Getter private final int _group = 1000;	// usually the group is pre-fixed 
 * 			@Getter private final int _code;
 *  		public MyThrowableSubType(final int code) {
 *  			_code = code;
 *  		}
 *  
 *  		private static EnrichedThrowableSubTypeWrapper<MyThrowableSubType> _wrapper = EnrichedThrowableSubTypeWrapper.create(MyThrowableSubType.class);
 *  		
 *  		@Override
 *  		public boolean is(final int group,final int code) {
 *  			return _wrapper.is(this,group,code);
 *  		}
 *  		// static factory
 *  		public static MyThrowableSubType from(final int group,final int code) {
 *  			return _wrapper.from(group,code);
 *  		}
 * 		}
 * </pre>
 * @param <E>
 */
@GwtIncompatible
public class EnrichedThrowableSubTypeWrapper<E extends EnrichedThrowableSubType<E>> 
	 extends EnumExtendedWrapper<E> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public EnrichedThrowableSubTypeWrapper(final E[] values) {
		super(values,
			  true);	
	}
	/**
	 * Factory
	 * @param enumType
	 * @return
	 */
	public static <T extends EnrichedThrowableSubType<T>> EnrichedThrowableSubTypeWrapper<T> create(final Class<T> enumType) {
		return new EnrichedThrowableSubTypeWrapper<T>(enumType.getEnumConstants());
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the enum element from the group and code
	 * @param group
	 * @param code
	 * @return 
	 */	
	public E from(final int group,final int code) {
		E outT = null;
		for (E ty : _values) {
			if (this.is(ty,group,code)) {
				outT = ty;
				break;
			}
		}
		if (outT == null) throw new IllegalArgumentException(Throwables.message("There's no {} element with group={} and code={}",
																				_values.getClass().getComponentType(),group,code));
		return outT;
	}
	/**
	 * Returns true if the provided group and code can be an Enunm element
	 * @param group
	 * @param code
	 * @return
	 */
	public boolean canBe(final int group,final int code) {
		E outT = null;
		try {
			outT = this.from(group,code);
		} catch(IllegalArgumentException illArgEx) {
			/* nothing to do */
		}
		return outT != null;
	}
	/**
	 * Returns if the element provided in the first parameter has the group and code provided in the second & third parameters
	 * @param el 
	 * @param group
	 * @param code
	 * @return true 
	 */
	public boolean is(final E el,
					  final int group,final int code) {
		return el.getGroup() == group && el.getCode() == code;
	}

}
