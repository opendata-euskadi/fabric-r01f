package r01f.events.index;

import r01f.events.PersistenceOperationEventListeners.PersistenceOperationOKEventListener;
import r01f.guids.OID;
import r01f.model.IndexableModelObject;
import r01f.services.interfaces.IndexServicesForModelObject;

/**
 * A {@link PersistenceOperationOKEventListener} that indexes at lucene (extends {@link IndexerCRUDOKEventListener}) 
 * @param <M>
 */
public abstract class LuceneIndexerCRUDOKEventListener<O extends OID,M extends IndexableModelObject> 
	          extends IndexerCRUDOKEventListener<O,M> {	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LuceneIndexerCRUDOKEventListener(final Class<M> type,
											final IndexServicesForModelObject<O,M> indexServices) {
		super(type,
			  indexServices);
	}
}
