package r01f.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class BeanReflection {
	private Class<?> _beanType;
	private Class<?>[] _constructorArgsTypes;	
	
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor from the type
	 * @param type
	 */
	public BeanReflection(final Class<?> type) {
		_beanType = type;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the bean type
	 */
	public Class<?> getType() {
		return _beanType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OBJECT BUILDING
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Sets the constructor to be used
     * @param constructorArgsTypes 
     */
    public BeanReflection withConstructor(final Class<?>... constructorArgsTypes) {
    	_constructorArgsTypes = constructorArgsTypes;
    	return this;
    }  
    /**
     * Gets an instance of the type using it's fully qualified name (including package)
     * @return a wrapper type to access the type methods
     */
    public BeanInstanceReflection load(final Object... constructorArgs) {
    	Class<?>[] theConstructorArgsTypes = null;
    	Object[] theConstructorArgs = null;
    	if (_constructorArgsTypes == null || _constructorArgsTypes.length == 0) {
    		if (constructorArgs != null && constructorArgs.length > 0) throw new IllegalArgumentException("load method was called with constructor argument types " + _constructorArgsTypes + ", BUT no argument was received!'");
    		theConstructorArgsTypes = new Class<?>[0];
    		theConstructorArgs = new Object[0];
    	} else {
    		theConstructorArgsTypes = _constructorArgsTypes;
    		theConstructorArgs = constructorArgs;
    	}
    	Object theBean = null;
    	try {
    		theBean = ReflectionUtils.createInstanceOf(_beanType,
    											 	   theConstructorArgsTypes,theConstructorArgs,
    											 	   true);
    	} catch (ReflectionException refEx) {    	
    		if (constructorArgs != null && constructorArgs.length > 0 && refEx.isNoMethodException()) {
	        	// The constructor arguments are guessed by identifying the type final fields, BUT the following case can occur:
    			//		@RequiredArgsConstructor
    			//		public abstract class MyTypeBase() {
    			//			private final String myField;
    			//		}
    			//		public class MyType extends MyTypeBase() {		<-- THIS type does NOT have a constructor with the final fields
    			//			public MyType() {
    			//				super("a");	<-- the final field ALWAYS is set at the constructor
    			//			}
    			//		}
	        	// On this case, the default constructor should be used
        		theBean = ReflectionUtils.createInstanceOf(_beanType,null,null,true);
    		} else {
    			throw refEx;
    		}
    	}
    	return new BeanInstanceReflection(_beanType,theBean);    	
    } 
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Gets an static method
     * @param methodName name of the method
     * @param paramTypes method parameter types
     * @return a wrapper type to invoke the static method
     */
    public MethodInvokeReflection staticMethod(final String methodName,Class<?>... paramTypes) {
    	Method method = ReflectionUtils.method(_beanType,methodName,paramTypes);
    	return new MethodInvokeReflection(_beanType,null,method);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC FIELDS
/////////////////////////////////////////////////////////////////////////////////////////     
    /**
     * Gets an static field
     * @param fieldName the name of the field
     * @return the field
     */
    public FieldReflection staticField(final String fieldName) {
    	Field field = ReflectionUtils.field(_beanType,fieldName,true);
    	return new FieldReflection(_beanType,null,field);
    }
}
