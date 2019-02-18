package r01f.reflection.fluent;

import static r01f.reflection.fluent.FieldInvoker.newInvoker;

import com.google.common.reflect.TypeToken;


public class StaticFieldTypes {
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////	
	static <T> StaticFieldType<T> newFieldType(final String fieldName,final Class<T> fieldType) {
		if (fieldType == null) throw new NullPointerException("The type of the static field to access should not be null");
		return new StaticFieldType<T>(fieldName,fieldType);
	}
	static <T> StaticFieldTypeGenerics<T> newFieldTypeGenerics(final String fieldName,final TypeToken<T> fieldType) {
		if (fieldType == null)
			throw new NullPointerException("The type reference of the static field to access should not be null");
		return new StaticFieldTypeGenerics<T>(fieldName,fieldType);
	}	
///////////////////////////////////////////////////////////////////////////////
//	STATICFIELDTYPE FOR "NORMAL" USE
///////////////////////////////////////////////////////////////////////////////	
/**
 * Static field to access using Java Reflection.
 * 
 * @param <T> the generic type of the field.
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the static field "count"
 *   int count = Reflection.staticField("count").ofType(int.class).in(Person.class).get();
 * 
 *   // Sets the value of the static field "count" to 3
 *   Reflection.staticField("count").ofType(int.class).in(Person.class).set(3);
 * </pre>
 */
public static class StaticFieldType<T> {
	private final String _fieldName;
	private final Class<T> _fieldType;	

	StaticFieldType(final String fieldName,final Class<T> fieldType) {
		_fieldName = fieldName;
		_fieldType = fieldType;
	}	
	/**
	 * Returns a new field invoker. A field invoker is capable of accessing (read/write) the underlying field.
	 * 
	 * @param target the type containing the static field of interest.
	 * @return the created field invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 * @throws ReflectionError if a static field with a matching name and type cannot be found.
	 */
	public FieldInvoker<T> in(final Class<?> target) {
		return newInvoker(_fieldName,_fieldType,target);
	}
}
///////////////////////////////////////////////////////////////////////////////
//	STATICFIELDTYPE FOR GENERICS USE
///////////////////////////////////////////////////////////////////////////////
/**
 * Static field to access using Java Reflection.
 * 
 * @param <T> the generic type of the field.
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the static field "commonPowers"
 *   List<String> commmonPowers = Reflection.staticField("commonPowers").ofType(new TypeRef<List<String>>() {}).in(Jedi.class).get();
 * 
 *   // Sets the value of the static field "commonPowers"
 *   List&lt;String&gt; commonPowers = new ArrayList&lt;String&gt;();
 *   commonPowers.add("jump");
 *   Reflection.staticField("commonPowers").ofType(new TypeRef<List<String>>() {}).in(Jedi.class).set(commonPowers);
 * </pre>
 */
public static class StaticFieldTypeGenerics<T> {
	private final String _fieldName;
	private final TypeToken<T> _fieldType;	
	
	StaticFieldTypeGenerics(final String fieldName,final TypeToken<T> fieldType) {
		_fieldName = fieldName;
		_fieldType = fieldType;
	}	
	/**
	 * Returns a new field invoker. A field invoker is capable of accessing (read/write) the underlying field.
	 * 
	 * @param target the type containing the static field of interest.
	 * @return the created field invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 * @throws ReflectionError if a static field with a matching name and type cannot be found.
	 */
	public FieldInvoker<T> in(final Class<?> target) {
		return newInvoker(_fieldName,_fieldType,target);
	}
}
}
