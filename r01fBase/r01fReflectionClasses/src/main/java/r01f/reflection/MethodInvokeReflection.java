package r01f.reflection;

import java.lang.reflect.Method;
 

public class MethodInvokeReflection {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Class<?> _beanType;
	private final Object _bean;
	private final Method _method;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public MethodInvokeReflection(final Class<?> beanType,final Object bean,final Method method) {
		_beanType = beanType;
		_bean = bean;
		_method = method;
	}
///////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Invokes a method on an object
	 * @return the invocation-returned object
	 */
	@SuppressWarnings("unchecked")
	public <T> T invoke() {
		return (T)this.invoke(new Object[] {});
	}
    /**
     * Invokes a method on an object
     * @param argValues method arguments
     * @return the invocation-returned object
     */
	@SuppressWarnings("unchecked")
    public <T> T invoke(final Object... argValues) {
		T outValue = null;
    	if (_bean != null) {
    		// Instance method
	    	Object retValue = ReflectionUtils.invokeMethod(_bean,_method,argValues);
	    	outValue = (T)retValue;
    	} else {
    		// static method
    		Object retValue = ReflectionUtils.invokeStaticMethod(_beanType,_method,argValues);
    		outValue = (T)retValue;
    	}
    	return outValue;
    }
}
