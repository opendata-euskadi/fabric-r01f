package r01f.ejie.xlnets.model;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xml.XMLUtils;

/**
 * Lee un documento xml con la informacion de XLNets de sesion
 */
@Slf4j
@Accessors(prefix="_")
public class XLNetsSession
     extends XLNetsObjectBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  SENTENCIAS XPath
/////////////////////////////////////////////////////////////////////////////////////////
	// El xml de sesión es casi igual para un login de app o un login de usuario
	// ... solo difiere en el atributo /n38/elementos/@tipo
	public static final String XLNETS_APP_SESSION_BASE_XPATH = "/n38/elementos[@tipo='n38APISesionCrearToken']";
	public static final String XLNETS_USER_SESSION_BASE_XPATH = "/n38/elementos[@tipo='n38APISesionValida']";

	// XML de sesión
    public static transient final String UIDSESSION = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38uidsesion']/valor[1]/text()";
    public static transient final String DNI = "/elemento[@subtipo='N38Sesion']/parametro[@id='dni']/valor[1]/text()";
    public static transient final String LOGIN = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38login']/valor[1]/text()";
    public static transient final String PERSONA = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38personauid']/valor[1]/text()";
    public static transient final String PUESTO = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38puestouid']/valor[1]/text()";
    public static transient final String IDIOMA = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38idioma']/valor[1]/text()";
    public static transient final String IP = "/elemento[@subtipo='N38Sesion']/parametro[@id='iphostnumber']/valor[1]/text()";
    public static transient final String LOGINAPP = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38loginasociado']/valor[1]/text()";
    public static transient final String HOME = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38urlfinal']/valor[1]/text()";
    public static transient final String ORGANIZACION = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38organizacion']/valor[1]/text()";
    public static transient final String GRUPO = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38grupoorganicouid']/valor[1]/text()";
    public static transient final String UNIDAD = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38orgunituid']/valor[1]/text()";
    public static transient final String PERFILES = "/elemento[@subtipo='N38Sesion']/parametro[@id='n38perfiles']/valor";

    // XML de usuario
    private static transient final String DISPLAYNAME = "/n38/elementos[@tipo='n38ItemObtenerPersonas']/elemento[@subtipo='n38persona']/parametro[@id='displayname']/valor[1]/text()";
    private static transient final String NAME = "/n38/elementos[@tipo='n38ItemObtenerPersonas']/elemento[@subtipo='n38persona']/parametro[@id='givenname']/valor[1]/text()";
    private static transient final String SURNAME = "/n38/elementos[@tipo='n38ItemObtenerPersonas']/elemento[@subtipo='n38persona']/parametro[@id='sn']/valor[1]/text()";

    private static transient final String MAIL = "/n38/elementos[@tipo='n38ItemObtenerPersonas']/elemento[@subtipo='n38persona']/parametro[@id='mail']/valor[1]/text()";
    private static transient final String TELEPHONE = "/n38/elementos[@tipo='n38ItemObtenerPersonas']/elemento[@subtipo='n38persona']/parametro[@id='telephonenumber']/valor[1]/text()";

    // XML de organizacion
    private static transient final String ORGANIZATION_ES = "/n38/elementos[@tipo='n38itemorganizacion']/elemento[@subtipo='n38organization']/parametro[@id='n38cadescripcion']/valor[1]/text()";
    private static transient final String ORGANIZATION_EU = "/n38/elementos[@tipo='n38itemorganizacion']/elemento[@subtipo='n38organization']/parametro[@id='n38eudescripcion']/valor[1]/text()";

    private static transient final String GROUP_ES = "/n38/elementos[@tipo='n38itemorganizacion']/elemento[@subtipo='n38gruposorganicos']/parametro[@id='n38cadescripcion']/valor[1]/text()";
    private static transient final String GROUP_EU = "/n38/elementos[@tipo='n38itemorganizacion']/elemento[@subtipo='n38gruposorganicos']/parametro[@id='n38eudescripcion']/valor[1]/text()";

    private static transient final String ORG_UNIT_ES = "/n38/elementos[@tipo='n38itemorganizacion']/elemento[@subtipo='n38organizationalunit']/parametro[@id='n38cadescripcion']/valor[1]/text()";
    private static transient final String ORG_UNIT_EU = "/n38/elementos[@tipo='n38itemorganizacion']/elemento[@subtipo='n38organizationalunit']/parametro[@id='n38eudescripcion']/valor[1]/text()";

    //XML de puesto
    private static transient final String TERRITORY_CODE = "/n38/elementos[@tipo='n38ItemObtenerPuestos']/elemento[@subtipo='n38puesto']/parametro[@id='n38codterri']/valor[1]/text()";
    private static transient final String BUILDING_CODE = "/n38/elementos[@tipo='n38ItemObtenerPuestos']/elemento[@subtipo='n38puesto']/parametro[@id='n38coedificio']/valor[1]/text()";

