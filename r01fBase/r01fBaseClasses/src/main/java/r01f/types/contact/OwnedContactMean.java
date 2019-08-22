package r01f.types.contact;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.types.CanBeRepresentedAsString;

/**
 * Represents a {@link ContactMean} like a {@link Phone} or an {@link EMail}
 * alongside with data about it's owner
 * @param <M>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class OwnedContactMean<M extends ContactMean>
  implements CanBeRepresentedAsString,
  			 Serializable {

	private static final long serialVersionUID = -1135076268050723452L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final M _id;
	@Getter private final String _owner;

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
