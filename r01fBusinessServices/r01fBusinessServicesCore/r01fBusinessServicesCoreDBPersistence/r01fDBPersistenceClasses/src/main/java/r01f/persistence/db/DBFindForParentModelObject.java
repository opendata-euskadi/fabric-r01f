package r01f.persistence.db;

import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.securitycontext.SecurityContext;

public interface DBFindForParentModelObject<O extends PersistableObjectOID,
											CO extends PersistableObjectOID,C extends PersistableModelObject<CO>> {
	/**
	 * Finds all children oids
	 * @param securityContext
	 * @param parentOid
	 * @return
	 */
	public FindOIDsResult<CO> findChildsOf(final SecurityContext securityContext,
										   final O parentOid);
	/**
	 * Finds all children summaries 
	 * @param securityContext
	 * @param parentOid
	 * @return
	 */
	public FindSummariesResult<C> findChildsSummariesOf(final SecurityContext securityContext,
											   		    final O parentOid);
	
}
