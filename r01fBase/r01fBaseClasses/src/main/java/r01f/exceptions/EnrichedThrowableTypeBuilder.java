package r01f.exceptions;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;

@RequiredArgsConstructor
public abstract class EnrichedThrowableTypeBuilder<T extends EnrichedThrowableType>
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    public EnrichedThrowableTypeBuilderCodesStep<T> withName(final String name) {
    	return new EnrichedThrowableTypeBuilderCodesStep<T>(name);
    } 
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EnrichedThrowableTypeBuilderCodesStep<T2 extends EnrichedThrowableType> {
    	private final String _name;
    	
	    public EnrichedThrowableTypeBuilderSeverityStep<T> coded(final int group,final int code) {
	    	return new EnrichedThrowableTypeBuilderSeverityStep<T>(_name,
	    														  group,code);
	    } 
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EnrichedThrowableTypeBuilderSeverityStep<T2 extends EnrichedThrowableType> {
    	private final String _name;
    	private final int _group;
    	private final int _code;
    	
    	public EnrichedThrowableTypeBuilderBuildStep<T2> severity(final ExceptionSeverity severity) {
    		return new EnrichedThrowableTypeBuilderBuildStep<T2>(_name,
    															 _group,_code,
    															 severity);
    	}
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EnrichedThrowableTypeBuilderBuildStep<T3 extends EnrichedThrowableType> {
    	private final String _name;
    	private final int _group;
    	private final int _code;
    	private final ExceptionSeverity _severity;
    	
    	public T build() {
    		return _build(_name,
    					  _group,_code,
    				      _severity);
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
    protected abstract T _build(final String name,
    						    final int group,final int code,
    						    final ExceptionSeverity severity);
}
