package r01f.ejie.xlnets;

import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import lombok.RequiredArgsConstructor;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.ejie.xlnets.api.XLNetsAPIBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Guice module for XLNets bindings
 * Binding a {@link XLNetsAuthTokenProvider} requires a properties file with the following section:
 *		<xlnets loginAppCode='theAppCode' token='httpProvided'>	<!-- token=file/httpProvided/loginApp -->
 *	 		<!--
 *			Token types:
 *				n38api			: use the http request cookies and N38 API
 *				mockFile 		: use a classpath-stored file with the xlnets session / user / auth / etc data 
 *											> the [sessionToken] element contains the session token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *											> the [userDataToken] element contains the user info token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *											> the [authToken] element contains the auth token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *				httpProvided	: Using a service that provides xlnets session tokens
 *											> the [loginAppCode] attribute is mandatory
 *											> the [sessionToken] element contains the url of the service that provides tokens (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=appId)
 *			-->
 *			<!--
 *			Login types:
 *				user			: user login
 *				app				: app login
 *			-->
 *			<sessionToken>
 *				if token=file: 			...path to a mock xlnets token (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate one)
 *				if token=httpProvided:  ...url to the url that provides the token (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=appId)
 *				if token=loginApp		...not used 
 *			</sessionToken>
 *		</xlnets>
 */
@RequiredArgsConstructor
public class XLNetsGuiceModule 
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final XMLPropertiesForAppComponent _props;
	private final String _propsRootXPath;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// uses provider
	}
	@Provides @Singleton
	public XLNetsAPI provideXLNetsAPI() {
		return XLNetsAPIBuilder.createAsDefinedAt(_props,_propsRootXPath);
	}
}
