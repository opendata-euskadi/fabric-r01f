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
	
	public <S extends EnrichedThrowableSubType<?>> S getSubType();
	public int getGroup();
	public int getCode();
	public int getExtendedCode();
	public ExceptionSeverity getSeverity();
	public String getRawMessage();
	public String getMessageAsXML();
	
	public boolean isMoreSeriousThan(final EnrichedThrowable otherEx);
	
	public <S extends EnrichedThrowableSubType<?>> boolean is(final S subClass);
	public <S extends EnrichedThrowableSubType<?>> boolean isAny(final S... subClasses); 
	
	public Throwable getRootCause();
	public List<Throwable> getCausalChain();
	public List<Throwable> getCausalChainReversed();
	public String getRootTypeName();
	public String getRootMethod();
	public String getRootTraceElement();
	public String getStackTraceAsString();
}
