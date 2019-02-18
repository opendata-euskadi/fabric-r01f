package r01f.reflection.fluent;

import static r01f.reflection.fluent.PropertyInvoker.newInvoker;

import com.google.common.reflect.TypeToken;


public class PropertyTypes {
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////		
	static <T> PropertyType<T> newPropertyType(final String name,final Class<T> type) {
		if (type == null)
			throw new NullPointerException("The type of the property to access should not be null");
		return new PropertyType<T>(name,type);
	}
	static <T> PropertyTypeGenerics<T> newPropertyTypeGenerics(final String name,final TypeToken<T> type) {
		if (type == null) throw new NullPointerException("The type reference of the property to access should not be null");
		return new PropertyTypeGenerics<T>(name,type);
	}
///////////////////////////////////////////////////////////////////////////////
//	PROPERTYTYPE FOR "NORMAL" USE
///////////////////////////////////////////////////////////////////////////////		
/**
 * Understands the type of a property to access using Bean Instrospection.
 * 
 * @param <T> the generic type of the property.
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the property "name"
 *   String name = Reflection.property("name").ofType(String.class).in(person).get();
 * 
 *   // Sets the value of the property "name" to "Yoda"
 *   Reflection.property("name").ofType(String.class).in(person).set("Yoda");
 * </pre>
 */
public static class PropertyType<T> {
	private final String _propertyName;
	private final Class<T> _propertyType;

	PropertyType(final String propertyName,final Class<T> type) {
		_propertyName = propertyName;
		_propertyType = type;
	}
	/**
	 * Returns a new property invoker. A property invoker is capable of accessing (read/write) the underlying property.
	 * 
	 * @param target the object containing the property of interest.
	 * @return the created property invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 * @throws ReflectionError if a property with a matching name and type cannot be found.
	 */
	public PropertyInvoker<T> in(final Object target) {
		if (target == null) throw new NullPointerException("Target should not be null");
		return newInvoker(_propertyName,_propertyType,target);
	}
}
///////////////////////////////////////////////////////////////////////////////
//	PROPERTYTYPE FOR GENERICS USE
///////////////////////////////////////////////////////////////////////////////	
/**
 * Understands the type of a property to access using Bean Instrospection. This implementation supports Java generics.
 * <p>
 * The following is an example of proper usage of this class:
 * 
 * <pre>
 *   // Retrieves the value of the property "powers"
 *   List<String> powers = Reflection.property("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).get();
 * 
 *   // Sets the value of the property "powers"
 *   List<String> powers = new ArrayList<String>();
 *   powers.add("heal");
 *   Reflection.property("powers").ofType(new TypeRef<List<String>>() {}).in(jedi).set(powers);
 * </pre>
 * 
 * </p>
 * 
 * @param <T> the generic type of the property.
 */
public static class PropertyTypeGenerics<T> {
	private final String _propertyName;	
	private final TypeToken<T> _propertyType;

	PropertyTypeGenerics(final String name,final TypeToken<T> type) {
		_propertyName = name;
		_propertyType = type;
	}
	/**
	 * Returns a new property invoker. A property invoker is capable of accessing (read/write) the underlying property.
	 * 
	 * @param target the object containing the property of interest.
	 * @return the created property invoker.
	 * @throws NullPointerException if the given target is <code>null</code>.
	 * @throws ReflectionError if a property with a matching name and type cannot be found.
	 */
	public PropertyInvoker<T> in(final Object target) {
		return newInvoker(_propertyName,_propertyType,target);
	}
	
}
}
