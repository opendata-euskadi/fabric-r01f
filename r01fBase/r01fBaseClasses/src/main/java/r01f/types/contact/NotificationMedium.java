package r01f.types.contact;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

public enum NotificationMedium
 implements EnumExtended<NotificationMedium> {
	EMAIL,
	SMS,
	VOICE,
	LOG,
	PUSH_MESSAGE;

	public String asStringLowerCase() {
		return this.name().toLowerCase();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static final transient EnumExtendedWrapper<NotificationMedium> DELEGATE = EnumExtendedWrapper.wrapEnumExtended(NotificationMedium.class);

	@Override
	public boolean isIn(final NotificationMedium... els) {
		return DELEGATE.isIn(this,els);
	}
	@Override
	public boolean is(final NotificationMedium el) {
		return DELEGATE.is(this,el);
	}
}
