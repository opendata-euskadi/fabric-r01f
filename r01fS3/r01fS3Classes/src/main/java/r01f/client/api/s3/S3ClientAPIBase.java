package r01f.client.api.s3;

import java.util.Set;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.types.url.Url;
import r01f.util.types.Strings;
@Slf4j
abstract class S3ClientAPIBase {
///////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	 protected final AmazonS3 _s3Client;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private S3ClientAPIBase(final ClientConfiguration clientConfiguration,
			          		final AWSCredentials credentials,
			          		final Url awsEndPoint){
		_s3Client = _buildAmazonS3Client(clientConfiguration,credentials,awsEndPoint);
	 }
     @SuppressWarnings("null")
	public S3ClientAPIBase(final S3ClientConfig clientConfig){
    	// Checks if client config is not null
    	if (clientConfig == null) {
 			Throwables.throwUnchecked(new IllegalArgumentException(Strings.customized("In order to create instance of S3api, " +
 																					  "a client config, must be provided")));
 		}
    	if (clientConfig.getProperties() != null) {
    		log.warn("\n\n WARNING !!! Updating System Properties !!!"
    				+ " ( check : https://aws-amplify.github.io/aws-sdk-android/docs/reference/com/amazonaws/SDKGlobalConfiguration.html !");

    		Set<String> keys = clientConfig.getProperties().stringPropertyNames();
    		for (final String key : keys) {
    			log.warn("\n Setting property {} to value {} !!! ", key,
    															clientConfig.getProperties().getProperty(key));
     			System.setProperty(key,clientConfig.getProperties().getProperty(key));
    		};

    	}
		Url awsEndPoint = clientConfig.getEndPoint();
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSignerOverride("AWSS3V4SignerType");
		if (clientConfig.getProxySettings() != null
				&& clientConfig.getProxySettings().isEnabled() ){
			log.warn(".. building S3API. Connection to enpoint will be through a proxy");
			clientConfiguration.setProxyPort(clientConfig.getProxySettings().getProxyPort());
	        clientConfiguration.setProxyHost(clientConfig.getProxySettings().getProxyHost().asString());
	        clientConfiguration.setProxyUsername(clientConfig.getProxySettings().getUser().asString());
	        clientConfiguration.setProxyPassword(clientConfig.getProxySettings().getPassword().asString());
		} else {
			log.warn(".. building S3API. Connection to enpoint will be direct");
		}
		AWSCredentials credentials = new BasicAWSCredentials(clientConfig.getAccessKey().asString(),
				                                             clientConfig.getSecretKey().asString());
		_s3Client = _buildAmazonS3Client(clientConfiguration,credentials,awsEndPoint);
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	 private AmazonS3 _buildAmazonS3Client(final ClientConfiguration clientConfiguration,
			                               final AWSCredentials credentials,
			                               final Url awsEndPoint) {
	   return AmazonS3ClientBuilder.standard()
										.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsEndPoint.asString(), Regions.EU_CENTRAL_1.name()))
										.withPathStyleAccessEnabled(true)
										.withClientConfiguration(clientConfiguration)
										.withCredentials(new AWSStaticCredentialsProvider(credentials))
								   .build();
	 }

}
