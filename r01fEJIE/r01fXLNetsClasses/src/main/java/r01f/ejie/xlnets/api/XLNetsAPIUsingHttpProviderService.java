package r01f.ejie.xlnets.api;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.httpclient.HttpClient;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.xml.XMLUtils;

@Slf4j
  class XLNetsAPIUsingHttpProviderService 
extends XLNetsAPIUsingN38API {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XLNetsAPIUsingHttpProviderService(final Url xlnetsProviderUrl,
											 final AppCode appCode) {
		super(appCode,
			  _requestXLNetsSessionToProvider(xlnetsProviderUrl,
											  appCode));
	}
	private static Document _requestXLNetsSessionToProvider(final Url xlnetsProviderUrl,
															final AppCode appCode) {
		// The url can contain a {} placeholder for the appCode
		Url theUrl = Url.from(Strings.customized(xlnetsProviderUrl.asStringNotUrlEncodingQueryStringParamsValues(),
												 appCode));
		// Do the request
		log.warn("Geting a xlnets auth token for appCode={} from url={}",
				 appCode,theUrl);
		Document outSessionToken = null;
		try {
			InputStream tokenIs = HttpClient.forUrl(theUrl)
											.GET()
											.loadAsStream()
												.notUsingProxy().withoutTimeOut().noAuth();
			outSessionToken = XMLUtils.parse(tokenIs);
			if (log.isDebugEnabled()) log.debug("http xlnets app token provider at {}:\n{}",
					 							theUrl,
					 							XMLUtils.asString(outSessionToken));
		} catch (IOException ioEx) {
			log.error("Could NOT get an app xlnests session token from http service at {}: {},",
					  theUrl,
					  ioEx.getMessage(),ioEx);
		} catch (SAXException saxEx) {
			log.error("Invalid app xlnests session token from http service at {}: {},",
					  theUrl,
					  saxEx.getMessage(),saxEx);			
		}
		return outSessionToken;
	}
}
