/*
 * @author Alex Lara Garachana
 * Created on 16-may-2004
 */
package r01f.ejie.xlnets.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.ejie.xlnets.api.XLNetsAPIBuilder;
import r01f.ejie.xlnets.config.XLNetsAppCfg;
import r01f.ejie.xlnets.config.XLNetsTargetCfg;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceAccess;
import r01f.ejie.xlnets.config.XLNetsTokenSource;
import r01f.ejie.xlnets.context.XLNetsAuthCtx;
import r01f.ejie.xlnets.context.XLNetsTargetCtx;
import r01f.exceptions.Throwables;
import r01f.patterns.FactoryFrom;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesComponent;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Filtro de entrada para control de la seguridad basada en XLNets de forma declarativa.
 * La configuración de seguridad se declara en el fichero [codApp].xlnets.properties.xml incluyendo una
 * seccion como la del siguiente ejemplo (ver {@link XLNetsAppCfg})
 * <pre class='brush:xml'>
 * 	<xlNets token='n38' login='user'>
 *     <authCfg useSession='true/false' override='true/false'>
 *     		<target id='theId' kind='restrict|allow'>
 *     			<uri>[Expresion regular para machear la uri que se solicita]</uri>
 *     			<resources>
 *     				<resource type='[itemType]' mandatory='true/false' oid='[itemOID]'>
 *     					<es>[Nombre en castellano]</es>
 *     					<eu>[Nombre en euskera]</eu>
 *     				</resource>
 *     				<resource type='[itemType]' mandatory='true/false' oid='[itemOID]'>
 *     					<es>[Nombre en castellano]</es>
 *     					<eu>[Nombre en euskera]</eu>
 *     				</resource>
 *     				....
 *     			</resources>
 *     		</target>
 *     		....
 *     </authCfg>
 * 	</xlNets>
 * </pre>
 * Notas:
 * <pre>
 * 		useSession:			Indica si la información de autorizacion se almacena en memoria o bien hay que
 * 							volver a obtenerla cada vez que se accede al recurso
 * 		override:			Indica si se ha de ignorar la configuracion de seguridad (no hay seguridad)
 *      provider:           Configuración del provider de seguridad
 * 		   className: 	    La clase que se encarga de consultar el almacén de seguridad
 * 							      Puede utilizarse XLNets o un povider que obtiene la seguridad de BD
 * 		target:			    Un recurso que se protege
 *          id:             Identificador del recurso
 *          kind:           Tipo de protección
 *                              allow   -> Permitir el acceso
 *                              restrict-> restringir el acceso
 * 			uri:			Una expresión regular con la url del recurso.
 * 			resources:		Elementos sobre los que hay que comprobar si el usuario tiene acceso
 *                          NOTA: Si el tipo es allow, NO se comprueban los recursos
 *              resource    Elemento individual sobre el que hay que comprobar si el usuario tiene acceso
 * 				  oid:		El oid del objeto de seguridad
 * 							En el caso de XLNets el oid puede corresponder al uid de una función o un
 * 							tipo de objeto
 *                mandatory	true/false: Indica si este item es OBLIGATORIO, lo cual implica que en caso de
 * 							no tener acceso, se prohibirá el acceso al recurso.
 * 							Si en todos los items mandatory es false, se permitirá el acceso aunque
 * 							no haya autorización a los items, sin embargo, la información de seguridad se
 * 							dejará en sesion.
 * 				  type:		El tipo de elemento a comprobar
 * 							En el caso de XLNets el tipo puede ser
 * 								function: Una funcion
 * 								object: Un tipo de objeto
 * 				  es/eu		La descripcion en euskera y castellano del item de seguridad
 *
 * </pre>
 * Se pueden definir múltiples recursos en una aplicacion, cada uno de ellos tendrá asociada una expresión regular
 * con la URI.
 * Cuando llega una petición, se aplicará la configuración de seguridad del primer recurso cuya uri
 * machee la url solicitada al filtro.
 *
 * La secuencia de autorizacion es la siguiente:
 * <pre>
 * 1.- INSTANCIAR EL PROVIDER DE AUTORIZACION ESPECIFICADO EN LA CONFIGURACION
 * 2.- COMPROBAR SI EL USUARIO ESTÁ AUTENTICADO
 * 	   Se llama al método getContext() para ver si el usuario está autenticado y si es así obtener el contexto de
 *     autenticación de usuario
 *     Aquí a su vez se pueden dar dos casos:
 * 	   2.1 - EL USUARIO NO ESTA AUTENTICADO
 * 	           Si el usuario NO está autenticado se le dirige a la página de login llamando
 *             al metodo redirectToLogin() del provider de autorizacion
 *     2.2 - EL USUARIO ESTA AUTENTICADO PERO NO HAY INFORMACION DE CONTEXTO DE AUTORIZACION EN LA SESION
 *             El usuario ha hecho login, pero es la primera vez que entra al recurso y no hay información de
 *             contexto de autorizacion en la sesion. El provider en la llamada a al funcion getContext() devuelve
 *             todos los datos del usuario y de la sesión de seguridad en la que esta autenticado
 *             Ahora ya se está como en el caso 2.3 (siguiente caso)
 * 	   2.3 - EL USUARIO ESTÁ AUTENTICADO Y HAY INFORMACION DE CONTEXTO DE AUTORIZACION EN LA SESION
 *             Si en la sesión hay información de contexto, se busca en el contexto la informacion de autorizacion
 *             del destino solicitado. Pueden darse dos casos:
 *             2.3.1 - En el contexto NO hay informacion de autorizacion del destino (es la primera vez que se accede)
 *                        Se llama a la funcion authorize() del provider de seguridad que se encarga obtener
 *                        la autorizacion correspondiente.
 *                        A partir de este momento, esta información de autorización se mantiene en sesión
 *                        y no es necesario volver a pedirla al provider de seguridad
 *             2.3.2 - En el contexto HAY informacion de autorizacion del destino (ya se ha accedido al recurso)
 *                        Directamente se devuelve la informacion de autorizacion almacenada en sesion
 * </pre>
 */
