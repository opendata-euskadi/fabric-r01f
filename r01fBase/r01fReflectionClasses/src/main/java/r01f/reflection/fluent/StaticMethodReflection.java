package r01f.reflection.fluent;


import com.google.common.reflect.TypeToken;

import r01f.reflection.fluent.StaticMethodReturnTypes.StaticMethodReturnType;
import r01f.reflection.fluent.StaticMethodReturnTypes.StaticMethodReturnTypeGenerics;

/**
 * Static methoc reflection.
 * 
 * Usage example:
 * <pre>
 *   // Equivalent to call 'Jedi.setCommonPower("Jump")'
 *   Reflection.staticMethod("setCommonPower").withParameterTypes(String.class).in(Jedi.class).invoke("Jump");
 * 
 *   // Equivalent to call 'Jedi.addPadawan()'
 *   Reflection.staticMethod("addPadawan").in(Jedi.class).invoke();
 * 
 *   // Equivalent to call 'Jedi.commonPowerCount()'
 *   String name = Reflection.staticMethod("commonPowerCount").withReturnType(String.class).in(Jedi.class).invoke();
 * 
 *   // Equivalent to call 'Jedi.getCommonPowers()'
 *   List<String> powers = Reflection.staticMethod("getCommonPowers").withReturnType(new TypeRef<List<String>>() {}).in(Jedi.class).invoke}();
 * </pre>
 */

public final class StaticMethodReflection {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final String _methodName;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	private StaticMethodReflection(final String methodName) {
		_methodName = methodName;
	}
///////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////		
	/**
	 * Creates a new </code>{@link StaticMethodReflection}</code>: the starting point of the fluent interface for accessing static methods using Java Reflection.
	 * 
	 * @param methodName the name of the method to access using Java Reflection.
	 * @return the created <code>StaticMethodName</code>.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	static StaticMethodReflection startStaticMethodAccess(final String methodName) {
		_validateIsNotNullOrEmpty(methodName);
		return new StaticMethodReflection(methodName);
	}
	/**
	 * Specifies the return type of the static method to invoke. This method call is optional if the return type of the method to invoke is <code>void</code>.
	 * 
	 * @param <T> the generic type of the method's return type.
	 * @param type the return type of the method to invoke.
	 * @return the created return type holder.
	 * @throws NullPointerException if the given type is <code>null</code>.
	 */
	public <T> StaticMethodReturnType<T> withReturnType(final Class<T> type) {
		return StaticMethodReturnTypes.newReturnType(_methodName,type);
	}
	/**
	 * Specifies the return type reference of the static method to invoke. This method call is optional if the return type of the method to invoke is <code>void</code>.
	 * 
	 * @param <T> the generic type of the method's return type.
	 * @param type the return type reference of the method to invoke.
	 * @return the created return type holder.
	 * @throws NullPointerException if the given type reference is <code>null</code>.
	 */
	public <T> StaticMethodReturnTypeGenerics<T> withReturnType(final TypeToken<T> type) {
		return StaticMethodReturnTypes.newReturnTypeGenerics(_methodName,type);
	}
	/**
	 * Specifies the parameter types of the static method to invoke. This method call is optional if the method to invoke does not take arguments.
	 * 
	 * @param parameterTypes the parameter types of the method to invoke.
	 * @return the created parameter types holder.
	 * @throws NullPointerException if the array of parameter types is <code>null</code>.
	 */
	public StaticMethodParameterTypes<Void> withParameterTypes(final Class<?>... parameterTypes) {
		return StaticMethodParameterTypes.newParameterTypes(_methodName,parameterTypes);
	}
	/**
	 * Creates a new invoker for a static method that takes no parameters and return value <code>void</code>.
	 * 
	 * @param target the object containing the method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<Void> in(final Class<?> target) {
		return MethodInvoker.newInvoker(_methodName,target);
	}
///////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////	
	private static void _validateIsNotNullOrEmpty(final String methodName) {
		if (methodName == null || methodName.length() == 0) throw new IllegalArgumentException("The name of the static method to access should not be empty");
	}	
}