/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final Date _loginDate = new Date();   // Fecha de login
    @Getter private final String _sessionUID;
    @Getter private final XLNetsUser _user;
    @Getter private final XLNetsOrgNode _organizacion;
    @Getter private final XLNetsOrgNode _grupo;
    @Getter private final XLNetsOrgNode _unidad;
    @Getter private final XLNetsPuesto _workplace;
    @Getter private final Collection<String> _perfiles;
    @Getter private 	  Map<String,String> _attributes;
    
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public XLNetsSession(final Document sessionDoc) {
        super();

        String xPathBase = null;

        XLNetsSessionType type = XLNetsSession.xlnetsSessionTypeFrom(sessionDoc);
        if (type == null) {
        	throw new IllegalArgumentException("The provided xlnets session xml is not valid!");
        } else if (type == XLNetsSessionType.USER) {
        	xPathBase = XLNETS_USER_SESSION_BASE_XPATH;
        } else if (type == XLNetsSessionType.APP) {
        	xPathBase = XLNETS_APP_SESSION_BASE_XPATH;
        } else {
        	throw new IllegalArgumentException("The provided xlnets session xml is not of an accepted type (user/app)!");
        }

        Node sessionNode = sessionDoc.getDocumentElement();

        _sessionUID = _extractValue(sessionNode,xPathBase + UIDSESSION);

        // Datos del usuario
        String login = _extractValue(sessionNode,xPathBase + LOGIN);
        String loginApp = _extractValue(sessionNode,xPathBase + LOGINAPP);
        String persona = _extractValue(sessionNode,xPathBase + PERSONA);
        String puesto = _extractValue(sessionNode,xPathBase + PUESTO);
        String dni = _extractValue(sessionNode,xPathBase + DNI);
        String home = _extractValue(sessionNode,xPathBase + HOME);
        String idioma = _extractValue(sessionNode,xPathBase + IDIOMA);
        String ip = _extractValue(sessionNode,xPathBase + IP);

        Boolean isLoginApp = loginApp != null && loginApp.equalsIgnoreCase("TRUE") ? true : false;
        Language lang = idioma != null && idioma.equals("3") ? Language.SPANISH : Language.BASQUE;

        _user = new XLNetsUser(login,
        					   isLoginApp,
        					   login,
        					   persona,
        					   puesto,
        					   null, //Name
        					   null, //Surname
        					   null, //Displayname
        					   dni,
        					   home,
        					   null, //mail
        					   null, //telephone
        					   lang,
        					   ip,
        					   null);

        // Organizacion del usuario
        String organizacion = _extractValue(sessionNode,xPathBase + ORGANIZACION);
        String grupo = _extractValue(sessionNode,xPathBase + GRUPO);
        String unidad = _extractValue(sessionNode,xPathBase + UNIDAD);
        _organizacion = new XLNetsOrgNode(organizacion,
        								  organizacion,organizacion,
        								  null);
        _grupo = new XLNetsOrgNode(grupo,
        						   grupo,grupo,
        						   _organizacion);		// apunta al padre
        _unidad = new XLNetsOrgNode(unidad,
        							unidad,unidad,
        							_grupo);			// apunta al padre

        // Perfiles
        String[] perfiles = _extractMultipleValue(sessionNode,xPathBase + PERFILES);
        if (CollectionUtils.hasData(perfiles)) {
        	_perfiles = Lists.newArrayListWithExpectedSize(perfiles.length);
        	for (String perfil : perfiles) {
        		_perfiles.add(perfil);
        	}
        } else {
        	_perfiles = null;
        }
        
        //Puesto
        String codigoTerritorial = _extractValue(sessionNode,xPathBase + TERRITORY_CODE);
        String codigoEdificio = _extractValue(sessionNode,xPathBase + BUILDING_CODE);
        
        _workplace = new XLNetsPuesto(puesto, 
        							  codigoTerritorial, 
        							  codigoEdificio);
    }
	/**
	 * Añade información del usuario que viene de otro XML de XLNets
	 * @param userNode nodo xml con la información de usuario
	 */
	public void setUserInfo(final Node userNode) {

    	String name = null;
    	String surname = null;
    	String displayName = null;
    	String mail = null;
    	String telephone = null;
        if (userNode != null) {
	    	//// Name
        	String nameOut = _extractValue(userNode,NAME);
    		name = Strings.isNullOrEmpty(nameOut) ? "unknown" : nameOut;
    		//// Surname
    		String surnameOut = _extractValue(userNode,SURNAME);
    		surname = Strings.isNullOrEmpty(surnameOut) ? "unknown" : surnameOut;
        	/////Display Name
        	String dispName = _extractValue(userNode,DISPLAYNAME);
    		displayName = Strings.isNullOrEmpty(dispName) ? "unknown" : dispName;
    		//// Mail
    		String mailValue = _extractValue(userNode,MAIL);
    		mail = Strings.isNullOrEmpty(mailValue) ? "unknown" : mailValue;
    		//// Telephone
    		String telephoneValue = _extractValue(userNode,TELEPHONE);
    		telephone = Strings.isNullOrEmpty(telephoneValue) ? "unknown" : telephoneValue;
 
        } else {
        	displayName = "unknown";
        	name = "unknown";
        	surname = "unknown";
        	mail = "unknown";
        	telephone = "unknown";
        }
        _user.setName(name);
        _user.setSurname(surname);
        _user.setDisplayName(displayName);
        _user.setMail(mail);
        _user.setTelephone(telephone);
    }
	
	
	 /**
	 * Añade información del usuario que viene de otro XML de XLNets
	 * @param workplaceNode nodo xml n38ObtenerPuestos con la información del puesto
	 */
	public void setWorkplaceInfo(final Node workplaceNode) {

    	String territoryCode = null;
    	String buildingCode = null;
    
        if (workplaceNode != null) {
	    	// Territory Code
        	String territoryCodeOut = _extractValue(workplaceNode,TERRITORY_CODE);
    		territoryCode = Strings.isNullOrEmpty(territoryCodeOut) ? "unknown" : territoryCodeOut;
    		
    		// Building Code
        	String buildingCodeOut = _extractValue(workplaceNode,BUILDING_CODE);
    		buildingCode = Strings.isNullOrEmpty(buildingCodeOut) ? "unknown" : buildingCodeOut;
    		
 
        } else {
        	territoryCode = "unknown";
        	buildingCode = "unknown";
        }
        _workplace.setCodigoTerritorial(territoryCode);
        _workplace.setCodigoEdificio(buildingCode);
    }
	/**
	 * Comprueba si un usuario pertenece a una determinada organizacion
	 * @param orgOID: El oid de la organizacion
	 * @return: true si pertenece o false si no es asi
	 */
	public boolean userBelongsTo(final String orgOID) {
        if (_organizacion == null) return false;
        if (_organizacion.getOid().equals(orgOID)) return true;
        if (_organizacion.isSubOrgOf(orgOID)) return true;
        if (_organizacion.isSupraOrgOf(orgOID)) return true;
        return false; // No!
    }

	/**
	 *  Añade información de organizaciones que viene de otro XML de XLNets (item organizaciones)
	 * @param organizationNode
	 * @param groupNode
	 * @param coNode
	 */
	public void setOrganizationInfo(final Node organizationNode, final Node groupNode,  final Node coNode ) {
        if (organizationNode != null) {
	    	String orgaNameES = _extractValue(organizationNode,ORGANIZATION_ES);
	    	String orgaNameEU = _extractValue(organizationNode,ORGANIZATION_EU);
	    	_organizacion.setName(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_DEFAULT_VALUE,"unknown")
		    	    										.add(Language.SPANISH,orgaNameES)
		    	    										.add(Language.BASQUE,orgaNameEU));

        }
        if (groupNode !=null ) {
        	String orgaNameES = _extractValue(groupNode,GROUP_ES);
	    	String orgaNameEU = _extractValue(groupNode,GROUP_EU);
	    	_grupo.setName(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_DEFAULT_VALUE,"unknown")
	    	    										.add(Language.SPANISH,orgaNameES)
	    	    										.add(Language.BASQUE,orgaNameEU));
        }
        if (coNode !=null ) {
        	String orgaNameES = _extractValue(coNode,ORG_UNIT_ES);
	    	String orgaNameEU = _extractValue(coNode,ORG_UNIT_EU);
	    	_unidad.setName(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_DEFAULT_VALUE,"unknown")
		    	    										.add(Language.SPANISH,orgaNameES)
		    	    										.add(Language.BASQUE,orgaNameEU));

        }
    }

	
	/**
	 * Devuelve true o false en funcion de si el usuario tiene o no el perfil solicitado
	 * @param profile El perfil solicitado
	 * @return True si el usuario tiene el perfil o false si no es asi
	 */
	public boolean hasProfile(final String profileOID) {
        return CollectionUtils.hasData(_perfiles) ? _perfiles.contains(profileOID)
        										  : false;
    }
	/**
	 * Devuelve los perfiles de un código de aplicacion
	 * @param appCode
	 * @return
	 */
	public Collection<String> profilesOf(final AppCode appCode) {
		Collection<String> outProfiles = Lists.newArrayList();
		if (CollectionUtils.hasData(_perfiles)) {
			outProfiles = FluentIterable.from(_perfiles)
										.filter(new Predicate<String>() {
													@Override
													public boolean apply(final String profile) {
														return profile.startsWith(appCode.asString().toUpperCase());
													}

												})
										.toList();
		}
		return outProfiles;
	}
	/**
	 * Devuelve un atributo del contexto (ip, paginaLogin, paginaPortal, paginaPrincipal, lenguaje, etc)
	 * @param attrName El nombre del atributo
	 * @return El atributo (String)
	 */
	public String getAttribute(final String attrName) {
        return CollectionUtils.hasData(_attributes) ? _attributes.get(attrName)
        										    : null;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		StringBuilder dbg = new StringBuilder();
		if (Strings.isNOTNullOrEmpty(_sessionUID)) dbg.append(" UID Session: ").append(this.getSessionUID()).append("\n");
		if (_user != null) 						   dbg.append("        User: ").append(this.getUser()).append("\n");
		if (_organizacion != null) 				   dbg.append("Organizacion: ").append(this.getOrganizacion()).append("\n");
		if (_grupo != null) 					   dbg.append("       Grupo: ").append(this.getGrupo()).append("\n");
		if (_unidad != null) 					   dbg.append("      Unidad: ").append(this.getUnidad()).append("\n");
		if (_perfiles != null)					   dbg.append("    Perfiles: ").append(this.getPerfiles()).append("\n");
	    return dbg.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guess the xlnets session type from the xml document
	 * @param doc
	 * @return null if the xml document does not match any session xml document
	 */
	public static XLNetsSessionType xlnetsSessionTypeFrom(final Document doc) {
		XLNetsSessionType outType = null;
		
        // Try to guess the xPath base that depends on whether it's an app login or a user login
        Node sessionTypeAttr = _executeXPathForNode(doc.getDocumentElement(),
        											"/n38/elementos/@tipo");
        if (sessionTypeAttr == null) {
        	log.warn("The provided xlnets session xml is not valid!");
        	log.warn(XMLUtils.asString(doc.getDocumentElement()));
        	//throw new IllegalArgumentException("The provided xlnets session xml is not valid!");
        } else if (sessionTypeAttr.getNodeValue().equals("n38APISesionValida")) {
        	outType = XLNetsSessionType.USER;
        } else if (sessionTypeAttr.getNodeValue().equals("n38APISesionCrearToken")) {
        	outType = XLNetsSessionType.APP;
        } else {
        	log.warn("The provided xlnets session xml is not of an accepted type (user/app)!");
        	//throw new IllegalArgumentException("The provided xlnets session xml is not of an accepted type (user/app)!");
        }
        return outType;
	}
	public enum XLNetsSessionType {
		USER,
		APP;
	}
}
