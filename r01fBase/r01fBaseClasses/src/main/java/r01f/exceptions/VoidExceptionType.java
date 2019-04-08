package r01f.exceptions;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Exception type for exceptions that do not have a sub-type
 * <pre class='brush:java'>
 *		@Accessors(prefix="_")
 *		public class MyException 
 *		     extends EnrichedRuntimeException {
 *		
 *			public MyException() {
 *				super(VoidExceptionType.class);
 *			}
 *			public MyException(final String msg) {
 *				super(VoidExceptionType.class,
 *					  msg);
 *			}
 *			public MyException(final Throwable otherEx) {
 *				super(VoidExceptionType.class,
 *					  otherEx);
 *			}
 *			public MyException(final String msg,
 *							   final Throwable otherEx) {
 *				super(VoidExceptionType.class,
 *					  msg,
 *					  otherEx);
 *			}
 *		}
 * </pre>
 */
@GwtIncompatible
@Accessors(prefix="_")
     class VoidExceptionType 
implements EnrichedThrowableSubType<VoidExceptionType> {
	
	@Getter private final int _group = -1;
	@Getter private final int _code = -1;
	
	@Override
	public boolean isIn(final VoidExceptionType... els) {
		return false;
	}
	@Override
	public boolean is(final VoidExceptionType el) {
		return false;
	}
	@Override
	public boolean is(final int group,final int code) {
		return false;
	}
	@Override
	public ExceptionSeverity getSeverity() {
		return null;
	}
	@Override
	public String name() {
		return null;
	}
}
