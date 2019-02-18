package r01f.types.contact;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Base type for every {@link ContactInfo} media related object: {@link ContactMail}, {@link ContactPhone}, {@link ContactSocialNetwork}, etc
 * @param <SELF_TYPE>
 */
@Accessors(prefix="_")
abstract class ContactInfoMediaBase<SELF_TYPE extends ContactInfoMediaBase<SELF_TYPE>>   
       extends ContactInfoBase<ContactInfoMediaBase<SELF_TYPE>> {


	private static final long serialVersionUID = 8474784639738421690L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * usage of the contact media
	 */
	@MarshallField(as="usage",
				   whenXml=@MarshallFieldAsXml(attr=true))	
	@Getter @Setter private ContactInfoUsage _usage;
	/**
	 * Usage details (usually used when _usage = OTHER
	 */
	@MarshallField(as="usageDetails",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _usageDetails;
	/**
	 * true if this media is the default one
	 */
	@MarshallField(as="default",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _default = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE useAsDefault() {
		_default = true;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE usedFor(final ContactInfoUsage usage) {
		_usage = usage;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withUsageDetails(final String details) {
		_usageDetails = details;
		return (SELF_TYPE)this;
	}
}
