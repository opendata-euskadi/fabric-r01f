package r01f.types.contact;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasLanguage;
import r01f.guids.CommonOIDs.UserCode;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
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
     extends ContactInfoMediaBase<ContactSocialNetwork>
  implements HasLanguage {
	
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
	@Getter @Setter private UserCode _user;
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
	public ContactSocialNetwork user(final UserCode user) {
		_user = user;
		return this;
	}
	public ContactSocialNetwork profileAt(final Url profileUrl) {
		_profileUrl = profileUrl;
		return this;
	}
}
