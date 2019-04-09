package r01f.model.latinia;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * <?xml version="1.0" encoding="UTF-8"?>
 * <PETICION>
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
 * </PETICION>
 */
@MarshallType(as="TELEFONO")
@Accessors(prefix="_")
public class LatiniaResponsePhone
  implements LatiniaObject {

	private static final long serialVersionUID = 350623609498625826L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

	@MarshallField(as="NUM",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _receiverNumber; // If delivery notification is requested (S/N).

	@MarshallField(as="RESULTADO")
	@Getter @Setter private String _result;      // Message response of state (OK/ERROR)

	@MarshallField(as="IDENTIFICADOR")
	@Getter @Setter private String _messageId;      // Identifier (qwerty...)

	@MarshallField(as="CODIGO_ERROR")
	@Getter @Setter private String _errorCode;      // Error code (301)

	@MarshallField(as="MENSAJE_ERROR")
	@Getter @Setter private String _errorMessage;   // Error message (for example "El mensaje ha expirado")

}
