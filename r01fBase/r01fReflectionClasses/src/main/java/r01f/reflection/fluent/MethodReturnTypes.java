package r01f.reflection.fluent;

import com.google.common.reflect.TypeToken;


public class MethodReturnTypes {
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////	
	static <T> MethodReturnType<T> newReturnType(final String methodName,final Class<T> type) {
		if (type == null) throw new NullPointerException("The return type of the method to access should not be null");
		return new MethodReturnType<T>(methodName);
	}
	static <T> MethodReturnTypeGenerics<T> newReturnTypeGenerics(final String methodName,final TypeToken<T> type) {
		if (type == null) throw new NullPointerException("The return type reference of the method to access should not be null");
		return new MethodReturnTypeGenerics<T>(methodName);
	}	
///////////////////////////////////////////////////////////////////////////////
//	METHODRETURNTYPE FOR "NORMAL" USE
///////////////////////////////////////////////////////////////////////////////		
/**
 * Return type of the method to invoke.
 * 
 * @param <T> the generic type of the method's return type.
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
public static class MethodReturnType<T> {
	private final String _methodName;
	
	MethodReturnType(final String methodName) {
		_methodName = methodName;
	}		
	/**
	 * Creates a new method invoker.
	 * 
	 * @param target the object containing the method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<T> in(final Object target) {
		return MethodInvoker.newInvoker(_methodName,target);
	}
	/**
	 * Specifies the parameter types of the method to invoke. This method call is optional if the method to invoke does not take arguments.
	 * 
	 * @param parameterTypes the parameter types of the method to invoke.
	 * @return the created parameter types holder.
	 * @throws NullPointerException if the array of parameter types is <code>null</code>.
	 */
	public MethodParameterTypes<T> withParameterTypes(final Class<?>... parameterTypes) {
		return MethodParameterTypes.newParameterTypes(_methodName,parameterTypes);
	}
}
///////////////////////////////////////////////////////////////////////////////
//	METHODRETURNTYPE FOR GENERICS USE
///////////////////////////////////////////////////////////////////////////////
/**
 * Return type of the method to invoke using generics
 * 
 * @param <T> the generic type of the method's return type.
 * 
 * Usage example:
 * <pre>
 *   // Equivalent to call 'jedi.getPowers()'
 *   List<String> powers = Reflection.method("getPowers").withReturnType(new TypeRef<List<String>>() {}).in(person).invoke();
 * </pre>
 */
public static class MethodReturnTypeGenerics<T> {
	private final String _methodName;

	MethodReturnTypeGenerics(final String methodName) {
		_methodName = methodName;
	}	
	/**
	 * Creates a new method invoker.
	 * 
	 * @param target the object containing the method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<T> in(final Object target) {
		return MethodInvoker.newInvoker(_methodName,target);
	}
	/**
	 * Specifies the parameter types of the method to invoke. This method call is optional if the method to invoke does not take arguments.
	 * 
	 * @param parameterTypes the parameter types of the method to invoke.
	 * @return the created parameter types holder.
	 * @throws NullPointerException if the array of parameter types is <code>null</code>.
	 */
	public MethodParameterTypes<T> withParameterTypes(final Class<?>... parameterTypes) {
		return MethodParameterTypes.newParameterTypes(_methodName,parameterTypes);
	}
}
}
