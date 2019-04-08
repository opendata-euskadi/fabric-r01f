package r01f.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import r01f.reflection.ReflectionUtils.FieldAnnotated;


public class BeanInstanceReflection {
	private Class<?> _beanType;
	private Object _bean;
	
	public BeanInstanceReflection(Class<?> beanType,Object bean) {
		_beanType = beanType;
		_bean = bean;
	}
///////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////
    /**
     * Finds the given method crawling the type hierarchy
     * PROBLEM:    class.getMethods()          returns only PUBLIC methods
     *             class.getDeclaredMethods()  returns all public and non-public methods
     * @param methodName 
     * @return wrapper to invoke the method
     * @throws ReflectionException NoSuchMethodException if method is not found
     */
    public MethodInvokeReflection method(final String methodName) {
    	Method method = ReflectionUtils.method(_beanType,methodName,new Class[] {});
    	return new MethodInvokeReflection(_beanType,_bean,method);
    }		
    /**
     * Finds the given method crawling th type hierarchy
     * PROBLEM:    class.getMethods()          returns only PUBLIC methods
     *             class.getDeclaredMethods()  returns all public and non-public methods
     * @param methodName method name
     * @param paramTypes param types
     * @return wrapper to invoke the method
     * @throws ReflectionException NoSuchMethodException if method is not found
     */
    public MethodInvokeReflection method(final String methodName,Class<?>... paramTypes) {
    	Method method = ReflectionUtils.method(_beanType,methodName,paramTypes);
    	return new MethodInvokeReflection(_beanType,_bean,method);
    }
    /**
     * Gets a wrapper to invoke the given method
     */
    public MethodInvokeReflection method(final Method method) {
    	return new MethodInvokeReflection(_beanType,_bean,method);
    }
    /**
     * Returns the instanced bean
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T instance() {
    	return (T)_bean;
    }
///////////////////////////////////////////////////////////////////////////////
//	FIELDS
///////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a bean's field
     */
    public FieldReflection field(final String fieldName) {
    	Field field = ReflectionUtils.field(_beanType,fieldName,true);
    	return new FieldReflection(_beanType,_bean,field);
    } 
    /**
     * Returns a bean's field
     */
    public FieldReflection field(final Field field) {
    	return new FieldReflection(_beanType,_bean,field);
    }
    /**
     * Returns a bean's field with a given type
     * @param fieldType 
     */
    public FieldReflection[] fieldsOfType(final Class<?> fieldType) {
    	Field[] fields = ReflectionUtils.fieldsOfType(_beanType,fieldType);
    	FieldReflection[] outFields = new FieldReflection[fields.length];
    	int i = 0;
    	for (Field f : fields) {
    		FieldReflection fr = new FieldReflection(_beanType,_bean,f);
    		outFields[i] = fr;
    		i++;
    	}
    	return outFields;
    }
    /**
     * Returns a bean's field annotated with a given annotation
     * @param annotationType 
     */
    @SuppressWarnings("unchecked")
	public FieldAnnotatedReflection<? extends Annotation>[] fieldsAnnotatedWith(final Class<? extends Annotation> annotationType) {
    	FieldAnnotated<? extends Annotation>[] fieldsAnnotated = ReflectionUtils.fieldsAnnotated(_beanType,annotationType);
    	
		FieldAnnotatedReflection<? extends Annotation>[] outFields = null;
    	if (fieldsAnnotated != null && fieldsAnnotated.length > 0) {
    		outFields = new FieldAnnotatedReflection[fieldsAnnotated.length];
    		int i=0;
    		for (FieldAnnotated<? extends Annotation> fieldAnnotated : fieldsAnnotated) {
    			FieldReflection fr = new FieldReflection(_beanType,_bean,fieldAnnotated.getField());
    			Annotation an = fieldAnnotated.getAnnotation();
    			outFields[i] = new FieldAnnotatedReflection<Annotation>(fr,an);
    			i++;
    		}
    	}
    	return outFields;
    }
}
