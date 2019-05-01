
package r01f.client.api.s3;

import java.util.Properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.httpclient.HttpClientProxySettings;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.s3.S3BucketName;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.SelfValidates;


@NoArgsConstructor
@MarshallType(as="s3ClientConfig")
@Accessors(prefix="_")
public class S3ClientConfig
  implements Debuggable,
  			 ContainsConfigData,
  			 SelfValidates<S3ClientConfig> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
   	@MarshallField(as="accessKey")
    @Getter private UserCode  _accessKey;

    @MarshallField(as="secretKey")
    @Getter private Password  _secretKey;

   	@MarshallField(as="endPoint")
    @Getter @Setter private Url _endPoint;

   	@MarshallField(as="proxySettings")
    @Getter @Setter private HttpClientProxySettings _proxySettings;

 	@MarshallField(as="properties")
    @Getter @Setter private Properties _properties;

 	@MarshallField(as="bucketInfo")
    @Getter @Setter private S3BucketInfo _bucketInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("\n S3 Config :{ \n ").append(Strings.customized(" Endpoint {}",
	    																   _endPoint ));
	    outDbgInfo.append("\n").append(Strings.customized("secretKey {}",
	    												  _secretKey  != null ? "*****" : " is null!" ));
	    outDbgInfo.append("\n").append(Strings.customized("accessKey {}",
	    												  _accessKey  != null ? "*****" : " is null!" ));
	    outDbgInfo.append("\n").append(Strings.customized("proxyEnabled  {}",
	    												  _proxySettings  != null && _proxySettings.isEnabled() ? " yes!" : " no!" ));
		outDbgInfo.append(" } \n")	;
	    return outDbgInfo.toString();
	}

 	@Override
	public ObjectValidationResult<S3ClientConfig> validate() {
 		boolean valid =  (_endPoint != null )
 			          && (_accessKey != null )
 			          && (_secretKey != null );
		return valid ? ObjectValidationResultBuilder.on(this)
												   .isValid()
					 : ObjectValidationResultBuilder.on(this)
												   .isNotValidBecause(" Somo of the client configurarion are not valid, fe {}",_endPoint);


	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
// ....Some bucket info, if needed....
//////////////////////////////////////////////////////////////////////////////////////////////////////////
 	@MarshallType(as="bucketInfo")
 	@Accessors(prefix="_")
 	public static class S3BucketInfo
 		     implements ContainsConfigData {
 	    @MarshallField(as="defaultBucket")
 	    @Getter private S3BucketName  _defaultBucket;

 	 	@MarshallField(as="properties")
 	    @Getter @Setter private Properties _properties;
 	}
}

