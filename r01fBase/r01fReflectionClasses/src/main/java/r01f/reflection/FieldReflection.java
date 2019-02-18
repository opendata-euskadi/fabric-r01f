package r01f.reflection;

import java.lang.reflect.Field;


public class FieldReflection {
	private Class<?> _beanType;
	private Object _bean;
	private Field _field;
	private boolean _useAccessors = true;
	
	public FieldReflection(Class<?> beanType,Object bean,Field field) {
		_beanType = beanType;
		_bean = bean;
		_field = field;
	}
///////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the field value
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public <T> T get() {
		T outFieldValue = null;
		if (_bean != null) {
			outFieldValue = (T)ReflectionUtils.fieldValue(_bean,_field,_useAccessors);
		} else {
			// it's an static field
			outFieldValue = (T)ReflectionUtils.getStaticFieldValue(_beanType,_field.getName());
		}
		return outFieldValue;
	}
	/**
	 * Sets the field value
	 * @param newValue the field value
	 */
	public <T> void set(final T newValue) {
		if (_bean != null) {
			ReflectionUtils.setFieldValue(_bean,_field,newValue,_useAccessors);
		} else {
			// it's an static field
			ReflectionUtils.setStaticFieldValue(_beanType,_field.getName(),newValue);
		}
	}
	/**
	 * Sets that the getter / setter accessors are used
	 */
	public FieldReflection usingAccessors()  {
		_useAccessors = true;
		return this;
	}
	/**
	 * Sets that the getter / setter accessors are NOT used
	 */
	public FieldReflection withoutUsingAccessors()  {
		_useAccessors = false;
		return this;
	}	

}
