package r01f.bootstrap.services;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.services.core.CoreService;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceInterface;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.util.types.collections.CollectionUtils;

/**
 * Encapsulates a collection of {@link ServiceInterfaceMatch} object
 */
@Slf4j
@Accessors(prefix="_")
public class ServiceInterfacesMatchings 
  implements Iterable<ServiceInterfaceMatch>,
  			 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ClientApiAppCode _clientApiAppCode;
			private final Collection<ServiceInterfaceMatch> _serviceInterfacesMatchings;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private ServiceInterfacesMatchings(final ClientApiAppCode clientApiAppCode) {
		_clientApiAppCode = clientApiAppCode;
		_serviceInterfacesMatchings = Lists.newArrayList();
	}
	private ServiceInterfacesMatchings(final ClientApiAppCode clientApiAppCode,
									   final int expSize) {
		_clientApiAppCode = clientApiAppCode;
		_serviceInterfacesMatchings = expSize > 0 ? Lists.<ServiceInterfaceMatch>newArrayListWithExpectedSize(expSize) 
												  : Lists.<ServiceInterfaceMatch>newArrayList();
	}
	public static ServiceInterfacesMatchings create(final ClientApiAppCode clientApiAppCode) {
		return new ServiceInterfacesMatchings(clientApiAppCode);
	}
	public static ServiceInterfacesMatchings create(final ClientApiAppCode clientApiAppCode,
													final Collection<Class<? extends ServiceInterface>> serviceIfaces) {
		return new ServiceInterfacesMatchings(clientApiAppCode,
											  serviceIfaces.size());
	}
	public static ServiceInterfacesMatchings createEmpty(final ClientApiAppCode clientApiAppCode) {
		return new ServiceInterfacesMatchings(clientApiAppCode,
											  0);
	}
	public static ServiceInterfacesMatchings createWithExpectedSize(final ClientApiAppCode clientApiAppCode,
																	final int size) {
		return new ServiceInterfacesMatchings(clientApiAppCode,
											  size);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ADD
/////////////////////////////////////////////////////////////////////////////////////////
	public ServiceInterfacesMatchings addMatching(final ServiceInterfaceMatch match) {
		_serviceInterfacesMatchings.add(match);
		return this;
	}
	public ServiceInterfacesMatchings addMatching(final Class<? extends ServiceInterface> serviceInterfaceType,
											 	  final CoreAppCode coreAppCode,final CoreModule coreMod,
											 	  final Class<? extends ServiceInterface> proxyOrImplMatchingType) {
		final ServiceInterfaceMatch match = new ServiceInterfaceMatch(serviceInterfaceType,
															    coreAppCode,coreMod,proxyOrImplMatchingType);
		return this.addMatching(match);
	}
	public ServiceInterfacesMatchings consolidateWith(final ServiceInterfacesMatchings other) {
		if (other == null) return this;
		
		if (other.getClientApiAppCode().isNOT(this.getClientApiAppCode())) throw new IllegalArgumentException("Cannot consolidate service interface matchings for client api appCode=" + this.getClientApiAppCode() + " with client api appCode=" + other.getClientApiAppCode());
		for (final ServiceInterfaceMatch otherMatch : other) {
			this.addMatching(otherMatch);
		}
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasData() {
		return CollectionUtils.hasData(_serviceInterfacesMatchings);
	}
	public boolean isEmpty() {
		return CollectionUtils.isNullOrEmpty(_serviceInterfacesMatchings);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FILTERING
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<ServiceInterfaceMatch> getServiceInterfacesMatchingsFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return Lists.newArrayList(this.iteratorFor(coreAppCode,coreMod));
	}
	public Collection<ServiceInterfaceMatch> getServiceInterfacesCoreImplMatchingsFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return Lists.newArrayList(this.coreImplMathingsIteratorFor(coreAppCode,coreMod));
	}
	public Collection<ServiceInterfaceMatch> getServiceInterfacesProxyMatchingsFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return Lists.newArrayList(this.proxyMathingsIteratorFor(coreAppCode,coreMod));
	}
	public Collection<ServiceInterfaceMatch> getServiceInterfacesMatchingsFor(final Class<? extends ServiceInterface> serviceInterfaceType) {
		return Lists.newArrayList(this.iteratorFor(serviceInterfaceType));
	}
	public Collection<ServiceInterfaceMatch> getServiceInterfacesMatchingsFor(final CoreAppCode coreAppCode,final CoreModule coreMod,
																			  final Class<? extends ServiceInterface> serviceInterfaceType) {
		return Lists.newArrayList(this.iteratorFor(coreAppCode,coreMod,
												   serviceInterfaceType));
	}
	public boolean existsCoreImplMatchingFor(final CoreAppCode coreAppCode,final CoreModule coreMod,
										     final Class<? extends ServiceInterface> serviceInterfaceType) {
		boolean outExists = false;
		final Collection<ServiceInterfaceMatch> ifaceMatchings = this.getServiceInterfacesMatchingsFor(coreAppCode,coreMod,
																								 serviceInterfaceType);
		if (CollectionUtils.hasData(ifaceMatchings)) {
			for (final ServiceInterfaceMatch match : ifaceMatchings) {
				if (match.isCoreImpl()) {
					outExists = true;
					break;
				}
			}
		}
		return outExists;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<ServiceInterfaceMatch> iterator() {
		return _serviceInterfacesMatchings.iterator();
	}
	public Iterable<ServiceInterfaceMatch> iterableFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return CollectionUtils.iterableFrom(this.iteratorFor(coreAppCode,coreMod));
	}
	public Iterator<ServiceInterfaceMatch> iteratorFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return FluentIterable.from(this)
							 .filter(_predicateForMatchWith(coreAppCode,coreMod))
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> iterableFor(final Class<? extends ServiceInterface> serviceInterfaceTyp) {
		return CollectionUtils.iterableFrom(this.iteratorFor(serviceInterfaceTyp));
	}
	public Iterator<ServiceInterfaceMatch> iteratorFor(final Class<? extends ServiceInterface> serviceInterfaceType) {
		return FluentIterable.from(this)
							 .filter(new Predicate<ServiceInterfaceMatch>() {
											@Override
											public boolean apply(final ServiceInterfaceMatch match) {
												return match.getServiceInterfaceType() == serviceInterfaceType;
											}
							 		 })
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> iterableFor(final CoreAppCode coreAppCode,final CoreModule coreMod,
													   final Class<? extends ServiceInterface> serviceInterfaceTyp) {
		return CollectionUtils.iterableFrom(this.iteratorFor(coreAppCode,coreMod,
											  			     serviceInterfaceTyp));
	}
	public Iterator<ServiceInterfaceMatch> iteratorFor(final CoreAppCode coreAppCode,final CoreModule coreMod,
													   final Class<? extends ServiceInterface> serviceInterfaceType) {
		return FluentIterable.from(this.iterableFor(serviceInterfaceType))
							 .filter(_predicateForMatchWith(coreAppCode,coreMod))
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> proxyMathingsIterable() {
		return CollectionUtils.iterableFrom(this.proxyMathingsIterator());
	}
	public Iterator<ServiceInterfaceMatch> proxyMathingsIterator() {
		return FluentIterable.from(this)
							 .filter(new Predicate<ServiceInterfaceMatch>() {
											@Override
											public boolean apply(final ServiceInterfaceMatch match) {
												return match.isProxy();
											}
							 		 })
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> proxyMathingsIterableFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return CollectionUtils.iterableFrom(this.proxyMathingsIteratorFor(coreAppCode,coreMod));
	}
	public Iterator<ServiceInterfaceMatch> proxyMathingsIteratorFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return FluentIterable.from(this.proxyMathingsIterable())
							 .filter(_predicateForMatchWith(coreAppCode,coreMod))
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> coreImplMathingsIterable() {
		return CollectionUtils.iterableFrom(this.coreImplMathingsIterator());
	}
	public Iterator<ServiceInterfaceMatch> coreImplMathingsIterator() {
		return FluentIterable.from(this)
							 .filter(new Predicate<ServiceInterfaceMatch>() {
											@Override
											public boolean apply(final ServiceInterfaceMatch match) {
												return match.isCoreImpl();
											}
							 		 })
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> coreImplMathingsIterableFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return CollectionUtils.iterableFrom(this.coreImplMathingsIteratorFor(coreAppCode,coreMod));
	}
	public Iterator<ServiceInterfaceMatch> coreImplMathingsIteratorFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return FluentIterable.from(this.coreImplMathingsIterable())
							 .filter(_predicateForMatchWith(coreAppCode,coreMod))
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> proxyMatchingsWithoutCoreMatchingIterable() {
		return CollectionUtils.iterableFrom(this.proxyMatchingsWithoutCoreMatchingIterator());
	}
	public Iterator<ServiceInterfaceMatch> proxyMatchingsWithoutCoreMatchingIterator() {
		return FluentIterable.from(this)
							 .filter(new Predicate<ServiceInterfaceMatch>() {
											@Override
											public boolean apply(final ServiceInterfaceMatch match) {
												// a proxy without a core impl
												return match.isProxy()
													&& !ServiceInterfacesMatchings.this.existsCoreImplMatchingFor(match.getCoreAppCode(),match.getCoreModule(),
																												  match.getServiceInterfaceType());
											}
							 		 })
							 .iterator();
	}
	public Iterable<ServiceInterfaceMatch> proxyMatchingsWithoutCoreMatchingIterableFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return CollectionUtils.iterableFrom(this.proxyMatchingsWithoutCoreMatchingIteratorFor(coreAppCode,coreMod));
	}
	public Iterator<ServiceInterfaceMatch> proxyMatchingsWithoutCoreMatchingIteratorFor(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return FluentIterable.from(this.proxyMatchingsWithoutCoreMatchingIterable())
							 .filter(_predicateForMatchWith(coreAppCode,coreMod))
							 .iterator();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final Predicate<ServiceInterfaceMatch> _predicateForMatchWith(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return new Predicate<ServiceInterfaceMatch>() {
						@Override
						public boolean apply(final ServiceInterfaceMatch match) {
							// a proxy without a core impl
							return match.isForCoreWith(coreAppCode,coreMod);
						}
	 		  };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHECK
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks that exists one and only one matching for every service interface
	 * @param serviceIfaceTypes
	 */
	public void checkMatchingsOrThrowfor(final Collection<Class<? extends ServiceInterface>> serviceIfaceTypes) {
		if (CollectionUtils.isNullOrEmpty(serviceIfaceTypes)) return;
		
		for (final Class<? extends ServiceInterface> serviceIfaceType : serviceIfaceTypes) {
			// every service interface MUST match at least a CORE impl or a PROXY
			final Collection<ServiceInterfaceMatch> matches = this.getServiceInterfacesMatchingsFor(serviceIfaceType);
			if (CollectionUtils.isNullOrEmpty(matches)) _noMatchFoundFor(serviceIfaceType);
			
			// Multiple matchings for the same core impl or proxy are NOT possible
			int numMatchingsForSameCoreImplOrProxy = 0;
			for (final ServiceInterfaceMatch match : matches) {
				for (final ServiceInterfaceMatch otherMatch : matches) {
					if (match.getProxyOrImplMatchingType() == otherMatch.getProxyOrImplMatchingType())  numMatchingsForSameCoreImplOrProxy = numMatchingsForSameCoreImplOrProxy + 1;
					if (numMatchingsForSameCoreImplOrProxy > 1) _multipleMatchesFoundFor(serviceIfaceType);
				}
			}
			
			// Multiple CORE matchings for the same ServiceInterface are NOT possible
			// (but a ServiceInterface can match a CORE impl and multiple PROXIES)
			final Collection<ServiceInterfaceMatch> coreMatches = FluentIterable.from(matches)
																		  .filter(new Predicate<ServiceInterfaceMatch>() {
																							@Override
																							public boolean apply(final ServiceInterfaceMatch match) {
																								return match.isCoreImpl();
																							}
																		  		  })
																		  .toList();
			if (coreMatches.size() > 1) _multipleCoreMatchesFoundFor(serviceIfaceType,
															 		 matches);
		}
	}
	private void _noMatchFoundFor(final Class<? extends ServiceInterface> serviceIfaceType) {
		log.error("Could NOT find a matching for {}-implementing type {}, either as {} neither as {}",
				  ServiceInterface.class.getSimpleName(),serviceIfaceType,
				  CoreService.class.getSimpleName(),ServiceProxyImpl.class.getSimpleName());
		log.warn("Service interfaced matchings:\n{}",
				 this.debugInfo());
		throw new IllegalStateException(String.format("Could NOT find a matching for %s-implementing type %s, either as %s neither as %s",
													  ServiceInterface.class.getSimpleName(),serviceIfaceType,
													  CoreService.class.getSimpleName(),ServiceProxyImpl.class.getSimpleName()));
	}
	private void _multipleMatchesFoundFor(final Class<? extends ServiceInterface> serviceIfaceType) {
		log.error("Multiple matchings for {}-implementing type {} found",
				  ServiceInterface.class.getSimpleName(),serviceIfaceType);
		log.warn("Service interfaced matchings:\n{}",
				 this.debugInfo());
		throw new IllegalStateException(String.format("Multiple matchings for %s-implementing type %s",
													  ServiceInterface.class.getSimpleName(),serviceIfaceType));
	}
	private void _multipleCoreMatchesFoundFor(final Class<? extends ServiceInterface> serviceIfaceType,
										  	  final Collection<ServiceInterfaceMatch> matches) {
		log.error("Multiple CORE matchings found for {}-implementing type {}",
				  ServiceInterface.class.getSimpleName(),serviceIfaceType);
		log.error("\tMatchings:");
		for (final ServiceInterfaceMatch match : matches) log.error("{}",match.debugInfo());
	
		throw new IllegalStateException(String.format("Multiple CORE matchings found for %s-implementing type %s",
													  ServiceInterface.class.getSimpleName(),serviceIfaceType));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		final StringBuilder sb = new StringBuilder(_serviceInterfacesMatchings.size() * 100);
		for (final Iterator<ServiceInterfaceMatch> matchIt = _serviceInterfacesMatchings.iterator(); matchIt.hasNext(); ) {
			final ServiceInterfaceMatch match = matchIt.next();
			sb.append("- ").append(match.debugInfo());
			if (matchIt.hasNext()) sb.append("\n");
		}
		return sb;
	}
	
}
