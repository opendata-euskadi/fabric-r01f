package r01f.notifier;

import com.google.common.annotations.GwtIncompatible;

import r01f.guids.OID;
import r01f.guids.OIDBaseImmutable;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * A {@link NotifierRequest} {@link OID}
 */
@GwtIncompatible("does not have de default no-args constructor")
@MarshallType(as="notifierRequest")
public class NotifierRequestOID 
	 extends OIDBaseImmutable<String> {

	private static final long serialVersionUID = -8429215763429456025L;
	
	protected NotifierRequestOID(final String id) {
		super(id);
	}
	public static NotifierRequestOID of(final String id) {
		return new NotifierRequestOID(id);
	}
	public static NotifierRequestOID valueOf(final String id) {
		return new NotifierRequestOID(id);
	}
}
