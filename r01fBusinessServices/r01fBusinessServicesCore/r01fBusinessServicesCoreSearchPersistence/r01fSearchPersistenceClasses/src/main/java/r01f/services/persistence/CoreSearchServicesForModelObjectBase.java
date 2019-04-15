package r01f.services.persistence;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.PersistableObjectOID;
import r01f.model.search.SearchFilterForModelObject;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.search.SearcherProvider;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.SearchServicesForModelObject;

/**
 * Core service base for search services
 */
@Accessors(prefix="_")
public abstract class CoreSearchServicesForModelObjectBase<F extends SearchFilterForModelObject,I extends SearchResultItemForModelObject<?>> 
     		  extends CoreSearchServicesBase<F,I>					  
     	   implements SearchServicesForModelObject<F,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param coreCfg 
	 * @param modelObjectsMarshaller annotated with @ModelObjectsMarshaller
	 * @param eventBus
	 * @param searcher
	 * @param modelObjectsMarshaller annotate with @ModelObjectsMarshaller
	 */
	public CoreSearchServicesForModelObjectBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												final Marshaller modelObjectsMarshaller,
										   	   	final EventBus eventBus,
										   	   	final SearcherProvider<F,I> searcherProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  searcherProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public <O extends PersistableObjectOID> Collection<O> filterRecordsOids(final SecurityContext securityContext,
													   	   					final F filter) {
		return  this.forSecurityContext(securityContext)
						.createDelegateAs(SearchServicesForModelObject.class)
							.filterRecordsOids(securityContext,
											   filter);
	}
}
