package r01f.aspects.interfaces.freezable;



/**
 * Interface that signs than an object is freezable
 * Usage
 * [1] Create a type implementing {@link Freezable}
 * 		<pre class='brush:java'>
 * 			public class MyFreezableObj implements Freezable {
 * 			}
 *		</pre>
 * 
 * [2] Set the object state and freeze it
 * 		<pre class='brush:java'>
 * 			MyFreezableObj obj = new MyFreezableObj();
 * 			obj.setXX
 * 			obj.setYY
 * 			obj.freeze();	<-- freeze: object state cannot be changed
 * 			obj.setXX 		<-- IllegalStateException!!! once an object is freezed it cannot be changed
 * 		</pre>
 */
public interface Freezable {
	/**
	 * Returns true if the object is frozen
	 * @return 
	 */
	public boolean isFrozen();
	/**
	 * Sets the frozen status
	 * @param value 
	 */
	public void setFrozen(boolean value);
	/**
	 * Freezes object
	 */
	public void freeze();
	/**
	 * Unfreezes an object
	 */
	public void unFreeze();
}
