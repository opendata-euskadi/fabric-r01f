package r01f.servlet.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.url.UrlPath;

/**
 * Uses a pathTrim or pathPrepend config to rewrite the url path
 */
@Slf4j
@RequiredArgsConstructor
public class HttpProxyServletUrlPathRewriterDefaultImpl 
  implements HttpProxyServletUrlPathRewriter {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final UrlPath _urlPathTrim;
	private final UrlPath _urlPathPrepend;

/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public UrlPath rewrite(final UrlPath requestedUrlPath) {
		UrlPath targetUrlPath = null;
		
		// --- path trim 
		if (_urlPathTrim == null) {
			// nothing to remove
			targetUrlPath = requestedUrlPath;
		}
		else if (requestedUrlPath.startsWith(_urlPathTrim)) {
			// remove the pathTrim part
			targetUrlPath = requestedUrlPath.urlPathAfter(_urlPathTrim);
			log.warn("path trim '{}' from url: resulting url > {}",
					 _urlPathTrim,targetUrlPath);
		}
		else {
			// nothing to remove
			targetUrlPath = requestedUrlPath;
		}
		
		// --- path prepend
		if (_urlPathPrepend != null) {
			targetUrlPath = _urlPathPrepend.joinedWith(targetUrlPath);
		}
		
		return targetUrlPath;
	}
}
