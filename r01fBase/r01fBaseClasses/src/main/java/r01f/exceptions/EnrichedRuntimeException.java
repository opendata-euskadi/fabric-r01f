package r01f.exceptions;

import java.util.List;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;


/**
 * Enriched exception with some gotchas like:
 * <pre>
 * 		- subType:		Sometimes different exceptions are created to express different error conditions (ie: RecordInsertException, RecordDeleteException, RecordUpdateException). 
 * 						this makes the client code capture all of them:</pre>
 * 						<pre class='brush:java'>	
 * 						try {
 * 							// database code
 * 						} catch(RecordInsertException riEx) {
 * 								...
 * 						} catch(RecordDeleteException rdEx) {
 * 								...
 * 						} catch(RecordUpdateException ruEx) {
 * 								...
 * 						}
 * 						</pre><pre>
 * 						It would be better to throw a single "generic" exception (ie. DBExcepcion) and set some property with the exception sub-type
 * 						This way the client-capturing code is more compact and still retain info about the error condition:</pre>
 * 						<pre class='brush:java'>
 * 						try {
 * 							// database code
 * 						} catch(DBException dbEx) {
 * 							... the sub-type can be known
 * 							dbEx.getSubType()
 * 							... and also actions could be taken depending on the sub-type
 * 							switch(dbEx.getSubType()) {
 * 							case A:
 * 								...
 * 							case B:
 * 								...
 * 							default:
 * 								...
 * 							}
 * 						}
 * 						</pre><pre>
 *		- group/code: 	numeric values for error group / code
 *		- severity:		a pre-defined value for the severity at {@link ExceptionSeverity}
 *		- Traceability messages
 *		- Whether the exception must be logged or not
 *		- If the exception should be re-thrown "as is" or as a new exception in which the stacktrace is replced
 *</pre>
 * The usual usage is:
 * <ul>
 * <li>1.- Create an enum with the sub-types (if there are any)
 * <pre class='brush:java'>
 *  @Accessors(prefix="_")
 *	@RequiredArgsConstructor
 *		  enum TestExceptionSubTypes 
 *  implements EnrichedThrowableSubType<TestExceptionSubTypes> {
 *			NOT_DELETE(1),
 *			VALIDATION(2);
 *
 *			@Getter private final int _group = 1;
 *			@Getter private final int _code;
 *
 *			private static EnrichedThrowableSubTypeWrapper<TestExceptionSubTypes> WRAPPER = EnrichedThrowableSubTypeWrapper.create(TestExceptionSubTypes.class);
 *
 *	 		public static TestExceptionSubTypes from(final int errorCode) {
 *				return WRAPPER.from(0,errorCode);
 *			}
 *			@Override
 *			public ExceptionSeverity getSeverity() {
 *				return ExceptionSeverity.FATAL;		
 *			}
 *			@Override
 *			public boolean is(final int group,final int code) {
 *				return WRAPPER.is(this,
 *								  group,code);
 *			}
 *			public boolean is(final int code) {
 *				return this.is(R01F.CORE_GROUP,code);
 *			}
 *			@Override
 *			public boolean isIn(final TestExceptionSubTypes... els) {
 *				return WRAPPER.isIn(this,els);
 *			}
 *			@Override
 *			public boolean is(final TestExceptionSubTypes el) {
 *				return WRAPPER.is(this,el);
 *			}
 *		}
 * </pre>
 * </li>
 * <li>2.- Create a type extending {@link EnrichedRuntimeException}
 * <pre class='brush:java'>
 *		public class TestException 
 *           extends EnrichedRuntimeException {
 *			public TestException(final String msg,
 *								 final TestExceptionSubTypes type) {
 *				super(TestExceptionSubTypes.class,
 *					  msg,
 *					  type);
 *			}	
 *			public TestException(final Throwable otherEx,
 *								 final TestExceptionSubTypes type) {
 *				super(TestExceptionSubTypes.class,
 *					  otherEx,
 *					  type);
 *			}
 *			public TestException(final String msg,final Throwable otherEx,
 *								 final TestExceptionSubTypes type) {
 *				super(TestExceptionSubTypes.class,
 *					  msg,otherEx,
 *					  type);
 *			}
 *		}
 *</pre>
 * </li>
 * <li>3.- Create N static builder methods like:</li>
 * <pre class='brush:java'>
 *		public static TestException deleteContentError(final String recordOid,
 *												       final Throwable th) {
 *			return new TestException("Delete content error at " + recordId,
 *									 th,
 *									 NOT_DELETE);
 *		}
 * </pre>
 * </li>
 * </ul>
 * 
 * 
 * <pre class='brush:java'>
 *		throw TestException.deleteContentError("myRecordOid",
 *											   th);
 * </pre>
 *  
 */
