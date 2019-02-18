package r01f.objectstreamer;

import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Exception thrown by the {@link Marshaller} 
 */
public class MarshallerException 
     extends RuntimeException {
	
    private static final long serialVersionUID = -1329474484762120728L;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////    

	public MarshallerException() {
		super();
	}
	public MarshallerException(final String msg) {
		super(msg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public MarshallerException(final Throwable otherEx) {
		super(otherEx);
	}
	public MarshallerException(final String msg,
							   final Throwable otherEx) {
		super(msg,otherEx);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public MarshallerException(final IOException otherEx) {
		super(otherEx);
	}
	public MarshallerException(final String msg,
							   final IOException otherEx) {
		super(msg,otherEx);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public MarshallerException(final JsonMappingException otherEx) {
		super(otherEx);
	}
	public MarshallerException(final String msg,
							   final JsonMappingException otherEx) {
		super(msg,otherEx);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public MarshallerException(final JsonParseException otherEx) {
		super(otherEx);
	}
	public MarshallerException(final String msg,
							   final JsonParseException otherEx) {
		super(msg,otherEx);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isMappingException() {
		return this.getCause() instanceof JsonMappingException
			|| this.getCause() instanceof AnnotationFormatError;
	}
	public boolean isParsingException() {
		return this.getCause() instanceof JsonParseException;
	}
}
