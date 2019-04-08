package r01f.reflection.fluent;

import r01f.reflection.ReflectionException;

/**
 * Understands how to obtain a reference to a static inner class.
 */

public class StaticInnerClassInvoker {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Class<?> _declaringClass;
	private final String _innerClassName;
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private StaticInnerClassInvoker(final Class<?> declaringClass,final String innerClassName) {
		_declaringClass = declaringClass;
		_innerClassName = innerClassName;
	}
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////	
	static StaticInnerClassInvoker newInvoker(final Class<?> declaringClass,final String innerClassName) {
		if (declaringClass == null)	throw new NullPointerException("The declaring class should not be null");
		return new StaticInnerClassInvoker(declaringClass,innerClassName);
	}	
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////		
	/**
	 * Returns a reference to the static inner class with the specified name in the specified declaring class.
	 * 
	 * @return a reference to the static inner class with the specified name in the specified declaring class.
	 * @throws ReflectionError if the static inner class does not exist (since 1.2).
	 */
	public Class<?> get() {
		String namespace = _declaringClass.getName();
		for (Class<?> innerClass : _declaringClass.getDeclaredClasses()) {
			if (innerClass.getName().equals(_expectedInnerClassName(namespace))) {
				return innerClass;
			}
		}
		throw ReflectionException.classNotFoundException(_declaringClass.getName() + "$" + _innerClassName);
	}
///////////////////////////////////////////////////////////////////////////////
// 	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////	
	private String _expectedInnerClassName(final String namespace) {
		return namespace + "$" +_innerClassName;
	}
}
