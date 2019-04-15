package r01f.ejie.xlnets.api;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.config.XLNetsLoginType;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceItemType;
import r01f.ejie.xlnets.model.XLNetsOrganizationType;
import r01f.ejie.xlnets.model.XLNetsSession;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;
import r01f.types.url.Url;
import r01f.xml.XMLUtils;

@Slf4j
public class XLNetsAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final XLNetsAPIImpl _api;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XLNetsAPI(final XLNetsAPIImpl api) {
		_api = api;
	}
	public XLNetsAPI(final HttpServletRequest req) {
		_api = new XLNetsAPIUsingN38API(req);
	}
	public XLNetsAPI(final AppCode appCode) {
		_api = new XLNetsAPIUsingN38API(appCode);
	}
	public XLNetsAPI(final Url tokenProviderService,
					 final AppCode appCode) {
		_api = new XLNetsAPIUsingHttpProviderService(tokenProviderService,
													 appCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTHORIZATION
/////////////////////////////////////////////////////////////////////////////////////////
	public Document getXLNetsSessionTokenDoc() {
		log.info("[XLNets]: get session token");
		Document outXmlDoc = _api.getXLNetsSessionTokenDoc();
		
		// a bit o degub
		if (outXmlDoc != null) { 
			log.warn("[XLNets] session token is NULL!");
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] session token:\n{}",
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
	}
	public Document getAuthorizationDoc(final String authResourceOid,final ResourceItemType resourceItemType) {
		log.info("[XLNets] authorization for {} ({})",
				 authResourceOid,resourceItemType);
		Document outXmlDoc = _api.getAuthorizationDoc(authResourceOid,resourceItemType);
		
		// a bit of debug
		if (outXmlDoc != null) { 
			log.warn("[XLNets] authorization for {} ({}) is NULL!",
				     authResourceOid,resourceItemType);
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] authorization document for {} ({}):\n{}",
					  authResourceOid,resourceItemType,
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	USER INFO
/////////////////////////////////////////////////////////////////////////////////////////	
	public Document getXLNetsUserDoc(final Document xlNetsSessionDoc) {
		log.info("[XLNets]: logged user info");
		
		// [1] - Get the user id from the session document
		String baseXPath = _api.getLoginType() == XLNetsLoginType.APP 
										? XLNetsSession.XLNETS_APP_SESSION_BASE_XPATH
									    : XLNetsSession.XLNETS_USER_SESSION_BASE_XPATH;
		String userIdXPath = baseXPath + XLNetsSession.PERSONA;
		
        Node userIdNode = null;
        try {
            userIdNode = XMLUtils.nodeByXPath(xlNetsSessionDoc.getDocumentElement(), 	// XPathAPI.selectSingleNode(xlNetsSessionDoc.getDocumentElement(),
            								  userIdXPath);	   							//							 userIdXPath);
        } catch (XPathExpressionException transEx) {
            log.error("Error while retrieving the user data using xPath {} on xlnets session doc: {}",
            		  userIdXPath,transEx.getMessage(),transEx);
        }
        UserCode userCode = userIdNode != null ? UserCode.forId(userIdNode.getNodeValue())
        								   	   : null;
        if (userCode == null) {
        	log.error("Could NOT get the user code from the xlnets session doc:\n",
        			  XMLUtils.asString(xlNetsSessionDoc));
        	throw new IllegalStateException("Could NOT get the user code from the xlnets session doc");
        }
        // [2] - Get the user info using the API
        Document outXmlDoc = _api.getXLNetsUserDoc(userCode);
        
		// a bit of debug
		if (outXmlDoc != null) { 
			log.warn("[XLNets] user info for {} is NULL!",
				     userCode);
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] user info for {}):\n{}",
					  userCode,
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
	}
	public Document getXLNetsUserDoc(final UserCode userCode) {
		log.info("[XLNets]: user info for {}",
				 userCode);
        Document outXmlDoc = _api.getXLNetsUserDoc(userCode);
        
		// a bit of debug
		if (outXmlDoc != null) { 
			log.warn("[XLNets] user info for {} is NULL!",
				     userCode);
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] user info for {}):\n{}",
					  userCode,
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WORKPLACE INFO
/////////////////////////////////////////////////////////////////////////////////////////
	public Document getXLNetsWorkplaceDoc(final Document xlNetsSessionDoc) {
		log.info("[XLNets]: logged user info");
		
		// [1] - Get the user id from the session document
		String baseXPath = _api.getLoginType() == XLNetsLoginType.APP 
										? XLNetsSession.XLNETS_APP_SESSION_BASE_XPATH
									    : XLNetsSession.XLNETS_USER_SESSION_BASE_XPATH;
		String workPlaceIdXPath = baseXPath + XLNetsSession.PUESTO;
		
        Node workPlaceIdNode = null;
        try {
            workPlaceIdNode = XMLUtils.nodeByXPath(xlNetsSessionDoc.getDocumentElement(),	// XPathAPI.selectSingleNode(xlNetsSessionDoc.getDocumentElement(),
            								 workPlaceIdXPath); 							//							 userIdXPath);
        } catch (XPathExpressionException transEx) {
            log.error("Error while retrieving the workplace data using xPath {} on xlnets session doc: {}",
            		  workPlaceIdXPath,transEx.getMessage(),transEx);
        }
        WorkPlaceCode workPlaceCode = workPlaceIdNode != null ? WorkPlaceCode.forId(workPlaceIdNode.getNodeValue())
        								   	  				  : null;
        if (workPlaceCode == null) {
        	log.error("Could NOT get the user code from the xlnets session doc:\n",
        			  XMLUtils.asString(xlNetsSessionDoc));
        	throw new IllegalStateException("Could NOT get the user code from the xlnets session doc");
        }
        // [2] - Get the user info using the API
        Document outXmlDoc = _api.getXLNetsWorkplaceDoc(workPlaceCode);
        
		// a bit of debug
		if (outXmlDoc != null) { 
			log.warn("[XLNets] user info for {} is NULL!",
				     workPlaceCode);
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] user info for {}):\n{}",
					  workPlaceCode,
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
	}
	public Document getXLNetsUserDoc(final WorkPlaceCode workPlaceCode) {
		log.info("[XLNets]: workplace info for {}",
				 workPlaceCode);
        Document outXmlDoc = _api.getXLNetsWorkplaceDoc(workPlaceCode);
        
		// a bit of debug
		if (outXmlDoc != null) { 
			log.warn("[XLNets] workplace info for {} is NULL!",
				     workPlaceCode);
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] workplace info for {}):\n{}",
					  workPlaceCode,
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ORG INFO
/////////////////////////////////////////////////////////////////////////////////////////
	public Document getXLNetsItemOrgDoc(final XLNetsOrganizationType type,
			                            final String uid) {
        log.warn("[XLNets]: info about org type {} with uid {}",
        		 type,uid);
        Document outXmlDoc = _api.getXLNetsItemOrgDoc(type,uid);
        
		// a bit of debug
		if (outXmlDoc != null) { 
			log.warn("[XLNets] info about org type {} with uid {} is NULL!",
				     type,uid);
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] info about org type {} with uid {}:\n{}",
					  type,uid,
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Document getXLNetsUserQueryDoc(final String filter) {
        log.warn("[XLNets]: query users with filter={}",
        		 filter);
        Document outXmlDoc = _api.getXLNetsUserQueryDoc(filter);
        
		// a bit of debug
		if (outXmlDoc != null) { 
			log.warn("[XLNets] query users with filter={}",
				     filter);
		} else if (log.isDebugEnabled()) { 
			log.debug("[XLNets] query users with filter={}",
					  filter,
					  XMLUtils.asString(outXmlDoc));
		}
		return outXmlDoc;
        
	}

}
