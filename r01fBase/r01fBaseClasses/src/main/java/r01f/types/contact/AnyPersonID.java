package r01f.types.contact;

import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@ConvertToDirtyStateTrackable
@Immutable
@MarshallType(as="personId")
public class AnyPersonID 
     extends OIDBaseMutable<String> 	// normally this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable
  implements PersonID {
	
	private static final long serialVersionUID = 4475634008696904179L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AnyPersonID() {
		/* default no args constructor for serialization purposes */
	}
	public AnyPersonID(final String id) {
		super(id);	// normalize!!
	}
	public static AnyPersonID valueOf(final String s) {
		return new AnyPersonID(s);
	}
	public static AnyPersonID forId(final String id) {
		return new AnyPersonID(id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public boolean isValid() {
		return true;
	}
}
