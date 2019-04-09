package r01f.model.latinia;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Los parámetros de conexión necesarios para autenticación en LATINIA son:
 *
 * Públicos (propios de la plataforma):
 *    - Empresa (Login enterprise): INNOVUS
 *    - Usuario Latinia: innovus.superusuario
 *    - Password Latinia: MARKSTAT
 *
 * Privados (propios de cada aplicación)
 *    - Aplicación (refProduct): <código aplicación>
 *    - Contrato (idcontract): <código de contrato asociado a la aplicación>
 *    - Contraseña: <por defecto código de aplicación en desarrollo>
 *
 * Con esto puede formarse un xml válido para envío SMS a través de los WebServices de W91D.
 *
 * Ejemplo para una aplicación Z99 con contrato 2000
 *
 * <authenticationLatinia>
 *     <loginEnterprise>INNOVUS</loginEnterprise>
 *     <userLatinia>innovus.superusuario</userLatinia>
 *     <passwordLatinia>MARKSTAT</passwordLatinia>
 *     <refProduct>Z99</refProduct>
 *     <idContract>2000</idContract>
 *     <password>Z99</password>
 * </authenticationLatinia>
 *
 * (La opción más recomendable para la autenticación es el uso de XLNETS. De
 * esta forma la aplicación se abstrae de los códigos y contraseñas de
 * autenticación en LATINIA que serán obtenidos por el conector W91D a partir
 * del token de sesión enviado en la petición al WebService)
 */
@MarshallType(as="authenticationLatinia")
@Accessors(prefix="_")
public class LatiniaAuthentication
	implements LatiniaObject {

	private static final long serialVersionUID = -9024193208434640204L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="loginEnterprise")
	@Getter @Setter private String _loginEnterprise; // INNOVUS

	@MarshallField(as="userLatinia")
	@Getter @Setter private String _userLatinia;     // innovus.superusuario

	@MarshallField(as="passwordLatinia")
	@Getter @Setter private String _passwordLatinia; // MARKSTAT

	@MarshallField(as="refProduct")
	@Getter @Setter private String _refProduct;      // X47B

	@MarshallField(as="idContract")
	@Getter @Setter private String _idContract;      // DESA:2066,PROD:2054 (el id que nos asignen...)

	@MarshallField(as="password")
	@Getter @Setter private String _password;        // X47N
}
