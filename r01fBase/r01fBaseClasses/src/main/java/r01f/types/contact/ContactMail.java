package r01f.types.contact;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;


/**
 * Email Data
 * <pre class='brush:java'>
 *	ContactMail user = ContactMail.createToBeUsedFor(ContactInfoUsage.PERSONAL)
 *								  .mailTo(EMail.of("futuretelematics@gmail.com"));
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="emailChannel")
@Accessors(prefix="_")
@NoArgsConstructor
public class ContactMail 
     extends ContactInfoMediaBase<ContactMail> {
	
	private static final long serialVersionUID = 586551188692425105L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Email
	 */
	@MarshallField(as="addr",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private EMail _mail;
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API: CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static ContactMail createToBeUsedFor(final ContactInfoUsage usage) {
		ContactMail outMail = new ContactMail();
		outMail.usedFor(usage);
		return outMail;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactMail mailTo(final EMail mail) {
		_mail = mail;
		return this;
	}
	public ContactMail mailTo(final String mail) {
		_mail = EMail.create(mail);
		return this;
	}
}
