package r01f.types.url;

import java.util.UUID;

import com.google.common.annotations.GwtIncompatible;

import lombok.experimental.Accessors;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="urlCollectionItemId")
@Accessors(prefix="_")
public class UrlCollectionItemID
     extends OIDBaseMutable<String>  {

	private static final long serialVersionUID = 3121699786663851476L;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlCollectionItemID() {
		/* default no args constructor for serialization purposes */
	}
	public UrlCollectionItemID(final String id) {
		super(id);
	}
	public static UrlCollectionItemID forId(final String id) {
		return new UrlCollectionItemID(id);
	}
	public static UrlCollectionItemID fromString(final String id) {
		return new UrlCollectionItemID(id);
	}
	public static UrlCollectionItemID valueOf(final String id) {
		return new UrlCollectionItemID(id);
	}
	@GwtIncompatible
	public static String supplyId() {
		return UUID.randomUUID().toString().toLowerCase();
	}

}
