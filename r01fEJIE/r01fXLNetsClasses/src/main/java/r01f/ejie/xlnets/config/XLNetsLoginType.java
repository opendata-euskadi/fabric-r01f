package r01f.ejie.xlnets.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum XLNetsLoginType
 implements EnumWithCode<String,XLNetsLoginType> {
	USER("user"),
	APP("app");

	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;

	private static EnumWithCodeWrapper<String,XLNetsLoginType> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(XLNetsLoginType.class);

	@Override
	public boolean isIn(final XLNetsLoginType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final XLNetsLoginType el) {
		return WRAPPER.is(this,el);
	}
}