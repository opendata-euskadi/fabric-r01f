package r01f.persistence.index;

import r01f.guids.OID;
import r01f.model.IndexableModelObject;
import r01f.securitycontext.SecurityContext;

/**
 * Interface to be implemented by types in charge to index persistable records
 * @param <M>
 */
public interface Indexer<M extends IndexableModelObject> {
	/**
	 * Index a model object
	 * @param securityContext
	 * @param record
	 */
	public void index(final SecurityContext securityContext,
					  final M record);
	/**
	 * Updates the indexed data for a model record
	 * @param securityContext
	 * @param record
	 */
	public void updateIndex(final SecurityContext securityContext,
							final M record);
	
	/**
	 * Deletes the indexed data for a record
	 * @param securityContext
	 * @param record
	 * @throws UnsupportedOperationException if the record is versionable
	 */
	public void removeFromIndex(final SecurityContext securityContext,
								final OID oid);
}
