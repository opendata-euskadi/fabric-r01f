package r01f.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Set;

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.OIDBaseImmutable;
import r01f.httpclient.HttpClientProxySettings;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.resources.ResourceToBeLoaded;
import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.types.Path;
import r01f.types.contact.EMail;

/**
 * Google API Helpper type
 */
public class GoogleAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static GoogleAPIHttpTransport createHttpTransport() {
		return new GoogleAPIHttpTransport();
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public static class GoogleAPIHttpTransport {
		@SuppressWarnings("static-method")
		public HttpTransport noProxy() throws GeneralSecurityException, 
											  IOException {
			HttpTransport httpTransport = new NetHttpTransport.Builder()
															  .trustCertificates(GoogleUtils.getCertificateTrustStore())
															  .build();
			return httpTransport;
		}
		public HttpTransport usingProxy(final HttpClientProxySettings proxySettings) throws GeneralSecurityException, 
														 						    		IOException {
			if (!proxySettings.isEnabled()) return this.noProxy();
			
        	final String proxyHost = proxySettings.getProxyHost().asString();
        	final int proxyPort = proxySettings.getProxyPort();
        	final String proxyUser = proxySettings.getUser().asString();
        	final char[] proxyPassword = proxySettings.getPassword().asString().toCharArray(); 
        	
			Proxy proxy = new Proxy(Proxy.Type.HTTP, 
									new InetSocketAddress(proxyHost,
														  proxyPort)); 
			HttpTransport httpTransport = new NetHttpTransport.Builder()
															  .trustCertificates(GoogleUtils.getCertificateTrustStore())
															  .setProxy(proxy)
															  .build();
	        Authenticator.setDefault(new Authenticator() {
									        @Override
									        protected PasswordAuthentication getPasswordAuthentication() {
									        	// check that the pasword-requesting site is the proxy server									        	
									        	if (this.getRequestingHost().contains(proxyHost) && this.getRequestingPort() == proxyPort
										         && this.getRequestorType().equals(RequestorType.PROXY)) {
										        	return new PasswordAuthentication(proxyUser,
										        									  proxyPassword);
										        }
										        return super.getPasswordAuthentication();
									        }
									 });
			//System.setProperty("http.proxyHost",PROXY_HOST);
			//System.setProperty("http.proxyPort",Integer.toString(PROXY_PORT));
			
			return httpTransport;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static JsonFactory createJsonFactory() {
		JsonFactory jsonFactory = new JacksonFactory();
		return jsonFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * see https://developers.google.com/accounts/docs/OAuth2ServiceAccount
	 * https://developers.google.com/console/help/#activatingapis 		<---- look for [Service accounts]!!!!
	 * https://developers.google.com/accounts/docs/OAuth2ServiceAccount <---- look for [Service accounts]!!!!
	 * @param httpTransport
	 * @param jsonFactory
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@SuppressWarnings("resource")
	public static GoogleCredential createCredentialForServiceAccount(final HttpTransport httpTransport,
																	 final JsonFactory jsonFactory,
																	 final GoogleAPIServiceAccountClientData serviceAccountClientId) throws IOException,
																										    			 				    GeneralSecurityException {
		// Load the private key from the p12 store
		PrivateKey serviceAccountPrivateKey = null;
		@Cleanup InputStream privateKeyIS = null;
		if (serviceAccountClientId.getP12KeyPath().isLoadedFromClassPath()) {
			privateKeyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(serviceAccountClientId.getP12KeyPath().getFilePathAsString());
		} else {
			File p12KeyFile = new File(serviceAccountClientId.getP12KeyPath().getFilePathAsString());
			privateKeyIS = new FileInputStream(p12KeyFile);
		}
		if (privateKeyIS == null) throw new IllegalStateException(Throwables.message("Could NOT load the P12 key file from {} using {} loader",
																					 serviceAccountClientId.getP12KeyPath().getFilePathAsString(),
																					 serviceAccountClientId.getP12KeyPath().getResourcesLoaderType()));	
		serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(),
																			privateKeyIS, 
																			"notasecret",
																			"privatekey",
																			"notasecret");
		// create the credential
		GoogleCredential credential = new GoogleCredential.Builder()
												    .setTransport(httpTransport)
												    .setJsonFactory(jsonFactory)
												    .setServiceAccountId(serviceAccountClientId.getClientEmail().asString())	
												    .setServiceAccountPrivateKey(serviceAccountPrivateKey)
												    .setServiceAccountScopes(serviceAccountClientId.getScopes())	// see https://developers.google.com/gmail/api/auth/scopes
												    .setServiceAccountUser(serviceAccountClientId.getEndUserEmail().asString())
												    .build();	
		credential.refreshToken();
		return credential;
	}
	/**
	 * see https://developers.google.com/gmail/api/quickstart/quickstart-java
	 * @param httpTransport
	 * @param jsonFactory
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "resource" })
	public static GoogleCredential createCredentialForNativeApp(final HttpTransport httpTransport,
																final JsonFactory jsonFactory,
																final GoogleAPINativeApplicationClientData nativeAppClientId) throws IOException {
		// Load the client secret
		Reader clientSecretReader = null;
		if (nativeAppClientId.getJsonKeyPath().isLoadedFromClassPath()) {
			@Cleanup InputStream clientSecretIS = GoogleAPI.class.getResourceAsStream(nativeAppClientId.getJsonKeyPath().getFilePathAsString());
			if (clientSecretIS == null) throw new IllegalStateException(Throwables.message("Could NOT load the client secret from {} using {} loader",
																						   nativeAppClientId.getJsonKeyPath().getFilePathAsString(),
																						   nativeAppClientId.getJsonKeyPath().getResourcesLoaderType()));
			clientSecretReader = new InputStreamReader(clientSecretIS);
		} else {
			clientSecretReader = new FileReader(nativeAppClientId.getJsonKeyPath().getFilePathAsString());
		}	
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, 
												 					 clientSecretReader);

		// Create the credential: Allow user to authorize via url.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, 
																				   jsonFactory, 
																				   clientSecrets,
																				   nativeAppClientId.getScopes())
																		   .setAccessType("online")
																		   .setApprovalPrompt("auto")
																		   .build();

		String url = flow.newAuthorizationUrl()
						 .setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI)
						 .build();
		System.out.println("Please open the following URL in your browser then type " + 
						   "the authorization code:\n" + url);

		// Read code entered by user.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();

		// Generate Credential using retrieved code.
		GoogleTokenResponse response = flow.newTokenRequest(code)
										   .setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI)
										   .execute();
		GoogleCredential credential = new GoogleCredential()
												.setFromTokenResponse(response);
		
		return credential;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  Google Services
/////////////////////////////////////////////////////////////////////////////////////////
	public static Gmail createGmailService(final HttpTransport httpTransport,
										   final JsonFactory jsonFactory,
										   final AppCode appCode,
										   final GoogleCredential credential) {
		// Create a new authorized Gmail API client
		Gmail gmailService = new Gmail.Builder(httpTransport,
										  	  jsonFactory,
										  	  credential)
									   .setApplicationName(appCode.asString())
									   .build();
		return gmailService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CLIENT ID (see [Google Developer Console] > [APIs & Auth] > [Credential]
//	There are three [ClientID] flavours:
//		[1] Web application: 		accessed by web browsers over a network
// 		[2] Installed Application: 	Runs on a desktop computer or handheld device (like Android or iPhone).
//		[3] Service Account:		Server-to-server: Calls Google APIs on behalf of your application instead of an end-user
//
//	[Web Application] and [Installed Application] involves the end user since they requires
//	the end user to accept the application accessing the data (three-legged OAuth)
//	NOTE: 	once the application gets a user acceptance (a token) it can be stored for later use
//   		and this way avoid asking the end-user again
//  [Service Account] is to be used for [Server-to-Google API] with no user interaction	
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface GoogleAPIClientData {
		public AppCode getAppCode();
		public GoogleAPIClientID getClientId();
		public Set<String> getScopes();
	}
	/**
	 * A [Service Account]-type google [ClientID] (see https://developers.google.com/accounts/docs/OAuth2ServiceAccount)
	 * Service Accounts are used for [Server-to-Server] applications (ie a web application and a google service with no user interaction)
	 * A [Service Account] is an account associated with an application rather than an accoun associated with an individual end user
	 * 
	 * In order to set-up a [Service Account] client ID: (see http://stackoverflow.com/questions/29327846/gmail-rest-api-400-bad-request-failed-precondition/29328258#29328258)
	 *	 1.- Using a google apps user open the developer console
	 *	 2.- Create a new project (ie MyProject)
	 * 	 3.- Go to [Apis & auth] > [Credentials] and create a new [Service Account] client ID
	 * 	 4.- Copy the [service account]'s [Client ID] (the one like xxx.apps.googleusercontent.com) for later use
	 * 	 5.- Now you have to Delegate domain-wide authority to the service account in order to authorize your appl to access user data on behalf of users in the Google Apps domain ... so go to your google apps domain admin console
	 * 	 6.- Go to the [Security] section and find the [Advanced Settings] (it might be hidden so you'd have to click [Show more..])
	 * 	 7.- Click con [Manage API Client Access]
	 *	 8.- Paste the [Client ID] you previously copied at [4] into the [Client Name] text box.
	 *	 9.- To grant your app full access to gmail, at the [API Scopes] text box enter: https://mail.google.com, https://www.googleapis.com/auth/gmail.compose, https://www.googleapis.com/auth/gmail.modify, https://www.googleapis.com/auth/gmail.readonly (it's very important that you enter ALL the scopes)
	 */
	@MarshallType(as="googleAPIServiceAccountClientData")
	@Accessors(prefix="_")
	@RequiredArgsConstructor @AllArgsConstructor
	public static class GoogleAPIServiceAccountClientData 
			 implements GoogleAPIClientData {
		@Getter private final AppCode _appCode;
		@Getter private final GoogleAPIClientID _clientId;
		@Getter private final GoogleAPIClientEMailAddress _clientEmail;
		@Getter private final GoogleAPIClientP12KeyPath _p12KeyPath;
		@Getter private final EMail _endUserEmail;
		@Getter private 	  Set<String> _scopes = GmailScopes.all(); 	// all scopes by default
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static class GoogleAPINativeApplicationClientData 
			 implements GoogleAPIClientData {
		@Getter private final AppCode _appCode;
		@Getter private final GoogleAPIClientID _clientId;
		@Getter private final GoogleAPIClientJsonKeyPath _JsonKeyPath;	
		@Getter private final Set<String> _scopes;
	}
	@MarshallType(as="googleAPIClientId")
	public static class GoogleAPIClientID 
				extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = 6988744585167182617L;
		private GoogleAPIClientID(final String id) {
			super(id);
		}
		public static GoogleAPIClientID of(final String id) {
			return new GoogleAPIClientID(id);
		}
	}
	@MarshallType(as="googleAPIClientEMail")
	public static class GoogleAPIClientEMailAddress 
				extends EMail {
		private static final long serialVersionUID = 5996665907146903030L;
		private GoogleAPIClientEMailAddress(final String email) {
			super(email);
		}
		public static GoogleAPIClientEMailAddress of(final String id) {
			return new GoogleAPIClientEMailAddress(id);
		}
	}
	@MarshallType(as="googleAPIClientJsonKeyPath")
	public static class GoogleAPIClientJsonKeyPath
			    extends ResourceToBeLoaded {
		private static final long serialVersionUID = 7795614064183544708L;
		private GoogleAPIClientJsonKeyPath(final ResourcesLoaderType resourcesLoaderType,
											 final String path) {
			super(resourcesLoaderType,
				  Path.from(path));
		}
		private GoogleAPIClientJsonKeyPath(final ResourcesLoaderType resourcesLoaderType,
											 final Path path) {
			super(resourcesLoaderType,
				  Path.from(path));
		}
		public static GoogleAPIClientJsonKeyPath loadedFromClassPath(final Path path) {
			return new GoogleAPIClientJsonKeyPath(ResourcesLoaderType.CLASSPATH,
													path);
		}
		public static GoogleAPIClientJsonKeyPath loadedFromFileSystem(final Path path) {
			return new GoogleAPIClientJsonKeyPath(ResourcesLoaderType.FILESYSTEM,
													path);
		}
		public static GoogleAPIClientJsonKeyPath loadedFromClassPath(final String path) {
			return new GoogleAPIClientJsonKeyPath(ResourcesLoaderType.CLASSPATH,
													path);
		}
		public static GoogleAPIClientJsonKeyPath loadedFromFileSystem(final String path) {
			return new GoogleAPIClientJsonKeyPath(ResourcesLoaderType.FILESYSTEM,
													path);
		}
	}
	@MarshallType(as="googleAPIClientP12KeyPath")
	public static class GoogleAPIClientP12KeyPath
			    extends ResourceToBeLoaded {
		private static final long serialVersionUID = 7962856594968469607L;
		public GoogleAPIClientP12KeyPath(final ResourcesLoaderType resourcesLoaderType,
										   final String path) {
			super(resourcesLoaderType,
				  Path.from(path));
		}
		public GoogleAPIClientP12KeyPath(final ResourcesLoaderType resourcesLoaderType,
										   final Path path) {
			super(resourcesLoaderType,
				  Path.from(path));
		}
		public static GoogleAPIClientP12KeyPath loadedFromClassPath(final Path path) {
			return new GoogleAPIClientP12KeyPath(ResourcesLoaderType.CLASSPATH,
												   path);
		}
		public static GoogleAPIClientP12KeyPath loadedFromFileSystem(final Path path) {
			return new GoogleAPIClientP12KeyPath(ResourcesLoaderType.FILESYSTEM,
												   path);
		}
		public static GoogleAPIClientP12KeyPath loadedFromClassPath(final String path) {
			return new GoogleAPIClientP12KeyPath(ResourcesLoaderType.CLASSPATH,
												   path);
		}
		public static GoogleAPIClientP12KeyPath loadedFromFileSystem(final String path) {
			return new GoogleAPIClientP12KeyPath(ResourcesLoaderType.FILESYSTEM,
												   path);
		}
	}
}
