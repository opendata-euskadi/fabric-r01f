package r01f.reflection.fluent;

import com.google.common.reflect.TypeToken;

import r01f.reflection.fluent.FieldTypes.FieldType;
import r01f.reflection.fluent.FieldTypes.FieldTypeGenerics;

/**
 * Reflection for a class Field
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the field "name"
 *   String name = Reflection.field("name").ofType(String.class).in(person).get}();
 * 
 *   // Sets the value of the field "name" to "Yoda"
 *   Reflection.field("name").ofType(String.class).in(person).set("Yoda");
 * 
 *   // Retrieves the value of the field "powers"
 *   List<String> powers = Reflection.field("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).get();
 * 
 *   // Sets the value of the field "powers"
 *   List<String> powers = new ArrayList<String>
 *   powers.add("heal");
 *   Reflection.field("powers").ofType(new TypeRef <List<String>>() {}).in(jedi).set(powers);
 * </pre>
 */

public final class FieldReflection {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final String _fieldName;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private FieldReflection(final String fieldName) {
		_fieldName = fieldName;
	}
///////////////////////////////////////////////////////////////////////////////
// 	FLUENT-API
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new <code>{@link FieldReflection}</code>: the starting point of the fluent interface for accessing fields using Java Reflection.
	 * 
	 * @param fieldName the name of the field to access using Java Reflection.
	 * @return the created <code>FieldName</code>.
	 * @throws NullPointerException if the given name is <code>null</code>.
	 * @throws IllegalArgumentException if the given name is empty.
	 */
	static FieldReflection startFieldAccess(final String fieldName) {
		_validateIsNotNullOrEmpty(fieldName);
		return new FieldReflection(fieldName);
	}
///////////////////////////////////////////////////////////////////////////////
//	INTERFAZ PUBLICA
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the type of the field to access.
	 * 
	 * @param <T> the generic type of the field type.
	 * @param type the type of the field to access.
	 * @return a recipient for the field type.
	 * @throws NullPointerException if the given type is <code>null</code>.
	 */
	public <T> FieldType<T> ofType(final Class<T> type) {
		return FieldTypes.newFieldType(_fieldName,type);
	}
	/**
	 * Sets the type reference of the field to access. 
	 * This method reduces casting when the type of the field to access uses generics.
	 * For example: 
	 * <pre>
	 *   List<String> powers = Reflection.field("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).get}();
	 * </pre>
	 * 
	 * @param <T> the generic type of the field type.
	 * @param type the type of the field to access.
	 * @return a recipient for the field type.
	 * @throws NullPointerException if the given type reference is <code>null</code>.
	 */
	public <T> FieldTypeGenerics<T> ofType(final TypeToken<T> type) {
		return FieldTypes.newFieldTypeGenerics(_fieldName,type);
	}
///////////////////////////////////////////////////////////////////////////////
//	METODOS PRIVADOS
///////////////////////////////////////////////////////////////////////////////	
	private static void _validateIsNotNullOrEmpty(final String fieldName) {
		if (fieldName == null || fieldName.length() == 0) throw new IllegalArgumentException("The name of the field to access should not be empty");
	}	
}