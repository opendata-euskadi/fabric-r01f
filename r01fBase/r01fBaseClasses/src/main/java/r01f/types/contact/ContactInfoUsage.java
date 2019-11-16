package r01f.types.contact;

import lombok.RequiredArgsConstructor;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.locale.I18NBundleAccess;
import r01f.locale.I18NKey;

/**
 * Contact info usage
 */
@RequiredArgsConstructor
public enum ContactInfoUsage
 implements EnumExtended<ContactInfoUsage> {
	WORK		(I18NKey.named("contactInfo.usge.work")),			// Work personal Phone
	PERSONAL	(I18NKey.named("contactInfo.usge.personal")),		// Personal Phone
	COMPANY		(I18NKey.named("contactInfo.usge.company")),		// Company's phone
	OTHER		(I18NKey.named("contactInfo.usge.other"));			// Other usage

	private final I18NKey _i18nKey;
	public String nameUsing(final I18NBundleAccess i18n) {
		return i18n.getMessage(_i18nKey);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final transient EnumExtendedWrapper<ContactInfoUsage> DELEGATE = EnumExtendedWrapper.wrapEnumExtended(ContactInfoUsage.class);

	@Override
	public boolean isIn(final ContactInfoUsage... els) {
		return DELEGATE.isIn(this,els);
	}
	public boolean isNOTIn(final ContactInfoUsage... els) {
		return DELEGATE.isNOTIn(this,els);
	}
	@Override
	public boolean is(final ContactInfoUsage el) {
		return DELEGATE.is(this,el);
	}
	public boolean isNOT(final ContactInfoUsage el) {
		return DELEGATE.isNOT(this,el);
	}	
}
