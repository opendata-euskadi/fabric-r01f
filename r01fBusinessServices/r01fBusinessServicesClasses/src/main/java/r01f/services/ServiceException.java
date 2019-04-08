package r01f.services;

import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedRuntimeException;


/**
 * A server exception that "bubbles" to the client-layer
 * This server exception could be caused by:
 * <ul>
 * 		<li>A client bad request (ie: illegal arguments)</li>
 * 		<li>A server exception</li>
 * </ul>
 */
@Accessors(prefix="_")
public class ServiceException 
     extends EnrichedRuntimeException {

	private static final long serialVersionUID = -4968119097697717368L;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServiceException(final ServiceExceptionType type) {
		super(ServiceExceptionType.class,
			  type);
	}
	public ServiceException(final String msg,
							final ServiceExceptionType type) {
		super(ServiceExceptionType.class,
			  msg,
			  type);
	}
	public ServiceException(final Throwable otherEx) {
		super(ServiceExceptionType.class,
			  otherEx,
			  ServiceExceptionType.from(otherEx));
	}
	public ServiceException(final String msg,
						    final Throwable otherEx) {
		super(ServiceExceptionType.class,
			  msg,
			  otherEx,
			  ServiceExceptionType.from(otherEx));
	}
}
