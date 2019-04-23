package r01f.ejie.xlnets.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.config.XLNetsLoginType;
import r01f.ejie.xlnets.config.XLNetsTokenSource;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgDivisionID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgDivisionServiceID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgObjectID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrganizationID;
import r01f.ejie.xlnets.model.XLNetsOrganizationType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;
import r01f.patterns.IsBuilder;
import r01f.types.Path;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryStringParam;
import r01f.types.url.Urls;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xml.XMLUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;


/**
 * Provides XLNets session tokens
 * Requires a properties file with a section like:
 *		<xlnets token='n38api/mockFile/httpProvided'
 *				login='user/app' loginAppCode='theAppCode'>
 *	 		<!--
 *			Token types:
 *				n38api			: use the http request cookies and N38 API
 *				mockFile 		: use a classpath-stored file with the xlnets session / user / auth / etc data 
 *											> the [sessionToken] element contains the session token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *											> the [userDataToken] element contains the user info token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *											> the [authToken] element contains the auth token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *											> the [workplaceDataToken] element contains the workplace (puesto) token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *				httpProvided	: Using a service that provides xlnets session tokens
 *											> the [loginAppCode] attribute is mandatory
 *											> the [sessionToken] element contains the url of the service that provides tokens (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=appId)
 *			-->
 *			<!--
 *			Login types:
 *				user			: user login
 *				app				: app login
 *			-->
 *
 *       	<!-- Http session token provider service -->
 *       	<httpSessionTokenProviderService>
 *       		<url>http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app={}</url>
 *       	</httpSessionTokenProviderService>
 *
 *      	<!-- mock tokens -->
 *      	<mockFiles>
 *      		<userSessionToken for='{userCode}'>aa14a/xlnets/mock/aa14a-xlnetsSession.xml</userSessionToken>
 *      		<appSessionToken for='{appCode}'>aa14a/xlnets/mock/aa14a-xlnetsSession.xml</appSessionToken>
 *      		<userDataToken for='{userCode}'>aa14a/xlnets/mock/aa14a-xlnetsItemObtenerPersonas.xml</userDataToken>
 *      		<workplaceDataToken for='{workPlaceCode}'>aa14a/xlnets/mock/aa14a-xlnetsItemObtenerPuestos.xml</workplaceDataToken>
 *      		<orgDataToken for='{o|go|co}_{uid}'>aa14a/xlnets/mock/aa14a-xlnetsItemObtenerPuestos.xml</orgDataToken>
 *      		<authToken for='{auth-id}'>aa14a/xlnets/mock/aa14a-xlnetsItemAutorizacion.xml</authToken>
 *      		<ldapFilterTestDataToken for='{appCode}'>aa14a/xlnets/mock/aa14a-xlnetsLDAPObtenerPersonas.xml</ldapFilterTestDataToken>
 *      	</mockFiles>
 *		</xlnets>
 */
