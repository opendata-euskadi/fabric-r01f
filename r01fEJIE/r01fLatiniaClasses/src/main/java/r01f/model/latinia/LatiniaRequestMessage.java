package r01f.model.latinia;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

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
@MarshallType(as="MENSAJE_INFO")
@Accessors(prefix="_")
public class LatiniaRequestMessage
  implements LatiniaObject {

	private static final long serialVersionUID = -5839280173208643311L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

	@MarshallField(as="ACUSE",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _acknowledge;     // If delivery notification is requested (S/N).

	@MarshallField(as="TS_DIFERIDO")
	@Getter @Setter private String _timeStamp;      // the date you want to deliver the message.

	@MarshallField(as="TS_EXPIRE")
	@Getter @Setter private String _expireTime;     // Date from which the message was not sent.

	@MarshallField(as="GSM_DEST")
	@Getter @Setter private String _receiverNumbers; // Phone numbers separated by commas (600123456,600987654,....) maximum ten telephone numbers.

	@MarshallField(as="TEXTO")
	@Getter @Setter private String _messageContent; // text to be sending in message.
}
