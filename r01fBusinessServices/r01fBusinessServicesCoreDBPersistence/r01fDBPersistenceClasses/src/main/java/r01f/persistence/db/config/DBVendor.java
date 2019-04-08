package r01f.persistence.db.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import org.eclipse.persistence.config.TargetDatabase;

import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum DBVendor
 implements EnumWithCode<String,DBVendor> {
	ORACLE(TargetDatabase.Oracle),
	MySQL(TargetDatabase.MySQL),
	PostgreSQL(TargetDatabase.PostgreSQL);

	@Getter private final Class<String> _codeType = String.class;
	@Getter private final String _code;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final static EnumWithCodeWrapper<String,DBVendor> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(DBVendor.class);

	public static DBVendor fromCode(final String code) {
		return WRAPPER.fromCode(code);
	}
	public static DBVendor fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	public static boolean canBeFromCode(final String code) {
		return WRAPPER.canBeFromCode(code);
	}
	public static boolean canBe(final String name) {
		return WRAPPER.canBe(name);
	}
	@Override
	public boolean isIn(final DBVendor... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final DBVendor el) {
		return WRAPPER.is(this,el);
	}
}
