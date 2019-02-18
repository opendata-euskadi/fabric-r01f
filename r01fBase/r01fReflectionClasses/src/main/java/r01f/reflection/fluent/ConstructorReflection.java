package r01f.reflection.fluent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * Constructor Reflection
 * 
 * Usage example:
 * <pre>
 * // Equivalent to call 'new MyObj()' 
 * MyObj p = Reflection.constructor().in(MyObj.class).newInstance(); 
 * // Equivalent to call 'new MyObj("myStrParam")' 
 * Person p = Reflection.constructor().withParameterTypes(String.class).in(MyObj.class).newInstance("myStrParam");
 * </pre>
 */

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class ConstructorReflection {
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////		
	static ConstructorReflection startConstructorAccess() {
		return new ConstructorReflection();
	}	
///////////////////////////////////////////////////////////////////////////////
//	PUBLIC INTERFACE
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a new constructor invoker.
	 * 
	 * @param <T> the generic type of the class containing the constructor to invoke.
	 * @param target the the type of object that the constructor invoker will create.
	 * @return the created constructor invoker.
	 */
	public static <T> ConstructorInvoker<T> in(final Class<T> target) {
		return ConstructorInvoker.newInvoker(target);
	}
	/**
	 * Specifies the parameter types for the constructor to invoke. 
	 * This method call is optional if the constructor to call does not accept arguments.
	 * 
	 * @param parameterTypes the types of the parameters to pass to the constructor.
	 * @return the created parameter type holder.
	 * @throws NullPointerException if the given array is <code>null</code>.
	 */
	public static ConstructorParameterTypes withParameterTypes(final Class<?>... parameterTypes) {
		return ConstructorParameterTypes.newParameterTypes(parameterTypes);
	}
}