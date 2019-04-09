package r01f.services.latinia;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Latinia config data
 */
@Accessors(prefix="_")
public class LatiniaServiceAPIData 
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _webServiceUrl;
	@Getter private final AppCode _enterpriseLogin;
	@Getter private final UserCode _publicUser;
	@Getter private final Password _publicPassword;
	@Getter private final AppCode _productId;
	@Getter private final UserCode _contractId;
	@Getter private final Password _password;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER  
/////////////////////////////////////////////////////////////////////////////////////////
	public LatiniaServiceAPIData(final Url serviceUrl,
						 		 final AppCode enterpriseLogin, 
						 		 final UserCode publicUser,     
						 		 final Password publicPassword, 
						 		 final AppCode productId,       
						 		 final UserCode contractId,     
						 		 final Password password) {		
		_webServiceUrl = serviceUrl;
		
		_enterpriseLogin = enterpriseLogin;
		_publicUser = publicUser;
		_publicPassword = publicPassword;
		_productId = productId;
		_contractId = contractId;
		_password = password;
	}
	public static LatiniaServiceAPIData createFrom(final XMLPropertiesForAppComponent latiniaProps) {
		return LatiniaServiceAPIData.createFrom(latiniaProps,
							 					"latinia");
	}
	public static LatiniaServiceAPIData createFrom(final XMLPropertiesForAppComponent latiniaProps,
										   		   final String propsRootNode) {
		// ensure the root node
		String thePropsRootNode = Strings.isNullOrEmpty(propsRootNode) ? "latinia" : propsRootNode;
		
		// [1] - Get the latinia service url
		Url latiniaWSUrl = latiniaProps.propertyAt(propsRootNode + "/latinia/wsURL")
									   .asUrl("http://svc.extra.integracion.jakina.ejiedes.net/ctxapp/W91dSendSms?WSDL");
		
		// [2] - Load the config from the properties file
		//            <authentication>
		//				  <enterprise>
		//                		<login>INNOVUS</login>
		//                		<user>innovus.superusuario</user>
		//                		<password>MARKSTAT</password>
		//				  </enterprise>
		//				  <clientApp>
		//                		<productId>X47B</productId>
		//                		<contractId>2066</contractId>
		//                		<password>X47N</password>
		//				  </clientApp>
		//            </authentication>
		AppCode enterpriseLogin = latiniaProps.propertyAt(thePropsRootNode + "/latinia/authentication/enterprise/login").asAppCode("INNOVUS");
		UserCode publicUser = latiniaProps.propertyAt(thePropsRootNode + "/latinia/authentication/enterprise/user").asUserCode("innovus.superusuario");
		Password publicPassword = latiniaProps.propertyAt(thePropsRootNode + "/latinia/authentication/enterprise/password").asPassword("MARKSTAT");
		AppCode productId = latiniaProps.propertyAt(thePropsRootNode + "/latinia/authentication/clientApp/productId").asAppCode("X47B");
		UserCode contractId = latiniaProps.propertyAt(thePropsRootNode + "/latinia/authentication/clientApp/contractId").asUserCode("2066");
		Password password = latiniaProps.propertyAt(thePropsRootNode + "/latinia/authentication/clientApp/password").asPassword("X47B");
		
		// [2] - Builde the config
		LatiniaServiceAPIData outCfg = new LatiniaServiceAPIData(latiniaWSUrl,
												 				 enterpriseLogin,
												 				 publicUser,publicPassword,
												 				 productId,contractId,password);
		return outCfg;
	}
}
