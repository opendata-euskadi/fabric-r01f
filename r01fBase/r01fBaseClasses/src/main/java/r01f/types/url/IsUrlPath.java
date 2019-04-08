package r01f.types.url;

import r01f.types.IsPath;


/**
 * Interface for every path types
 */
public interface IsUrlPath 
		 extends IsPath {
	/**
	 * Returns the url path as an absolute string url-encoding the given query string 
	 * @param queryString
	 * @return
	 */
	public String asAbsoluteStringIncludingQueryStringEncoded(final UrlQueryString queryString);
	
	/**
	 * Returns the url path as an absolute string not url-encoding the given query string
	 * @param queryString
	 * @return
	 */
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString);
	
	/**
	 * Returns the url path as an absolute string url-encoding or not the given query string
	 * depending on the provided boolean param
	 * @param queryString
	 * @param encodeParamValues
	 * @return
	 */
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues);
	
	/**
	 * Returns the url path as relative string url-encoding the given query string
	 * @param queryString
	 * @return
	 */
	public String asRelativeStringIncludingQueryStringEncoded(final UrlQueryString queryString);
	
	/**
	 * Returns the url path as a relative string not url-encoding the given query string
	 * @param queryString
	 * @return
	 */
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString);
	
	/**
	 * Returns the url path as a relative string url-encoding or not the given query string
	 * depending on the provided boolean param
	 * @param queryString
	 * @param encodeParamValues
	 * @return
	 */
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues);
}
