package r01f.locale.services;

import com.google.common.annotations.GwtIncompatible;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bundles.ResourceBundleControl;
import r01f.bundles.ResourceBundleControlBuilder;
import r01f.patterns.IsBuilder;

/**
 * Factory of {@link I18NService} objects.
 * A {@link ResourceBundleControl} instance should be provided to the instances of this type
 */

@Accessors(prefix="_")
@GwtIncompatible
@NoArgsConstructor 
public class I18NServiceBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	I18NServiceBuilder(final ResourceBundleControl resBundleControl) {
		this();
		_resourceBundleControl = resBundleControl;
	}
    /**
     * {@link ResourceBundleControl} type instance factory that maintains a cache of {@link ResourceBundleControl}
     * instances
     * The {@link ResourceBundleControl} type manages the loading/reloading of resources
     */
    private transient ResourceBundleControl _resourceBundleControl;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a {@link I18NServiceBuilder} instance using a {@link ResourceBundleControl} instance that
     * controls the loading & reloading of resources
     * This builder is the one to use when not using injection (guice)
     * <pre class='brush:java'>
     * 		ResourceBundleControl resBundleControl = ResourceBundleControlFactory.create(XMLProperties.create())
     * 																			 .forLoadingDefinitionAt(appCode,component,xPath);
     * 		I18NServiceFactory factory = I18NServiceFactory.create(resBundleControl);
     * </pre>
     * 
     * If guice is used, the {@link ResourceBundleControl} can be retrieved from an injected {@link ResourceBundleControlBuilder}
     * <pre class='brush:java'>
     * 		public class MyGuiceManagedType {
     * 			@Inject
     * 			private ResourceBundleControlFactory _resBundleControlFactory;
     * 
     * 			public void myMethod(...) {
     * 				ResourceBundleControl ctrl = _resBundleControlFactory.forLoadingDefinitionAt(appCode,component,xPath);
     * 				I18NServiceFactory factory = I18NServiceFactory.create(ctrl);
     * 			}
     * 		}
     * </pre>
     * @param resControlFactory
     * @return
     */
    public static I18NServiceBuilderBundleChainStep createUsing(final ResourceBundleControl resControl) {
    	return new I18NServiceBuilderBundleChainStep(resControl) { /*nothing*/};
    }

/////////////////////////////////////////////////////////////////////////////////////////
//  STEPS
/////////////////////////////////////////////////////////////////////////////////////////
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public static abstract class I18NServiceBuilderBundleChainStep {
    	private final ResourceBundleControl _resControl;
    	
		/**
		 * Creates an access to the messages using the {@link I18NService} object from:
		 * @param bundleChain comma separated bundle squence to find the messages
		 */
		public I18NServiceBuilderClassPathStep forBundleChain(final String bundleChain) {
			String[] bundleChainSplitted = bundleChain.split(",");
			return this.forBundleChain(bundleChainSplitted);
		}
		/**
		 * Creates an access to the messages using the {@link I18NService} object from.
		 * @param bundleChain bundle sequence to look for the messages
		 */
		public I18NServiceBuilderClassPathStep forBundleChain(final String... bundleChain) {
			return new I18NServiceBuilderClassPathStep(_resControl,bundleChain) { /* nothing */};
		}
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public static abstract class I18NServiceBuilderClassPathStep {
    	private final ResourceBundleControl _resControl;
    	private final String[] _bundleChain;
    	
    	public I18NService usingClassLoader(final ClassLoader classLoader) {
	    	I18NService outCfgProps = new I18NService(_resControl,
	    											  classLoader,
	    											  _bundleChain);
	    	return outCfgProps;
    	}
    	public I18NService usingDefaultClassLoader() {
	    	I18NService outCfgProps = new I18NService(_resControl,
	    											  _bundleChain);
	    	return outCfgProps;
    	}
    	public I18NService usingThreadContextClassLoader() {
	    	I18NService outCfgProps = new I18NService(_resControl,
	    											  Thread.currentThread().getContextClassLoader(),
	    											  _bundleChain);
	    	return outCfgProps;
    	}
    }
}
