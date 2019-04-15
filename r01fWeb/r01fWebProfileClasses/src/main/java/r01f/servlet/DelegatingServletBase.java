package r01f.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import lombok.experimental.Accessors;


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
 * If the init-param section needs to be EXTERNALIZED outside the web.xml file, this {@link DelegatingServletBase}
 * can be used:
 * 1.- Create a subtype of {@link DelegatingServletBase} defining the loadInitProperties() method
 *     (this method gives the opportunity to load the init properties using whatever mechanism)
 *     
 * 2.- Configure the {@link DelegatingServletBase} at the web.xml setting the name of the delegated servlet as
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
@Accessors(prefix="_")
abstract class DelegatingServletBase {
//       extends HttpServlet { 
//	
//	private static final long serialVersionUID = -6291550568810808899L;
//
/////////////////////////////////////////////////////////////////////////////////////////
////  
/////////////////////////////////////////////////////////////////////////////////////////
//    private HttpServlet _delegate;
//    private Properties _initParams;
/////////////////////////////////////////////////////////////////////////////////////////
////  
/////////////////////////////////////////////////////////////////////////////////////////
//    /**
//     * Creates an instance of the delegated servlet
//     * If Spring is in use, the servlet type can be instanced using spring
//     * If Spring is NOT in use, the servlet type can be instanced using normal reflection
//     * @param delegateName
//     * @return
//     */
//    abstract HttpServlet getDelegate(String delegateName); 
//    /**
//     * Loads the init params using whatever method
//     * @return
//     */
//    public abstract Properties getInitParams();
/////////////////////////////////////////////////////////////////////////////////////////
////  
/////////////////////////////////////////////////////////////////////////////////////////
//    @Override
//    public void init(final ServletConfig config) throws ServletException {
//        super.init(config);
//        
//        String delegateName = config.getInitParameter("delegatedServletType");
//        _delegate = this.getDelegate(delegateName);
//        _delegate.init(new ServletConfigWrapper(config));
//    }
//
//    @Override
//    public void destroy() {
//        _delegate.destroy();
//    }
//    @Override
//    public void service(final ServletRequest req, 
//    					final ServletResponse res) throws ServletException,
//    													  IOException {
//        _delegate.service(req, res);
//    }
/////////////////////////////////////////////////////////////////////////////////////////
////  
/////////////////////////////////////////////////////////////////////////////////////////
//    private class ServletConfigWrapper
//       implements ServletConfig,
//       			  ServletContext {	// ServletContext is also overridden because it also exposes getInitParameterNames()/getInitParemter()
//    	
////    	@Delegate(excludes= {ServletConfigAndContextNotDelegated.class})
//        private final ServletConfig _wrappedDelegate;
//
//		//    	
////    	@Delegate(excludes= {ServletConfigAndContextNotDelegated.class})
//        private final ServletContext _delegateContext;
//        
//        public ServletConfigWrapper(final ServletConfig config) {
//            _wrappedDelegate = config;
//            _delegateContext = config.getServletContext();
//        }
//        // NOT DELEGATED METHODS ----------------------------------------------------------------------------------
//        @Override
//        public String getInitParameter(final String s) {
//            return _initParams.getProperty(s);
//        }        
//        
//		@Override @SuppressWarnings({ "unchecked","rawtypes" })
//        public Enumeration getInitParameterNames() {
//            return _initParams.propertyNames();
//        }
//        // DELEGATED METHODS --------------------------------------------------------------------------------------
//        @Override
//        public ServletContext getServletContext() {
//			return _wrappedDelegate.getServletContext();
//		}
//        @Override
//		public String getServletName() {
//			return _wrappedDelegate.getServletName();
//		}
//        @Override
//		public Dynamic addFilter(final String arg0,final  Class<? extends Filter> arg1) {
//			return _delegateContext.addFilter(arg0, arg1);
//		}
//        @Override
//		public Dynamic addFilter(final String arg0,final  Filter arg1) {
//			return _delegateContext.addFilter(arg0, arg1);
//		}
//        @Override
//		public Dynamic addFilter(final String arg0,final  String arg1) {
//			return _delegateContext.addFilter(arg0, arg1);
//		}
//        @Override
//		public void addListener(final Class<? extends EventListener> arg0) {
//			_delegateContext.addListener(arg0);
//		}
//        @Override
//		public void addListener(final String arg0) {
//			_delegateContext.addListener(arg0);
//		}
//        @Override
//		public <T extends EventListener> void addListener(final T arg0) {
//			_delegateContext.addListener(arg0);
//		}
//        @Override
//		public javax.servlet.ServletRegistration.Dynamic addServlet(final String arg0,final Class<? extends Servlet> arg1) {
//			return _delegateContext.addServlet(arg0, arg1);
//		}
//        @Override
//		public javax.servlet.ServletRegistration.Dynamic addServlet(final String arg0,final  Servlet arg1) {
//			return _delegateContext.addServlet(arg0, arg1);
//		}
//        @Override
//		public javax.servlet.ServletRegistration.Dynamic addServlet(final String arg0,final  String arg1) {
//			return _delegateContext.addServlet(arg0, arg1);
//		}
//        @Override
//		public <T extends Filter> T createFilter(final Class<T> arg0)
//				throws ServletException {
//			return _delegateContext.createFilter(arg0);
//		}
//        @Override
//		public <T extends EventListener> T createListener(final Class<T> arg0)
//				throws ServletException {
//			return _delegateContext.createListener(arg0);
//		}
//        @Override
//		public <T extends Servlet> T createServlet(final Class<T> arg0)
//				throws ServletException {
//			return _delegateContext.createServlet(arg0);
//		}
//        @Override
//		public void declareRoles(final String... arg0) {
//			_delegateContext.declareRoles(arg0);
//		}
//        @Override
//		public Object getAttribute(String arg0) {
//			return _delegateContext.getAttribute(arg0);
//		}
//        @Override
//		public Enumeration<String> getAttributeNames() {
//			return _delegateContext.getAttributeNames();
//		}
//        @Override
//		public ClassLoader getClassLoader() {
//			return _delegateContext.getClassLoader();
//		}
//        @Override
//		public ServletContext getContext(final String arg0) {
//			return _delegateContext.getContext(arg0);
//		}
//        @Override
//		public String getContextPath() {
//			return _delegateContext.getContextPath();
//		}
//        @Override
//		public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
//			return _delegateContext.getDefaultSessionTrackingModes();
//		}
//        @Override
//		public int getEffectiveMajorVersion() {
//			return _delegateContext.getEffectiveMajorVersion();
//		}
//        @Override
//		public int getEffectiveMinorVersion() {
//			return _delegateContext.getEffectiveMinorVersion();
//		}
//        @Override
//		public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
//			return _delegateContext.getEffectiveSessionTrackingModes();
//		}
//        @Override
//		public FilterRegistration getFilterRegistration(final String arg0) {
//			return _delegateContext.getFilterRegistration(arg0);
//		}
//        @Override
//		public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
//			return _delegateContext.getFilterRegistrations();
//		}
//        @Override
//		public JspConfigDescriptor getJspConfigDescriptor() {
//			return _delegateContext.getJspConfigDescriptor();
//		}
//        @Override
//		public int getMajorVersion() {
//			return _delegateContext.getMajorVersion();
//		}
//        @Override
//		public String getMimeType(final String arg0) {
//			return _delegateContext.getMimeType(arg0);
//		}
//        @Override
//		public int getMinorVersion() {
//			return _delegateContext.getMinorVersion();
//		}
//        @Override
//		public RequestDispatcher getNamedDispatcher(final String arg0) {
//			return _delegateContext.getNamedDispatcher(arg0);
//		}
//        @Override
//		public String getRealPath(final String arg0) {
//			return _delegateContext.getRealPath(arg0);
//		}
//        @Override
//		public RequestDispatcher getRequestDispatcher(final String arg0) {
//			return _delegateContext.getRequestDispatcher(arg0);
//		}
//        @Override
//		public URL getResource(final String arg0) throws MalformedURLException {
//			return _delegateContext.getResource(arg0);
//		}
//        @Override
//		public InputStream getResourceAsStream(final String arg0) {
//			return _delegateContext.getResourceAsStream(arg0);
//		}
//        @Override
//		public Set<String> getResourcePaths(final String arg0) {
//			return _delegateContext.getResourcePaths(arg0);
//		}
//        @Override
//		public String getServerInfo() {
//			return _delegateContext.getServerInfo();
//		}
//        @Override @Deprecated
//		public Servlet getServlet(final String arg0) throws ServletException {
//			return _delegateContext.getServlet(arg0);
//		}
//        @Override
//		public String getServletContextName() {
//			return _delegateContext.getServletContextName();
//		}
//        @Override @Deprecated
//		public Enumeration<String> getServletNames() {
//			return _delegateContext.getServletNames();
//		}
//        @Override
//		public ServletRegistration getServletRegistration(final String arg0) {
//			return _delegateContext.getServletRegistration(arg0);
//		}
//        @Override
//		public Map<String, ? extends ServletRegistration> getServletRegistrations() {
//			return _delegateContext.getServletRegistrations();
//		}
//        @Override @Deprecated
//		public Enumeration<Servlet> getServlets() {
//			return _delegateContext.getServlets();
//		}
//        @Override
//		public SessionCookieConfig getSessionCookieConfig() {
//			return _delegateContext.getSessionCookieConfig();
//		}
//        @Override
//		public String getVirtualServerName() {
//			return _delegateContext.getVirtualServerName();
//		}
//        @Override @Deprecated
//		public void log(final Exception arg0,final  String arg1) {
//			_delegateContext.log(arg0, arg1);
//		}
//        @Override
//		public void log(final String arg0,final  Throwable arg1) {
//			_delegateContext.log(arg0, arg1);
//		}
//        @Override
//		public void log(final String arg0) {
//			_delegateContext.log(arg0);
//		}
//        @Override
//		public void removeAttribute(final String arg0) {
//			_delegateContext.removeAttribute(arg0);
//		}
//        @Override
//		public void setAttribute(final String arg0,final  Object arg1) {
//			_delegateContext.setAttribute(arg0, arg1);
//		}
//        @Override
//		public boolean setInitParameter(final String arg0,final  String arg1) {
//			return _delegateContext.setInitParameter(arg0, arg1);
//		}
//        @Override
//		public void setSessionTrackingModes(final Set<SessionTrackingMode> arg0) {
//			_delegateContext.setSessionTrackingModes(arg0);
//		}
//    }
}
