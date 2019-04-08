package r01f.xmlproperties;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableSubType;
import r01f.exceptions.EnrichedThrowableSubTypeWrapper;
import r01f.exceptions.ExceptionSeverity;
import r01f.exceptions.Throwables;

/**
 * {@link XMLPropertiesException} types 
 */
@Accessors(prefix="_")
public enum XMLPropertiesErrorType 
 implements EnrichedThrowableSubType<XMLPropertiesErrorType> {	
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	COMPONENTDEF_NOT_FOUND(0+1),
	COMPONENTDEF_XML_MALFORMED(0+2),
	PROPERTIES_NOT_FOUND(0+3),
	PROPERTIES_XML_MALFORMED(0+4);
	
	@Getter private final int _group = 0;
	@Getter private final int _code;
	
	private XMLPropertiesErrorType(final int code) {
		_code = code;
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	private static EnrichedThrowableSubTypeWrapper<XMLPropertiesErrorType> WRAPPER = EnrichedThrowableSubTypeWrapper.create(XMLPropertiesErrorType.class); 
	
	public static XMLPropertiesErrorType from(final int errorCode) {
		return WRAPPER.from(0,errorCode);
	}
	public static XMLPropertiesErrorType from(final int groupCode,final int errorCode) {
		if (groupCode != 0) throw new IllegalArgumentException(Throwables.message("The group code for a {} MUST be {}",
																								XMLPropertiesErrorType.class,0));
		return WRAPPER.from(0,errorCode);
	}
	@Override
	public ExceptionSeverity getSeverity() {
		return ExceptionSeverity.FATAL;		// All xml properties errors are fatal
	}
	@Override
	public boolean is(final int group,final int code) {
		return WRAPPER.is(this,
						  group,code);
	}
	public boolean is(final int code) {
		return this.is(0,code);
	}
	@Override
	public boolean isIn(final XMLPropertiesErrorType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final XMLPropertiesErrorType el) {
		return WRAPPER.is(this,el);
	}
}
