package r01f.reflection.fluent;

import com.google.common.reflect.TypeToken;


public class StaticMethodReturnTypes {
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////	
	static <T> StaticMethodReturnType<T> newReturnType(final String methodName,final Class<T> type) {
		if (type == null) throw new NullPointerException("The return type of the static method to access should not be null");
		return new StaticMethodReturnType<T>(methodName);
	}	
	static <T> StaticMethodReturnTypeGenerics<T> newReturnTypeGenerics(final String methodName,final TypeToken<T> type) {
		if (type == null)
			throw new NullPointerException("The return type reference of the static method to access should not be null");
		return new StaticMethodReturnTypeGenerics<T>(methodName);
	}	
///////////////////////////////////////////////////////////////////////////////
//	STATICMETHODRETURNTYPES FOR "NORMAL" USE
///////////////////////////////////////////////////////////////////////////////		
/**
 * Return type of the static method to invoke.
 * 
 * @param <T> the generic type of the static method's return type.
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
 * </pre>
 */
public static class StaticMethodReturnType<T> {
	private final String _methodName;
	
	StaticMethodReturnType(final String methodName) {
		_methodName = methodName;
	}	
	/**
	 * Creates a new method invoker.
	 * 
	 * @param target the object containing the method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<T> in(final Class<?> target) {
		return MethodInvoker.newInvoker(_methodName,target);
	}
	/**
	 * Specifies the parameter types of the static method to invoke. This method call is optional if the method to invoke does not take arguments.
	 * 
	 * @param parameterTypes the parameter types of the method to invoke.
	 * @return the created parameter types holder.
	 * @throws NullPointerException if the array of parameter types is <code>null</code>.
	 */
	public StaticMethodParameterTypes<T> withParameterTypes(final Class<?>... parameterTypes) {
		return StaticMethodParameterTypes.newParameterTypes(_methodName,parameterTypes);
	}
}
///////////////////////////////////////////////////////////////////////////////
//	STATICMETHODRETURNTYPES FOR GENERICS USE
///////////////////////////////////////////////////////////////////////////////
/**
 * Return type of the static method to invoke.
 * 
 * @param <T> the generic type of the static method's return type.
 * 
 * Usage example:
 * <pre>
 *   // Equivalent to call 'Jedi.getCommonPowers()'
 *   List<String> powers = Reflection.staticMethod("getCommonPowers").withReturnType(new TypeRef<List<String>>() {}).in(Jedi.class).invoke}();
 * </pre>
 */
public static class StaticMethodReturnTypeGenerics<T> {
	private final String _methodName;
	
	StaticMethodReturnTypeGenerics(final String methodName) {
		_methodName = methodName;
	}
	/**
	 * Creates a new method invoker.
	 * 
	 * @param target the object containing the method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<T> in(final Class<?> target) {
		return MethodInvoker.newInvoker(_methodName,target);
	}
	/**
	 * Specifies the parameter types of the static method to invoke. This method call is optional if the method to invoke does not take arguments.
	 * 
	 * @param parameterTypes the parameter types of the method to invoke.
	 * @return the created parameter types holder.
	 * @throws NullPointerException if the array of parameter types is <code>null</code>.
	 */
	public StaticMethodParameterTypes<T> withParameterTypes(final Class<?>... parameterTypes) {
		return StaticMethodParameterTypes.newParameterTypes(_methodName,parameterTypes);
	}
}
}
