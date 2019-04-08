package r01f.model;

import r01f.guids.OID;
import r01f.model.facets.ModelObjectFacet;

/**
 * Every {@link IndexableModelObject} object which has an OID should implement this interface
 * @param <O> the oid type
 */
public interface HasIndexedOID<O extends OID>
	     extends ModelObjectFacet {
	
	/**
	 * gets the oid
	 * @return the oid
	 */
	public O getIndexedOid();
}
