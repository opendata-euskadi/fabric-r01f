package r01f.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Models a model object summarized as oid and name
 * @param <O>
 */
@MarshallType(as="summarizedObjectAsOidAndName")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class SummarizedObjectAsOIDAndName<O extends OID>
  implements SummarizedObject {

	private static final long serialVersionUID = 3445551483946725817L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private O _oid;

	@MarshallField(as="name",escape=true,
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _name;
}
