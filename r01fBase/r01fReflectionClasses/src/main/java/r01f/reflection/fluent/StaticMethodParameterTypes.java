package r01f.reflection.fluent;

/**
 * Parameter types of the static method to invoke.
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
 * 
 *   // Equivalent to call 'Jedi.getCommonPowers()'
 *   List<String> powers = Reflection.staticMethod("getCommonPowers").withReturnType(new TypeRef<List<String>>() {}).in(Jedi.class).invoke();
 * </pre>
 */

public final class StaticMethodParameterTypes<T> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final String _methodName;
	private final Class<?>[] _parameterTypes;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	private StaticMethodParameterTypes(final String methodName,final Class<?>[] parameterTypes) {
		_methodName = methodName;
		_parameterTypes = parameterTypes;
	}
///////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////		
	static <T> StaticMethodParameterTypes<T> newParameterTypes(final String methodName,final Class<?>[] parameterTypes) {
		if (parameterTypes == null) throw new NullPointerException("The array of parameter types for the static method to access should not be null");
		return new StaticMethodParameterTypes<T>(methodName,parameterTypes);
	}
	/**
	 * Creates a new method invoker.
	 * 
	 * @param target the class containing the static method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<T> in(final Class<?> target) {
		return MethodInvoker.newInvoker(_methodName,target,_parameterTypes);
	}
}