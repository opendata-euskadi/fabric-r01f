package r01f.model.persistence;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

/**
 * Persistence-related operations
 * Note that the performed operation is NOT always the same as the requested one
 * (ie: an update could be requested by the client BUT the record didn't exist so a creation is performed)
 */
public enum PersistenceRequestedOperation 
 implements EnumExtended<PersistenceRequestedOperation> {
	LOAD,
	CREATE,
	UPDATE,
	SAVE,		// create or update
	DELETE,
	FIND,
	COUNT,
	OTHER;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumExtendedWrapper<PersistenceRequestedOperation> WRAPPER = EnumExtendedWrapper.wrapEnumExtended(PersistenceRequestedOperation.class);
	
	public static PersistenceRequestedOperation fromName(final String name) {
		return WRAPPER.fromName(name);
	}

	@Override
	public boolean isIn(final PersistenceRequestedOperation... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final PersistenceRequestedOperation el) {
		return WRAPPER.is(this,el);
	}
	public static boolean canBe(final String name) {
		return WRAPPER.canBe(name);
	}
}