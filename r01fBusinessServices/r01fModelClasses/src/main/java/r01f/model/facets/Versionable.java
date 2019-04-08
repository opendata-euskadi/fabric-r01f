package r01f.model.facets; 

import java.util.Date;

import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.model.VersionInfo;

public interface Versionable {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasVersionableFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasVersionableFacet
				    extends ModelObjectFacet {
		
		public Versionable asVersionable();
		
		public VersionInfo getVersionInfo();
		public void setVersionInfo(VersionInfo versionInfo);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public VersionIndependentOID getVersionIndependentOid();
	public VersionOID getVersionOid();
	
	public Date getStartOfUseDate();
	public void setStartOfUseDate(Date date);

	public Date getEndOfUseDate();
	public void setEndOfUseDate(Date date);

	public VersionOID getNextVersion();
	public void setNextVersion(VersionOID nextVersion);
	/**
	 * @return true if version is the current version in use
	 */
	public boolean isActive();
	/**
	 * @return true the version is not the current version in use
	 */
	public boolean isNotActive();
	/**
	 * @return the version is not currently activated (another version is the current version until this
	 * 		   version is finished)
	 */
	public boolean isDraft();
	/**
	 * Activates the version
	 * @param activationDate
	 */
	public void activate(Date activationDate);
	/**
	 * Overrides this version with another which becomes the NEXT version
	 * @param otherVersion
	 * @param otherVersionStartOfUseDate
	 */
	public void overrideBy(VersionOID otherVersion,
    					   Date otherVersionStartOfUseDate);
}
