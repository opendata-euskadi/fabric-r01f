package r01f.types;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

public enum ExecutionMode 
 implements EnumExtended<ExecutionMode> {
	SYNC,
	ASYNC;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumExtendedWrapper<ExecutionMode> _enums = EnumExtendedWrapper.wrapEnumExtended(ExecutionMode.class);
	
	public static ExecutionMode fromName(final String name) {
		return _enums.fromName(name);
	}
	
	@Override
	public boolean isIn(final ExecutionMode... els) {
		return _enums.isIn(this,els);
	}

	@Override
	public boolean is(final ExecutionMode el) {
		return _enums.is(this,el);
	}
}
