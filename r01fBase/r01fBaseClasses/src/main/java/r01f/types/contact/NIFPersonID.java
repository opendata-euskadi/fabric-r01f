package r01f.types.contact;

import com.google.common.base.Function;

import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;

@ConvertToDirtyStateTrackable
@Immutable
@MarshallType(as="dni")
public class NIFPersonID
     extends AnyPersonID {

	private static final long serialVersionUID = -1411418440276118326L;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NIFPersonID() {
		/* default no args constructor for serialization purposes */
	}
	public NIFPersonID(final String id,
					   final boolean strict) {
		super(_normalize(id));	// normalize!!
		//if (strict && !this.isValid()) throw new IllegalArgumentException(Throwables.message("{} is NOT a valid NIF",this.getId()));
	}
	public NIFPersonID(final String id) {
		this(id,
			 false);		// do not validate by default
	}
	public static NIFPersonID valueOf(final String s) {
		return new NIFPersonID(s,false);
	}
// jackson mapper complaints about having two creators (valueOf & fromString)
//	public static NIFPersonID fromString(final String s) {
//		return new NIFPersonID(s,false);
//	}
	public static NIFPersonID forId(final String id,
									final boolean strict) {
		return new NIFPersonID(id,strict);
	}
	public static NIFPersonID forId(final String id) {
		return new NIFPersonID(id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isValid() {
		return new NIFValidator()
						.validate(this)
						.isValid();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NORMALIZE
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _normalize(final String id) {
		return id.replaceAll("[^0-9a-zA-Z]","");	// Remove all non digit or letters
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,NIFPersonID> FROM_STRING_TRANSFORM = new Function<String,NIFPersonID>() {
																					@Override
																					public NIFPersonID apply(final String id) {
																						return NIFPersonID.forId(id);
																					}
																			 };
	public static final Function<NIFPersonID,String> TO_STRING_TRANSFORM = new Function<NIFPersonID,String>() {
																			@Override
																			public String apply(final NIFPersonID nif) {
																				return nif.asString();
																			}
																  	   };
}
