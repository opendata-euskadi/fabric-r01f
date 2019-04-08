package r01f.internal;

import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Module;

import r01f.marshalling.json.GsonMarshaller;
import r01f.marshalling.json.GsonMarshallerBuilder;


/**
 * Guice Mapping for {@link GsonMarshaller}
 */
public class GsonMarshallerGuiceModule
  implements Module {

	@Override
	public void configure(final Binder binder) {
		binder.bind(Gson.class).toProvider(GSonProvider.class)
			  .in(Singleton.class);
	}
	/**
	 * Provider 
	 */
	static class GSonProvider 
	  implements Provider<Gson> {
			@Override
			public Gson get() {
				return GsonMarshallerBuilder.createGson();
			}
		
	}
	
}
