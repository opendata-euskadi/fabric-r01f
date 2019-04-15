package r01f.services.client.servicesproxy.rest;

import lombok.Getter;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.index.IndexManagementCommand;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilder;
import r01f.services.interfaces.IndexManagementServices;
import r01f.types.jobs.EnqueuedJob;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;

public abstract class RESTServicesForIndexManagementProxyBase
     		  extends RESTServicesProxyBase
     	   implements IndexManagementServices {
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE
/////////////////////////////////////////////////////////////////////////////////////////	
    @Getter private DelegateForRawRESTIndexManagement _rawIndexManagementDelegate;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public RESTServicesForIndexManagementProxyBase(final Marshaller marshaller,
												   final RESTServiceResourceUrlPathBuilder servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  servicesRESTResourceUrlPathBuilder);
		_rawIndexManagementDelegate = new DelegateForRawRESTIndexManagement(marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH BUILDING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Composes the complete REST endpoint URI for a path
	 * @param path
	 * @return
	 */
	protected Url composeSearchIndexURIFor(final UrlPath path) {
		RESTServiceResourceUrlPathBuilder pathBuilder = this.getServicesRESTResourceUrlPathBuilder();
		return Url.from(pathBuilder.getHost(),
					    pathBuilder.getEndPointBasePath()
					    		   .joinedWith(path));
	}
	/**
	 * @return the index Path
	 */
	protected abstract UrlPath getIndexPath();
/////////////////////////////////////////////////////////////////////////////////////////
//  IndexManagementServices
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public EnqueuedJob openIndex(final SecurityContext securityContext) {
		return _rawIndexManagementDelegate.doIndexManagementCommand(this.composeSearchIndexURIFor(this.getIndexPath().joinedWith("status")),
															 		securityContext,
															 		IndexManagementCommand.toOpenIndex());
	}
	@Override
	public EnqueuedJob closeIndex(final SecurityContext securityContext) {
		return _rawIndexManagementDelegate.doIndexManagementCommand(this.composeSearchIndexURIFor(this.getIndexPath().joinedWith("status")),
															 		securityContext,
															 		IndexManagementCommand.toCloseIndex());
	}
	@Override
	public EnqueuedJob optimizeIndex(SecurityContext securityContext) {
		return _rawIndexManagementDelegate.doIndexManagementCommand(this.composeSearchIndexURIFor(this.getIndexPath().joinedWith("status")),
															 		securityContext,
															 		IndexManagementCommand.toOptimizeIndex());
	}
	@Override
	public EnqueuedJob truncateIndex(final SecurityContext securityContext) {
		return _rawIndexManagementDelegate.doIndexManagementCommand(this.composeSearchIndexURIFor(this.getIndexPath().joinedWith("status")),
															 		securityContext,
															 		IndexManagementCommand.toTruncateIndex());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start() {
		this.openIndex(null);
	}
	@Override
	public void stop() {
		this.closeIndex(null);
	}	
}
