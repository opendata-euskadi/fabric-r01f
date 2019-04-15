package r01f.servlet;

import javax.servlet.ServletContext;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.reflection.types.TypeInstanceFromString;
import r01f.util.types.Strings;

/**
 * Utility to interact with the {@link ServletContext}
 */
@Slf4j
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class ServletContextHelper {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final ServletContext _servletContext;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServletContextHelper create(final ServletContext servletContext) {
		return new ServletContextHelper(servletContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INIT PARAMETERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a ServletContext init parameter configured at web.xml file as
	 * <pre class='brush:java'>
	 *       <init-param>
	 *           <param-name>paramName</param-name>
	 *           <param-value>parmaValue</param-value>
	 *       </init-param>
	 * </pre>
	 * @param paramName
	 * @return
	 */
	public String getInitParam(final String paramName) {
		if (_servletContext == null) throw new IllegalStateException("ServletContext cannot be null!!");
		String outParamValue = _servletContext.getInitParameter(paramName);
		if (outParamValue == null) log.warn("{} servlet context is NOT defined at web.xml file",paramName);
		return outParamValue;
	}
	/**
	 * Gets a ServletContext init parameter configured at web.xml file as
	 * <pre class='brush:java'>
	 *       <init-param>
	 *           <param-name>paramName</param-name>
	 *           <param-value>parmaValue</param-value>
	 *       </init-param>
	 * </pre>
	 * @param paramName
	 * @param paramType
	 * @return
	 */
	public <T> T getInitParam(final String paramName,
							  final Class<T> paramType) {
		String paramStr = this.getInitParam(paramName);
		T outParam = TypeInstanceFromString.instanceFrom(paramStr,
												   		 paramType);
		return outParam;																			
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONTEXT ATTRIBUTES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retrieves an object from the {@link ServletContext}
	 * @param attrName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(final String attrName) {
		Object outObj = _servletContext.getAttribute(attrName);
		return (T)outObj;
	}
	/**
	 * Retrieves an object from the {@link ServletContext} or throws an {@link IllegalStateException} if
	 * the attribute is NOT available
	 * @param attrName
	 * @return
	 */
	public <T> T getAttributeOrThrow(final String attrName) {
		T outObj = this.<T>getAttribute(attrName);
		if (outObj == null) throw new IllegalStateException(Strings.customized("The attribute {} is NOT present at {} servlet context",
																			   attrName,_servletContext.getContextPath()));
		return outObj;
	}
	/**
	 * Sets an attribute at the {@link ServletContext} returning the previous
	 * value if there was one
	 * @param attrName
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(final String attrName,
							  final T value) {
		Object prevValue = this.getAttribute(attrName);
		_servletContext.setAttribute(attrName,
									 value);
		return (T)prevValue;
	}
	/**
	 * Sets a {@link ServletContext} attribute if it's not already present
	 * @param attrName
	 * @param value
	 */
	public <T> void setAttributeIfDidNotExists(final String attrName,
									   	  	   final T value) {
		T existingValue = this.<T>getAttribute(attrName);
		if (existingValue == null) _servletContext.setAttribute(attrName,
									     						value);
	}
	/**
	 * Returns true if the attribute exists at the {@link ServletContext}
	 * @param attrName
	 * @return
	 */
	public boolean existsAttribute(final String attrName) {
		return this.getAttribute(attrName) != null;
	}
	/**
	 * Removes a {@link ServletContext} attribute and returns the existing value (if already exists)
	 * @param attrName
	 * @return
	 */
	public <T> T removeAttribute(final String attrName) {
		T prevValue = this.<T>getAttribute(attrName);
		_servletContext.removeAttribute(attrName);
		return prevValue;
	}
}
