package r01f.ejie.xlnets.api;

import org.w3c.dom.Document;

import r01f.ejie.xlnets.config.XLNetsLoginType;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceItemType;
import r01f.ejie.xlnets.model.XLNetsOrganizationType;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;

public interface XLNetsAPIImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public XLNetsLoginType getLoginType();
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTH
/////////////////////////////////////////////////////////////////////////////////////////	
	public Document getXLNetsSessionTokenDoc();
	public Document getAuthorizationDoc(final String authResourceOid,final ResourceItemType resourceItemType);
/////////////////////////////////////////////////////////////////////////////////////////
//	USER & WORKPLACE
/////////////////////////////////////////////////////////////////////////////////////////
	public Document getXLNetsUserDoc(final UserCode userCode);
	public Document getXLNetsWorkplaceDoc(final WorkPlaceCode workPlaceCode);
/////////////////////////////////////////////////////////////////////////////////////////
//	ORG
/////////////////////////////////////////////////////////////////////////////////////////	
	public Document getXLNetsItemOrgDoc(final XLNetsOrganizationType type,
			                         	final String uid);
/////////////////////////////////////////////////////////////////////////////////////////
//	QUERY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Search for people whose given name, surname, dni or user code starts with the text provided
	 * @param query	text to filter
	 * @return	document with n38 structure containing only the following atributes for each element:
	 * <ul><li>dni</li><li>givenname</li><li>sn</li><li>displayname</li><li>telephonenumber</li>
	 * <li>mail</li><li>uid</li><li>n38puestouid</li><li>n38login</li><li>n38idioma</li></ul>
	 */
	public Document getXLNetsUserQueryDoc(final String query);
}
