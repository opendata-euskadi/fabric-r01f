package r01f.ejie.xlnets.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum XLNetsOrganizationType
 implements EnumWithCode<String,XLNetsOrganizationType> {
	ORGANIZATION("O"), 	//value for n38api O means "organizacion"
	GROUP("GO"),		//value for n38api GO means "grupo organico"
	CENTER("CO");		//value for n38api CO means "centro organico"

	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;

	private static EnumWithCodeWrapper<String,XLNetsOrganizationType> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(XLNetsOrganizationType.class);

	@Override
	public boolean isIn(final XLNetsOrganizationType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final XLNetsOrganizationType el) {
		return WRAPPER.is(this,el);
	}
}