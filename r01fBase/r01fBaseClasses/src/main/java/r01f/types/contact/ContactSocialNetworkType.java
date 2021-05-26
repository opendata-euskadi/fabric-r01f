package r01f.types.contact;

import lombok.RequiredArgsConstructor;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.locale.I18NBundleAccess;
import r01f.locale.I18NKey;

@RequiredArgsConstructor
public enum ContactSocialNetworkType 
 implements EnumExtended<ContactSocialNetworkType> {
	TWITTER(I18NKey.named("contact.socialNetwork.twitter")),
	FACEBOOK(I18NKey.named("contact.socialNetwork.facebook")),
	LINKEDIN(I18NKey.named("contact.socialNetwork.linkedin")),
	YOUTUBE(I18NKey.named("contact.socialNetwork.youtube")),
	VIMEO(I18NKey.named("contact.socialNetwork.vimeo")),
	GOOGLE(I18NKey.named("contact.socialNetwork.google")),
	WHATSAPP(I18NKey.named("contact.socialNetwork.whatsapp")),
	TELEGRAM(I18NKey.named("contact.socialNetwork.telegram")),
	LINE(I18NKey.named("contact.socialNetwork.line")),
	SKYPE(I18NKey.named("contact.socialNetwork.skype")),
	FLICKR(I18NKey.named("contact.socialNetwork.flickr")),
	PINTEREST(I18NKey.named("contact.socialNetwork.pinterest")),
	INSTAGRAM(I18NKey.named("contact.socialNetwork.instagram")),
	SNAPCHAT(I18NKey.named("contact.socialNetwork.snapchat")),;
	
	private final I18NKey _i18nKey;
	public String nameUsing(final I18NBundleAccess i18n) {
		return i18n.getMessage(_i18nKey);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	private static final transient EnumExtendedWrapper<ContactSocialNetworkType> DELEGATE = EnumExtendedWrapper.wrapEnumExtended(ContactSocialNetworkType.class);
	
	@Override
	public boolean isIn(final ContactSocialNetworkType... els) {
		return DELEGATE.isIn(this,els);
	}
	public boolean isNOTIn(final ContactSocialNetworkType... els) {
		return DELEGATE.isNOTIn(this,els);
	}
	@Override
	public boolean is(final ContactSocialNetworkType el) {
		return DELEGATE.is(this,el);
	}
	public boolean isNOT(final ContactSocialNetworkType el) {
		return DELEGATE.isNOT(this,el);
	}
}
