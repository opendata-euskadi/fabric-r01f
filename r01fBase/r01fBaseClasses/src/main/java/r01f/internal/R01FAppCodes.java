package r01f.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class R01FAppCodes {
	public static final String R01_APP_CODE_STR = "r01f";
	public static final String R01_LEGACY_APP_CODE_STR = "r01ft";
	public static final String R01_UI_APP_CODE_STR = "r01fui";
	
	
	public static final AppCode R01_APP_CODE = AppCode.forId(R01_APP_CODE_STR);
	public static final AppCode APP_CODE = R01_APP_CODE;
	public static final AppCode R01_UI_APP_CODE = AppCode.forId(R01_UI_APP_CODE_STR);
	public static final AppCode LEGACY_APP_CODE = AppCode.forId(R01_LEGACY_APP_CODE_STR);
}
