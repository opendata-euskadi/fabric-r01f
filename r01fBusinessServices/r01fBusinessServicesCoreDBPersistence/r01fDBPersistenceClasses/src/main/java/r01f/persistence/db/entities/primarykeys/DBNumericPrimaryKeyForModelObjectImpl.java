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
public class DBNumericPrimaryKeyForModelObjectImpl 
  implements DBPrimaryKeyForModelObject {

	private static final long serialVersionUID = 2405812557686382440L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Oid
     */
    @Getter @Setter private long _oid;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public static <O extends OID> DBNumericPrimaryKeyForModelObjectImpl from(final long id) {
    	return new DBNumericPrimaryKeyForModelObjectImpl(id);
    }
    public static <O extends OID> DBNumericPrimaryKeyForModelObjectImpl from(final O oid) {
    	return new DBNumericPrimaryKeyForModelObjectImpl(Long.parseLong(oid.asString()));
    }
    public static DBNumericPrimaryKeyForModelObjectImpl from(final String oid) {
    	return new DBNumericPrimaryKeyForModelObjectImpl(Long.parseLong(oid));
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return Long.toString(_oid);
	}
}
