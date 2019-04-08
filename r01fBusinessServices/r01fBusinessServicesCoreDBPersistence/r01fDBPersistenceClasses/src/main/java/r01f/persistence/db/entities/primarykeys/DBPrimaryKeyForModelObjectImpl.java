package r01f.persistence.db.entities.primarykeys;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;

/**
 * PrimaryKey for any NOT versionable object
 */
@Accessors(prefix="_")
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class DBPrimaryKeyForModelObjectImpl 
  implements DBPrimaryKeyForModelObject {

	private static final long serialVersionUID = 2405812557686382440L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Oid
     */
    @Getter @Setter private String _oid;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public static <O extends OID> DBPrimaryKeyForModelObjectImpl from(final O oid) {
    	return new DBPrimaryKeyForModelObjectImpl(oid.asString());
    }
    public static DBPrimaryKeyForModelObjectImpl from(final String oid) {
    	return new DBPrimaryKeyForModelObjectImpl(oid);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _oid;
	}
}
