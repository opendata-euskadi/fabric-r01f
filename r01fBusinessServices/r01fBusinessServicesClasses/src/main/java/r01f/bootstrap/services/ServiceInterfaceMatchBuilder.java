package r01f.bootstrap.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceInterface;

/**
 * Builder for {@link ServiceInterfaceMatch} objects
 * <pre class='brush:java'>
 * 		ServiceInterfaceMatch match = ServiceInterfaceMatchBuilder.serviceInterface(ifaceType)
 * 																  .at(apiAppCode)
 * 																  .matchesWith(proxyOrImplType)
 * 																  .forCore(coreAppCode,coreMod);
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServiceInterfaceMatchBuilder 
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServiceInterfaceMatchBuilderClientMatchStep serviceInterface(final Class<? extends ServiceInterface> serviceInterfaceType) {
		return new ServiceInterfaceMatchBuilder() { /* nothing */ }
						.new ServiceInterfaceMatchBuilderClientMatchStep(serviceInterfaceType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor
	public final class ServiceInterfaceMatchBuilderClientMatchStep {
		private final Class<? extends ServiceInterface> _serviceInterfaceType;
		public ServiceInterfaceMatchBuilderCoreStep matchesWith(final Class<? extends ServiceInterface> proxyOrImplMatchingType) {
			return new ServiceInterfaceMatchBuilderCoreStep(_serviceInterfaceType,
															proxyOrImplMatchingType);	
		}
	}
	@RequiredArgsConstructor
	public final class ServiceInterfaceMatchBuilderCoreStep {
		private final Class<? extends ServiceInterface> _serviceInterfaceType;
		private final Class<? extends ServiceInterface> _proxyOrImplMatchingType;
		
		public ServiceInterfaceMatch forCore(final CoreAppCode coreAppCode,final CoreModule coreModule) {
			return new ServiceInterfaceMatch(_serviceInterfaceType,
											 coreAppCode,coreModule,_proxyOrImplMatchingType);
		}
	}
}
