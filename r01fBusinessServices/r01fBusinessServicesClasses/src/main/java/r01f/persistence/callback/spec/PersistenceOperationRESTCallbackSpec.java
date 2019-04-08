package r01f.persistence.callback.spec;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Url;

@MarshallType(as="restCallbackSpecs")
@Accessors(prefix="_")
@NoArgsConstructor
public class PersistenceOperationRESTCallbackSpec
     extends PersistenceOperationCallbackSpecBase {
	
	private static final long serialVersionUID = -989576814692507039L;
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="endPointUrlTemplate",escape=true)
	@Getter @Setter private String _endPointUrlTemplate;
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	public Url getEndPointUrlCustomizedWith(final OID oid) {
		Map<String,String> vars = Maps.newHashMapWithExpectedSize(1);
		vars.put("oid",oid.asString());
		return Url.fromTemplate(_endPointUrlTemplate,
								vars);
	}
}
