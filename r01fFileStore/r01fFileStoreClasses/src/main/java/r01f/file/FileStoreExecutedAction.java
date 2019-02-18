package r01f.file;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

public enum FileStoreExecutedAction
 implements EnumExtended<FileStoreExecutedAction> {
	READED,
	CREATED,
	UPDATED,
	DELETED,
	UNKNOWN;
/////////////////////////////////////////////////////////////////////////////////////////
//	ENUM EXTENDED 
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumExtendedWrapper<FileStoreExecutedAction> WRAPPER = EnumExtendedWrapper.wrapEnumExtended(FileStoreExecutedAction.class);
	
	@Override
	public boolean isIn(final FileStoreExecutedAction... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final FileStoreExecutedAction el) {
		return WRAPPER.is(this,el);
	}
}
