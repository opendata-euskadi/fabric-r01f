package r01f.ejie.xlnets.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.ejie.xlnets.config.XLNetsTargetCfg;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceCfg;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceItemType;
import r01f.ejie.xlnets.context.XLNetsAuthCtx;
import r01f.ejie.xlnets.context.XLNetsResourceCtx;
import r01f.ejie.xlnets.context.XLNetsTargetCtx;
import r01f.ejie.xlnets.model.XLNetsFuncion;
import r01f.ejie.xlnets.model.XLNetsItemSeguridad;
import r01f.ejie.xlnets.model.XLNetsOrganizationType;
import r01f.ejie.xlnets.model.XLNetsSession;
import r01f.ejie.xlnets.model.XLNetsSession.XLNetsSessionType;
import r01f.ejie.xlnets.model.XLNetsTipoObjeto;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;
/**
 * Clase base para el provider de autenticación. 
 * Actualmente implementan esta interface tres clases que definen los distintos
 * sistemas de seguridad:
 * <pre>
 * 1) XLNetsFileAuthProvider : Seguridad implementada a través de ficheros físicos, se usa para simular el acceso a XL-Nets en entornos locales.
 * 2) EHULdapAuthProvider 	 : Seguridad implementada en la infraestructura de la UPV.
 * 3) XLNetsAuthProvider	 : Seguridad implementada mediante el sistema XL-Nets del Gobierno, utilizada para accesos vía web.
 * 4) XLNetsAppAuthProvider  : Seguridad implementada mediante el sistema XL-Nets del Gobierno, utilizada para accesos vía cliente.
 * </pre>
 */
