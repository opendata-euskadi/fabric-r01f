package r01f.persistence.db.entities.primarykeys;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.util.types.Strings;

/**
 * PrimaryKey for any versionable object
 */
@Accessors(prefix="_")
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor 
public class DBPrimaryKeyForVersionableModelObjectImpl 
	 extends DBPrimaryKeyForModelObjectImpl
  implements DBPrimaryKeyForVersionableModelObject {

	private static final long serialVersionUID = -5356804843117150607L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Version
     */
    @Getter @Setter private String _version;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public DBPrimaryKeyForVersionableModelObjectImpl(final String versionIndependentOid,
    											     final String version) {
    	super(versionIndependentOid);
    	_version = version;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public static <O extends OIDForVersionableModelObject> DBPrimaryKeyForVersionableModelObjectImpl from(final O oid) {
    	return DBPrimaryKeyForVersionableModelObjectImpl.from(oid.getOid(),
    														  oid.getVersion());
    }
    public static DBPrimaryKeyForVersionableModelObjectImpl from(final VersionIndependentOID oid ,
    														 	 final VersionOID version) {
    	return new DBPrimaryKeyForVersionableModelObjectImpl(oid.asString(),
    											 		 	 version.asString());
    }
    public static DBPrimaryKeyForVersionableModelObjectImpl from(final String oid,
    												    		 final String version) {
    	return new DBPrimaryKeyForVersionableModelObjectImpl(oid,
    												    	 version);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return Strings.customized("{}/{}",
					  			  this.getOid(),this.getVersion());
	}
}
