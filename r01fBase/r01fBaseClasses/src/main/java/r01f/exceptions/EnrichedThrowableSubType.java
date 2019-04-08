package r01f.exceptions;

import com.google.common.annotations.GwtIncompatible;

import r01f.enums.EnumExtended;

/**
 * Interface for {@link Enum}s representing an {@link EnrichedThrowable} sub-types
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
 * 		}
 * 		...
 * 		Implement the EnrichedThrowableSubType interface (see {@link EnrichedThrowableSubTypeWrapper})
 * </pre>
 * @param <E>
 */
@GwtIncompatible
public interface EnrichedThrowableSubType<E>
	     extends EnumExtended<E> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the group (usually is the same for all enum elements)
	 */
	public int getGroup();
	/**
	 * @return the code
	 */
	public int getCode();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns true if the enum element has the same group and code as the provided ones 
	 * @param group
	 * @param code
	 * @return
	 */
	public boolean is(final int group,final int code);
	/**
	 * @return the exception severity from the exception type
	 */
	public ExceptionSeverity getSeverity();
}
