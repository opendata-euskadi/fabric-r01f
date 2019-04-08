package r01f.bootstrap.services.core;

import com.google.inject.Module;

/**
 * Used when the REST core module DOES NOT exposes service interfaces
 */
public interface RESTCoreBootstrapGuiceModule
		 extends Module {
	// just a marker interface
}
