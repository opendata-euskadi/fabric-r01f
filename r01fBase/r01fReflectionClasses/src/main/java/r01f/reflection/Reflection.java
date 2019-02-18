package r01f.reflection;

/**
 * Fluent API
 */

public class Reflection {
	/**
     * Gets a type wrapper from the type
     * @param typeName
     * @return a wrapper type that provides access to the type
	 */
	public static BeanReflection wrap(final Class<?> type) {
		return new BeanReflection(type);
	}
    /**
     * Gets a type wrapper from it's fully qualified name
     * @param typeName
     * @return a wrapper type that provides access to the type
     */
    public static BeanReflection of(final String typeName) {
        Class<?> type = ReflectionUtils.typeFromClassName(typeName);
        return new BeanReflection(type);
    }	
    /**
     * Gets a type wrapper from an object instance
     * @param typeName
     * @return a wrapper type that provides access to the type
     */
    public static BeanInstanceReflection of(final Object bean) {
    	BeanInstanceReflection beanInstanceRef = new BeanInstanceReflection(bean.getClass(),bean);
    	return beanInstanceRef;
    }
}
