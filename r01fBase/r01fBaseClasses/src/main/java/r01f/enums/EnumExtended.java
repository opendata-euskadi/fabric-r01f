package r01f.enums;

/**
 * Basic operations with an {@link Enum}
 * @see EnumExtendedWrapper
 * @param <T>
 */
public interface EnumExtended<T> {	
	/**
	 * Returns the {@link Enum}'s element name
	 * (it uses java's type erasure to emulate the name() method of an enum)
	 * @return
	 */
	public String name();
	/**
	 * Checks if the element is within a provided list of elements
	 * @param els
	 * @return
	 */
	public boolean isIn(T... els);	
	/**
	 * Checks if this element is the same as the provided one
	 * @param el
	 * @return
	 */
	public boolean is(T el);
}
