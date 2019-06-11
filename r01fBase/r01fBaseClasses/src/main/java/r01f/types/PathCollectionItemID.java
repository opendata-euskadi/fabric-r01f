package r01f.types;

import java.util.UUID;

import com.google.common.annotations.GwtIncompatible;

import lombok.experimental.Accessors;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="pathCollectionItemId")
@Accessors(prefix="_")
public class PathCollectionItemID
     extends OIDBaseMutable<String>  {

	private static final long serialVersionUID = -4033270219524857151L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public PathCollectionItemID() {
		/* default no args constructor for serialization purposes */
	}
	public PathCollectionItemID(final String id) {
		super(id);
	}
	public static PathCollectionItemID forId(final String id) {
		return new PathCollectionItemID(id);
	}
	public static PathCollectionItemID fromString(final String id) {
		return new PathCollectionItemID(id);
	}
	public static PathCollectionItemID valueOf(final String id) {
		return new PathCollectionItemID(id);
	}
	@GwtIncompatible
	public static String supplyId() {
		return UUID.randomUUID().toString().toLowerCase();
	}
}
