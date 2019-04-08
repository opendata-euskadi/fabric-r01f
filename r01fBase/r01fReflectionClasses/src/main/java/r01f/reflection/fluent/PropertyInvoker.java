package r01f.reflection.fluent;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import com.google.common.reflect.TypeToken;

import r01f.reflection.ReflectionException;

/**
 * Reflection to access a property from a JavaBean.
 * 
 * @param <T> the declared type for the property to access.
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the property "name"
 *   String name = Reflection.property("name").ofType(String.class).in(person).get();
 * 
 *   // Sets the value of the property "name" to "Yoda"
 *   Reflection.property("name").ofType(String.class).in(person).set("Yoda");
 * 
 *   // Retrieves the value of the static property "count"
 *   int count = Reflection.staticField("count").ofType(int.class).in(Person.class).get();
 * 
 *   // Sets the value of the static property "count" to 3
 *   Reflection.property("count").ofType(int.class).in(Person.class).set(3);
 * </pre>
 */

public final class PropertyInvoker<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Object _targetObj;
	private final PropertyDescriptor _propertyDescriptor;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private PropertyInvoker(final Object target,final PropertyDescriptor descriptor) {
		_targetObj = target;
		_propertyDescriptor = descriptor;
	}
///////////////////////////////////////////////////////////////////////////////
//	INTERFAZ PUBLICA
///////////////////////////////////////////////////////////////////////////////		
	static <T> PropertyInvoker<T> newInvoker(final String propertyName,final TypeToken<T> expectedType,final Object target) {
		return _createInvoker(propertyName,expectedType.getRawType(),target);
	}
	static <T> PropertyInvoker<T> newInvoker(final String propertyName,final Class<T> expectedType,final Object target) {
		return _createInvoker(propertyName,expectedType,target);
	}
	/**
	 * Sets a value in the property managed by this class.
	 * 
	 * @param value the value to set.
	 * @throws ReflectionError if the given value cannot be set.
	 */
	public void set(final T value) {
		try {
			_propertyDescriptor.getWriteMethod().invoke(_targetObj,value);
		} catch (Exception ex) {
			throw ReflectionException.of(ex);
		}
	}
	/**
	 * Returns the value of the property managed by this class.
	 * 
	 * @return the value of the property managed by this class.
	 * @throws ReflectionError if the value of the property cannot be retrieved.
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		try {
			return (T) _propertyDescriptor.getReadMethod().invoke(_targetObj);
		} catch (Exception ex) {
			throw ReflectionException.of(ex);
		}
	}
	/**
	 * Returns the "real" property managed by this class.
	 * 
	 * @return the "real" property managed by this class.
	 */
	public PropertyDescriptor info() {
		return _propertyDescriptor;
	}
///////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////		
	private static <T> PropertyInvoker<T> _createInvoker(final String name,final Class<?> expectedType,final Object target) {
		PropertyDescriptor descriptor = _descriptorForProperty(name,target);
		_verifyCorrectType(name,target,expectedType,descriptor);
		return new PropertyInvoker<T>(target,descriptor);
	}
	private static void _verifyCorrectType(final String propertyName,final Object target,final Class<?> expectedType,final PropertyDescriptor descriptor) {
		Class<?> actualType = descriptor.getPropertyType();
		if (!expectedType.isAssignableFrom(actualType)) throw _incorrectPropertyType(propertyName,target,actualType,expectedType);
	}	
	private static PropertyDescriptor _descriptorForProperty(final String propertyName,final Object target) {
		BeanInfo beanInfo = null;
		Class<?> type = target.getClass();
		try {
			beanInfo = Introspector.getBeanInfo(type,Object.class);
		} catch (IntrospectionException ex) {
			throw ReflectionException.of(ex);
		}
		for (PropertyDescriptor d : beanInfo.getPropertyDescriptors()) {
			if (propertyName.equals(d.getName())) return d;
		}
		throw ReflectionException.noFieldException(target.getClass(),propertyName);
	}	
	private static IllegalArgumentException _incorrectPropertyType(final String name,final Object target,final Class<?> actual,final Class<?> expected) {
		String typeName = target.getClass().getName();
		throw new IllegalArgumentException("The type of the property '" + name + " in " + typeName + " should be <" + expected.getName() + "> but was <" + actual.getName() + ">");
	}	
}
