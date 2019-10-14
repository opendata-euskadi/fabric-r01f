package r01f.exceptions;

import com.google.common.annotations.GwtIncompatible;

import lombok.experimental.Accessors;

/**
 * Exception type for exceptions that do not have a sub-type
 */
@GwtIncompatible
@Accessors(prefix="_")
     class VoidExceptionType 
   extends EnrichedThrowableTypeBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public VoidExceptionType() {
		super("VOID",
			  -1,-1,
			  ExceptionSeverity.DEFAULT);
	}
	public static VoidExceptionType create() {
		return new VoidExceptionType();
	}
}
