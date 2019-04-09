package r01f.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpClientProxySettings;
import r01f.mail.GoogleAPI.GoogleAPIClientEMailAddress;
import r01f.mail.GoogleAPI.GoogleAPIClientID;
import r01f.mail.GoogleAPI.GoogleAPIClientP12KeyPath;
import r01f.mail.GoogleAPI.GoogleAPIServiceAccountClientData;
import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class JavaMailSenderConfigForGoogleAPI 
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final HttpClientProxySettings _proxySettings;
	@Getter private final GoogleAPIServiceAccountClientData _serviceAccountClientData;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForGoogleAPI(final HttpClientProxySettings proxySettigs,
											final GoogleAPIServiceAccountClientData serviceAccountClientData,
											final boolean disabled) {
		super(JavaMailSenderImpl.GOOGLE_API,
			  disabled);
		_proxySettings = proxySettigs;
		_serviceAccountClientData = serviceAccountClientData;
	}
	public static JavaMailSenderConfigForGoogleAPI createFrom(final XMLPropertiesForAppComponent xmlProps,
												      		  final String propsRootNode) {
		boolean disableMailSender = false;

		// check if a proxy is needed
		HttpClientProxySettings proxySettings = null;
		try {
			proxySettings = HttpClient.guessProxySettings(xmlProps,
														  propsRootNode);
		} catch(Throwable th) {
			log.error("Error while guessing the internet connection proxy settings to use GMail: {}",th.getMessage(),th);
			disableMailSender = true;	// the mail sender cannot be used
		}

		// Get the google api info from the properties file
		GoogleAPIServiceAccountClientData serviceAccountClientData = JavaMailSenderConfigForGoogleAPI.googleAPIServiceAccountClientDataFromProperties(xmlProps,
																																				      propsRootNode);
		
		// create the config object
		return new JavaMailSenderConfigForGoogleAPI(proxySettings,
													serviceAccountClientData,
													disableMailSender);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String GOOGLE_API_PROPS_XPATH = "/javaMailSenderImpls/javaMailSenderImpl[@id='google_API']";
	static GoogleAPIServiceAccountClientData googleAPIServiceAccountClientDataFromProperties(final XMLPropertiesForAppComponent props,
											 												 final String propsRootNode)	{
		String serviceAccountClientID = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/serviceAccountClientID")
											 .asString();
		String serviceAccountEMail = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/serviceAccountEmailAddress")
										  .asString();
		ResourcesLoaderType p12KeyLoader = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/p12Key/@loadedUsing")
								   				 .asEnumElementIgnoringCase(ResourcesLoaderType.class,
								   										    ResourcesLoaderType.FILESYSTEM);
		Path p12KeyFilePath = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/p12Key")
								   .asPath();
		EMail googleAppsUser = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/googleAppsUser")
									.asEMail();

		// Check
		if (serviceAccountClientID == null || serviceAccountEMail == null || p12KeyFilePath == null || googleAppsUser == null) {
			throw new IllegalStateException(Throwables.message("Cannot configure Google API: the properties file does NOT contains a the serviceAccountClientID, serviceAccountEMail, p12KeyFilePath or googleAppsUser at {} in {} properties file",
															   propsRootNode + GOOGLE_API_PROPS_XPATH,props.getAppCode()));
		}
		return new GoogleAPIServiceAccountClientData(props.getAppCode(),
												     GoogleAPIClientID.of(serviceAccountClientID),
												     GoogleAPIClientEMailAddress.of(serviceAccountEMail),
												     p12KeyLoader == ResourcesLoaderType.CLASSPATH ? GoogleAPIClientP12KeyPath.loadedFromClassPath(p12KeyFilePath)
														 									       : GoogleAPIClientP12KeyPath.loadedFromFileSystem(p12KeyFilePath),
												     googleAppsUser);
	}
}
