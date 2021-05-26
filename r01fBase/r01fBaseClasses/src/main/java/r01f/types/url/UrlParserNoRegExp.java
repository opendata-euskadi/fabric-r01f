package r01f.types.url;

import r01f.util.types.Strings;

public class UrlParserNoRegExp
	 extends UrlParserBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlParserNoRegExp(final String url) {
		super(url);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	UrlComponents _parse(final String url) {
		UrlProtocol protocol = UrlProtocol.of(url);
		String urlWithoutProtocol = protocol != null ? UrlProtocol.removeFrom(url)	// < was buggy.
													 : url;

		int pathBeginIndex = urlWithoutProtocol.indexOf('/');
		int portBeginIndex = urlWithoutProtocol.indexOf(':');
		int qryStringBeginIndex = urlWithoutProtocol.indexOf('?',pathBeginIndex > 0 ? pathBeginIndex : 0);
		int anchorBeginIndex = urlWithoutProtocol.indexOf('#',pathBeginIndex > 0 ? pathBeginIndex : 0);

		String anchor = anchorBeginIndex > 0 ? urlWithoutProtocol.substring(anchorBeginIndex + 1,
																			urlWithoutProtocol.length())
											 : null;
		String qryString = qryStringBeginIndex > 0 ? urlWithoutProtocol.substring(qryStringBeginIndex + 1,
																				  anchorBeginIndex > 0 ? anchorBeginIndex
																						  			   : urlWithoutProtocol.length())
												   : null;
		String urlPath = pathBeginIndex > 0 ? urlWithoutProtocol.substring(pathBeginIndex,
																		   qryStringBeginIndex > 0 ? qryStringBeginIndex
																				   				   : anchorBeginIndex > 0 ? anchorBeginIndex
																				   						   				  : urlWithoutProtocol.length())
											: null;
		String port = portBeginIndex > 0 ? urlWithoutProtocol.substring(portBeginIndex+1,
																		pathBeginIndex > 0 ? pathBeginIndex
																				 		   : urlWithoutProtocol.length())
										 : null;
		String host = urlWithoutProtocol.substring(0,
												   portBeginIndex > 0 ? portBeginIndex
														   			  : pathBeginIndex > 0 ? pathBeginIndex
															   			   			       : urlWithoutProtocol.length());
		return new UrlComponents(protocol != null ? protocol : null,
								 Host.strict(host),
								 Strings.isNOTNullOrEmpty(port) ? Integer.parseInt(port) : -1,
								 Strings.isNOTNullOrEmpty(urlPath) ? UrlPath.preservingTrailingSlash()		// BEWARE!
										 									.from(urlPath)
										 						   : null,
								 Strings.isNOTNullOrEmpty(qryString) ? UrlQueryString.fromParamsString(qryString) : null,
								 anchor);
	}
}
