package r01f.guids;

import r01f.objectstreamer.annotations.MarshallType;

/**
 * Any oid
 */
@MarshallType(as="anyOid")
public final class AnyOID 
	 extends OIDBaseMutable<String> {
	
	private static final long serialVersionUID = 3726152162388157103L;
	
	private AnyOID(final String id) {
		super(id);
	}
	public static AnyOID forId(final String id) {
		return new AnyOID(id);
	}
}
