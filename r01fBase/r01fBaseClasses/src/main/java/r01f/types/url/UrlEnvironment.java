package r01f.types.url;


/**
 * Environment for a {@link WebUrlBase}
 * Usually this interface is implemented as an {@link Enum} 
 * <pre class='brush:java'>
 * 		public enum MyEnvironment implements WebUrlEnvironment<MyEnvironment>,
 * 											 EnumExtended<MyEnvironment> {
 * 			DES,
 * 			PRU,
 * 			PROD;
 * 
 * 			private EnumExtendedWrapper<MyEnvironment> enums = new EnumExtendedWrapper<MyEnvironment>(MyEnvironment.values());
 * 			@Override
 * 			public boolean isIn(MyEnvironment... envs) {
 * 				return enums.isIn(envs);
 * 			}
 * 			@Override
 * 			public boolean is(MyEnvironment env) {
 * 				return enums.is(env);
 * 			}
 * 		}
 * </pre>
 */
public interface UrlEnvironment<T extends UrlEnvironment<T>> {
	/**
	 * Returns true if the environment represented by this object is within the environments provided as param
	 * @param envs
	 * @return false 
	 */
	public boolean isIn(T... envs);
	/**
	 * Returns true if this the environment represented by this object is the provided as param
	 * @param env 
	 * @return false 
	 */
	public boolean is(T env);
}