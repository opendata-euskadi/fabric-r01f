package r01f.services;

import lombok.experimental.Accessors;


/**
 * Exception thrown by the proxies because the server layer could not be reached or 
 * because the server response is not valid
 */
@Accessors(prefix="_")
public class ServiceProxyException 
     extends RuntimeException {

	private static final long serialVersionUID = -4968119097697717368L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServiceProxyException() {
		super();
	}
	public ServiceProxyException(final String msg) {
		super(msg);
	}
	public ServiceProxyException(final Throwable otherEx) {
		super(otherEx);
	}
	public ServiceProxyException(final String msg,
								 final Throwable otherEx) {
		super(msg,
			  otherEx);
	}
}
