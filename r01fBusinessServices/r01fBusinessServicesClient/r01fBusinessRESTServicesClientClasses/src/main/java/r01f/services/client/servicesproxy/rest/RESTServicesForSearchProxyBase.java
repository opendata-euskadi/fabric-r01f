package r01f.services.client.servicesproxy.rest;

import java.util.Collection;

import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.model.search.SearchFilterForModelObject;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderForModelObjectPersistence;
import r01f.services.interfaces.SearchServicesForModelObject;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;

public abstract class RESTServicesForSearchProxyBase<F extends SearchFilterForModelObject,I extends SearchResultItemForModelObject<?>> 
              extends RESTServicesProxyBase
           implements SearchServicesForModelObject<F,I> {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE
/////////////////////////////////////////////////////////////////////////////////////////
	private final DelegateForRawRESTSearch<F,I> _rawSearchDelegate;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public <P extends RESTServiceResourceUrlPathBuilderForModelObjectPersistence<? extends OID>>
		   RESTServicesForSearchProxyBase(final Marshaller marshaller,
									   	  final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  servicesRESTResourceUrlPathBuilder);
		_rawSearchDelegate = new DelegateForRawRESTSearch<F,I>(marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int countRecords(final SecurityContext securityContext,
							final F filter) {
		throw new UnsupportedOperationException("NOT yet implemented!!");	// TODO implement REST countRecords proxy
	}
	@Override
	public <U extends PersistableObjectOID> Collection<U> filterRecordsOids(final SecurityContext securityContext,
													       				    final F filter) {
		throw new UnsupportedOperationException("NOT yet implemented!!");	// TODO implement REST filterRecordsOids proxy
	}
	@Override
	public SearchResults<F,I> filterRecords(final SecurityContext securityContext,
										    final F filter,final Collection<SearchResultsOrdering> ordering,
										    final int firstRowNum,final int numberOfRows) {
		Url restResourceUrl = this.composeURIFor(UrlPath.from("index"));
		return _rawSearchDelegate.doSEARCH(restResourceUrl,
										   securityContext,	
									       filter,ordering,
										   firstRowNum,numberOfRows);
	}
}
