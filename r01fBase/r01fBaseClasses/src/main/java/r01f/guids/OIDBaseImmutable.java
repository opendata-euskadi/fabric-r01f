package r01f.guids;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Models an oid by encapsulating an id that can be either a String, an int, a long, etc
 * @param <T> the type of the id
 */
@Immutable
@Accessors(prefix="_")
@EqualsAndHashCode(callSuper=false)
public abstract class OIDBaseImmutable<T>
	          extends OIDBase<T> {
	
	private static final long serialVersionUID = 3256491732245845341L;
///////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final T _id;
///////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	protected OIDBaseImmutable(final T id) {
		if (id == null) throw new IllegalArgumentException("An OID cannot be created with null value!");
		_id = id;
	}
}
