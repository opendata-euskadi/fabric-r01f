package r01f.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedRuntimeException;
import r01f.exceptions.EnrichedThrowableSubType;
import r01f.exceptions.EnrichedThrowableSubTypeWrapper;
import r01f.exceptions.ExceptionSeverity;
import r01f.exceptions.Throwables;

/**
 * Exception thrown at {@link ReflectionUtils} utility type
 */

public class ReflectionException 
     extends EnrichedRuntimeException {
	
	private static final long serialVersionUID = -3758897550813211878L;
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	private ReflectionException(final String msg,
							    final ReflectionExceptionType errorType) {
		super(ReflectionExceptionType.class,
			  msg,
			  errorType);	// all reflection exceptions are fatal
	}
	private ReflectionException(final Throwable th) {
		super(ReflectionExceptionType.class,
			  th,
			  ReflectionExceptionType.from(th));	// all reflection exceptions are fatal
	}
	private ReflectionException(final String msg,
								final Throwable th) {
		super(ReflectionExceptionType.class,
			  msg,
			  th,
			  ReflectionExceptionType.from(th));	// all reflection exceptions are fatal
	}
///////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////
	@Override 
	public synchronized Throwable getCause() {
		// If it's and InvocationTagetException, return the cause
		Throwable cause = super.getCause();
		boolean isInvocationTargetEx = cause != null ? ReflectionUtils.isSameClassAs(cause.getClass(),
																					 InvocationTargetException.class)
												     : false;
		if (isInvocationTargetEx && cause != null) {
			cause = ((InvocationTargetException)cause).getTargetException();
		}
		return super.getCause();
	}
///////////////////////////////////////////////////////////////////////////////
//	SUB_TYPE
///////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	      enum ReflectionExceptionType
	implements EnrichedThrowableSubType<ReflectionExceptionType> {
		UNKNOWN(-1),
		CLASS_NOT_FOUND(1),
		NO_CONSTRUCTOR(2),
		NO_METHOD(3),
		NO_FIELD(4),
		SECURITY(5),
		ILLEGAL_ARGUMENT(6),
		INSTANTIATION(7),
		INVOCATION_TARGET(8);
		
		@Getter private final int _group = 0;
		@Getter private int _code;
		
		private ReflectionExceptionType(final int code) {
			_code = code;
		}
		private static EnrichedThrowableSubTypeWrapper<ReflectionExceptionType> WRAPPER = EnrichedThrowableSubTypeWrapper.create(ReflectionExceptionType.class); 
		
		public static ReflectionExceptionType from(final int errorCode) {
			return WRAPPER.from(0,errorCode);
		}
		public static ReflectionExceptionType from(final int groupCode,final int errorCode) {
			if (groupCode != 0) throw new IllegalArgumentException(Throwables.message("The group code for a {} MUST be {}",
																									ReflectionExceptionType.class,0));
			return WRAPPER.from(0,errorCode);
		}
		/**
		 * Gets the sub type of the exception
		 * @param th the exception
		 */
		public static ReflectionExceptionType from(final Throwable th) {
			ReflectionExceptionType outType = null;
			if (th instanceof ClassNotFoundException) {
				outType = ReflectionExceptionType.CLASS_NOT_FOUND;
			} else if (th instanceof NoSuchMethodException) {
				outType = ReflectionExceptionType.NO_METHOD;
			} else if (th instanceof NoSuchFieldException) {
				outType = ReflectionExceptionType.NO_FIELD;
			} else if (th instanceof SecurityException) {
				outType = ReflectionExceptionType.SECURITY;
			} else if (th instanceof InstantiationException) {
				outType = ReflectionExceptionType.INSTANTIATION;
			} else if (th instanceof IllegalAccessException) {
				outType = ReflectionExceptionType.SECURITY;
			} else if (th instanceof IllegalArgumentException) {
				outType = ReflectionExceptionType.ILLEGAL_ARGUMENT;
			} else if (th instanceof InvocationTargetException) {
				outType = ReflectionExceptionType.INVOCATION_TARGET;	// when invoking a method or constructor
			} else {
				outType = ReflectionExceptionType.UNKNOWN;
			}
			return outType;
		}
		@Override
		public ExceptionSeverity getSeverity() {
			return ExceptionSeverity.FATAL;		// All reflection exceptions are fatal
		}
		@Override
		public boolean is(final int group,final int code) {
			return WRAPPER.is(this,
							  group,code);
		}
		public boolean is(final int code) {
			return this.is(0,code);
		}
		@Override
		public boolean isIn(final ReflectionExceptionType... els) {
			return WRAPPER.isIn(this,els);
		}
		@Override
		public boolean is(final ReflectionExceptionType el) {
			return WRAPPER.is(this,el);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isClassNotFoundException() {
		return this.is(ReflectionExceptionType.CLASS_NOT_FOUND);
	}
	public boolean isNoConstructorException() {
		return this.is(ReflectionExceptionType.NO_CONSTRUCTOR);
	}
	public boolean isNoMethodException() {
		return this.is(ReflectionExceptionType.NO_METHOD);
	}
	public boolean isNoFieldExcepton() {
		return this.is(ReflectionExceptionType.NO_FIELD);
	}
	public boolean isSecurityException() {
		return this.is(ReflectionExceptionType.SECURITY);
	}
	public boolean isIllegalArgumentException() {
		return this.is(ReflectionExceptionType.ILLEGAL_ARGUMENT);
	}
	public boolean isInstantiationException() {
		return this.is(ReflectionExceptionType.INSTANTIATION);
	}
	public boolean isInvocationTargetException() {
		return this.is(ReflectionExceptionType.INVOCATION_TARGET);
	}
	public boolean isunknownSubClassException() {
		return this.is(ReflectionExceptionType.UNKNOWN);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ReflectionException of(final Throwable th) {
		return new ReflectionException(th);
	}
	public static ReflectionException classNotFoundException(final String typeName) {
		return new ReflectionException("Could NOT load type with name: " + typeName,
									   ReflectionExceptionType.CLASS_NOT_FOUND);
	}
	public static ReflectionException instantiationException(final String typeName) {
		return new ReflectionException("Could NOT create an instance of the type: {}" + typeName,
									   ReflectionExceptionType.INSTANTIATION);
	}
	public static ReflectionException instantiationException(final Class<?> type,final Class<?>[] constructorArgs) {
		if (constructorArgs == null || constructorArgs.length == 0) {
			return new ReflectionException("Could NOT create an instance of the type: " + type + " using the no-args constructor",
										   ReflectionExceptionType.INSTANTIATION);			
		}
		return new ReflectionException("Could NOT create an instance of the type: " + type + " using the constructor with args: " + Arrays.asList(constructorArgs),
									   ReflectionExceptionType.INSTANTIATION);
	}
	public static ReflectionException securityException(final Class<?> type) {
		return new ReflectionException("Security exception when creating an instance of type " + type,
									   ReflectionExceptionType.SECURITY);
	}	
	public static ReflectionException securityException(final Class<?> type,final Method method) {
		return new ReflectionException("Security exception when calling " + method + " method in an instance of type " + type,
									   ReflectionExceptionType.SECURITY);
	}
	public static ReflectionException noFieldException(final Class<?> type,final String fieldName) {
		return new ReflectionException("Could NOT find field " + fieldName + " in type " + type,
									   ReflectionExceptionType.NO_FIELD);
	}
	public static ReflectionException noConstructorException(final Class<?> type,final Class<?>[] constructorArgs) {
		return new ReflectionException("Could NOT find constructor with args " + Arrays.asList(constructorArgs) + " in type " + type,
									   ReflectionExceptionType.NO_CONSTRUCTOR);
	}
	public static ReflectionException noMethodException(final Class<?> type,final String methodName) {
		return new ReflectionException("Could NOT find method with name " + methodName + " in type " + type,
									   ReflectionExceptionType.NO_METHOD);
	}
	public static ReflectionException noMethodException(final Class<?> type,final String methodName,final Class<?>[] methodArgs) {
		return new ReflectionException("Could NOT find method with name " + methodName + " and arguments " + Arrays.asList(methodArgs) + " in type " + type,
									   ReflectionExceptionType.NO_METHOD);
	}
	public static ReflectionException illegalArgumentException(final Class<?> expectedType,final Class<?> providedType) {
		return new ReflectionException("The expected type was " + expectedType + " but the provided type was " + providedType,
									   ReflectionExceptionType.NO_METHOD);	}
	public static ReflectionException invocationTargetException(final Throwable th) {
		return new ReflectionException(th.getMessage(),
									   ReflectionExceptionType.INVOCATION_TARGET);
	}
}
