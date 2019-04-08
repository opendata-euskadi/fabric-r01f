package r01f.guids;


/**
 * An unique identifier backed up by a type
 * @param <T> the type that models the {@link OID} (normally a {@link String} or a {@link Long})
 */
public interface OIDTyped<T> 
         extends OID {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the raw value encapsulated by the OID
	 * @return the raw value
	 */
	public T getRaw();
	/**
	 * Returns true if the underlyin id equals the provided one
	 * @param otherId
	 * @return
	 */
	public boolean is(final T otherId);
}
