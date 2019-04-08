/*
 * Created on Aug 17, 2007
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright @2007-2009 the original author or authors.
 */
package r01f.reflection.fluent;


import com.google.common.reflect.TypeToken;

import r01f.reflection.fluent.MethodReturnTypes.MethodReturnType;
import r01f.reflection.fluent.MethodReturnTypes.MethodReturnTypeGenerics;

/**
 * Method Reflection
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
 * 
 *   // Equivalent to call 'jedi.getPowers()'
 *   List<String> powers = Reflection.method("getPowers").withReturnType(new TypeRef<List<String>>() {}).in(person).invoke();
 * </pre>
 */

public final class MethodReflection {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final String _methodName;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private MethodReflection(final String name) {
		_methodName = name;
	}
///////////////////////////////////////////////////////////////////////////////
// 	FLUENT-API
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new <code>{@link MethodReflection}</code>: the starting point of the fluent interface for accessing methods using Java Reflection.
	 * 
	 * @param methodName the name of the method to invoke using Java Reflection.
	 * @return the created <code>MethodName</code>.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	static MethodReflection startMethodAccess(final String methodName) {
		_validateIsNotNullOrEmpty(methodName);
		return new MethodReflection(methodName);
	}
///////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////		
	/**
	 * Specifies the return type of the method to invoke. This method call is optional if the return type of the method to invoke is <code>void</code>.
	 * 
	 * @param <T> the generic type of the method's return type.
	 * @param type the return type of the method to invoke.
	 * @return the created return type holder.
	 * @throws NullPointerException if the given type is <code>null</code>.
	 */
	public <T> MethodReturnType<T> withReturnType(final Class<T> type) {
		return MethodReturnTypes.newReturnType(_methodName,type);
	}
	/**
	 * Specifies the return type reference of the method to invoke. This method call is optional if the return type of the method to invoke is <code>void</code>.
	 * 
	 * @param <T> the generic type of the method's return type.
	 * @param type the return type reference of the method to invoke.
	 * @return the created return type holder.
	 * @throws NullPointerException if the given type reference is <code>null</code>.
	 */
	public <T> MethodReturnTypeGenerics<T> withReturnType(final TypeToken<T> type) {
		return MethodReturnTypes.newReturnTypeGenerics(_methodName,type);
	}
	/**
	 * Specifies the parameter types of the method to invoke. This method call is optional if the method to invoke does not take arguments.
	 * 
	 * @param parameterTypes the parameter types of the method to invoke.
	 * @return the created parameter types holder.
	 * @throws NullPointerException if the array of parameter types is <code>null</code>.
	 */
	public MethodParameterTypes<Void> withParameterTypes(final Class<?>... parameterTypes) {
		return MethodParameterTypes.newParameterTypes(_methodName,parameterTypes);
	}
	/**
	 * Creates a new invoker for a method that takes no parameters and return value <code>void</code>.
	 * 
	 * @param target the object containing the method to invoke.
	 * @return the created method invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 */
	public MethodInvoker<Void> in(final Object target) {
		return MethodInvoker.newInvoker(_methodName,target);
	}
///////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////		
	private static void _validateIsNotNullOrEmpty(final String methodName) {
		if (methodName == null || methodName.length() == 0) throw new IllegalArgumentException("The name of the method to access should not be empty");
	}
}