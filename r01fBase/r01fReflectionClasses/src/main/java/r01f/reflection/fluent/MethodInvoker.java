package r01f.reflection.fluent;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;

/**
 * Method invocation using reflection
 * 
 * @param <T> the return type of the method invocation.
 * 
 * Usage example:
 * <pre>
 *   // Equivalent to call 'person.setName("Luke")'
 *   Reflection.method("setName").withParameterTypes(String.class).in(person).invoke("Luke");
 * 
 *   // Equivalent to call 'person.concentrate()'
 *   Reflection.method("concentrate").in(person).invoke();
 * 
 *   // Equivalent to call 'person.getName()'
 *   String name = Reflection.method("getName").withReturnType(String.class).in(person).invoke();
 * </pre>
 */

public final class MethodInvoker<T> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Object _targetObj;
	private final Method _method;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private MethodInvoker(final Object target,final Method method) {
		_targetObj = target;
		_method = method;
	}
///////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////		
	static <T> MethodInvoker<T> newInvoker(final String methodName,final Object target,final Class<?>... parameterTypes) {
		return _createInvoker(methodName,target,parameterTypes);
	}
	/**
	 * Invokes the method managed by this class using the given arguments.
	 * 
	 * @param args the arguments to use to call the method managed by this class.
	 * @return the result of the method call.
	 * @throws ReflectionError if the method cannot be invoked.
	 */
	@SuppressWarnings("unchecked")
	public T invoke(final Object... args) {
		boolean accessible = _method.isAccessible();
		try {
			ReflectionUtils.makeAccessible(_method);
			return (T) _method.invoke(_targetObj,args);
		} catch (Throwable t) {
			Throwable cause = t instanceof InvocationTargetException ? t.getCause() : t;
			if (cause instanceof RuntimeException) throw (RuntimeException)cause;
			throw ReflectionException.of(cause);
		} finally {
			ReflectionUtils.setAccessibleIgnoringExceptions(_method,accessible);
		}
	}
	/**
	 * Returns the "real" method managed by this class.
	 * @return the "real" method managed by this class.
	 */
	public java.lang.reflect.Method getMethod() {
		return _method;
	}	
///////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////	
	private static <T> MethodInvoker<T> _createInvoker(final String methodName,final Object target,final Class<?>... parameterTypes) {
		if (target == null) throw new NullPointerException("Target should not be null");
		Method method = _lookupInClassHierarchy(methodName,typeOf(target),parameterTypes);
		return new MethodInvoker<T>(target,method);
	}
	private static Class<?> typeOf(final Object target) {
		if (target instanceof Class<?>) return (Class<?>) target;
		return target.getClass();
	}
	private static Method _lookupInClassHierarchy(final String methodName,final Class<?> targetType,final Class<?>[] parameterTypes) {
		Method method = null;
		Class<?> type = targetType;
		while (type != null) {
			method = _findMethod(type,methodName,parameterTypes);
			if (method != null) break;
			type = type.getSuperclass();
		}
		if (method == null) throw ReflectionException.noMethodException(targetType,methodName);
		return method;
	}
	private static Method _findMethod(final Class<?> type,final String methodName,final Class<?>[] parameterTypes) {
		try {
			return type.getDeclaredMethod(methodName,parameterTypes);
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
}
