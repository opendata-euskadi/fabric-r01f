package r01f.bootstrap;

import com.google.common.annotations.GwtIncompatible;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.ProvisionListener;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.configproperties.ConfigPropertiesGuiceModule;
import r01f.xmlproperties.XMLPropertiesGuiceModule;

@Slf4j
@EqualsAndHashCode				// This is important for guice modules
@GwtIncompatible
@NoArgsConstructor
public class R01FBootstrapGuiceModule
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONFIGURE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(Binder binder) {
		// Some debugging
		if (log.isTraceEnabled()) {
			binder.bindListener(Matchers.any(),
								new ProvisionListener() {
					@Override @SuppressWarnings("rawtypes")
					public void onProvision(final ProvisionInvocation provision) {
						log.trace(">> Guice provisioning: {}",provision.getBinding());
					}
			});
		}
        log.warn("[START] R01F Bootstraping ________________________________");

		//binder.requireExplicitBindings();	// All the injected members MUST be defined at the guice modules

		binder.install(new XMLPropertiesGuiceModule());		// XMLProperties
		binder.install(new ConfigPropertiesGuiceModule());	// Configs
//		binder.install(new I18NGuiceModule());				// I18N
//      binder.install(new GUIDDispenserGuiceModule());		// GUIDDispenser
        log.warn("  [END] R01F Bootstraping ________________________________");
	}
}
