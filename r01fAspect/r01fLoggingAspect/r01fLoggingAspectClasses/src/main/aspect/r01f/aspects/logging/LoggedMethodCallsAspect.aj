package r01f.aspects.logging;

import java.lang.reflect.Method;

import org.aspectj.lang.reflect.MethodSignature;

import r01f.aspects.core.logging.LoggedMethodCallsLogger;
import r01f.aspects.core.logging.LoggedMethodCallsLogger.LoggedMethodCallsLogs;
import r01f.aspects.interfaces.logging.DoNotLog;
import r01f.aspects.interfaces.logging.LoggedMethodCalls;


/**
 * Aspecto que se encarga de inyectar trazas justo ANTES de llamar a un método y justo DESPUES de 
 * terminar la ejecución del mismo
 * TODO el aspecto es poco eficiente ya que vuelve a crear el logger para cada llamada a un método debería crearse una variable estática con los LoggedMethodCallsLogs 
 */
privileged public aspect LoggedMethodCallsAspect {
/////////////////////////////////////////////////////////////////////////////////////////
//  POINTCUT: Cualquier método de un tipo anotado con @R01MMethodCallsLogged
/////////////////////////////////////////////////////////////////////////////////////////	
	pointcut publicMethod() :
			 execution(!@DoNotLog public * @LoggedMethodCalls *.*(..));	// métodos públicos NO anotados con @DoNotLog en objetos anotados con @LoggedMethodCalls 
//		  && @within(LoggedMethodCalls) 		// tipos anotados con @LoggedMethodCalls
/////////////////////////////////////////////////////////////////////////////////////////
//  ADVICE
/////////////////////////////////////////////////////////////////////////////////////////
	Object around() : publicMethod() {
		// Obtener el método y a partir de este la clase anotada con @R01MMethodCallsLogged
		MethodSignature methodSignature = (MethodSignature)thisJoinPointStaticPart.getSignature();
		Method method = methodSignature.getMethod();
		LoggedMethodCallsLogs logs = LoggedMethodCallsLogger.getLogger(method,
																 		thisJoinPoint.getArgs());
		
    	Object outVal = null;
    	
    	if (logs != null) logs.beginMethodCall();		// log principio de la llamada

    	// --------- llamada al método ---------
    	outVal = proceed();
    	// --------- llamada al método ---------
    	
    	if (logs != null) logs.endMethodCall();			// log final de la llamada
    	
    	return outVal;
    }
}
