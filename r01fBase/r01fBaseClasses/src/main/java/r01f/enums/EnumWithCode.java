package r01f.enums;



/**
 * An {@link Enum} that assigns codes to every enum element
 * @see EnumWithCodeWrapper
 * @param <C> the code type
 * @param <T> the enum type
 */
public interface EnumWithCode<C,T>
         extends EnumExtended<T> {
	/**
	 * Returns the code
	 * @return
	 */
	public C getCode();
	/**
	 * Returns the type of the code
	 * @return
	 */
	public Class<C> getCodeType();
}
