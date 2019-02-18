package r01f.configproperties;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * Guice module for the ResourceBundles 
 */
public class ConfigPropertiesGuiceModule 
  implements Module {
	
	@Override
	public void configure(final Binder binder) {
		// The ResourceBundleControlFactory mantains a CACHE of ResourceBundleControl
		// which encapsulates the ResourcesLoader and ResourcesReloadControl
        binder.bind(ConfigPropertiesBuilder.class)
        	  .in(Singleton.class);
	}
}
