package r01f.s3.services;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Module;
import com.google.inject.spi.Message;

import r01f.client.api.s3.S3ClientAPI;
import r01f.client.api.s3.S3ClientConfig;

public class S3GuiceModule
	implements Module {
///////////////////////////////////////////////////////////////////////
/// MEMBERS
///////////////////////////////////////////////////////////////////////	
	private final S3ClientConfig _clientConfig;
///////////////////////////////////////////////////////////////////////
/// CONSTRUCTOR
///////////////////////////////////////////////////////////////////////	
	public S3GuiceModule(final S3ClientConfig clientConfig ) {
		_clientConfig = clientConfig;
	}
///////////////////////////////////////////////////////////////////////
/// CONSTRUCTOR
///////////////////////////////////////////////////////////////////////	
	@Override
	public void configure(final Binder binder) {
		if (_clientConfig.validate().isNOTValid()) {			
			throw new ConfigurationException(new Iterable<Message>() {										
														@Override
														public Iterator<Message> iterator() {
															Collection<Message> error = Lists.newArrayList();
															error.add(new Message(_clientConfig,_clientConfig.debugInfo().toString()));
															return error.iterator();
														}}); 
		}
		binder.bind(S3ClientAPI.class)
		      .toInstance(new S3ClientAPI(_clientConfig));	
	}
}