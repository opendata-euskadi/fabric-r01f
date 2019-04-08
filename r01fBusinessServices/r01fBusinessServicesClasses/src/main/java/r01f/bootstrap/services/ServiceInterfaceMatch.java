package r01f.bootstrap.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.patterns.Memoized;
import r01f.reflection.ReflectionUtils;
import r01f.services.core.CoreService;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceInterface;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.util.types.Strings;

/**
 * Encapsulates a {@link ServiceInterface} match with a core impl or a proxy 
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServiceInterfaceMatch
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Class<? extends ServiceInterface> _serviceInterfaceType;
	
	@Getter private final CoreAppCode _coreAppCode;
	@Getter private final CoreModule _coreModule;
	@Getter private final Class<? extends ServiceInterface> _proxyOrImplMatchingType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient Memoized<Boolean> _isProxy = new Memoized<Boolean>() {
																	@Override
																	protected Boolean supply() {
																		return _proxyOrImplMatchingType != null 
																					? ReflectionUtils.isImplementing(_proxyOrImplMatchingType,ServiceProxyImpl.class)
																					: false; 
																	}
														 };
	public boolean isProxy() {
		return _isProxy.get();
	}
	private final transient Memoized<Boolean> _isCoreImpl = new Memoized<Boolean>() {
																	@Override
																	protected Boolean supply() {
																		return _proxyOrImplMatchingType != null 
																					? ReflectionUtils.isImplementing(_proxyOrImplMatchingType,CoreService.class)
																					: false;
																	}
														 };
	public boolean isCoreImpl() {
		return _isCoreImpl.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean isForCoreWith(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return _coreAppCode.is(coreAppCode) && _coreModule.is(coreMod);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} > {} match with {} for core {}.{}",
								  _serviceInterfaceType,
								  this.isProxy() ? "PROXY" : "CORE IMPL",
								  _proxyOrImplMatchingType,_coreAppCode,_coreModule);
	}
}
