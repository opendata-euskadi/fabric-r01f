package r01f.s3.services;

import com.google.inject.Binder;
import com.google.inject.Module;

import r01f.client.api.s3.S3ClientAPI;
import r01f.client.api.s3.S3ClientConfig;

public class S3GuiceModule
	implements Module {
	
	private final S3ClientConfig _clientConfig;
	
	public S3GuiceModule(final S3ClientConfig clientConfig ) {
		_clientConfig = clientConfig;
	}
	@Override
	public void configure(final Binder binder) {
		binder.bind(S3ClientAPI.class)
		      .toInstance(new S3ClientAPI(_clientConfig));
	
	}
}