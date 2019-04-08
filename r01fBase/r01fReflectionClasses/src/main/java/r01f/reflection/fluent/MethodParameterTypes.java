package r01f.reflection.fluent;

/**
 * Encapsulates a method parameter type
 * 
 * @param <T> the generic type of the method's return type.
 * 
 * Usage example:
 * <pre>
 *   // Equivalent to call 'person.setName("Luke")'
 *   Reflection.method("setName").withParameterTypes(String.class).in(person).invoke("Luke");
 * 
 *   // Equivalent to call 'person.concentrate()'
 *   Reflection#method(String).method("concentrate").in(person).invoke}();
 * 
 *   // Equivalent to call 'person.getName()'
 *   String name = Reflection.method("getName").withReturnType(String.class).in(person).invoke();
 * </pre>
 */

public final class MethodParameterTypes<T> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final String _methodName;
	private final Class<?>[] _parameterTypes;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	private MethodParameterTypes(final String methodName,final Class<?>[] parameterTypes) {
		_methodName = methodName;
		_parameterTypes = parameterTypes;
	}
///////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////	
	static <T> MethodParameterTypes<T> newParameterTypes(final String methodName,final Class<?>[] parameterTypes) {
		if (parameterTypes == null) throw new NullPointerException("The array of parameter types for the method to access should not be null");
		return new MethodParameterTypes<T>(methodName,parameterTypes);
	}
	/**
	 * Creates a new method invoker.
	 * 
	 * @param target the object containing the method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<T> in(final Object target) {
		return MethodInvoker.newInvoker(_methodName,target,_parameterTypes);
	}
}