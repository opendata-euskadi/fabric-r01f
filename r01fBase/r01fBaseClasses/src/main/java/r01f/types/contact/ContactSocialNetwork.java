package r01f.types.contact;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.types.url.Url;


/**
 * Contact's social network
 * <pre class='brush:java'>
 *	ContactSocialNetwork user = ContactSocialNetwork.createToBeUsedFor(ContactInfoUsage.PERSONAL)
 *													.forNetwork(R01MContactSocialNetworkType.TWITTER)
 *												   	.user("futuretelematics")
 *													.profileAt("http://twitter.com/futuretelematics");
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="socialNetworkData")
@Accessors(prefix="_")
@NoArgsConstructor
public class ContactSocialNetwork 
     extends ContactMeanDataBase<ContactSocialNetwork>
  implements ContactMean,
  			 HasLanguage {
	
	private static final long serialVersionUID = 4611690233960483088L;
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Social network type: twitter, facebook, youtube, tec
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ContactSocialNetworkType _type;
	/**
	 * The language
	 */
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _language;
	/**
	 * Phone number
	 */
	@MarshallField(as="user",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private LoginID _user;
	/**
	 * Profile url (ie: twitter.com/futuretelematics)
	 */
	@MarshallField(as="profileUrl",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Url _profileUrl;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactSocialNetwork createToBeUsedFor(final ContactInfoUsage usage) {
		ContactSocialNetwork outNetwork = new ContactSocialNetwork();
		outNetwork.usedFor(usage);
		return outNetwork;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactSocialNetwork forNetwork(final ContactSocialNetworkType type) {
		_type = type;
		return this;
	}
	public ContactSocialNetwork user(final LoginID user) {
		_user = user;
		return this;
	}
	public ContactSocialNetwork profileAt(final Url profileUrl) {
		_profileUrl = profileUrl;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateFrom(final ContactSocialNetwork other) {
		super.updateFrom(other);
		_type = other.getType();
		_language = other.getLanguage();
		_user = other.getUser();
		_profileUrl = other.getProfileUrl();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _user != null ? _user.asString()
							 : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContactSocialNetwork)) return false;
		ContactSocialNetwork other = (ContactSocialNetwork)obj;
		return super.equals(other)
			&& Objects.equal(this.getType(),other.getType())
			&& Objects.equal(this.getUser(),other.getUser())
			&& Objects.equal(this.getLanguage(),other.getLanguage())
			&& Objects.equal(this.getProfileUrl(),other.getProfileUrl());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_private,
								_usage,
							    _usageDetails,
							    _default,
							    _type,
							    _user,
							    _language,
							    _profileUrl);
	}
}