@Singleton
@Accessors(prefix="_")
@Slf4j
public abstract class XLNetsAuthServletFilterBase
           implements Filter {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String LEGITIMATE_COOKIE_NAME = "r01SecurityToken";
	
	public static final String AUTHCTX_SESSIONATTR = "XLNetsAuthCtx";	// Contexto de usuario en la sesion
	public static final String AUTHCTX_REQUESTATTR = "XLNetsAuthCtx";	// Contexto de usuario en la request
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The session token source
	 */
	private final XLNetsTokenSource _tokenSource;
	/**
	 * XLNets config
	 */
	private final XLNetsAppCfg _appCfg;
	/**
	 * Auth provide
	 */
	private final FactoryFrom<HttpServletRequest,XLNetsAuthProvider> _authProviderFactory;
	/**
	 * web.xml config
	 */
	private FilterConfig _servletFilterConfig = null;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public XLNetsAuthServletFilterBase(@XMLPropertiesComponent("xlNets") final XMLPropertiesForAppComponent props) {
		_tokenSource = props.propertyAt("/xlnets/@token")
						   .asEnumFromCode(XLNetsTokenSource.class,
										   XLNetsTokenSource.N38API);		// default value
		log.warn("[XLNetsAuthServletFilter]: token source: {}");

		_appCfg = new XLNetsAppCfg(props);
		_authProviderFactory = new FactoryFrom<HttpServletRequest,XLNetsAuthProvider>() {
										@Override
										public XLNetsAuthProvider from(final HttpServletRequest req) {
											XLNetsAPI outApi = null;
											if (_tokenSource == XLNetsTokenSource.N38API) {
												outApi = XLNetsAPIBuilder.createFrom(req);
											} else {
												outApi = XLNetsAPIBuilder.createAsDefinedAt(props,"");
											}
											return new XLNetsAuthProvider(outApi);
										}
							   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void init(final FilterConfig config) throws ServletException {
		_servletFilterConfig = config;
	}
	@Override
	public void destroy() {
		_servletFilterConfig = null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void doFilter(final ServletRequest request,final ServletResponse response,
						 final FilterChain chain) throws IOException,
						 								 ServletException {
		log.warn("XLNetsAuthServletFilter: Init >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		try {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse res = (HttpServletResponse)response;

	        // Get the requested url and see if it matches one of the patterns defined at the auth config
	        UrlPath urlPath = UrlPath.from(_fullURI(req));//getRequestURI only returns URL BEFORE query string -- UrlPath.from(req.getRequestURI());
	        log.warn("requested url: {} > path: {}",
	        		 req.getRequestURI(),urlPath);
	        XLNetsTargetCfg targetCfg = _resourceThatFirstMatches(_appCfg,
	        													  urlPath);
	        if (targetCfg == null) throw new ServletException(Throwables.message("NO authorized resource matches uri '{}'.\nCheck <authCfg> section at xlnets config file {appCode}.xlnets.properties.xml",
	        												   					 urlPath));
	        log.warn("Resource that first matches {}: {}",
	        		 targetCfg.toString(),
	        		 _appCfg.isOverride() ? "Auth is NOT checked (override=true)"
		    							  : "checking auth...");

		    XLNetsAuthCtx authCtx = null;
		    if (!_appCfg.isOverride()
		     && targetCfg.getKind() == ResourceAccess.RESTRICT ) {

		    	log.warn("url {} is restricted: authCfg.override={} targetCfg.kind={}",
	            		 urlPath,_appCfg.isOverride(),targetCfg.getKind());

				// Get the session context (if there's session)
		    	authCtx = _retrieveSessionStoredAuthCtx(_appCfg,req);		// Auth context

		    	// Create the auth provider
				final XLNetsAuthProvider authProvider = _authProviderFactory.from(req);

			    // NO auth context info due to:
			    //		1.- The user has just authenticated and it's the first time he/she is entering the restricted target
			    //		2.- User is not authenticated
				if (authCtx == null) {
				    log.warn("There's NO auth context: [1]- It's the first time the user is accessing [2]- There's NO auth info");
				    // create a new auth context
				    authCtx = authProvider.getAuthContext(req);
				    if (authCtx == null) {
				        // NO user session: redirect to login page (attach the url where the user should be redirected after successful login)
				        log.warn("User is NOT authenticated: redirect to login page");
				        _redirToLoginPage(authProvider,
				        				  req,res);
	                    return;		// END!!
	                }
				}
				log.warn("Auth Context {}",
						 authCtx.toString() );
				// Now there's an user context whether it was at the server session, whether it has just been created
				log.warn("User is authenticated. It has the following profiles: {}.\n ===> Check auth for url: {}",
						 authCtx.getUserProfiles(),urlPath);

				XLNetsTargetCtx targetCtx = authCtx.getTargetAuth(targetCfg.getUrlPathPattern());	// Auth info for target can already be at the auth context

				if (targetCtx == null) targetCtx = _authorize(authProvider,
															  targetCfg,
															  authCtx,
															  req,res);


				// Now the auth info is available
	            if (targetCtx == null
	             || CollectionUtils.isNullOrEmpty(targetCtx.getAuthorizedResources()) ) {
	            	if (targetCtx != null
	            	 && targetCtx.getTargetCfg().containsAnyNonMandatoryResource()) {
	            		log.warn("User does NOT have any authorization to any resource, BUT at least a non-mandatory resource exists... so let the user in");
	            	} else {
		            	// The auth info could not be retrieved or just the user does NOT have access
				        log.warn("Resource auth info could not be retrieved: [1.-] The user does NOT have access [2.-] The auth provider has failed");
				        res.sendError(HttpServletResponse.SC_FORBIDDEN,
				        			  "Security filter does NOT allow resource access!");
				        return;
	            	}
	            }

			    // Before chaing to the resource being accessed, if server session is used, store there the auth
			    // Anyway, whether server session is used or not, the auth contest is attached to the request as an attribute
	            log.warn("Guardando el contexto de autenticación en sesión... {}",authCtx.toString());
		    	req.setAttribute(AUTHCTX_REQUESTATTR,
		    					 authCtx);
		    	if (_appCfg.isUseSession()) {
			        HttpSession ses = req.getSession(true);		// Crear la session por huevos.
			        ses.setAttribute(AUTHCTX_SESSIONATTR,
			        				 authCtx);
		    	}

			} else {
			    // Auth is NOT checked, a virtual context is created
			    // Resource being accessed should ched appCfg.override parameter in order to check if auth is being checked
	            log.warn("Auth access is NOT checked: appCfg.override={} targetCfg.kind={}: " +
	                     "override=true or target matching {} is kind={}",
	                     _appCfg.isOverride(),targetCfg.getKind(),urlPath,ResourceAccess.ALLOW);
			    authCtx = new XLNetsAuthCtx();
			}

		    // access allowed
	    	log.warn("Authorized!");

		    // chain
	    	_attachBusinessModelObjectToLocalThreadIfNeeed(req); //Attach some business model object to a local thread
		    chain.doFilter(request,response);
			log.warn("XLNetsAuthServletFilter: End >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n\n\n\n");

		} finally {
			_doFinallyAfterFilter();
		}
	}
	protected String getInitParameter(String s) {
		return _servletFilterConfig.getInitParameter(s);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract void _attachBusinessModelObjectToLocalThreadIfNeeed(final HttpServletRequest request);
	protected abstract void _doFinallyAfterFilter();
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private static XLNetsAuthCtx _retrieveSessionStoredAuthCtx(final XLNetsAppCfg authCfg,
										   		 			   final HttpServletRequest req) {
		log.debug("_retrieveSessionStoredAuthCtx for {}", authCfg.toString());
		XLNetsAuthCtx authCtx = null;
		if (authCfg.isUseSession()) {
			HttpSession ses = req.getSession(false);	// do not create a new session
			if (ses != null) {
                authCtx = (XLNetsAuthCtx)ses.getAttribute(AUTHCTX_SESSIONATTR);

                log.warn("Auth context is present in server session {}!",
                		 ses.getId());
            } else {
                log.warn("NO auth context present in servers session");
            }
		} else {
		    log.warn("Auth context is NOT being stored in servers session");
		}
		return authCtx;		// null if servers session is not used
	}
	private XLNetsTargetCtx _authorize(final XLNetsAuthProvider authProvider,
									   final XLNetsTargetCfg targetCfg,
									   final XLNetsAuthCtx authCtx,
									   final HttpServletRequest req,final HttpServletResponse res) {
		log.warn("Checking access for url: {}",
				 req.getRequestURI());

	    XLNetsTargetCtx targetCtx = authProvider.authorize(authCtx,
	    								   				   targetCfg,
	    								   				   _appCfg.isOverride(),
	    								   				   req);

        // Add the auth context to the server session (if it can be used)
	    if (targetCtx != null) {
	        log.warn("Store the auth context at the server session");
	        if (authCtx.getAuthorizedTargets() == null)  authCtx.setAuthorizedTargets(new HashMap<String,XLNetsTargetCtx>());
	        authCtx.getAuthorizedTargets().put(targetCtx.getTargetCfg().getUrlPathPattern(),
	        								   targetCtx);
	    }
        return targetCtx;
	}
	private void _redirToLoginPage(final XLNetsAuthProvider authProvider,
								   final HttpServletRequest req,final HttpServletResponse res) {
        // first attempt: app config
        Url loginPage = _appCfg.getLoginUrl();
        // second attempt: a filter param
        if (loginPage == null) {
        	String filterConfigLoginUrlParam = _servletFilterConfig.getInitParameter("xlnetsLoginURL");
        	if (Strings.isNOTNullOrEmpty(filterConfigLoginUrlParam)) loginPage = Url.from(filterConfigLoginUrlParam.trim());
        }
       log.warn("Login Page {} ",
    		    loginPage);
        // ERROR
        if ( loginPage == null ) {
            log.warn("Login page url was NOT found. It was looked after at 'xlNets/authCfg/provider/loginPage' app property and then at 'xlnetsLoginURL' web.xml param");
        } else {
            // Dirty trick: XLNets needs N38API param with the target url (the url to be redirected to after login)
            Url theLoginPage = loginPage.joinWith(UrlQueryString.fromParams(UrlQueryStringParam.of("N38API",
            																					   _fullURI(req))));
            log.warn("redirecting to login page: {}",
            		 theLoginPage);
            authProvider.redirectToLogin(res,
            							 theLoginPage);
        }
	}
    private static XLNetsTargetCfg _resourceThatFirstMatches(final XLNetsAppCfg appCfg,
    														 final UrlPath urlPath) {
    	UrlPath theUrlPath = urlPath;
        if (theUrlPath == null) {
            theUrlPath = UrlPath.from("/");
            log.warn("Url is null.. a dummy url is used");
        }
        log.warn("... trying to match url={} with properties file-configured target patterns",
        		 theUrlPath);

        XLNetsTargetCfg outTargetCfg = null;
        if (CollectionUtils.hasData(appCfg.getTargets())) {
            for (XLNetsTargetCfg cfg : appCfg.getTargets() ) {
            	Pattern p = Pattern.compile(cfg.getUrlPathPattern());
                if ( _matches(theUrlPath,
                			  p) ) {
                    log.warn("pattern: {} MATCHES!!!",cfg.getUrlPathPattern());
                    outTargetCfg = cfg;
                    break;
                }
            }
        }
        if (outTargetCfg == null) log.warn("NO pattern matches url={}",
        			 					   theUrlPath.asAbsoluteString());
        log.warn("outTargetCfg {}",
        		 outTargetCfg != null ? outTargetCfg.toString() : null);
        return outTargetCfg;
    }
	private static boolean _matches(final UrlPath urlPath,
									final Pattern pattern) {
		Matcher m = pattern.matcher(urlPath.asAbsoluteString());
		boolean matches = m.find();
		return matches;
	}

	private String _fullURI(final HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
	    String queryString = request.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}
}