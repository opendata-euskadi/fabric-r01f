package r01f.model;

import r01f.facets.Facetable;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;
import r01f.model.metadata.MetaDataDescribable;





/**
 * interface for model objects that can be persisted in some kind of search engine storage
 * IMPORTANT!!
 * 		This interface is roughtly the SAME as {@link PersistableModelObject} BUT NOT the same
 * 		since contrary to a database, search engines are usually unstructured in the sense that
 * 		usually a DB table is mapped to an entity but that's not allways the same with search
 * 		engine's DBs where there're no tables and a row can be for an entity and the next row
 * 		for another entity
 *
 *  	A rule of thumb can be that traditional DBs rows store concrete classes where a search engine
 *  	DB row store super-interface for many concrete clases
 *
 *  	For example, a every rows in a table in a traditional DB can store instances of MyObject while a
 * 		row at the serach engine db can store instances of MyObject or MyOtherObject
 *
 * @param <O> the indexed oid type (it must not have to be the same as the model object's oid)
 */
public interface IndexableModelObject
		 extends ModelObject,
		 		 MetaDataDescribable,
		 		 Facetable,
		 		 HasEntityVersion,
		 		 HasNumericID {
	/* just extend */
}
