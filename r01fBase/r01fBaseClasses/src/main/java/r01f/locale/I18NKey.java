package r01f.locale;

import r01f.guids.OIDBaseMutable;

/**
 * A view key
 */
public class I18NKey 
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 7659549152655411096L;
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public I18NKey(final String oid) {
		super(oid);
	}
	public static I18NKey forId(final String id) {
		return new I18NKey(id);
	}
	public static I18NKey valueOf(final String id) {
		return I18NKey.forId(id);
	}
	@Override
	public boolean is(final String id) {
		return this.asString().equals(id);
	}
}
