package r01f.model.latinia;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallCollectionField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * <?xml version="1.0" encoding="UTF-8"?>
 * <RESPUESTA>
 * 	<MENSAJE NUM="1">
 * 		<TELEFONO NUM="659000001">
 * 			<RESULTADO>OK</RESULTADO>
 * 			<IDENTIFICADOR>UGsiZ7E1naZX/Uey32A1hFUq</IDENTIFICADOR>
 * 		</TELEFONO>
 * 		<TELEFONO NUM="666000001">
 * 			<RESULTADO>OK</RESULTADO>
 * 			<IDENTIFICADOR>UGsiZ7E2efSshUey32A1mU7o</IDENTIFICADOR>
 * 		</TELEFONO>
 * 		<TELEFONO NUM="600123456">
 * 			<RESULTADO>ERROR</RESULTADO>
 * 			<CODIGO_ERROR>301</CODIGO_ERROR>
 * 			<MENSAJE_ERROR>El mensaje ha expirado</MENSAJE_ERROR>
 * 		</TELEFONO>
 * 	</MENSAJE>
 * <MENSAJE NUM="2">
 * ........
 * </MENSAJE>
 * </RESPUESTA>
 */
@MarshallType(as="MENSAJE")
@Accessors(prefix="_")
public class LatiniaResponseMessage
  implements LatiniaObject {

	private static final long serialVersionUID = 4262827727118295752L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

	@MarshallField(as="NUM",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _receiverNumber;

	@MarshallField(as="RESULTS",
				   whenCollectionLike=@MarshallCollectionField(useWrapping=false))
	@Getter @Setter private List<LatiniaResponsePhone> _responsePhoneResults;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public LatiniaResponseMessage addResponsePhone(final LatiniaResponsePhone responsePhone) {
		if (_responsePhoneResults == null) _responsePhoneResults = Lists.newArrayList();
		_responsePhoneResults.add(responsePhone);
		return this;
	}
}
