package r01f.exceptions;

import java.util.List;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;


/**
 * Enriched exception with some gotchas like this:
 * <pre>
 * 		- group / code or subTypes
 * 				Sometimes different exceptions are created to express different error conditions (ie: RecordInsertException, RecordDeleteException, RecordUpdateException). 
 * 				this makes the client code capture all of them:</pre>
 * 				<pre class='brush:java'>	
 * 				try {
 * 					// database code
 * 				} catch (RecordInsertException riEx) {
 * 						...
 * 				} catch (RecordDeleteException rdEx) {
 * 						...
 * 				} catch (RecordUpdateException ruEx) {
 * 						...
 * 				}
 * 				</pre><pre>
 * 				It would be better to throw a single "generic" exception (ie. DBExcepcion) and set some property with the exception sub-type
 * 				This way the client-capturing code is more compact and still retain info about the error condition:</pre>
 * 				<pre class='brush:java'>
 * 				try {
 * 					// database code
 * 				} catch (DBException dbEx) {
 * 					... the sub-type can be known
 * 					dbEx.getSubType()
 * 					... and also actions could be taken depending on the sub-type
 * 					switch(dbEx.getSubType()) {
 * 					case A:
 * 						...
 * 					case B:
 * 						...
 * 					default:
 * 						...
 * 					}
 * 				}
 * 				</pre><pre>
 *		- group/code: 	numeric values for error group / code
 *		- severity:		a pre-defined value for the severity at {@link ExceptionSeverity}
 *		- Traceability messages
 *		- if the exception should be logged or not
 *		- if the exception should be thrown "as is" (with all stack trace) or as a NEW exception with a new stack trace
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
 * <li>2.- Create a type extending {@link EnrichedException}
 * <pre class='brush:java'>
 *		public class TestException 
 *           extends EnrichedException {
 *			public TestException(final TestExceptionSubTypes type,
 *								 final String msg) {
 *				super(type,
 *					  msg);
 *			}	
 *			public TestException(final TestExceptionSubTypes type,
 *								 final Throwable otherEx) {
 *				super(type,
 *					  otherEx);
 *			}
 *			public TestException(final TestExceptionSubTypes type,
 *								 final String msg,final Throwable otherEx) {
 *				super(type,
 *					  msg,otherEx);
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
public abstract class EnrichedException 
              extends Exception
		   implements EnrichedThrowable {
	
	private static final long serialVersionUID = -2026592397288534675L;
///////////////////////////////////////////////////////////////////////////////
//	FIELDS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * The subType java type 
	 * (necessary to create the EnrichedThrowableType from group and code)
	 */
	private final EnrichedThrowableType _type;
	/**
	 * Extended code (used for application-specific codes)
	 */
	@Getter protected final int _extendedCode;
	
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	public EnrichedException(final EnrichedThrowableType type) {
		this(type,-1);	// no extended code
	}
	public EnrichedException(final EnrichedThrowableType type,final int extendedCode) {
		_type = type;
		_extendedCode = extendedCode;
	}
	public EnrichedException(final EnrichedThrowableType type,
							 final String msg) {
		this(type,-1,
			 msg);
	}
	public EnrichedException(final EnrichedThrowableType type,final int extendedCode,
							 final String msg) {
		super(msg);
		_type = type;
		_extendedCode = extendedCode;
	}
	public EnrichedException(final EnrichedThrowableType type,
							 final Throwable th) {
		this(type,-1,	// no extended code
			 th);	
	}
	public EnrichedException(final EnrichedThrowableType type,final int extendedCode,
							 final Throwable th) {
		super(th);
		_type = type;
		_extendedCode = extendedCode;
	}
	public EnrichedException(final EnrichedThrowableType type,
							 final String msg,
							 final Throwable th) {
		this(type,-1,	// no extended code
			 msg,
			 th);	
	}
	public EnrichedException(final EnrichedThrowableType type,final int extendedCode,
							 final String msg,
							 final Throwable th) {
		super(msg,th);
		_type = type;
		_extendedCode = extendedCode;
	}
	public EnrichedException(final String msg) {
		super(msg);
		_type = new VoidExceptionType();
		_extendedCode = -1;
	}
	public EnrichedException(final Throwable th) {
		super(th);
		if (th instanceof EnrichedThrowable) {
			final EnrichedThrowable enrichedTh = (EnrichedThrowable)th;
			_type = enrichedTh.getType();
			_extendedCode = enrichedTh.getExtendedCode();
		} else {
			_type = new VoidExceptionType();
			_extendedCode = -1;
		}
	}
	public EnrichedException(final String msg,
							 final Throwable th) {
		super(msg,th);
		if (th instanceof EnrichedThrowable) {
			final EnrichedThrowable enrichedTh = (EnrichedThrowable)th;
			_type = enrichedTh.getType();
			_extendedCode = enrichedTh.getExtendedCode();
		} else {
			_type = new VoidExceptionType();
			_extendedCode = -1;
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
	public <T extends EnrichedThrowableType> T getType() {
		final T outSubType = (T)_type;
		return outSubType;
	}
	@Override
	public int getGroup() {
		return _type != null ? _type.getGroup() : -1;
	}
	@Override
	public int getCode() {
		return _type != null ? _type.getCode() : -1;
	}
	@Override
	public ExceptionSeverity getSeverity() {
		return _type != null ? _type.getSeverity() : null;
	}
	@Override
	public boolean isMoreSeriousThan(final EnrichedThrowable otherEx) {
		return Throwables.isMoreSerious(this,otherEx);
	}
	@Override
	public <T extends EnrichedThrowableType> boolean is(final T subType) {		
		return Throwables.is(this,subType);
	}
	@Override
	public <T extends EnrichedThrowableType> boolean isAny(final T... subClasses) {
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
