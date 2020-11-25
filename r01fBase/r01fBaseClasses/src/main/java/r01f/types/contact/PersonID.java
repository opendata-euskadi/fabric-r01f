package r01f.types.contact;

import com.google.common.base.Function;

import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Models a person identity card number (spanish dni or social security number)
 */
@ConvertToDirtyStateTrackable
@Immutable
@MarshallType(as="personId")
public class PersonID 
     extends OIDBaseMutable<String> { 	// usually this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable
	
	private static final long serialVersionUID = 4475634008696904179L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersonID() {
		/* default no args constructor for serialization purposes */
	}
	public PersonID(final String id) {
		super(id);	// normalize!!
	}
	public PersonID(final String id,
					final boolean strict) {
		this(_normalize(id));	// normalize!!
		//if (strict && !this.isValid()) throw new IllegalArgumentException(Throwables.message("{} is NOT a valid NIF",this.getId()));
	}
	public static PersonID valueOf(final String s) {
		return new PersonID(s);
	}
	public static PersonID forId(final String id) {
		return new PersonID(id);
	}
	public static PersonID forId(final String id,
								 final boolean strict) {
		return new PersonID(id,strict);
	}
	public static PersonID fromSpanishNIF(final String id,
										  final boolean normalize) {
		return new PersonID(_normalize(id));	// normalize!!
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _normalize(final String id) {
		return id.replaceAll("[^0-9a-zA-Z]","");	// Remove all non digit or letters
	}
	@Override 
	public boolean isValid() {
		return true;
	}
	public boolean isValidSpanishNIF() {
		return new NIFValidator()
						.validate(this)
						.isValid();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,PersonID> SPANISH_NIF_FROM_STRING_TRANSFORM = new Function<String,PersonID>() {
																								@Override
																								public PersonID apply(final String id) {
																									return PersonID.forId(id);
																								}
																					   };
	public static final Function<PersonID,String> SPAHINSH_NIF_TO_STRING_TRANSFORM = new Function<PersonID,String>() {
																							@Override
																							public String apply(final PersonID nif) {
																								return nif.asString();
																							}
																				  	 };
}
