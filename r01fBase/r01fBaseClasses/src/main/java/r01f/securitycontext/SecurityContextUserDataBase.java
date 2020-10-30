package r01f.securitycontext;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.Strings;

/**
 * Data about a [user] stored at the {@link SecurityContext}
 * Beware that this object is very similar to the [user] object from the [security system] (google, local users db,...)
 * but it does NOT have to be the same 
 */
@Accessors(prefix="_")
public abstract class SecurityContextUserDataBase
           implements SecurityContextUserData {

	private static final long serialVersionUID = 8114955252922390809L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="user",
			   	   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final UserCode _user;

	@MarshallField(as="workPlace",
			   	   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final WorkPlaceCode _workPlace;

	@MarshallField(as="name",escape=true)
	@Getter @Setter private String _name;

	@MarshallField(as="suname",escape=true)
	@Getter @Setter private String _surname;

	@MarshallField(as="displayName",escape=true)
	@Getter @Setter private String _displayName;

	@MarshallField(as="prefLang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _prefLang = Language.DEFAULT;

	@MarshallField(as="email",
			   	   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final EMail _email;

	@MarshallField(as="phone",
			   	   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final Phone _phone;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name,final String surname,
									   final String displayName,
									   final Language prefLang,
									   final EMail email,final Phone phone) {
		_user = userCode;
		_workPlace = workPlace;

		_name = name;
		_surname = surname;
		_displayName = displayName != null
							? displayName
							: _displayNameFrom(_name,_surname);
		_prefLang = prefLang;
		_email = email;
		_phone = phone;
	}
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name,final String surname,
									   final String displeyName,
									   final EMail email,final Phone phone) {
		this(userCode,workPlace,
			 name,surname,
			 displeyName,
			 Language.DEFAULT,
			 email,phone);
	}
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name,final String surname,
									   final Language prefLang,
									   final EMail email,final Phone phone) {
		this(userCode,workPlace,
			 name,surname,
			 _displayNameFrom(name,surname),			// display name
			 prefLang,
			 email,phone);
	}
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name,final String surname,
									   final EMail email,final Phone phone) {
		this(userCode,workPlace,
			 name,surname,
			 Language.DEFAULT,
			 email,phone);
	}
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name,final String surname,
									   final Language prefLang) {
		this(userCode,workPlace,
			 name,surname,
			 _displayNameFrom(name,surname),			// display name
			 prefLang,
			 null,null);	// phone & email
	}
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name,final String surname) {
		this(userCode,workPlace,
			 name,surname,
			 Language.DEFAULT);
	}
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name,
									   final Language prefLang) {
		this(userCode,workPlace,
			 name,null,
			 name,			// display name
			 prefLang,
			 null,null);	// phone & email
	}
	public SecurityContextUserDataBase(final UserCode userCode,final WorkPlaceCode workPlace,
									   final String name) {
		this(userCode,workPlace,
			 name,
			 Language.DEFAULT);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected static String _displayNameFrom(final String name,final String surname) {
		return name != null && surname != null
							? Strings.customized("{} {}",name,surname)
							: surname != null
									? surname
									: name;
	}
}
