package r01f.exceptions;

import java.io.Serializable;
import java.util.List;

import com.google.common.annotations.GwtIncompatible;

/**
 * Interface for enriched exceptions
 */
@GwtIncompatible
public interface EnrichedThrowable 
		 extends Serializable {
	
	public <T extends EnrichedThrowableType> T getType();
	public int getGroup();
	public int getCode();
	public int getExtendedCode();
	public ExceptionSeverity getSeverity();
	public String getRawMessage();
	public String getMessageAsXML();
	
	public boolean isMoreSeriousThan(final EnrichedThrowable otherEx);
	
	public <T extends EnrichedThrowableType> boolean is(final T subClass);
	public <T extends EnrichedThrowableType> boolean isAny(final T... subClasses); 
	
	public Throwable getRootCause();
	public List<Throwable> getCausalChain();
	public List<Throwable> getCausalChainReversed();
	public String getRootTypeName();
	public String getRootMethod();
	public String getRootTraceElement();
	public String getStackTraceAsString();
}
