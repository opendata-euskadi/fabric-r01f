package r01f.xmlproperties;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.Environment;

public class GuiceManagedXMLPropertiesForAppCache 
	 extends XMLPropertiesForAppCache {
	/**
	 * Cache size based constructor<br>
	 * {@link AssistedInject} is used to create {@link XMLPropertiesForAppCache} instances since appCode, prop number estimation and cache usage
	 * are NOT known at compile time
	 * {@link AssistedInject} usage:
	 * <ul>
	 * <ol>Create an interface for the object to be created (ie {@link XMLPropertiesForAppCache}) factory: {@link XMLPropertiesForAppCacheFactory}
	 * 	This factory must have a create method with the object's constructor params that are only known at runtime:
	 * 		<pre class="brush:java">
	 * 			public XMLPropertiesForAppCache createFor(String appCode,int componentsNumberEstimation,
	 * 													  boolean useCache);
	 *		</pre>
	 *	(BEWARE that this interface DOES NOT have to be implemented, guice will implement it automatically).
	 *</ol>
	 *<ol>The constructor of the type being created (ie {@link XMLPropertiesForAppCache}) MUST have a constructor with the SAME params in th SAME order
	 *	as the ones at the factory createXX method; they MUST be annotated with @Assisted
	 *	<pre class="brush:java">
	 *		@Inject
	 *		public XMLPropertiesForAppCacheImpl(@Assisted final String appCode,
	 *											@Assisted final int componentsNumberEstimation,
	 *											@Assisted final boolean useCache)
	 *	</pre>
	 *</ol>
	 *<ol>At the guice module everything is tied together:
	 *	<pre class="brush:java">
	 *		Module assistedModuleForPropertiesCacheFactory = new FactoryModuleBuilder().implement(XMLPropertiesForAppCache.class,
	 *																							  XMLPropertiesForAppCache.class)
	 *																				   .build(XMLPropertiesForAppCacheFactory.class);
	 *		binder.install(assistedModuleForPropertiesCacheFactory);
	 *	</pre>
	 *</ol>
	 *</ul>
	 * @param appCode appCode
	 * @param componentsNumberEstimation an approximation of the number of properties
	 * @param useCache
	 */
    @Inject
	public GuiceManagedXMLPropertiesForAppCache(@XMLPropertiesEnvironment final Environment env,		// see XMLPropertiesGuiceModule
							      				@Assisted final AppCode appCode,
							      				@Assisted final int componentsNumberEstimation) {
    	super(env,
    		  appCode,
    		  componentsNumberEstimation,
    		  true);	// always USE CACHE when injected by guice
    }

}
