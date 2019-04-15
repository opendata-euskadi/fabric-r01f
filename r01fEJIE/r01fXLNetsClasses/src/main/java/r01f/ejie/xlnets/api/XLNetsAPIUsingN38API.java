
package r01f.ejie.xlnets.api;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;
import n38a.exe.N38APISesion;
import n38c.exe.N38API;
import n38c.exe.N38Estructura;
import r01f.ejie.xlnets.config.XLNetsLoginType;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceItemType;
import r01f.ejie.xlnets.model.XLNetsOrganizationType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;
import r01f.util.types.Strings;
import r01f.xml.XMLUtils;

@Slf4j
  class XLNetsAPIUsingN38API
extends XLNetsAPIImplBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final N38API _n38Api;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XLNetsAPIUsingN38API(final HttpServletRequest req) {
		super(XLNetsLoginType.USER);
		_n38Api = new N38API(req);
	}
	public XLNetsAPIUsingN38API(final AppCode appCode) {
		this(appCode,
			 new N38APISesion().n38APISesionCrearApp(appCode.asString()));		
	}
	protected XLNetsAPIUsingN38API(final AppCode appCode,
								   final Document n38AppSession) {
		super(XLNetsLoginType.APP);
		if (log.isDebugEnabled()) log.debug("XLNets session token for {}:\n",
											appCode,
											XMLUtils.asString(n38AppSession));
		_n38Api = new N38API(n38AppSession);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTH
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsSessionTokenDoc() {
		Document outSessionToken = _n38Api.n38ItemSesion();
		return outSessionToken;
	}
	@Override
	public Document getAuthorizationDoc(final String authResourceOid,final ResourceItemType resourceItemType) {
		Document outAuthToken = null;
    	if (resourceItemType == ResourceItemType.FUNCTION) {
    		outAuthToken = _n38Api.n38ItemObtenerAutorizacion(authResourceOid);
    	} else if (resourceItemType == ResourceItemType.OBJECT) {
    		outAuthToken = _n38Api.n38ItemSeguridad(authResourceOid);
    	}
		return outAuthToken;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsUserDoc(final UserCode userCode) {
        Document outUserDoc = _n38Api.n38ItemObtenerPersonas("uid=" + userCode);
        return outUserDoc;
	}
	@Override
	public Document getXLNetsWorkplaceDoc(final WorkPlaceCode workPlaceCode) {
        Document outUserDoc = _n38Api.n38ItemObtenerPuestos("n38uidPuesto=" + workPlaceCode);
        return outUserDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ORG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsItemOrgDoc(final XLNetsOrganizationType type,
			                            final String uid) {
    	N38Estructura estructura = new N38Estructura();
    	estructura.setTipoEntrada(type.getCode());
    	estructura.setUidEntrada(uid);
    	Document organizationDoc = _n38Api.n38ItemOrganizacion(estructura);
        return organizationDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsUserQueryDoc(final String filter) {
		final String theFilter = _escapeLDAPSearchFilter(filter);
		String ldapFilter = Strings.customized("(&" +
											   		"(objectclass=n38persona)" +
												    "(|" +
												   		"(givenname={}*)" +
												   		"(sn={}*)" +
												   		"(uid={}*)" + 
												   		"(dni={}*)" + 
												   	 ")" +
											   ")"+
											   "#ATRIB" + //retrieve only the folloing atribute values
											   "#dni" + 
											   "#givenname" +
											   "#sn" +
											   "#displayname" +
											   "#telephonenumber" +
											   "#mail" +
											   "#uid" + //user code
											   "#n38puestouid" + //personal code
											   "#n38login" + //login
											   "#n38idioma",
											   theFilter,	// given name
											   theFilter,	// sn
											   theFilter,	// uuid
											   theFilter);	// nif
		log.warn("xlnets ldap query: {}",ldapFilter);
		Document doc = _n38Api.n38ItemObtenerPersonas(ldapFilter);
		return doc;
	}
	private static String _escapeLDAPSearchFilter(final String filter) {
	    StringBuffer sb = new StringBuffer(); 
	     for (int i = 0; i < filter.length(); i++) {
	         char curChar = filter.charAt(i);
	         switch (curChar) {
	             case '\\':
	                 sb.append("\\5c");
	                 break;
	             case '*':
	                 sb.append("\\2a");
	                 break;
	             case '(':
	                 sb.append("\\28");
	                 break;
	             case ')':
	                 sb.append("\\29");
	                 break;
	             case '\u0000': 
	                 sb.append("\\00"); 
	                 break;
	             default:
	                 sb.append(curChar);
	         }
	     }
	     return sb.toString();
	 }
}
