package r01f.ejie.xlnets.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum XLNetsTokenSource
 implements EnumWithCode<String,XLNetsTokenSource> {
	N38API("n38api"),
	MOCK_FILE("mockFile"),
	HTTP_PROVIDED("httpProvided");

	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;

	private static EnumWithCodeWrapper<String,XLNetsTokenSource> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(XLNetsTokenSource.class);

	@Override
	public boolean isIn(final XLNetsTokenSource... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final XLNetsTokenSource el) {
		return WRAPPER.is(this,el);
	}
}