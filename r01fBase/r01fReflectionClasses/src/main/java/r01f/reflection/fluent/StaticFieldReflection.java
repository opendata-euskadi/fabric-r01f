package r01f.reflection.fluent;

import com.google.common.reflect.TypeToken;

import r01f.reflection.fluent.StaticFieldTypes.StaticFieldType;
import r01f.reflection.fluent.StaticFieldTypes.StaticFieldTypeGenerics;

/**
 * Static field reflection
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the static field "count"
 *   int count = Reflection.staticField("count").ofType(int.class).in(Person.class).get();
 * 
 *   // Sets the value of the static field "count" to 3
 *   Reflection.staticField("count").ofType(int.class).in(Person.class).set(3);
 * 
 *   // Retrieves the value of the static field "commonPowers"
 *   List<String> commmonPowers = Reflection.staticField("commonPowers").ofType(new TypeRef<List<String>>() {}).in(Jedi.class).get();
 * 
 *   // Sets the value of the static field "commonPowers"
 *   List<String> commonPowers = new ArrayList<String>();
 *   commonPowers.add("jump");
 *   Reflection.staticField("commonPowers").ofType(new TypeRef<List<String>>() {}).in(Jedi.class).set(commonPowers);
 * </pre>
 */

public final class StaticFieldReflection {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final String _fieldName;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private StaticFieldReflection(final String fieldName) {
		_fieldName = fieldName;
	}	
///////////////////////////////////////////////////////////////////////////////
// 	FLUENT-API
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new <code>{@link StaticFieldReflection}</code>: the starting point of the fluent interface for accessing static fields using Java Reflection.
	 * 
	 * @param fieldName the name of the field to access using Java Reflection.
	 * @return the created <code>StaticFieldName</code>.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	static StaticFieldReflection startStaticFieldAccess(final String fieldName) {
		_validateIsNotNullOrEmpty(fieldName);
		return new StaticFieldReflection(fieldName);
	}
///////////////////////////////////////////////////////////////////////////////
//	
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the type of the field to access.
	 * 
	 * @param <T> the generic type of the field type.
	 * @param type the type of the field to access.
	 * @return a recipient for the field type.
	 * @throws NullPointerException if the given type is <code>null</code>.
	 */
	public <T> StaticFieldType<T> ofType(final Class<T> type) {
		return StaticFieldTypes.newFieldType(_fieldName,type);
	}
	/**
	 * Sets the type reference of the field to access. 
	 * This method reduces casting when the type of the field to access uses generics.
	 * 
	 * Usage example:
	 * <pre>
	 *   List<String> commmonPowers = Reflection.staticField("commonPowers").ofType(new TypeRef<List<String>>() {}).in(Jedi.class).get();
	 * </pre> 
	 * 
	 * @param <T> the generic type of the field type.
	 * @param type the type of the field to access.
	 * @return a recipient for the field type.
	 * @throws NullPointerException if the given type reference is <code>null</code>.
	 */
	public <T> StaticFieldTypeGenerics<T> ofType(final TypeToken<T> type) {
		return StaticFieldTypes.newFieldTypeGenerics(_fieldName,type);
	}	
///////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////	
	private static void _validateIsNotNullOrEmpty(final String fieldName) {
		if (fieldName == null || fieldName.length() == 0) throw new IllegalArgumentException("The name of the static field to access should not be empty");
	}
}