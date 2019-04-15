package r01f.ejie.xlnets.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Iterables;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.config.XLNetsLoginType;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceItemType;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgDivisionID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgDivisionServiceID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrgObjectID;
import r01f.ejie.xlnets.model.XLNetsIDs.XLNetsOrganizationID;
import r01f.ejie.xlnets.model.XLNetsOrganizationType;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xml.XMLUtils;

@Slf4j
  class XLNetsAPIUsingMockFile 
extends XLNetsAPIImplBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Map<UserCode,Path> _mockUsersSessionFilesPaths;
	private final Map<AppCode,Path> _mockAppsSessionFilesPaths;
	private final Map<UserCode,Path> _mockUsersInfoFilesPaths;
	private final Map<WorkPlaceCode,Path> _mockWorkPlacesInfoFilesPaths;
	private final Map<XLNetsOrgObjectID,Path> _mockOrgsFilesPaths;
	private final Map<String,Path> _mockAuthFilesPaths;
	private final Map<AppCode,Path> _mockPersonFilterFilesPaths;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XLNetsAPIUsingMockFile(final XLNetsLoginType loginType,
								  final Map<UserCode,Path> mockUsersSessionFilesPaths,
								  final Map<AppCode,Path> mockAppsSessionFilesPaths,
								  final Map<UserCode,Path> mockUsersInfoFilesPaths,
								  final Map<WorkPlaceCode,Path> mockWorkPlacesInfoFilesPaths,
								  final Map<XLNetsOrgObjectID,Path> mockOrgsFilesPath,
								  final Map<String,Path> mockAuthFilesPaths,
								  final Map<AppCode,Path> mockPersonFilterFilesPaths) {
		super(loginType);
		_mockUsersSessionFilesPaths = mockUsersSessionFilesPaths;
		_mockAppsSessionFilesPaths = mockAppsSessionFilesPaths;
		_mockUsersInfoFilesPaths = mockUsersInfoFilesPaths;
		_mockWorkPlacesInfoFilesPaths = mockWorkPlacesInfoFilesPaths;
		_mockOrgsFilesPaths = mockOrgsFilesPath;
		_mockAuthFilesPaths = mockAuthFilesPaths;
		_mockPersonFilterFilesPaths = mockPersonFilterFilesPaths;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTHORIZATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsSessionTokenDoc() {
		Path mockFilePath = null;
		if (_loginType == XLNetsLoginType.USER) {
			if (CollectionUtils.isNullOrEmpty(_mockUsersSessionFilesPaths)) throw new IllegalStateException("NO mock user session file!");
			mockFilePath = Iterables.getFirst(_mockUsersSessionFilesPaths.values(),
											  null);
			if (mockFilePath == null) throw new IllegalStateException("There's NO mock user session file!");
		} else if (_loginType == XLNetsLoginType.APP) {
			if (CollectionUtils.isNullOrEmpty(_mockAppsSessionFilesPaths)) throw new IllegalStateException("NO mock app session file!");
			mockFilePath = Iterables.getFirst(_mockAppsSessionFilesPaths.values(),
											  null);
			if (mockFilePath == null) throw new IllegalStateException("There's NO mock app session file!");
		}
		Document outSessionToken = XLNetsAPIUsingMockFile.loadAndParseXLNetsMockFile(mockFilePath);
		return outSessionToken;
	}
	@Override
	public Document getAuthorizationDoc(final String authResourceOid,final ResourceItemType resourceItemType) {
		if (CollectionUtils.isNullOrEmpty(_mockAuthFilesPaths)) throw new IllegalStateException("NO mock auth files!");
		Path mockFilePath = _mockAuthFilesPaths.get(authResourceOid);
		if (mockFilePath == null) throw new IllegalStateException("NO mock auth file for " + authResourceOid);
		
		Document outAuthToken = XLNetsAPIUsingMockFile.loadAndParseXLNetsMockFile(mockFilePath);
		return outAuthToken;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	USER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsUserDoc(final UserCode userCode) {
		if (CollectionUtils.isNullOrEmpty(_mockUsersInfoFilesPaths)) throw new IllegalStateException("NO mock user info files!");
		Path mockFilePath = _mockUsersInfoFilesPaths.get(userCode);
		if (mockFilePath == null) throw new IllegalStateException("NO mock user info file for " + userCode);
		
        Document outUserDoc = XLNetsAPIUsingMockFile.loadAndParseXLNetsMockFile(mockFilePath);
        return outUserDoc;
	}
	@Override
	public Document getXLNetsWorkplaceDoc(final WorkPlaceCode workPlaceCode) {
		if (CollectionUtils.isNullOrEmpty(_mockWorkPlacesInfoFilesPaths)) throw new IllegalStateException("NO mock workplace info files!");
		Path mockFilePath = _mockWorkPlacesInfoFilesPaths.get(workPlaceCode);
		if (mockFilePath == null) throw new IllegalStateException("NO mock workplace info file for " + workPlaceCode);
		
        Document outUserDoc = XLNetsAPIUsingMockFile.loadAndParseXLNetsMockFile(mockFilePath);
        return outUserDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ORG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsItemOrgDoc(final XLNetsOrganizationType type,
			                         	final String uid) {
		if (CollectionUtils.isNullOrEmpty(_mockOrgsFilesPaths)) throw new IllegalStateException("NO mock org info files!");
		
		XLNetsOrgObjectID id = null;
		if  (type.equals(XLNetsOrganizationType.ORGANIZATION)) {
			id = XLNetsOrganizationID.forId(Strings.customized("{}_{}",
															   XLNetsOrganizationType.ORGANIZATION.getCode().toLowerCase(),uid));
		} else if (type.equals(XLNetsOrganizationType.GROUP)) {
			id = XLNetsOrgDivisionID.forId(Strings.customized("{}_{}",
															   XLNetsOrganizationType.GROUP.getCode().toLowerCase(),uid));
		} else if (type.equals(XLNetsOrganizationType.CENTER)) {
			id = XLNetsOrgDivisionServiceID.forId(Strings.customized("{}_{}",
															   XLNetsOrganizationType.CENTER.getCode().toLowerCase(),uid));
		} else {
			throw new IllegalStateException(Strings.customized("not a valid of org type {}",type,uid));
		}
		Path mockFilePath = _mockOrgsFilesPaths.get(id);
		if (mockFilePath == null) throw new IllegalStateException("NO mock " + type + " file for " + uid);
		
		Document organizationDoc = XLNetsAPIUsingMockFile.loadAndParseXLNetsMockFile(mockFilePath);
        return organizationDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static Document loadAndParseXLNetsMockFile(final Path xlNetsMockFilePath) {
		Document outDoc = null;
		try {
			InputStream is = ResourcesLoaderBuilder.DEFAULT_RESOURCES_LOADER
												   .getInputStream(xlNetsMockFilePath);
			if (is == null) throw new IOException("There does NOT exists the xlnets mock  file at classpath location " + xlNetsMockFilePath);
			outDoc = XMLUtils.parse(is);
		} catch (IOException ioEx) {
			log.error("Could NOT load mock xlnets file at {}: {}",
					  xlNetsMockFilePath,
					  ioEx.getMessage(),ioEx);			
	    } catch (SAXException saxEx) {
			log.error("Invalid mock xlnets file at {}: {}",
					  xlNetsMockFilePath,
					  saxEx.getMessage(),saxEx);
		}
		return outDoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Document getXLNetsUserQueryDoc(final String filter) {	
		//[1] Get the test data xml document
		if (CollectionUtils.isNullOrEmpty(_mockPersonFilterFilesPaths))  throw new IllegalStateException("NO mock person filter test data files!");
		Path mockFilePath = Iterables.getFirst(_mockPersonFilterFilesPaths.values(),
											  null);
		if (mockFilePath == null) throw new IllegalStateException("There's NO mock person filter test data file!");
		Document userQueryDoc = XLNetsAPIUsingMockFile.loadAndParseXLNetsMockFile(mockFilePath);
		try {
			//[2] Search elements that meet the criteria
			String xpathExpression = Strings.customized("/n38/elementos[@tipo='n38ItemObtenerPersonas']/elemento[@subtipo='n38persona']/parametro[(@id='givenname' and starts-with(translate(valor, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),translate('{}', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')))" + 
																																" or (@id='sn' and starts-with(translate(valor, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),translate('{}', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')))" + 
																																" or (@id='uid' and starts-with(translate(valor, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),translate('{}', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')))" + 
																																" or (@id='dni' and starts-with(translate(valor, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),translate('{}', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')))" + 
																															 "]/..", filter, filter, filter, filter); 
			NodeList nodeList =XMLUtils.nodeListByXPath(userQueryDoc, xpathExpression); 
			
			//[3] Build a document with the resulting list of nodes
			Document outDocument = null;
			try {
				outDocument = DocumentBuilderFactory.newInstance()
													.newDocumentBuilder()
													.newDocument();
				Element root = outDocument.createElement("n38");
				Element list = outDocument.createElement("elementos");
				list.setAttribute("tipo", "n38ItemObtenerPersonas");
				root.appendChild(list);
				outDocument.appendChild(root);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					Node copyNode = outDocument.importNode(node, true);
					list.appendChild(copyNode);
				}
			} catch (ParserConfigurationException e) {
				log.error("Could build resulting document for filter: {}", filter, e.getMessage(), e);
			}
			
			return outDocument;
		} catch (XPathExpressionException e) {
			log.error("Could search by filter {}: {}", filter, e.getMessage(), e);
		}
		//[4] if search fails return whole document
		return userQueryDoc;
	}
}
