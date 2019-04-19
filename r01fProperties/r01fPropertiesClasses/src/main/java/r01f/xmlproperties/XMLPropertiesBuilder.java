package r01f.xmlproperties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.patterns.IsBuilder;
import r01f.xmlproperties.XMLPropertiesForAppCache.XMLPropertiesForAppCacheFactory;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class XMLPropertiesBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static XMLPropertiesCacheUsageStep create() {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesCacheUsageStep();
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final AppCode appCode) {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesForAppCacheUsageStep(appCode);
	}
	public static XMLPropertiesForAppCacheUsageStep createForApp(final String appCode) {
		return XMLPropertiesBuilder.createForApp(AppCode.forId(appCode));
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final AppCode appCode,final AppComponent component) {
		return new XMLPropertiesBuilder() { /* nothing */ }
						.new XMLPropertiesForAppComponentCacheUsageStep(appCode,component);
	}
	public static XMLPropertiesForAppComponentCacheUsageStep createForAppComponent(final String appCode,final String component) {
		return XMLPropertiesBuilder.createForAppComponent(AppCode.forId(appCode),AppComponent.forId(component));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesCacheUsageStep {
		public XMLProperties notUsingCache() {
			return _buildXMLProperties(false);
		}
		public XMLProperties usingCache() {
			return _buildXMLProperties(true);
		}
		private XMLProperties _buildXMLProperties(final boolean useCache) {
			return new XMLPropertiesImpl(new XMLPropertiesForAppCacheFactory() {
												@Override
												public XMLPropertiesForAppCache createFor(final Environment env,
																						  final AppCode appCode,
																						  final int componentsNumberEstimation) {
													return new XMLPropertiesForAppCache(env,
																						appCode,
																						componentsNumberEstimation,
																						useCache);
												}
								   	 	 });
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesForAppCacheUsageStep {
		private final AppCode _appCode;
		public XMLPropertiesForApp notUsingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.notUsingCache();
			return props.forApp(_appCode);
		}
		public XMLPropertiesForApp usingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.usingCache();
			return props.forApp(_appCode);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class XMLPropertiesForAppComponentCacheUsageStep {
		private final AppCode _appCode;
		private final AppComponent _appComponent;
		public XMLPropertiesForAppComponent notUsingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.notUsingCache();
			return props.forAppComponent(_appCode,_appComponent);
		}
		public XMLPropertiesForAppComponent usingCache() {
			XMLProperties props = new XMLPropertiesCacheUsageStep()
											.usingCache();
			return props.forAppComponent(_appCode,_appComponent);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method is used in guice binding to:
	 * <pre>
	 * 		Annotate an instance of {@link XMLPropertiesForApp} or {@link XMLPropertiesForAppComponent}
	 * 		to be injected by Guice
	 * </pre>
	 * Ej:
	 * <pre class='brush:java'>
	 * 		public class MyType {
	 * 			@Inject @XMLPropertiesComponent("r01m") XMLPropertiesForApp _manager;
	 * 			...
	 * 		}
	 * </pre>
	 * or
	 * <pre class='brush:java'>
	 * 		public class MyType {
	 * 			@Inject @XMLPropertiesComponent("default") 
	 * 			XMLPropertiesForAppComponent _component;
	 * 			...
	 * 		}
	 * </pre> 
	 * The guice bindings are:
	 * <pre class='brush:java'>
	 * 		@Override
	 *		public void configure(Binder binder) {
	 *			binder.bind(XMLPropertiesForApp.class).annotatedWith(XMLProperties.named("r01m")
	 *				  .toProvider(new XMLPropertiesForAppGuiceProvider("r01m");
	 *			binder.bind(XMLPropertiesForAppComponent.class).annotatedWith(XMLProperties.named("default"))
	 *				  .toInstance(new XMLPropertiesForAppComponent("default")
	 *				  .in(Singleton.class);
	 *		}
	 * </pre>
	 * Returns a {@link XMLPropertiesComponent}
	 */
//	public static XMLPropertiesComponent named(final String name) {
//		return new XMLPropertiesComponentImpl(name);
//	}
}
