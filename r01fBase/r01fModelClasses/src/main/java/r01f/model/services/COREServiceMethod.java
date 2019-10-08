package r01f.model.services;

import r01f.guids.OIDBaseMutable;
import r01f.util.types.collections.CollectionUtils;

/**
 * An id of the called [core service] method
 */
public class COREServiceMethod 
     extends OIDBaseMutable<String> {
	
	private static final long serialVersionUID = -7020929167799107328L;

/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public static COREServiceMethod UNKNOWN = COREServiceMethod.named("unknown");
	public COREServiceMethod() {
		super();
	}
	public COREServiceMethod(final String oid) {
		super(oid);
	}
	public static COREServiceMethod valueOf(final String s) {
		return COREServiceMethod.named(s);
	}
	public static COREServiceMethod fromString(final String s) {
		return COREServiceMethod.named(s);
	}
	public static COREServiceMethod named(final String id) {
		return new COREServiceMethod(id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isIn(final COREServiceMethod... methods) {
		if (CollectionUtils.isNullOrEmpty(methods)) return false;
		boolean outIn = false;
		for (COREServiceMethod m : methods) {
			if (m.is(this)) {
				outIn = true;
				break;
			}
		}
		return outIn;
	}
}