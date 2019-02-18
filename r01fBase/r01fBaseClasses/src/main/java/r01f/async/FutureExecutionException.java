package r01f.async;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;



/**
 * Execution exception for {@link FutureResult}
 */
public class FutureExecutionException 
	 extends RuntimeException {

	private static final long serialVersionUID = 205593321852553374L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private enum ExecutionExceptionType 
	  implements EnumExtended<ExecutionExceptionType> {
		INCOMPLETE_RESULT,
		CANCELLED,
		EXECUTION;
		
		private static EnumExtendedWrapper<ExecutionExceptionType> _enum = EnumExtendedWrapper.wrapEnumExtended(ExecutionExceptionType.class);

		@Override
		public boolean isIn(ExecutionExceptionType... els) {
			return _enum.isIn(this,els);
		}
		@Override
		public boolean is(final ExecutionExceptionType el) {
			return _enum.is(this,el);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private ExecutionExceptionType _subClass;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FutureExecutionException() {
		super();
	}
	public FutureExecutionException(final String msg) {
		super(msg);
	}
	public FutureExecutionException(final Throwable other) {
		super(other);
	}
	/**
	 * Creates an {@link FutureExecutionException} due to the result not being available
	 * @return
	 */
	public static FutureExecutionException becauseOfIncompleteResult() {
		FutureExecutionException outEx = new FutureExecutionException();
		return outEx.subClass(ExecutionExceptionType.INCOMPLETE_RESULT);
	}
	/**
	 * Creates a {@link FutureExecutionException} due to the cancellation of the request
	 * @return
	 */
	public static FutureExecutionException becauseOfRequestCancellation() {
		FutureExecutionException outEx = new FutureExecutionException();
		return outEx.subClass(ExecutionExceptionType.CANCELLED);
	}
	/**
	 * Creates a {@link FutureExecutionException} due to some error while executing the request
	 * @param th
	 * @return
	 */
	public static FutureExecutionException becauseOfExecutionException(final Throwable th) {
		FutureExecutionException outEx = new FutureExecutionException(th);
		return outEx.subClass(ExecutionExceptionType.EXECUTION);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public ExecutionExceptionType getSubClass() {
		return _subClass;
	}
	public FutureExecutionException subClass(final ExecutionExceptionType theSubClass) {
		_subClass = theSubClass;
		return this;
	}
	public boolean isSubClassOf(final ExecutionExceptionType subClass) {		
		return subClass.is(_subClass);
	}
	public boolean isAnyOfSubClasses(final ExecutionExceptionType... subClasses) {
		return _subClass.isIn(subClasses);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the {@link FutureExecutionException} was due to the result not being available
	 */
	public boolean wasBecauseOfIncompleteResult() {
		return this.isSubClassOf(ExecutionExceptionType.INCOMPLETE_RESULT);
	}
	/**
	 * @return true if the {@link FutureExecutionException} was due the cancellation of the request
	 */
	public boolean wasCancelled() {
		return this.isSubClassOf(ExecutionExceptionType.CANCELLED);
	}
	/**
	 * @return true if the {@link FutureExecutionException} was due an error while executing the request
	 */
	public boolean wasExecutionException() {
		return this.isSubClassOf(ExecutionExceptionType.EXECUTION);
	}
}
