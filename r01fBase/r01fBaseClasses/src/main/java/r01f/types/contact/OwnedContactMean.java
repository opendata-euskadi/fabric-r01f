package r01f.types.contact;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents a {@link ContactMean} like a {@link Phone} or an {@link EMail}
 * alongside with data about it's owner
 * @param <M>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class OwnedContactMean<M extends ContactMean>
  implements Serializable {

	private static final long serialVersionUID = -1135076268050723452L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final M _id;
	@Getter private final String _owner;
}
