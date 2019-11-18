package r01f.types.contact;

import com.google.common.base.Objects;

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
abstract class ContactMeanDataBase<SELF_TYPE extends ContactMeanDataBase<SELF_TYPE>>   
       extends ContactInfoBase<ContactMeanDataBase<SELF_TYPE>>
	implements ContactMeanData {

	private static final long serialVersionUID = 8474784639738421690L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * usage of the contact media
	 */
	@MarshallField(as="usage",
				   whenXml=@MarshallFieldAsXml(attr=true))	
	@Getter @Setter protected ContactInfoUsage _usage;
	/**
	 * Usage details (usually used when _usage = OTHER
	 */
	@MarshallField(as="usageDetails",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected String _usageDetails;
	/**
	 * true if this media is the default one
	 */
	@MarshallField(as="default",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected boolean _default = false;
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
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public void updateFrom(final SELF_TYPE other) {
		super.updateFrom(other);
		_usage = other.getUsage();
		_usageDetails = other.getUsageDetails();
		_default = other.isDefault();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContactMeanDataBase)) return false;
		SELF_TYPE other = (SELF_TYPE)obj;
		return super.equals(other)
			&& Objects.equal(this.getUsage(),other.getUsage())
			&& Objects.equal(this.getUsageDetails(),other.getUsageDetails())
			&& Objects.equal(this.isDefault(),other.isDefault());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_private,
								_usage,
							    _usageDetails,
							    _default);
	}	
}
