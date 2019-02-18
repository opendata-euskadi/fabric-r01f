package r01f.types.html;

import java.util.UUID;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="htmlElementId")
@NoArgsConstructor
public class HtmlElementId
	 extends OIDBaseMutable<String> 
  implements OID {
	private static final long serialVersionUID = -9181977752174509450L;
	public HtmlElementId(final String oid) {
		super(oid);
	}
	public static HtmlElementId valueOf(final String id) {
		return new HtmlElementId(id);
	}
	public static HtmlElementId forId(final String id) {
		return new HtmlElementId(id);
	}
	public static HtmlElementId supply() {
		return HtmlElementId.forId(UUID.randomUUID().toString());
	}
}
