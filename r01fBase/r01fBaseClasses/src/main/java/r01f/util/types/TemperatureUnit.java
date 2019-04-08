package r01f.util.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum TemperatureUnit
 implements EnumWithCode<String,TemperatureUnit> {
	CELSIUS("C"),
	FARENHEIT("F");

	@Getter private final Class<String> _codeType = String.class;
	@Getter private final String _code;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumWithCodeWrapper<String,TemperatureUnit> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(TemperatureUnit.class);
	@Override
	public boolean isIn(final TemperatureUnit... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final TemperatureUnit el) {
		return WRAPPER.is(this,el);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static TemperatureUnit from(final String code) {
		return WRAPPER.fromCode(code);
	}
	public static TemperatureUnit fromName(final String name) {
		return WRAPPER.fromName(name);
	}
}