@Accessors(prefix="_")
@Slf4j
public class XLNetsAuthProvider {
// /////////////////////////////////////////////////////////////////////////////////////////
// CONSTANTES
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Clave de la propiedad que aloja el código de aplicación en el login de aplicación.
     */
    public static final String APP_CODE_PROPERTY = "appCode";
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private final XLNetsAPI _xlNetsApi;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public XLNetsAuthProvider(final XLNetsAPI xlNetsApi) {
    	_xlNetsApi = xlNetsApi;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Devuelve un contexto de seguridad que construye el filtro de autorización
	 * Puede hacer login de Aplicación o de usuario en XLNets, en caso de hacer de usuario:
	 * <ul>
	 *      <li>Si el usuario se ha autenticado, devuelve un objeto con el contexto
	 *        de la sesión</li>
	 *      <li>Si el usuario no se ha autenticado devuelve null</li>
	 * @param req
	 * @return Un objeto con el contexto o null si el usuario no se ha autenticado
	 */
	public XLNetsAuthCtx getAuthContext(final HttpServletRequest req) {
        try {
        	// Use the xlnets api to get a session xml document
        	Document xlnetsSessionTokenDoc = _xlNetsApi.getXLNetsSessionTokenDoc();
        	
        	// Check that the returned document is a valid XLNets session
        	XLNetsSessionType sessionType = XLNetsSession.xlnetsSessionTypeFrom(xlnetsSessionTokenDoc);
        	if (xlnetsSessionTokenDoc != null
        	 && sessionType != null) {
        		// Create a more manageable object
	        	XLNetsSession xlnetsSessionToken = new XLNetsSession(xlnetsSessionTokenDoc);
	        	// add the user name: issues another n38 api call to get the user info
	        	log.debug( "....getAuthContext (add the user name: issues another n38 api call to get the user info");
	        	
	        	Document xlnetsUserInfoDoc = null;
	        	try {
	        		xlnetsUserInfoDoc= _xlNetsApi.getXLNetsUserDoc(xlnetsSessionTokenDoc);
	        	} catch (Throwable th) {
	        		log.error("Error retrieving user info: {}",th.getMessage(),th);
	        	}
	        	if (xlnetsUserInfoDoc != null) {
	        		xlnetsSessionToken.setUserInfo(xlnetsUserInfoDoc.getDocumentElement());
	        	} else {
	        		log.warn("NO user info was retrieved from xlnets!!");
	        	}
	        	
	        	Document xlnetsOrganizationDoc = null;
	        	try {
	        		xlnetsOrganizationDoc = _xlNetsApi.getXLNetsItemOrgDoc(XLNetsOrganizationType.ORGANIZATION,
	        														   	   xlnetsSessionToken.getOrganizacion().getOid());
	        	} catch (Throwable th) {
	        		log.error("Error retrieving org info: {}",th.getMessage(),th);
	        	}
	         	Document xlnetsGroupDoc = null;
	         	try {
	         		xlnetsGroupDoc = _xlNetsApi.getXLNetsItemOrgDoc(XLNetsOrganizationType.GROUP,
															   	   	xlnetsSessionToken.getGrupo().getOid());
	         	} catch (Throwable th) {
	        		log.error("Error retrieving org info: {}",th.getMessage(),th);	         		
	         	}
	        	Document xlnetsCenterDoc = null;
	        	try {
	        		xlnetsCenterDoc = _xlNetsApi.getXLNetsItemOrgDoc(XLNetsOrganizationType.CENTER,
															     	 xlnetsSessionToken.getUnidad().getOid());
	        	} catch (Throwable th) {
	        		log.error("Error retrieving org info: {}",th.getMessage(),th);
	        	}
	        	
	        	if (xlnetsOrganizationDoc != null || xlnetsGroupDoc !=null || xlnetsCenterDoc != null) {
	        		xlnetsSessionToken.setOrganizationInfo((xlnetsOrganizationDoc != null )? xlnetsOrganizationDoc.getDocumentElement() : null,
	        											   (xlnetsGroupDoc != null ) ? xlnetsGroupDoc.getDocumentElement() : null,
	        											   (xlnetsCenterDoc != null ) ? xlnetsCenterDoc.getDocumentElement() : null);
	        	} else {
	        		log.warn("NO user info was retrieved from xlnets about organizations !!");
	        	}
	        	

	        	Document xlnetsWorkplaceDoc = null;
	        	try {
	        		xlnetsWorkplaceDoc = _xlNetsApi.getXLNetsWorkplaceDoc(xlnetsSessionTokenDoc);
	        	} catch (Throwable th) {
	        		log.error("Error retrieving workplace info: {}",th.getMessage(),th);
	        	}
	        	if (xlnetsWorkplaceDoc != null ) {
	        		xlnetsSessionToken.setWorkplaceInfo(xlnetsWorkplaceDoc);
	        	} else {
	        		log.warn("NO user info was retrieved from xlnets about workplace !!");
	        	}
	        	
	        
		        // Comprobar si la sesión es válida y en tal caso devolver el contexto.
		        if ( xlnetsSessionToken.getSessionUID() != null ) {
		        	XLNetsAuthCtx  xLNetsAuthCtx = new XLNetsAuthCtx(xlnetsSessionToken);
		        	log.warn("XLNetsAuth context created ! : {}",xLNetsAuthCtx.toString());
			        // Crear el contexto de autorizacion sin recursos autorizados ya que estos se irán
			        // cargando a medida que el filtro llama a la funcion authorize
			        return xLNetsAuthCtx;		// targets.... de momento nada... se va rellenando conforme se llama a authorize
				}
        	} else {
        		log.warn("NO XLNets auth token returned!");
        	}
            return null;
        } catch (Exception ex) {
             log.error("Error desconocido!!: {}",ex.getMessage(),ex);
        }
        return null;	// No se ha podido autenticar
    }
	/**
	 * Redirige al usuario a la página de login
	 * @param res la response
	 * @param returnURL La url a la que ha de devolver al usuario la aplicación de login una vez
	 *                   que este ha hecho login
	 */
	@SuppressWarnings("static-method")
	public void redirectToLogin(final HttpServletResponse res,
								final Url returnURL) {
        try {
            // Redirigir a la pagina de login, pasando como parametro la url a la que hay que
            // volver después de hacer login
            res.sendRedirect(returnURL.asString());
        } catch (IOException ioEx) {
            ioEx.printStackTrace(System.out);
            log.warn("No se ha podido redirigir a la pagina de login");
        }
    }
	/**
	 * Consulta los datos de autorización del destino cuya configuracion
	 * se pasa como parametro
	 * @param authCtx contexto de autorización
	 * @param targetCfg La configuracion del target
	 * @param override
	 * @param req
	 * @return un objeto {@link XLNetsTargetCtx} con el contexto de autorizacion para el destino
	 */
	public XLNetsTargetCtx authorize(final XLNetsAuthCtx authCtx,
    								 final XLNetsTargetCfg targetCfg,
    								 final boolean override,
    								 final HttpServletRequest req) {

		log.warn("::::::::::::: Auth Provider : authorize :::::::::::::::::::");
        if (targetCfg == null || CollectionUtils.isNullOrEmpty(targetCfg.getResources())) return null;

        // Obtener del proveedor la autorizacion para cada item
        XLNetsTargetCtx targetCtx = null;							// Contexto del destino
        Map<String,XLNetsResourceCtx> authorizedResources = null;	// Recursos autorizados en el destino

        try {
	        for (ResourceCfg currResCfg : targetCfg.getResources()) {
	        	XLNetsResourceCtx resourceCtx = null;				// Recurso actual

	        	if (!override) {
		            // Obtener la informacion de autorizacion del recurso a partir del oid de la funcion
		            Document doc = null;
	            	log.warn("...checking access to {} with id={}",
	            			 currResCfg.getType(),currResCfg.getOid());
		            if (currResCfg.getType() == ResourceItemType.FUNCTION) {
		            	log.warn("Resource type function with oid {}",currResCfg.getOid() );
		                doc = _xlNetsApi.getAuthorizationDoc(currResCfg.getOid(),ResourceItemType.FUNCTION);
		                if (doc != null ) {
			                XLNetsFuncion func = new XLNetsFuncion(doc.getDocumentElement());
			                if (func.getItemSeguridad() != null 
			                 && func.getItemSeguridad().getUID() != null
			                 && func.getItemSeguridad().getUID().equalsIgnoreCase(currResCfg.getOid())) {
				                log.warn(" This is the authorized function: ", func.toString());
				                // Contexto del recurso
				                resourceCtx = new XLNetsResourceCtx(func);				// autorizaciones
					            // Meter el recurso autorizado en el contexto
					            if (authorizedResources == null) authorizedResources = new HashMap<String,XLNetsResourceCtx>();
					            authorizedResources.put(resourceCtx.getOid(),
					            						resourceCtx);
			                } else {
			                	log.warn("Has no permission function with oid {} .\n\n" +
			                			 "WARNING ! Check if n38ItemObtenerAutorizacion returns a valid n38ItemSeguridad object with same oid/id",
			                			 currResCfg.getOid() );
			                }
		                } else {
		                	log.warn("Has no permission function with oid {}",currResCfg.getOid() );
		                }

		            } else if (currResCfg.getType() == ResourceItemType.OBJECT) {
		            	doc = _xlNetsApi.getAuthorizationDoc(currResCfg.getOid(),ResourceItemType.OBJECT);

		                XLNetsTipoObjeto tipoObj = new XLNetsTipoObjeto(doc.getDocumentElement());

		                // Contexto del recurso
		                resourceCtx = new XLNetsResourceCtx(tipoObj);

			            // Meter el recurso autorizado en el contexto
			            if (authorizedResources == null) authorizedResources = new HashMap<String,XLNetsResourceCtx>();
			            authorizedResources.put(resourceCtx.getOid(),
			            						resourceCtx);

		                // A diferencia de una funcion, un tipo de objeto tiene instancias que tambien hay
		                // que meter como recursos
		                if (CollectionUtils.hasData(tipoObj.getInstances())) {
		                    for (int i=0; i < tipoObj.getInstances().length; i++) {
		                        XLNetsItemSeguridad itemSeg = tipoObj.getInstances()[i];
		    	                // Contexto del recurso
		    	                resourceCtx = new XLNetsResourceCtx(itemSeg);
		        	            // Meter el recurso autorizado en el contexto
		        	            authorizedResources = new HashMap<String,XLNetsResourceCtx>();
		        	            authorizedResources.put(resourceCtx.getOid(),resourceCtx);
		                    }
		                }
		            }
	        	} else {
	        	    // Se hace override...
		            if (currResCfg.getType() == ResourceItemType.FUNCTION) {
		                // Contexto del recurso
		                resourceCtx = new XLNetsResourceCtx(null,						// parent oid
		                									currResCfg.getOid(),		// oid
								  						  	currResCfg.getName(),		// name
								  						  	currResCfg.getType().getCode(),null,	// tipo / subtipo
								  						  	null);						// autorizaciones

			            // Meter el recurso autorizado en el contexto
			            if (authorizedResources == null) authorizedResources = new HashMap<String,XLNetsResourceCtx>();
			            authorizedResources.put(resourceCtx.getOid(),resourceCtx);

		            } else if (currResCfg.getType() == ResourceItemType.OBJECT) {
		                // Contexto del recurso
                        resourceCtx = new XLNetsResourceCtx(null,						// parent oid
                        									currResCfg.getOid(),		// oid
                        									currResCfg.getName(),		// name
                        									currResCfg.getType().getCode(),null,	// tipo / subtipo
                        									null);						// autorizaciones

			            // Meter el recurso autorizado en el contexto
			            if (authorizedResources == null) authorizedResources = new HashMap<String,XLNetsResourceCtx>();
			            authorizedResources.put(resourceCtx.getOid(),resourceCtx);
		            }
	        	}
                if (resourceCtx != null) log.trace("Nuevo recurso: {}",resourceCtx.getOid());
	        } // del for...
        } catch (Exception saxEx) {
            log.error("Error al parsear el documento xml de informacion de xlnets: {}",saxEx.getMessage(),saxEx);
            return null;
        }
        // Devolver la autorizacion del recurso
        targetCtx = new XLNetsTargetCtx(targetCfg,authorizedResources);		// Contexto de autorizacion del recurso
        return targetCtx;
    }
}
