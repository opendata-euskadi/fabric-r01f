package r01f.aspects.core.logging;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.logging.LogLevel;
import r01f.aspects.interfaces.logging.LoggedMethodCalls;
import r01f.aspects.interfaces.logging.LoggedMethodCallsParamsFormatter;
import r01f.aspects.interfaces.logging.LoggedMethodCallsWhen;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.Strings;

public class LoggedMethodCallsLogger {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static class LoggedMethodCallsLogs {
		private final Logger _logger;
		private final LogLevel _level;
		private final String _beginMethodCallLog;
		private final String _endMethodCallLog;
		
		public void beginMethodCall() {
			_doLog(_logger,_level,_beginMethodCallLog);
		}
		public void endMethodCall() {
			_doLog(_logger,_level,_endMethodCallLog);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static LoggedMethodCallsLogs getLogger(final Method method,final Object... params) {
		LoggedMethodCallsLogs outLogs = null;
		
		Class<?> declaringClass = method.getDeclaringClass();
		LoggedMethodCalls loggedAnnot = ReflectionUtils.typeAnnotation(declaringClass,
																	   LoggedMethodCalls.class);
		// Get the annotation info
		Class<?> logType = declaringClass;
		LogLevel logLevel = loggedAnnot.level();
		Logger logger = LoggerFactory.getLogger(logType);
		
		// Compose the log message
		if (_isLogEnabled(logger,logLevel)) {
			String module = loggedAnnot.module();
			String start = loggedAnnot.start();
			String end = loggedAnnot.end();
			int indent = loggedAnnot.indent();
		
			
			// Format the parameters
			LoggedMethodCallsParamsFormatter paramsFormatter = ReflectionUtils.createInstanceOf(loggedAnnot.paramsFormatter());
			
			String paramsFormatted = paramsFormatter != null ? paramsFormatter.formatParams(params) 
															 : "";
			if (Strings.isNullOrEmpty(paramsFormatted)) paramsFormatted = "";
			
			// compose the message
			String indentStr = indent > 0 ? _tabs(indent) : ""; 
			
			String msgStart = loggedAnnot.when() == LoggedMethodCallsWhen.AROUND 
					       || loggedAnnot.when() == LoggedMethodCallsWhen.BEGIN ? Strings.customized("{}{}{}: {} {}",
									 													 			 indentStr,start,module,method.getName(),paramsFormatted)
									 											: null;
			String msgEnd = loggedAnnot.when() == LoggedMethodCallsWhen.AROUND 
					     || loggedAnnot.when() == LoggedMethodCallsWhen.END ? Strings.customized("{}{}{}: {} {}",
									 												 			 indentStr,end,module,method.getName(),paramsFormatted)
									 											: null;
			outLogs = new LoggedMethodCallsLogs(logger,logLevel,
							   				 	msgStart,msgEnd);
		}
		return outLogs;
	}
	/**
	 * Do the logging 
	 * @param logger 
	 * @param logs 
	 */
	private static void _doLog(final Logger logger,final LogLevel level,
							   final String msg) {
		if (Strings.isNullOrEmpty(msg) || level == LogLevel.OFF) return;
		if (level == LogLevel.TRACE && logger.isTraceEnabled()) {
			logger.trace(msg);
		} else if (level == LogLevel.DEBUG && logger.isDebugEnabled()) {
			logger.debug(msg);
		} else if (level == LogLevel.INFO && logger.isInfoEnabled()) {
			logger.info(msg);
		} else if (level == LogLevel.WARN && logger.isWarnEnabled()) {
			logger.warn(msg);
		} else if (level == LogLevel.ERROR && logger.isErrorEnabled()) {
			logger.error(msg);
		}
	}
	/**
	 * Returns true if the logger is enabled
	 * @param logger 
	 * @param level 
	 */
	private static boolean _isLogEnabled(final Logger logger,
								 		 final LogLevel level) {
		if (level == LogLevel.OFF) return false;
		if (level == LogLevel.TRACE && logger.isTraceEnabled()) return true;
		if (level == LogLevel.DEBUG && logger.isDebugEnabled()) return true;
		if (level == LogLevel.INFO && logger.isInfoEnabled()) return true;
		if (level == LogLevel.WARN && logger.isWarnEnabled()) return true;
		if (level == LogLevel.ERROR && logger.isErrorEnabled()) return true;
		return false;
	}
	/**
	 * Creates a String with the given number of tabs
	 * @param indent 
	 * @return 
	 */
	private static String _tabs(final int indent) {
		char[] outTabs = new char[indent];
		for (int i=0; i<indent; i++) outTabs[i] = '\t';
		return new String(outTabs);
	}
}
