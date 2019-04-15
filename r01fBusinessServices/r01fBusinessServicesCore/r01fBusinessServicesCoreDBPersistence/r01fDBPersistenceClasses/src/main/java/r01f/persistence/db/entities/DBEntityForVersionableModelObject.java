package r01f.persistence.db.entities;

import java.util.Calendar;

import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForVersionableModelObject;


/**
 * Interface for versionable DB entities
 * @param <R>
 */
public interface DBEntityForVersionableModelObject<PK extends DBPrimaryKeyForVersionableModelObject> 
		 extends DBEntityForModelObject<PK> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the entity version
	 */
	public abstract String getVersion();
	/**
	 * @param version the entity version
	 */
	public abstract void setVersion(String version);
	/**
	 * @return the start of use date (the date when this version is set to be active)
	 */
	public abstract Calendar getStartOfUseDate();
	/**
	 * Version start of use date (the date when this version is set to be active)
	 */
	public abstract void setStartOfUseDate(Calendar date);
	/**
	 * Version end of use date (the date when another version was created and this one becomes obsolete)
	 */
	public abstract Calendar getEndOfUseDate();
	/**
	 * @param date Version end of use date (the date when another version was created and this one becomes obsolete)
	 */
	public abstract void setEndOfUseDate(Calendar date);
	/**
	 * Next version identifier (if this version is not the current version)
	 */
	public abstract String getNextVersion();
	/**
	 * @param version Next version identifier (if this version is not the current version)
	 */
	public abstract void setNextVersion(String version);
	
}
