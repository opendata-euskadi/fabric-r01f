package r01f.types.contact;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

public enum ContactMeanType 
 implements EnumExtended<ContactMeanType> {
	EMAIL,
	PHONE,
	SOCIAL_NETWORK,
	WEB_SITE;
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient EnumExtendedWrapper<ContactMeanType> DELEGATE = EnumExtendedWrapper.wrapEnumExtended(ContactMeanType.class);

	@Override
	public boolean isIn(final ContactMeanType... els) {
		return DELEGATE.isIn(this,els);
	}
	public boolean isNOTIn(final ContactMeanType... els) {
		return DELEGATE.isNOTIn(this,els);
	}
	@Override
	public boolean is(final ContactMeanType el) {
		return DELEGATE.is(this,el);
	}
	public boolean isNOT(final ContactMeanType el) {
		return DELEGATE.isNOT(this,el);
	}	
}
