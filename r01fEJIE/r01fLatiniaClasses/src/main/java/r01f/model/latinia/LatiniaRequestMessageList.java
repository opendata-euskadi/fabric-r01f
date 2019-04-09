package r01f.model.latinia;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;

/**
 * [ MENSAJE_INFO ==> SMSMessage ]
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <PETICION>
 * 	<LATINIA>
 * 		<MENSAJES>
 * 			<MENSAJE_INFO ACUSE="S">
 * 				<TS_DIFERIDO>620000000</TS_DIFERIDO> <!-- TimeStamp (long)-->
 * 				<TEXTO><![CDATA[prueba mensaje viernes 2]]></TEXTO>
 * 				<GSM_DEST>659000001,666000001</GSM_DEST>
 * 			</MENSAJE_INFO>
 * 			<MENSAJE_INFO ACUSE="N">
 * 				<TS_EXPIRE>10</TS_EXPIRE> <!-- Minutos de vida del mensaje-->
 * 				<TEXTO><![CDATA[Hola mundo!!!]]></TEXTO>
 * 				<GSM_DEST>659000001</GSM_DEST>
 * 			</MENSAJE_INFO>
 * 		</MENSAJES>
 * 	</LATINIA>
 * </PETICION>
 */
@Accessors(prefix="_")
public class LatiniaRequestMessageList
  implements LatiniaObject {

	private static final long serialVersionUID = -1826536971422556993L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="MENSAJES")
	@Getter @Setter private List<LatiniaRequestMessage> _latiniaMessages; //One or more messages
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public LatiniaRequestMessageList() {
		_latiniaMessages = new ArrayList<LatiniaRequestMessage>();
	}
}
