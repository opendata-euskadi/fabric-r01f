package r01f.reflection.fluent;

import com.google.common.reflect.TypeToken;

import r01f.reflection.fluent.PropertyTypes.PropertyType;
import r01f.reflection.fluent.PropertyTypes.PropertyTypeGenerics;

/**
 * Bean Introspection
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the property "name"
 *   String name = Reflection.property("name").ofType(String.class).in(person).get();
 * 
 *   // Sets the value of the property "name" to "Yoda"
 *   Reflection.property("name").ofType(String.class).in(person).set("Yoda");
 * 
 *   // Retrieves the value of the property "powers"
 *   List<String> powers = Reflection.property("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).get();
 * 
 *   // Sets the value of the property "powers"
 *   List<String> powers = new ArrayList<String>();
 *   powers.add("heal");
 *   Reflection.property("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).set(powers);
 * </pre>
 */

public final class PropertyReflection {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final String _propertyName;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private PropertyReflection(final String name) {
		_propertyName = name;
	}
///////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////		
	/**
	 * Creates a new <code>{@link PropertyReflection}</code>: the starting point of the fluent interface for accessing properties using Bean Introspection.
	 * 
	 * @param propertyName the name of the property to access using Bean Introspection.
	 * @return the created <code>PropertyName</code>.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	static PropertyReflection startPropertyAccess(final String propertyName) {
		validateIsNotNullOrEmpty(propertyName);
		return new PropertyReflection(propertyName);
	}
	/**
	 * Sets the type of the property to access.
	 * 
	 * @param <T> the generic type of the property type.
	 * @param type the type of the property to access.
	 * @return a recipient for the property type.
	 * @throws NullPointerException if the given type is <code>null</code>.
	 */
	public <T> PropertyType<T> ofType(final Class<T> type) {
		return PropertyTypes.newPropertyType(_propertyName,type);
	}
	/**
	 * Sets the type reference of the property to access. 
	 * This method reduces casting when the type of the property to access uses generics.
	 * 
	 * Usage example:
	 * <pre>
	 * List<String> powers = Reflection.property("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).get();
	 * </pre>
	 *  
	 * @param <T> the generic type of the property type.
	 * @param type the type of the property to access.
	 * @return a recipient for the property type.
	 * @throws NullPointerException if the given type reference is <code>null</code>.
	 */
	public <T> PropertyTypeGenerics<T> ofType(final TypeToken<T> type) {
		return PropertyTypes.newPropertyTypeGenerics(_propertyName,type);
	}
///////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////	
	private static void validateIsNotNullOrEmpty(final String name) {
		if (name == null || name.length() == 0) throw new IllegalArgumentException("The name of the property to access should not be empty");
	}	
}