package r01f.aspects.logging;

import java.lang.reflect.Method;

import org.aspectj.lang.reflect.MethodSignature;

import r01f.aspects.core.logging.LoggedMethodCallsLogger;
import r01f.aspects.core.logging.LoggedMethodCallsLogger.LoggedMethodCallsLogs;
import r01f.aspects.interfaces.logging.DoNotLog;
import r01f.aspects.interfaces.logging.LoggedMethodCalls;


/**
 * Aspect that is responsible for injecting traces just BEFORE calling a method and just AFTER finishing the execution of it.
 * TODO el aspecto es poco eficiente ya que vuelve a crear el logger para cada llamada a un metodo deberia crearse una variable estatica con los LoggedMethodCallsLogs
 */
privileged public aspect LoggedMethodCallsAspect {
/////////////////////////////////////////////////////////////////////////////////////////
//  POINTCUT: Cualquier metodo de un tipo anotado con @R01MMethodCallsLogged
/////////////////////////////////////////////////////////////////////////////////////////
	pointcut publicMethod() :
			 execution(!@DoNotLog public * @LoggedMethodCalls *.*(..));	// metodos publicos NO anotados con @DoNotLog en objetos anotados con @LoggedMethodCalls
//		  && @within(LoggedMethodCalls) 		// tipos anotados con @LoggedMethodCalls
/////////////////////////////////////////////////////////////////////////////////////////
//  ADVICE
/////////////////////////////////////////////////////////////////////////////////////////
	Object around() : publicMethod() {
		// Obtener el metodo y a partir de este la clase anotada con @LoggedMethodCalls
		MethodSignature methodSignature = (MethodSignature)thisJoinPointStaticPart.getSignature();
		Method method = methodSignature.getMethod();
		LoggedMethodCallsLogs logs = LoggedMethodCallsLogger.getLogger(method,
																 		thisJoinPoint.getArgs());

    	Object outVal = null;

    	if (logs != null) logs.beginMethodCall();		// log principio de la llamada

    	// --------- llamada al metodo ---------
    	outVal = proceed();
    	// --------- fin llamada al metodo ---------

    	if (logs != null) logs.endMethodCall();			// log final de la llamada

    	return outVal;
    }
}
