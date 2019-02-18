package r01f.reflection.fluent;

import java.lang.reflect.Field;

import com.google.common.reflect.TypeToken;

import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;


/**
 * Object's field access using reflection
 * 
 * @param <T> the declared type for the field to access.
 * 
 * Usage example:
 * <pre>
 *   // Retrieves the value of the field "name"
 *   String name = Reflection.field("name").ofType(String.class).in(person).get();
 * 
 *   // Sets the value of the field "name" to "Yoda"
 *   Reflection.field("name").ofType(String.class).in(person).set("Yoda");
 * 
 *   // Retrieves the value of the static field "count"
 *   int count = Reflection.staticField("count").ofType(int.class).in(Person.class).get();
 * 
 *   // Sets the value of the static field "count" to 3
 *   Reflection.staticField("count").ofType(int.class).in(Person.class).set(3);
 * </pre>
 */

public final class FieldInvoker<T> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Object _targetObj;
	private final Field _field;
	private final boolean _accessible;
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private FieldInvoker(final Object target,final Field field) {
		_targetObj = target;
		_field = field;
		_accessible = field.isAccessible();
	}
///////////////////////////////////////////////////////////////////////////////
//	FLUENT API FACTORY
///////////////////////////////////////////////////////////////////////////////		
	static <T> FieldInvoker<T> newInvoker(final String fieldName,final Class<T> expectedType,final Object target) {
		return _createInvoker(fieldName,expectedType,target);
	}	
	static <T> FieldInvoker<T> newInvoker(final String fieldName,final TypeToken<T> expectedType,final Object target) {
		return _createInvoker(fieldName,expectedType.getRawType(),target);
	}	
///////////////////////////////////////////////////////////////////////////////
//	INTERFAZ PUBLICA
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets a value in the field managed by this class.
	 * 
	 * @param value the value to set.
	 * @throws ReflectionError if the given value cannot be set.
	 */
	public void set(final T value) {
		try {
			ReflectionUtils.setAccessible(_field,true);
			_field.set(_targetObj,value);
		} catch (Exception ex) {
			throw ReflectionException.of(ex);
		} finally {
			ReflectionUtils.setAccessibleIgnoringExceptions(_field,_accessible);
		}
	}
	/**
	 * Returns the value of the field managed by this class.
	 * 
	 * @return the value of the field managed by this class.
	 * @throws ReflectionError if the value of the field cannot be retrieved.
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		try {
			ReflectionUtils.setAccessible(_field,true);
			T outFieldValue = (T)_field.get(_targetObj);
			return outFieldValue;
		} catch (Exception ex) {
			throw ReflectionException.of(ex);
		} finally {
			ReflectionUtils.setAccessibleIgnoringExceptions(_field,_accessible);
		}
	}
	/**
	 * Returns the "real" field managed by this class.
	 * 
	 * @return the "real" field managed by this class.
	 */
	public Field getField() {
		return _field;
	}	
///////////////////////////////////////////////////////////////////////////////
//	INTERFAZ PRIVADA
///////////////////////////////////////////////////////////////////////////////
	private static <T> FieldInvoker<T> _createInvoker(final String fieldName,final Class<?> expectedType,final Object target) {
		if (target == null) throw new NullPointerException("Target should not be null");
		Field field = _lookupInClassHierarchy(fieldName,_typeOf(target));
		_makeAccessible(field);
		_verifyCorrectType(field,expectedType);
		return new FieldInvoker<T>(target,field);
	}
	private static Class<?> _typeOf(final Object target) {
		if (target instanceof Class<?>) return (Class<?>) target;
		return target.getClass();
	}
	private static Field _lookupInClassHierarchy(final String fieldName,final Class<?> declaringType) {
		Field field = null;
		Class<?> target = declaringType;
		while (target != null) {
			field = _field(fieldName,target);
			if (field != null) break;
			target = target.getSuperclass();
		}
		if (field != null) return field;
		throw ReflectionException.noFieldException(declaringType,fieldName);
	}
	private static void _makeAccessible(final Field field) {
		boolean isAccessible = field.isAccessible();
		try {
			ReflectionUtils.makeAccessible(field);
		} finally {
			ReflectionUtils.setAccessibleIgnoringExceptions(field,isAccessible);
		}		
	}
	private static void _verifyCorrectType(final Field field,final Class<?> expectedType) {
		if (expectedType == null) return;	// es el caso en el que el tipo esperado nose conoce;
		Class<?> actualType = field.getType();
		if (!expectedType.isAssignableFrom(actualType)) throw _incorrectFieldType(field,actualType,expectedType);
	}
	private static Field _field(String fieldName, Class<?> declaringType) {
		try {
			return declaringType.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}
	private static IllegalArgumentException _incorrectFieldType(final Field field,final Class<?> actual,final Class<?> expected) {
		String fieldTypeName = field.getDeclaringClass().getName();
		throw new IllegalArgumentException("The type of the field " + field.getName() + 
					     				   " in " + fieldTypeName + " should be <" + expected.getName() + "> but was <" + actual.getName() + ">");
	}
}
