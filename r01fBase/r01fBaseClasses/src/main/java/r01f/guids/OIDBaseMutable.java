package r01f.guids;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;

/**
 * Models an oid by encapsulating an id that can be either a String, an int, a long, etc
 * The difference with the normal {@link OIDBaseImmutable} is that the encapsulated id is NOT final
 * This is required when the derivated OID type must have a default no-args constructor 
 * to be serializable (for example, GWT requires all the objects to have a default no-args constructor, 
 * otherwise a WARNING is thrown and the final field is NOT serialized)
 * @param <T> the type of the id
 */
@Immutable	// the object is immutable (but underneath it's mutable... so it's name is OIDBaseMutable
@Accessors(prefix="_")
@EqualsAndHashCode(callSuper=false)
public abstract class OIDBaseMutable<T> 
        	  extends OIDBase<T> {
	
	private static final long serialVersionUID = 17407534484486917L;
///////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////
	@Getter @Setter /*(AccessLevel.PROTECTED)*/ private T _id;	// it's important the setter method for the marshalling
	
///////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	protected OIDBaseMutable() {
		super();
	}
	protected OIDBaseMutable(final T id) {
		super();
		if (id == null) throw new IllegalArgumentException("An OID cannot be created with null value!");
		_id = id;
	}
}
