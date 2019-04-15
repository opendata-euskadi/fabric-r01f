package r01f.persistence.db.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum PersistenceUnitType
 implements EnumExtended<PersistenceUnitType> {
	DRIVER_MANAGER,
	DATASOURCE;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final static EnumExtendedWrapper<PersistenceUnitType> WRAPPER = EnumExtendedWrapper.wrapEnumExtended(PersistenceUnitType.class);

	public static PersistenceUnitType fromName(final String name) {
		return WRAPPER.fromName(name);
	}

	@Override
	public boolean isIn(final PersistenceUnitType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final PersistenceUnitType el) {
		return WRAPPER.is(this,el);
	}
}
