package r01f.xmlproperties;

import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableTypeBase;
import r01f.exceptions.EnrichedThrowableTypeBuilder;
import r01f.exceptions.ExceptionSeverity;

/**
 * {@link XMLPropertiesException} types 
 */
@Accessors(prefix="_")
public final class XMLPropertiesErrorType 
           extends EnrichedThrowableTypeBase {	
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	private XMLPropertiesErrorType(final String name,
								   final int group,final int code,								
								   final ExceptionSeverity severity) {
		super(name,
			  group,code,
			  severity);
	}
	private static EnrichedThrowableTypeBuilder<XMLPropertiesErrorType>.EnrichedThrowableTypeBuilderCodesStep<XMLPropertiesErrorType> withName(final String name) {		
		return new EnrichedThrowableTypeBuilder<XMLPropertiesErrorType>() {
						@Override
						protected XMLPropertiesErrorType _build(final String name, 
															  	final int group,final int code,
															  	final ExceptionSeverity severity) {
							return new XMLPropertiesErrorType(name,
														      group,code,
														      severity);
						}
			   }.withName(name);
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	public static final XMLPropertiesErrorType COMPONENTDEF_NOT_FOUND = XMLPropertiesErrorType.withName("COMPONENTDEF_NOT_FOUND")
																					 .coded(2,1)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
	public static final XMLPropertiesErrorType COMPONENTDEF_XML_MALFORMED = XMLPropertiesErrorType.withName("COMPONENTDEF_XML_MALFORMED")
																					 .coded(2,2)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
	public static final XMLPropertiesErrorType PROPERTIES_NOT_FOUND = XMLPropertiesErrorType.withName("PROPERTIES_NOT_FOUND")
																					 .coded(2,3)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
	public static final XMLPropertiesErrorType PROPERTIES_XML_MALFORMED = XMLPropertiesErrorType.withName("PROPERTIES_XML_MALFORMED")
																					 .coded(2,4)
																					 .severity(ExceptionSeverity.FATAL)
																					 .build();
}
