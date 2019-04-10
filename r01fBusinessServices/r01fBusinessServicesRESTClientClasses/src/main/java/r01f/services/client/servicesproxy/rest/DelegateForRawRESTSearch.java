package r01f.services.client.servicesproxy.rest;

import java.io.IOException;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpResponse;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchFilterAsCriteriaString;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.ServiceProxyException;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;

@Slf4j
public class DelegateForRawRESTSearch<F extends SearchFilter,I extends SearchResultItem>
	 extends DelegateForRawREST {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public DelegateForRawRESTSearch(final Marshaller marshaller) {
		super(marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
	public SearchResults<F,I> doSEARCH(final Url restResourceUrl,
									   final SecurityContext securityContext,
 								       final SearchFilter filter,final Collection<SearchResultsOrdering> ordering,
 								       final long firstRowNum,final int numberOfRows) {
		log.trace("\t\tSEARCH resource: {}",restResourceUrl);
		
		
		// [1] - Serialize params
		String securityContextXml = _marshaller.forWriting().toXml(securityContext);
		SearchFilterAsCriteriaString filterCriteriaStr = filter.toCriteriaString();
		
		// [2] - Do http request
		HttpResponse httpResponse = null;
		try {
			Url url = restResourceUrl.joinWith(UrlQueryString.fromParams(UrlQueryStringParam.of("filter",filterCriteriaStr.asString()),
																	     UrlQueryStringParam.of("start",firstRowNum),
																		 UrlQueryStringParam.of("items",numberOfRows)));
			httpResponse = HttpClient.forUrl(url)		
						             .withHeader("securityContext",securityContextXml)
									 .GET()
									 	.getResponse()
									 		.directNoAuthConnected();
		} catch(IOException ioEx) {
			log.error("Error connecting to {}",restResourceUrl,ioEx);
			throw new ServiceProxyException(ioEx);
		}		
		
		// [2] - De-serialize response
		SearchResults<F,I> outSearchResult = this.mapHttpResponseForSearchResults(securityContext,
																			      restResourceUrl,
																			      httpResponse);
		return outSearchResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public SearchResults<F,I> mapHttpResponseForSearchResults(final SecurityContext securityContext,
															  final Url restResourceUrl,
														   	  final HttpResponse httpResponse) {
		SearchResults<F,I> outSearchResults = this.getResponseToResultMapper()
														.mapHttpResponse(securityContext,
																   		 restResourceUrl,
																   		 httpResponse,
																   		 SearchResults.class);
		return outSearchResults;
	}
}
