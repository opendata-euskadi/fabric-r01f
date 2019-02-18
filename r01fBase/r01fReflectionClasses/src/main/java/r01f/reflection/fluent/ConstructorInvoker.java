package r01f.reflection.fluent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;

/**
 * Constructor invocation via reflection
 * @param <T> the class in which the constructor is declared.
 * 
 * Usage example:
 * <pre>
 * // Equivalent to call 'new MyObj()' 
 * MyObj p = Reflection.constructor().in(MyObj.class).newInstance(); 
 * // Equivalent to call 'new MyObj("myStrParam")' 
 * Person p = Reflection.constructor().withParameterTypes(String.class).in(MyObj.class).newInstance("myStrParam");
 * </pre>
 */

public final class ConstructorInvoker<T> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Constructor<T> _constructor;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	private ConstructorInvoker(final Constructor<T> constructor) {
		_constructor = constructor;
	}
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////	
	public static <T> ConstructorInvoker<T> newInvoker(final Class<T> target,final Class<?>... parameterTypes) {
		Constructor<T> constructor = constructor(target,parameterTypes);
		return new ConstructorInvoker<T>(constructor);
	}	
///////////////////////////////////////////////////////////////////////////////
//	INTERFAZ PUBLICA
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a new instance of <code>T</code> by calling a constructor with the given arguments.
	 * 
	 * @param args the arguments to pass to the constructor (can be zero or more).
	 * @return the created instance of <code>T</code>.
	 * @throws ReflectionException if a new instance cannot be created.
	 */
	public T newInstance(final Object... args) {
		boolean accessible = _constructor.isAccessible();
		try {
			ReflectionUtils.makeAccessible(_constructor);
			T newInstance = _constructor.newInstance(args);
			return newInstance;
		} catch (Throwable t) {
			Throwable cause = t instanceof InvocationTargetException ? t.getCause() : t;
			if (cause instanceof RuntimeException) throw (RuntimeException)cause;
			throw ReflectionException.of(cause);
		} finally {
			ReflectionUtils.setAccessibleIgnoringExceptions(_constructor,accessible);
		}
	}
	/**
	 * Returns the "real" constructor managed by this class. 
	 * @return the "real" constructor managed by this class.
	 */
	public Constructor<T> getConstructor() {
		return _constructor;
	}
///////////////////////////////////////////////////////////////////////////////
//	METODOS PRIVADOS
///////////////////////////////////////////////////////////////////////////////	
	private static <T> Constructor<T> constructor(final Class<T> target,final Class<?>... parameterTypes) {
		try {
			return target.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException nsmEx) {
			throw ReflectionException.noConstructorException(target.getClass(),parameterTypes);
		}
	}	
}
