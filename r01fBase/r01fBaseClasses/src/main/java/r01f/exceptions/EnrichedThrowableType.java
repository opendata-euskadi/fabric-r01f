package r01f.exceptions;

import com.google.common.annotations.GwtIncompatible;

import r01f.debug.Debuggable;

/**
 * Interface for ids representing an {@link EnrichedThrowable} types
 * Usage:
 * <pre class='brush:java'>
 *      @Accessors(prefix="_")
 *      public final class XMLPropertiesErrorType 
 *                 extends EnrichedThrowableTypeBase {	
 *      	private XMLPropertiesErrorType(final String name,
 *      								   final int group,final int code,								
 *      								   final ExceptionSeverity severity) {
 *      		super(name,
 *      			  group,code,
 *      			  severity);
 *      	}
 *      	private static EnrichedThrowableTypeBuilder<XMLPropertiesErrorType>.EnrichedThrowableTypeBuilderCodesStep<XMLPropertiesErrorType> withName(final String name) {		
 *      		return new EnrichedThrowableTypeBuilder<XMLPropertiesErrorType>() {
 *      						@Override
 *      						protected XMLPropertiesErrorType _build(final String name, 
 *      															  	final int group,final int code,
 *      															  	final ExceptionSeverity severity) {
 *      							return new XMLPropertiesErrorType(name,
 *      														      group,code,
 *      														      severity);
 *      						}
 *      			   }.withName(name);
 *      	}
 *      	public static final XMLPropertiesErrorType COMPONENTDEF_NOT_FOUND = XMLPropertiesErrorType.withName("COMPONENTDEF_NOT_FOUND")
 *      																					 .coded(2,1)
 *      																					 .severity(ExceptionSeverity.FATAL)
 *      																					 .build();
 *      	public static final XMLPropertiesErrorType COMPONENTDEF_XML_MALFORMED = XMLPropertiesErrorType.withName("COMPONENTDEF_XML_MALFORMED")
 *      																					 .coded(2,2)
 *      																					 .severity(ExceptionSeverity.FATAL)
 *      																					 .build();
 *      	public static final XMLPropertiesErrorType PROPERTIES_NOT_FOUND = XMLPropertiesErrorType.withName("PROPERTIES_NOT_FOUND")
 *      																					 .coded(2,3)
 *      																					 .severity(ExceptionSeverity.FATAL)
 *      																					 .build();
 *      	public static final XMLPropertiesErrorType PROPERTIES_XML_MALFORMED = XMLPropertiesErrorType.withName("PROPERTIES_XML_MALFORMED")
 *      																					 .coded(2,4)
 *      																					 .severity(ExceptionSeverity.FATAL)
 *      																					 .build();
 * </pre>
 * Just use it like an enum: <code>XMLPropertiesErrorType.COMPONENTDEF_NOT_FOUND</code>
 * @param <E>
 */
@GwtIncompatible
public interface EnrichedThrowableType 
		 extends Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the group (usually is the same for all enum elements)
	 */
	public int getGroup();
	/**
	 * @return the code
	 */
	public int getCode();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the name
	 * (it uses java's type erasure to emulate the name() method of an enum)
	 * @return
	 */
	public String getName();
	/**
	 * Checks if the element is within a provided list of elements
	 * @param els
	 * @return
	 */
	public boolean isIn(EnrichedThrowableType... els);	
	/**
	 * Checks if this element is the same as the provided one
	 * @param el
	 * @return
	 */
	public boolean is(EnrichedThrowableType el);
	/**
	 * Returns true if the enum element has the same group and code as the provided ones 
	 * @param group
	 * @param code
	 * @return
	 */
	public boolean is(final int group,final int code);
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the exception severity from the exception type
	 */
	public ExceptionSeverity getSeverity();
	/**
	 * Checks if this type has the same severity as the given one
	 * @param other
	 * @return
	 */
	public boolean hasSameSeverityAs(final EnrichedThrowableType other);
	/**
	 * Checks if this type is more serious than the given one
	 * @param other
	 * @return
	 */
	public boolean isMoreSeriousThan(final EnrichedThrowableType other);
}
