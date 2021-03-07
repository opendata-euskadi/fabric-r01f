package r01f.guids;

/**
 * Interface for composite oids of versionable model objects
 * @param <O>
 */
public interface OIDForVersionableModelObject 
		 extends CompositeOID {
	/**
	 * @return the version independent oid
	 */
	public VersionIndependentOID getVersionIndependentOid();
//	/**
//	 * Sets the version independent oid
//	 * @param versionIndependentOid
//	 */
//	public void setVersionIndependentOid(final VersionIndependentOID versionIndependentOid);
	/**
	 * @return the version
	 */
	public VersionOID getVersion();
//	/**
//	 * @param versionOid
//	 */
//	public void setVersion(final VersionOID versionOid);
}
