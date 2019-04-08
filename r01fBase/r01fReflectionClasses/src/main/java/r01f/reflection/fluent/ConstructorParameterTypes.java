package r01f.reflection.fluent;

import static r01f.reflection.fluent.ConstructorInvoker.newInvoker;

/**
 * Parameter types for the constructor to invoke.
 * 
 * Usage example:
 * <pre>
 * <pre>
 * // Equivalent to call 'new MyObj()' 
 * MyObj p = Reflection.constructor().in(MyObj.class).newInstance(); 
 * // Equivalent to call 'new MyObj("myStrParam")' 
 * Person p = Reflection.constructor().withParameterTypes(String.class).in(MyObj.class).newInstance("myStrParam");
 * </pre>
 */

public final class ConstructorParameterTypes {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Class<?>[] _parameterTypes;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	private ConstructorParameterTypes(final Class<?>[] parameterTypes) {
		_parameterTypes = parameterTypes;
	}
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////	
	static ConstructorParameterTypes newParameterTypes(final Class<?>[] parameterTypes) {
		if (parameterTypes == null)	throw new NullPointerException("The array of parameter types should not be null");
		return new ConstructorParameterTypes(parameterTypes);
	}	
///////////////////////////////////////////////////////////////////////////////
//	INTERFAZ PUBLICA
///////////////////////////////////////////////////////////////////////////////		
	/**
	 * Creates a new constructor invoker.
	 * 
	 * @param <T> the generic type of the class containing the constructor to invoke.
	 * @param target the the type of object that the constructor invoker will create.
	 * @return the created constructor invoker.
	 */
	public <T> ConstructorInvoker<T> in(final Class<T> target) {
		return newInvoker(target,_parameterTypes);
	}
}