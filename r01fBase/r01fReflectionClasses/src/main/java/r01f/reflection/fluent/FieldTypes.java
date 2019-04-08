package r01f.reflection.fluent;

import com.google.common.reflect.TypeToken;


public class FieldTypes {
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////	
	static <T> FieldType<T> newFieldType(final String fieldName,final Class<T> fieldType) {
		if (fieldType == null) throw new NullPointerException("The type of the field to access should not be null");
		return new FieldType<T>(fieldName,fieldType);
	}	
	static <T> FieldTypeGenerics<T> newFieldTypeGenerics(final String name,final TypeToken<T> type) {
		if (type == null) throw new NullPointerException("The type reference of the field to access should not be null");
		return new FieldTypeGenerics<T>(name,type);
	}	
///////////////////////////////////////////////////////////////////////////////
//	FIELDTYPE FOR "NORMAL" USE
///////////////////////////////////////////////////////////////////////////////	
/**
 * Encapsulates a Field type
 * 
 * @param <T> the generic type of the field.
 * 
 * Usage example:
 * <pre>
 * // Retrieves the value of the field "name"
 * String name = Reflection.field("name").ofType(String.class).in(person).get();
 * // Sets the value of the field "name" to "Yoda"
 * Reflection.field("name").ofType(String.class).in(person).set("Yoda");
 * </pre>
 */
public static class FieldType<T> {
	final String _fieldName;
	final Class<T> _fieldType;
	
	FieldType(final String fieldName,final Class<T> fieldType) {
		_fieldName = fieldName;
		_fieldType = fieldType;
	}	
	/**
	 * Returns a new field access invoker, capable of accessing (read/write) the underlying field.
	 * 
	 * @param target the object containing the field of interest.
	 * @return the created field access invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 * @throws ReflectionError if a field with a matching name and type cannot be found.
	 */
	public FieldInvoker<T> in(final Object target) {
		return FieldInvoker.newInvoker(_fieldName,_fieldType,target);
	}
}
///////////////////////////////////////////////////////////////////////////////
//	FIELDTYPE FOR GENERICS USE
///////////////////////////////////////////////////////////////////////////////
/**
 * Encapsulates a Field type suporting generics
 * 
 * @param <T> the generic type of the field.
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the field "powers"
 *   List<String> powers = Reflection.field("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).get}();
 * 
 *   // Sets the value of the field "powers"
 *   List<String> powers = new ArrayList<String>();
 *   powers.add("heal");
 *   Reflection.field("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).set(powers);
 * </pre>
 */
public static class FieldTypeGenerics<T> {
	private final String _fieldName;
	private final TypeToken<T> _fieldType;

	FieldTypeGenerics(final String name,final TypeToken<T> type) {
		_fieldName = name;
		_fieldType = type;
	}		
	/**
	 * Returns a new field invoker. A field invoker is capable of accessing (read/write) the underlying field.
	 * 
	 * @param target the object containing the field of interest.
	 * @return the created field invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 * @throws ReflectionError if a field with a matching name and type cannot be found.
	 */
	public FieldInvoker<T> in(final Object target) {
		return FieldInvoker.newInvoker(_fieldName,_fieldType,target);
	}
}


}
