package r01f.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * A servlet that delegates to another servlet overriding the {@link ServletConfig} and {@link ServletContext}
 * getInitParameterNames() and getInitParameter() method in order to be able to externalize the servlet config
 * (set the init parameters outside the web.xml file)
 * 
 * The standard way of setting init parameters of a servlet is to put them at the web.xml file:
 * <pre class='brush:xml'>
 *	<servlet>
 *  	<servlet-name>...</servlet-name>
 *   	<servlet-class>acme.MyServlet</servlet-class>
 *   	<init-param>
 *        	<param-name>myParamName</param-name>
 *        	<param-value>myParamValue</param-value>
 *   	</init-param>
 *	</servlet>
 * </pre>
 * If the init-param section needs to be EXTERNALIZED outside the web.xml file, this {@link SpringDelegatingServletBase}
 * can be used:
 * 1.- Create a subtype of {@link SpringDelegatingServletBase} defining the loadInitProperties() method
 *     (this method gives the opportunity to load the init properties using whatever mechanism)
 *     
 * 2.- Configure the {@link SpringDelegatingServletBase} at the web.xml setting the name of the delegated servlet as
 * 	   the "delegatedServlet" init-param
 * 	   <pre class='brush:xml'>
 *		<servlet>
 *  		<servlet-name>MyServlet</servlet-name>
 *   		<servlet-class>acme.MyDelegatingServletSubType</servlet-class>	<!-- a subtype of DelegatingServletBase -->
 *   		<init-param>
 *        		<param-name>delegatedServletType</param-name>
 *        		<param-value>acme.MyDelegatedServletType</param-value>	<!-- the type of the servlet to which we're delegating to -->
 *   		</init-param>
 *		</servlet>
 * 	   </pre> 
 */
public abstract class SpringDelegatingServletBase { 
//			  extends DelegatingServletBase {
//
//	private static final long serialVersionUID = 4882314249977537567L;
/////////////////////////////////////////////////////////////////////////////////////////
////  
/////////////////////////////////////////////////////////////////////////////////////////
//	@Override
//	HttpServlet getDelegate(final String delegateName) {
//        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
//        return wac.getBean(delegateName, 
//        				   HttpServlet.class);
//	}
}
