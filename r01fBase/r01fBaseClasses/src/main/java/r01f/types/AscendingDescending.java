package r01f.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

/**
 * An enum for ascending / descending values to be used when setting ordering
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public enum AscendingDescending
 implements EnumWithCode<String,AscendingDescending> {
	ASCENDING("ASC"),
	DESCENDING("DESC");
	
	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;
	
	private static EnumWithCodeWrapper<String,AscendingDescending> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(AscendingDescending.class);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isIn(final AscendingDescending... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final AscendingDescending el) {
		return WRAPPER.is(this,el);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String el) {
		return WRAPPER.canBe(el);
	}
	public static AscendingDescending fromCode(final String code) {
		return WRAPPER.fromCode(code);
	}
}
