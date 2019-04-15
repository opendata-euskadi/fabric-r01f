package r01f.persistence.index;

import r01f.model.IndexableModelObject;

/**
 * Interface to be implemented by types that holds a {@link Indexer}
 */
public interface HasIndexer<M extends IndexableModelObject> {
	/**
	 * @return the repository {@link Indexer}
	 */
	public Indexer<M> getIndexer();
}
