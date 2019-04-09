package r01f.model.latinia;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Ejemplo (de documentación Latinia):
 *
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
 *
 * MENSAJES: Lista de MENSAJE_INFO (mensajes) que se desean enviar.
 * + MENSAJE_INFO: información del mensaje que se quiere enviar.
 * 	- ACUSE: S o N.
 * 		 S: Se pide notificación de entrega al proveedor.
 * 		 N: No se pide notificación de entrega al  proveedor.
 * 	- TS_DIFERIDO: Timestamp en milisegundos (long) con la fecha concreta en la que se quiere entregar el mensaje.
 * 	- TS_EXPIRE: Minutos de vida del mensaje (int). Fecha a partir de la cual el mensaje ya no se va a enviar.
 * 	- TEXTO: Sección CDATA con el texto del mensaje que se quiere enviar al usuario.
 * 	- GSM_DEST: Lista de los números de teléfono a los que se quiere enviar el mensaje. Como máximo se pueden enviar 10 y tienen que ir
 *              separados por comas.
 */
@MarshallType(as="PETICION")
@Accessors(prefix="_")
@NoArgsConstructor
public class LatiniaRequest
	implements LatiniaObject {

	private static final long serialVersionUID = 5290061386415635099L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="LATINIA")
	@Getter @Setter private LatiniaRequestMessageList _latiniaRequests;

/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Add new message
	 * @param newMsg the message
	 */
	public LatiniaRequest addMessage(final LatiniaRequestMessage newMsg) {
		if (_latiniaRequests == null) _latiniaRequests = new LatiniaRequestMessageList();
		_latiniaRequests.getLatiniaMessages().add(newMsg);
		return this;
	}
}
