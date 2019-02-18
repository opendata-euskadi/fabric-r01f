package r01f.types.url;


/**
 * Security zones
 * Usually this interface is implemented as an {@link Enum}
 * <pre class='brush:java'>
 * 		public enum MySecurityZone implements WebUrlSecurityZone<MySecurityZone>,
 * 											  EnumExtended<MySecurityZone> {
 * 			INTRANET,
 * 			INTERNET;
 * 
 * 			private EnumExtendedWrapper<MySecurityZone> enums = new EnumExtendedWrapper<MySecurityZone>(MySecurityZone.values());
 * 			@Override
 * 			public boolean isIn(MySecurityZone... zones) {
 * 				return enums.isIn(zones);
 * 			}
 * 			@Override
 * 			public boolean is(MySecurityZone zone) {
 * 				return enums.is(zone);
 * 			}
 * 		}
 * </pre>
 */
public interface UrlSecurityZone<T extends UrlSecurityZone<T>> {
	/**
	 * Returns true if the zone represented by this object is within the provided ones
	 * @param zones 
	 * @return 
	 */
	public boolean isIn(T... zones);
	/**
	 * Returns true if the zone represented by this object is within the provided one
	 * @param zone s
	 * @return 
	 */
	public boolean is(T zone);
	/**
	 * @return true if it's an EXTERNAL url (one that's not an intranet, extranet, euskadi.net, etc... url like www.google.com)
	 */
	public boolean isExternal();
}