@GwtIncompatible
@Accessors(prefix="_") 
public abstract class EnrichedRuntimeException 
              extends RuntimeException
		   implements EnrichedThrowable {
	
	private static final long serialVersionUID = -2026592397288534675L;
///////////////////////////////////////////////////////////////////////////////
//	final FIELDS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * The subType java type 
	 * (necessary to create the EnrichedThrowableSubType from group and code)
	 */
	private final Class<? extends EnrichedThrowableSubType<?>> _subTypeType;
	/**
	 * The group
	 */
	@Getter protected final int _group;
	/**
	 * The code
	 */
	@Getter protected final int _code;
	/**
	 * Extended code (used for application-specific codes)
	 */
	@Getter protected final int _extendedCode;
	/**
	 * Severity
	 */
	@Getter protected final ExceptionSeverity _severity;
	
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final EnrichedThrowableSubType<?> type) {
		this(subTypeType,
			 type,-1);	// no extended code
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final EnrichedThrowableSubType<?> type,final int extendedCode) {
		_subTypeType = subTypeType;
		_group = type.getGroup();
		_code = type.getCode();
		_extendedCode = extendedCode;
		_severity = type.getSeverity();
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final String msg,
							 		final EnrichedThrowableSubType<?> type) {
		this(subTypeType,
			 msg,
			 type,-1);	// no extended code
		
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final String msg,
							 		final EnrichedThrowableSubType<?> type,final int extendedCode) {
		super(msg);
		_subTypeType = subTypeType;
		_group = type.getGroup();
		_code = type.getCode();
		_extendedCode = extendedCode;
		_severity = type.getSeverity();		
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final String msg,
							 		final int group,final int code) {
		this(subTypeType,
			 msg,
			 group,code,-1);	// no extended code
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final String msg,
							 		final int group,final int code,final int extendedCode) {
		super(msg);
		_subTypeType = subTypeType;
		_group = group;
		_code = code;	
		_extendedCode = extendedCode;
		_severity = Throwables.getSubType(subTypeType,
										  group,code)
							  .getSeverity();
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 	    final Throwable th,
							 	    final EnrichedThrowableSubType<?> type) {
		this(subTypeType,
			 th,
			 type,-1);	// no extended code
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 	    final Throwable th,
							 	    final EnrichedThrowableSubType<?> type,final int extendedCode) {
		super(th);
		_subTypeType = subTypeType;
		_group = type.getGroup();
		_code = type.getCode();
		_extendedCode = extendedCode;
		_severity = type.getSeverity();
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final Throwable th,
							 		final int group,final int code) {
		this(subTypeType,
			 th,
			 group,code,-1);	// no extended code
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final Throwable th,
							 		final int group,final int code,final int extendedCode) {
		super(th);
		_subTypeType = subTypeType;
		_group = group;
		_code = code;	
		_extendedCode = extendedCode;
		_severity = Throwables.getSubType(subTypeType,
										  group,code)
							  .getSeverity();
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 	    final String msg,
							 	    final Throwable th,
							 	    final EnrichedThrowableSubType<?> type) {
		this(subTypeType,
			 msg,
			 th,
			 type,-1);	// no extended code
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 	    final String msg,
							 	    final Throwable th,
							 	    final EnrichedThrowableSubType<?> type,final int extendedCode) {
		super(msg,th);
		_subTypeType = subTypeType;
		_group = type.getGroup();
		_code = type.getCode();
		_extendedCode = extendedCode;
		_severity = type.getSeverity();
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final String msg,
							 		final Throwable th,
							 		final int group,final int code) {
		this(subTypeType,
			 msg,
			 th,
			 group,code,-1);	// no extended code
	}
	public EnrichedRuntimeException(final Class<? extends EnrichedThrowableSubType<?>> subTypeType,
							 		final String msg,
							 		final Throwable th,
							 		final int group,final int code,final int extendedCode) {
		super(msg,th);
		_subTypeType = subTypeType;
		_group = group;
		_code = code;	
		_extendedCode = extendedCode;
		_severity = Throwables.getSubType(subTypeType,
										  group,code)
							  .getSeverity();
	}
	public EnrichedRuntimeException(final String msg) {
		super(msg);
		_subTypeType = VoidExceptionType.class;
		_group = -1;
		_code = -1;
		_extendedCode = -1;
		_severity = null;
	}
	@SuppressWarnings("unchecked")
	public EnrichedRuntimeException(final Throwable th) {
		super(th);
		if (th instanceof EnrichedThrowable) {
			final EnrichedThrowable enrichedTh = (EnrichedThrowable)th;
			final Object subType =  enrichedTh.getSubType();
			_subTypeType = (Class<? extends EnrichedThrowableSubType<?>>) subType;
			_group = enrichedTh.getGroup();
			_code = enrichedTh.getCode();
			_extendedCode = enrichedTh.getExtendedCode();
			_severity = enrichedTh.getSeverity();
		} else {
			_subTypeType = VoidExceptionType.class;
			_group = -1;
			_code = -1;
			_extendedCode = -1;
			_severity = null;
		}
	}
	@SuppressWarnings("unchecked")
	public EnrichedRuntimeException(final String msg,
							 	    final Throwable th) {
		super(msg,th);
		if (th instanceof EnrichedThrowable) {
			final EnrichedThrowable enrichedTh = (EnrichedThrowable)th;
			final Object subType =  enrichedTh.getSubType();
			_subTypeType = (Class<? extends EnrichedThrowableSubType<?>>) subType;
			_group = enrichedTh.getGroup();
			_code = enrichedTh.getCode();
			_extendedCode = enrichedTh.getExtendedCode();
			_severity = enrichedTh.getSeverity();
		} else {
			_subTypeType = VoidExceptionType.class;
			_group = -1;
			_code = -1;
			_extendedCode = -1;
			_severity = null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override	
	public String getMessage() {
		return Throwables.composeMessage(this);
	}
	@Override
	public String getRawMessage() {
		return super.getMessage();
	}
	@Override
	public String getMessageAsXML() {
		return Throwables.composeXMLMessage(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-CLASSING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <S extends EnrichedThrowableSubType<?>> S getSubType() {
		final S outSubType = (S)Throwables.getSubType(this,
												_subTypeType);	
		return outSubType;
	}
	@Override
	public boolean isMoreSeriousThan(final EnrichedThrowable otherEx) {
		return Throwables.isMoreSerious(this,otherEx);
	}
	@Override
	public <S extends EnrichedThrowableSubType<?>> boolean is(final S subType) {		
		return Throwables.is(this,subType);
	}
	@Override
	public <S extends EnrichedThrowableSubType<?>> boolean isAny(final S... subClasses) {
		return Throwables.isAny(this,subClasses);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UTILITY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Throwable getRootCause() {
		return Throwables.getRootCause(this);
	}
	@Override
	public List<Throwable> getCausalChain() {
		return Throwables.getCausalChain(this);
	}
	@Override
	public List<Throwable> getCausalChainReversed() {
		final List<Throwable> causalChain = this.getCausalChain();
		return Lists.reverse(causalChain);
	}
	@Override
	public String getRootTypeName() {
		final StackTraceElement[] tes = this.getStackTrace();
		return tes[tes.length-1].getClassName();
	}
	@Override
	public String getRootMethod() {
		final StackTraceElement[] tes = this.getStackTrace();
		return tes[tes.length-1].getMethodName();
	}
	@Override
	public String getRootTraceElement() {
		final StackTraceElement[] tes = this.getStackTrace();
		return tes[tes.length-1].toString();
	}
	@Override
	public String getStackTraceAsString() {
		final String outStackTraceAsStr = Throwables.getStackTraceAsString(this);
		return outStackTraceAsStr;
	}
}
