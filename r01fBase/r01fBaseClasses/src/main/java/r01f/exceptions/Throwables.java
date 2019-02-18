package r01f.exceptions;

import java.util.List;
import java.util.regex.Matcher;

import javax.management.ReflectionException;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.GwtIncompatible;

/**
 * Some exception-related utilities
 */
@GwtIncompatible
public class Throwables {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static String composeMessage(final EnrichedThrowable th) {
		StringBuilder msg = new StringBuilder();
		msg.append("[");
		if (th.getGroup() >0) msg.append(" group=").append(th.getGroup());
		if (th.getCode() > 0) msg.append(" code=").append(th.getCode());
		if (th.getCode() > 0) msg.append(" code=").append(th.getCode());
		if (th.getSeverity() != null) msg.append(" severity=").append(th.getSeverity());
		msg.append(" ]");
		if (th.getRawMessage() != null) msg.append(": ").append(_message(th));
		return msg.toString();
	}
	static String composeXMLMessage(final EnrichedThrowable th) {
		StringBuilder msg = new StringBuilder();
		msg.append("<exceptionData");
		if (th.getGroup() >0) msg.append(" group='").append(th.getGroup()).append("'");
		if (th.getCode() > 0) msg.append(" code='").append(th.getCode()).append("'");
		if (th.getCode() > 0) msg.append(" code='").append(th.getCode()).append("'");
		if (th.getSeverity() != null) msg.append(" severity='").append(th.getSeverity()).append("'");
		msg.append(">");
		if (th.getRawMessage() != null) msg.append(": ").append(_message(th));
		msg.append("</exceptionData>");
		return msg.toString();
	}
	private static String _message(final EnrichedThrowable th) {
		String msg = th.getRawMessage() != null ? th.getRawMessage().replaceAll("[\n\r]"," ")
											    : th.getSubType() != null ? th.getSubType().toString() : "";
		return msg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static <S extends EnrichedThrowableSubType<?>> S getSubType(final EnrichedThrowable th,
																final Class<S> subTypeType) {
		S outSubType = null;
		if (th.getGroup() > 0 && th.getCode() > 0) {
			// Call the static factory
			outSubType = Throwables.<S>_invokeStaticMethod(subTypeType,
														   "from",
														   new Class<?>[] {Integer.class,Integer.class},
														   new Object[] {th.getGroup(),th.getCode()});
		}
		return outSubType;
	}
	static <S extends EnrichedThrowableSubType<?>> S getSubType(final Class<S> subTypeType,
																final int group,final int code) {
		return Throwables.<S>_invokeStaticMethod(subTypeType,
								   				 "from",new Class<?>[] {Integer.class,Integer.class},new Object[] {group,code});	
	}
	static boolean isMoreSerious(final EnrichedThrowable th,final EnrichedThrowable otherTh) {
		ExceptionSeverity thSeverity = th.getSeverity();
		ExceptionSeverity otherThSeverity = otherTh.getSeverity();
		
		if (thSeverity == null && otherThSeverity == null) {
			return false;
		} else if (thSeverity != null) { 
			return thSeverity.isMoreSeriousThan(otherThSeverity);
		}
		return false;
	}
	static <S extends EnrichedThrowableSubType<?>> boolean is(final EnrichedThrowable th,
															  final S subType) {
		return subType != null ? subType.is(th.getGroup(),
						  					th.getCode())
						  	   : false;
	}
	static <S extends EnrichedThrowableSubType<?>> boolean isAny(final EnrichedThrowable th,final S... subClasses) {
		if (subClasses == null || subClasses.length == 0) {
			return false;
		}
		boolean found = false;
		for (S sub : subClasses) {
			if (sub.is(th.getGroup(),
					   th.getCode())) {
				found = true;
				break;
			}
		}
		return found;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  UTILLITIES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Customizes an exception message replacing {}-like placeholder with vars
	 * @param msg the message
	 * @param vars the vars
	 * @return the customized message
	 */
	public static String message(final String msg,final Object... vars) {
		return Throwables.customize(msg,vars).toString();
	}
	public static String customize(final CharSequence strToCustomize,final Object... vars) {
		if (strToCustomize == null) return null;
		if (vars == null || vars.length == 0) return strToCustomize.toString();
		// see MessageFormatter from SL4FJ
		// custom impl
		String workStr = strToCustomize.toString();
		for (Object var : vars) {
			workStr = workStr.replaceFirst("\\{\\}",(var != null ? _matcherQuoteReplacement(var.toString())	// should be _objectToString(var) but it's problematic in GWT 
																 : "null"));	
		}
		return workStr;
	}
    /**
     * Copy of {@link Matcher#quoteReplacement(String)} to make it possible to use
     * this with GWT
     * @param s
     * @return
     * @see Matcher#quoteReplacement(String)
     */
	private static String _matcherQuoteReplacement(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1)) return s;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '$') sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }
	/**
	 * Logs an exception
	 * ie:
	 * <pre class='brush:java'>
	 * 		public class MyType {
	 * 			public void myMethod(String param) {
	 * 				try {
	 * 					doSomething(param);
	 *				} catch (Exception ex) {
	 *					Throwables.log(MyType.class,
	 *								   ex,
	 *								   "Error {} calling doSomething() with parameter {}",ex.getClass().getName(),param); 
	 *				}
	 * </pre>
	 * @param throwingType the type where the exception is catched and logged
	 * @param th the throwed exception
	 * @param msg the message to log (can contain {}-like placeholder -see SL4FJ-)
	 * @param vars the params of the message to log
	 */
	public static void log(final Class<?> throwingType,
						   final Throwable th,
						   final String msg,final Object... vars) {
		Logger logger = LoggerFactory.getLogger(throwingType);
		logger.error(msg,vars,th);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WRAP OF com.google.common.base.Throwables
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @see com.google.common.base.Throwables.getCausalChain
	 */
	public static List<Throwable> getCausalChain(final Throwable throwable) {
		 return com.google.common.base.Throwables.getCausalChain(throwable);
	}
	/**
	 * @see com.google.common.base.Throwables.getRootCause
	 */
	public static Throwable getRootCause(final Throwable throwable) {
		return com.google.common.base.Throwables.getRootCause(throwable);
	}
	/**
	 * @see com.google.common.base.Throwables.getStackTraceAsString
	 */
	public static String getStackTraceAsString(final Throwable throwable) {
		return  com.google.common.base.Throwables.getStackTraceAsString(throwable);
	}
	/**
	 * @see com.google.common.base.Throwables.propagate
	 */
	public static RuntimeException propagate(final Throwable throwable) {
		return new RuntimeException(throwable);
	}
	/**
	 * @see com.google.common.base.Throwables.propagateIfInstanceOf
	 */
	public static <X extends Throwable> void propagateIfInstanceOf(final Throwable throwable,
																   final Class<X> declaredType) throws X {
		com.google.common.base.Throwables.throwIfInstanceOf(throwable,
															declaredType);
	}
	/**
	 * @see com.google.common.base.Throwables.propagateIfPossible
	 */
	@GwtIncompatible("Guava's Throwables NOT usable in GWT")
	public static void propagateIfPossible(Throwable throwable) {
		com.google.common.base.Throwables.throwIfUnchecked(throwable);
	}
	/**
	 * @see com.google.common.base.Throwables.propagateIfPossible
	 */
	public static <X extends Throwable> void propagateIfPossible(final Throwable throwable,
																 final Class<X> declaredType) throws X {
		com.google.common.base.Throwables.propagateIfPossible(throwable,
															  declaredType);
	}
	/**
	 * @see com.google.common.base.Throwables.propagateIfPossible
	 */
	public static <X1 extends Throwable,X2 extends Throwable> void propagateIfPossible(final Throwable throwable, 
																					   final Class<X1> declaredType1,final Class<X2> declaredType2) throws X1,X2 {
		com.google.common.base.Throwables.propagateIfPossible(throwable,
															  declaredType1,declaredType2);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Transforms a checked exception into an unchecked exception
	 * @param ex the exception
	 * @return the unchecked-transformed exception
	 */
	public static RuntimeException throwUnchecked(final Exception ex) {
		Throwables.<RuntimeException>_throwUnchecked(ex);
		throw new AssertionError("This code is never executed");
	}
	@SuppressWarnings("unchecked")
	private static <T extends Exception> void _throwUnchecked(final Exception ex) throws T {
		throw (T)ex;
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
    /**
     * Invokes an static method in a type
     * @param type 
     * @param methodName 
     * @param argsTypes 
     * @param argsValues 
     * @return the method-returned object
     * @throws ReflectionException 
     */
    @SuppressWarnings("unchecked")
	public static <T> T _invokeStaticMethod(final Class<?> type,
    									    final String methodName,final Class<?>[] argsTypes,final Object[] argsValues) {
    	try {
	    	return (T)MethodUtils.invokeStaticMethod(type,
	        		   								 methodName,
	        		   								 argsValues,argsTypes);
    	} catch(Throwable th) {
    		th.printStackTrace(System.out);
    	}
    	return null;
    } 
}