@Slf4j
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class XLNetsAPIBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	FROM REQUEST
/////////////////////////////////////////////////////////////////////////////////////////
	public static XLNetsAPI createFrom(final HttpServletRequest req) {
		XLNetsAPI outApi = new XLNetsAPI(new XLNetsAPIUsingN38API(req));
		return outApi;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FROM PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	public static XLNetsAPI createAsDefinedAt(final XMLPropertiesForAppComponent props,final String propsRootNode) {
		final XLNetsTokenSource tokenSource = props.propertyAt(propsRootNode + "/xlnets/@token")
												   .asEnumFromCode(XLNetsTokenSource.class,
																   XLNetsTokenSource.N38API);		// default value
		final XLNetsLoginType loginType = props.propertyAt(propsRootNode + "/xlnets/@login")
											   .asEnumFromCode(XLNetsLoginType.class,
															   XLNetsLoginType.APP);				// default value
		XLNetsAPI outApi = null;
		if (tokenSource == XLNetsTokenSource.N38API) {
			if (loginType == XLNetsLoginType.USER) {
				throw new IllegalStateException("Cannot build an N38API without the http servlet request: " +
											    "use the " + XLNetsAPIUsingN38API.class.getSimpleName() + "(req) instead");
			} else {
				// get the appcode
				AppCode appCode = props.propertyAt(propsRootNode + "/xlnets/@loginAppCode")
									   .asAppCode();
				if (appCode == null) throw new IllegalStateException("The properties file DOES NOT contains " +
																	 "the appCode for which the session tokens " + 
																	 "will be provided (xpath=" + propsRootNode + "/xlnets/@loginAppCode)");
				// build the api
				outApi = new XLNetsAPI(new XLNetsAPIUsingN38API(appCode));
			}
		}
		else if (tokenSource == XLNetsTokenSource.HTTP_PROVIDED) {
			if (loginType == XLNetsLoginType.USER) {
				throw new IllegalStateException("Cannot build an N38API without the http servlet request: " +
											    "use the " + XLNetsAPIUsingN38API.class.getSimpleName() + "(req) instead");
			} else {
				// get the appcode
				AppCode appCode = props.propertyAt(propsRootNode + "/xlnets/@loginAppCode")
									   .asAppCode();
				if (appCode == null) throw new IllegalStateException("The properties file DOES NOT contains " +
																	 "the appCode for which the session tokens " + 
																	 "will be provided (xpath=" + propsRootNode + "/xlnets/@loginAppCode)");
				// http provided (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=X42T)
				Url xlnetsProviderBaseUrl = props.propertyAt(propsRootNode + "/xlnets/httpSessionTokenProviderService/url")
												 .asUrl();
				if (xlnetsProviderBaseUrl == null) throw new IllegalStateException("The properties file DOES NOT contains " +
																				   "the url that provides xlnets session tokens at xpath " + propsRootNode + "/xlnets/httpSessionTokenProviderService/url");
				
				// build the api
				outApi = new XLNetsAPI(new XLNetsAPIUsingHttpProviderService(xlnetsProviderBaseUrl,
																			 appCode));
			}
		}
		else  if (tokenSource == XLNetsTokenSource.MOCK_FILE) {
			Map<UserCode,Path> mockUsersSessionFilesPaths = Maps.newHashMap();        
			Map<AppCode,Path> mockAppsSessionFilesPaths = Maps.newHashMap();          
			Map<UserCode,Path> mockUsersInfoFilesPaths = Maps.newHashMap();
			Map<WorkPlaceCode,Path> mockWorkPlacesInfoFilesPaths = Maps.newHashMap();
			Map<XLNetsOrgObjectID,Path> mockOrgsFilesPaths = Maps.newHashMap();
			Map<String,Path> mockAuthFilesPaths = Maps.newHashMap();
			Map<AppCode,Path> mockPersonFilterFilesPaths = Maps.newHashMap();
			
			NodeList nodes = props.propertyAt(propsRootNode + "/xlnets/mockFiles")
								  .nodeList();			
			if (nodes != null && nodes.getLength() > 0) {
				for (Node node : XMLUtils.nodeListIterableFrom(nodes)) {
					String targetObjId = XMLUtils.nodeAttributeValue(node,"for");
					if (Strings.isNOTNullOrEmpty(targetObjId)) {
						String pathStr = node.getFirstChild().getNodeValue();
						if (Strings.isNOTNullOrEmpty(pathStr)) {
							Path path = Path.from(pathStr);
							if (node.getNodeName().equals("userSessionToken")) {
								mockUsersSessionFilesPaths.put(UserCode.forId(targetObjId),path);
							} 
							else if (node.getNodeName().equals("appSessionToken")) {
								mockAppsSessionFilesPaths.put(AppCode.forId(targetObjId),path);
							} 
							else if (node.getNodeName().equals("userDataToken")) {
								mockUsersInfoFilesPaths.put(UserCode.forId(targetObjId),path);
							} 
							else if (node.getNodeName().equals("workplaceDataToken")) {
								mockWorkPlacesInfoFilesPaths.put(WorkPlaceCode.forId(targetObjId),path);
							} 
							else if (node.getNodeName().equals("orgDataToken")) {
								XLNetsOrgObjectID id = null;
								if (targetObjId.startsWith(XLNetsOrganizationType.ORGANIZATION.getCode().toLowerCase())) {
									id = XLNetsOrganizationID.forId(targetObjId);
								} else if (targetObjId.startsWith(XLNetsOrganizationType.GROUP.getCode().toLowerCase())) {
									id = XLNetsOrgDivisionID.forId(targetObjId);
								} else if (targetObjId.startsWith(XLNetsOrganizationType.CENTER.getCode().toLowerCase())) {
									id = XLNetsOrgDivisionServiceID.forId(targetObjId);	
								}
								if (id != null) mockOrgsFilesPaths.put(id,path);
							} 
							else if (node.getNodeName().equals("authToken")) {
								mockAuthFilesPaths.put(targetObjId,path);
							}
							else if (node.getNodeName().equals("ldapFilterTestDataToken")) {
								mockPersonFilterFilesPaths.put(AppCode.forId(targetObjId),path);
							}
							else {
								log.error("No mock file path configured for nodeName={}!!",node.getNodeName());
							}
						}
					}
				}
			}
			if (CollectionUtils.isNullOrEmpty(mockUsersSessionFilesPaths)) log.warn("Could NOT get the mock xlnets user session files at xpath= {}/xlnets/mockFiles/userSessionToken/", propsRootNode);
			if (CollectionUtils.isNullOrEmpty(mockAppsSessionFilesPaths)) log.warn("Could NOT get the mock xlnets app session files at xpath= {}/xlnets/mockFiles/appSessionToken/", propsRootNode);
			if (CollectionUtils.isNullOrEmpty(mockUsersInfoFilesPaths)) log.warn("Could NOT get the mock xlnets user info files at xpath= {}/xlnets/mockFiles/userDataToken/", propsRootNode);
			if (CollectionUtils.isNullOrEmpty(mockWorkPlacesInfoFilesPaths)) log.warn("Could NOT get the mock xlnets workplace info files at xpath= {}/xlnets/mockFiles/workplaceDataToken/", propsRootNode);
			if (CollectionUtils.isNullOrEmpty(mockAuthFilesPaths)) log.warn("Could NOT get the mock xlnets auth files at xpath= {}/xlnets/mockFiles/authToken/", propsRootNode);
			if (CollectionUtils.isNullOrEmpty(mockPersonFilterFilesPaths)) log.warn("Could NOT get the mock xlnets filter test data files at xpath= {}/xlnets/mockFiles/ldapFilterTestDataToken[/", propsRootNode);
			
			_logMockFiles("User session",mockUsersSessionFilesPaths);
			_logMockFiles("App session",mockAppsSessionFilesPaths);
			_logMockFiles("User info",mockUsersInfoFilesPaths);
			_logMockFiles("Workplaces info",mockWorkPlacesInfoFilesPaths);
			_logMockFiles("Auth info",mockAuthFilesPaths);
			_logMockFiles("Person filter info",mockPersonFilterFilesPaths);
			
			// Create the api
			outApi = new XLNetsAPI(new XLNetsAPIUsingMockFile(loginType,
															  mockUsersSessionFilesPaths,
															  mockAppsSessionFilesPaths,
															  mockUsersInfoFilesPaths,
															  mockWorkPlacesInfoFilesPaths,
															  mockOrgsFilesPaths,
															  mockAuthFilesPaths,
															  mockPersonFilterFilesPaths));
		}
		return outApi;
	}
	private static void _logMockFiles(final String title,final Map<?,Path> map) {
		log.warn("Mock files for: {} > {} files",
				 title,CollectionUtils.hasData(map) ? map.size() : 0);
		if (CollectionUtils.hasData(map)) {
			for (Map.Entry<?,Path> me : map.entrySet()) {
				log.warn("\t- {} > {}",me.getKey(),me.getValue());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TOKEN PROVIDER SERVICE
/////////////////////////////////////////////////////////////////////////////////////////	
	public static XLNetsAPIBuilderUsingTokenProviderAppStep createUsingTokenProviderService() {
		return new XLNetsAPIBuilder() { /* nothing */ }
					.new XLNetsAPIBuilderUsingTokenProviderAppStep();
	}
	public class XLNetsAPIBuilderUsingTokenProviderAppStep {
		public XLNetsAPIBuilderUsingTokenProviderPropertiesOrUrlStep forApp(final AppCode appCode) {
			return new XLNetsAPIBuilderUsingTokenProviderPropertiesOrUrlStep(appCode);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class XLNetsAPIBuilderUsingTokenProviderPropertiesOrUrlStep {
		private final AppCode _appCode;
		public XLNetsAPIBuilderUsingTokenProviderRootPropertyStep using(final XMLPropertiesForAppComponent props) {
			return new XLNetsAPIBuilderUsingTokenProviderRootPropertyStep(_appCode,
																		  props);
		}
		public XLNetsAPIBuilderUsingTokenProviderBuildFromUrlStep usingProviderAt(final Url url) {
			return new XLNetsAPIBuilderUsingTokenProviderBuildFromUrlStep(_appCode,
																	  	  url);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class XLNetsAPIBuilderUsingTokenProviderRootPropertyStep {
		private final AppCode _appCode;
		private final XMLPropertiesForAppComponent _props;
		
		public XLNetsAPIBuilderUsingTokenProviderBuildFromPropertiesStep from(final String propsRootNode) {
			return new XLNetsAPIBuilderUsingTokenProviderBuildFromPropertiesStep(_appCode,
																   				 _props,propsRootNode);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class XLNetsAPIBuilderUsingTokenProviderBuildFromPropertiesStep {
		private final AppCode _appCode;
		private final XMLPropertiesForAppComponent _props;
		private final String _propsRootNode;
		
		public XLNetsAPI build() {
			// http provided (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=X42T)
			Url xlnetsProviderBaseUrl = _props.propertyAt(_propsRootNode + "/xlnets/httpSessionTokenProviderService/url")
											  .asUrl();
			if (xlnetsProviderBaseUrl == null) throw new IllegalStateException("The properties file DOES NOT contains " +
																			   "the url that provides xlnets session tokens at xpath " + _propsRootNode + "/xlnets/sessionToken");
			Url xlnetsProviderUrl = Urls.join(xlnetsProviderBaseUrl,
										   	  new UrlQueryStringParam("login_app",_appCode));
			return new XLNetsAPIBuilderUsingTokenProviderBuildFromUrlStep(_appCode,
																		  xlnetsProviderUrl)
							.build(); 
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class XLNetsAPIBuilderUsingTokenProviderBuildFromUrlStep {
		private final AppCode _appCode;
		private final Url _xlnetsProviderUrl;
		
		public XLNetsAPI build() {
			return new XLNetsAPI(new XLNetsAPIUsingHttpProviderService(_xlnetsProviderUrl,
														 			   _appCode)); 
		}
	}
}
