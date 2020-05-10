package r01f.types.contact;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.CanBeRepresentedAsString;

/**
 * Represents a {@link ContactMean} like a {@link Phone} or an {@link EMail}
 * alongside with data about it's owner
 * @param <M>
 */
@Accessors(prefix="_")
public class OwnedContactMean<M extends ContactMean>
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = -1135076268050723452L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final M _id;
	@Getter private final String _owner;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public OwnedContactMean(final M id,final String owner) {
		_id = id;
		_owner = owner;
	}
	public static <M extends ContactMean> OwnedContactMean<M> from(final M id,final String owner) {
		return new OwnedContactMean<M>(id,owner);
	}
	public static <M extends ContactMean> OwnedContactMean<M> from(final M id) {
		return new OwnedContactMean<M>(id,id.asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
        // rfc822 format
        return String.format("\"%s\" <%s>",
        					 _owner != null ? _owner : _id.asString(),_id.asString());
	}
	@Override
	public String toString() {
		return this.asString();
	}
}
