package r01f.servlet.proxy;

import r01f.types.url.UrlPath;

public interface HttpProxyServletUrlPathRewriter {
	/**
	 * Rewrites the url path
	 * @param requestedUrlPath
	 * @return
	 */
	public UrlPath rewrite(final UrlPath requestedUrlPath);
}
