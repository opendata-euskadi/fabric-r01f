package r01f.aspects.freezable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.aspectj.lang.reflect.FieldSignature;

import r01f.aspects.core.freezable.FreezableCollection;
import r01f.aspects.core.freezable.FreezableMap;
import r01f.aspects.core.freezable.Freezer;
import r01f.aspects.interfaces.freezable.Freezable;
import r01f.reflection.ReflectionUtils;



/**
 * Implementación del interfaz {@link Freezable} en base a ASPECTJ
 * 
 * Para hacer que un tipo implemente el interfaz {@link Freezable} hay que:
 *		- PASO 1: Establecer que todas las clases anotadas con @ConvertToFreezable implementen
 *				  el interfaz FreezableInterface
 *						declare parents: @ConvertToFreezable * implements Freezable;
 *		- PASO 2: Crear el interfaz Freezable (ver aspecto {@link FreezableInterfaceAspect})
 *		- PASO 3: Implementar los pointcuts específicos para el interfaz {@link Freezable}
 * 
 * NOTA: 	Para mejorar la reutilización, el aspecto se divide en DOS
 * 				- La implementación del interfaz Freezable (este aspecto)
 * 				- Otro aspecto que "inyecta" el interfaz {@link Freezable} a aquellas clases 
 * 				  que se considere necesario, por ejemplo aquellas anotadas con @ConvertToFreezable
 * 				  (Ver {@link FreezableInterfaceAspect})
 * 			
 * 			De esta forma, es posible asociar el comportamiento {@link Freezable} a cualquier objeto;
 * 			simplemente basta con crear otro aspecto que haga que los tipos implementen el interfaz {@link Freezable}
 * 				declare parents: {pointcut} implements Freezable;
 * 			(es lo que se ha hecho en {@link ConvertToFreezableAnnotationAspect})
 * 
 * IMPORTANTE!!	En este aspecto se implementa el PASO 2 y el PASO 3 y en el aspecto {@link ConvertToFreezableAnnotationAspect}
 * 				se implementa el PASO 1
 */
privileged public aspect FreezableAspect { // perthis(freezableAnnotatedObj() || freezableImplementingInterfaceObj()) {
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFAZ Freezable (definida en un fichero .java propio)
/////////////////////////////////////////////////////////////////////////////////////////
	private boolean Freezable._frozen = false;
	
	public boolean Freezable.isFrozen() {
		return _frozen;
	}
	public void Freezable.setFrozen(boolean value) {
		_frozen = value;
	}
	public void Freezable.freeze() {
		Freezer.freeze(this);
	}
	public void Freezable.unFreeze() {
		Freezer.unFreeze(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * pointcut for a joinpoint in which the object is a Freezable instance
	 */
	pointcut freezableInterfaceImplementingObj() : within(Freezable+);
	
/////////////////////////////////////////////////////////////////////////////////////////
//	Field modifications on Freezable objects
/////////////////////////////////////////////////////////////////////////////////////////
	pointcut fieldSetInFreezableClass(Freezable fz) : 
					(set(!static !final * Freezable+.*) 			// any field...
						&& !set(boolean Freezable._frozen)) &&		// ... not the  _frozen field
					//within(Freezable+) && 	// any field at any Freezable type
					target(fz);		// object receiving the advice (the object that contains the field)
	
	/**
	 * Advice before: se ejecuta ANTES de establecer el valor de un miembro en un objeto Freezable
	 */
	before(Freezable fz) : fieldSetInFreezableClass(fz) {
		if (fz.isFrozen()) throw new IllegalStateException("The object " + fz.getClass().getName() + " state is FROZEN so it cannot be changed!!");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Collection-type (arrays, maps...) fields modifications of freezable objects 
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Return a wrapped map that gives access to the instance containing the collection field
     */
    Map around() : get(Map+ Freezable+.*) {	// any Map field of a frezable object
    	// joinPoint info
    	Object container = thisJoinPoint.getTarget();
		FieldSignature fs = (FieldSignature)thisJoinPoint.getSignature();
		Field f = fs.getField();					// Field being accessed
		Class<?> c = f.getDeclaringClass();			// object containing the field
    	
		// Get the map
    	Map theMap = proceed();	
    	
    	// If these conditions are meet:
    	//		a.- The container object is a Freezable instance and it's frozen 
    	//		b.- The map is NOT a FreezableMap instance
    	// dar el cambiazo por uno freezable
    	boolean containerIsFrozen = (container instanceof Freezable) && ((Freezable)container).isFrozen();
    	if (containerIsFrozen && theMap != null && !(theMap instanceof FreezableMap)) {
    		// Ensure that the field is a Map (the interface, NOT a concrete type)
    		if (!f.getType().isAssignableFrom(Map.class)) throw new IllegalArgumentException("The Map field " + c.getName() + "." + f.getName() + " (" + f.getType() + ") type is NOT a java.util.Map (the interface), so it cannot be converted to a FreezableMap.");
    		
    		// Change the Map instance for a FreezableMap
    		theMap = new FreezableMap(theMap,true);
    		ReflectionUtils.setFieldValue(thisJoinPoint.getTarget(),f,theMap,false);
    	} else {
    		// the flow goes over here when the Map was already changed for a FreezableMap
    		// ... or the container object is NOT frozen
    	}
    	return theMap;
    }
    /**
     * Return a wrapped Map that gives access to the Map container
     */
    Collection around() : get(Collection+ Freezable+.*) {	// any collection type of a Freezable object
    	// joinpoint info
    	Object container = thisJoinPoint.getTarget();
		FieldSignature fs = (FieldSignature)thisJoinPoint.getSignature();
		Field f = fs.getField();					// Field being accessed
		Class<?> c = f.getDeclaringClass();			// object containing the field
    	
		// Get the collection field
    	Collection theCol = proceed();		
    	
    	// If these conditions are meet:
    	//		a.- The container object is a Freezable instance and it's frozen 
    	//		b.- The map is NOT a FreezableCollection instance
    	// dar el cambiazo por uno freezable
    	boolean containerIsFrozen = (container instanceof Freezable) && ((Freezable)container).isFrozen();
    	if (containerIsFrozen && theCol != null && !(theCol instanceof FreezableCollection)) {
    		// Ensure that the field is a Collection (the interface, NOT a concrete type)
    		if (!f.getType().isAssignableFrom(Collection.class)) throw new IllegalArgumentException("The Collection field " + c.getName() + "." + f.getName() + " (" + f.getType() + ") type is NOT a java.util.Collection (the interface), so it cannot be converted to a FreezableCollection.");
    		
    		// Change the Map instance for a FreezableMap
    		theCol = new FreezableCollection(theCol,true);
    		ReflectionUtils.setFieldValue(thisJoinPoint.getTarget(),f,theCol,false);
    	} else {
    		// the flow goes over here when the Collection was already changed for a FreezableMap
    		// ... or the container object is NOT frozen
    	}
    	return theCol;
    }
	/**
	 * IMPORTANTE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * Hay que tener en cuenta que hay dos formas de invocar un método mutator de un mapa o colección 
	 * que es un miembro (field) de un objeto Container:
	 * 		public class Container {
	 * 			public Map _mapField;
	 * 		}
	 * CASO 1: Desde FUERA del objeto que contiene el miembro colección (map o collection):
	 * 			public class OtherObj {
	 * 				public void method() { 
	 * 					container.getMapField().put(..) <-- el pointcut está en el objeto/método que hace la llamada a put(..) en el Map,
	 * 				}										es decir en el objeto OtherObj y NO en el objeto container que contiene el Map 
	 * 			}											
	 * CASO 2: Desde FUERA del objeto que contiene el miembro colección (map o collection):
	 * 			public class OtherObj {
	 * 				public void method() {
	 * 					container.putIntoMapField(..) <-- el método putIntoMapField del objeto container es el que 
	 * 				}								 	  hace la llamada al método put(..) del Map así que ahí está el pointcut
	 * 			} 
	 * Esto implica que en un solo pointcut NO se puede tener acceso al objeto que contiene el objeto colección (map / collection), 
	 * unicamente se puede obtener el objeto colección y los argumentos del método mutator
	 * 
	 * IMPORTANTE!!	Para evitar que este pointcut se aplique a TODOS los métodos mutator de todas las colecciones, se ha de implementar el 
	 * 				pointcut collectionsMutatorMethodsCallingTypes, por ejemplo para capturar las llamadas a put() en el CASO 1 en 
	 * 				cualquier clase de r01f:
	 * 					pointcut collectionsMutatorMethodsCallingTypes() : within(r01f..*);
	 * 
	 * Esta implementación es compleja, así que se ha optado por capturar los accesos a los miembros tipo coleccion y devolver un wrapper
	 * del tipo concreto (map/collection) que "lleva" el control de si está congelado o no 
     */
	
	// Este POINTCUT NO SIRVE para todos los casos:
	//		Funciona para el caso 2: freezableObj.putIntoMap(...)
	//		NO funciona para el caso 1: freezableObj.getMapField().put(...)
//	pointcut mutatorMethodCallOnFieldImplementingMap(Freezable theObj,Map theMap) : 
//					(call(* Map+.remove(*)) || call(* Map+.clear()) ||
//					call(* Map+.put(*,*))  || call(* Map+.putAll(*))) &&	// metodo mutator de un mapa	 
//					freezableInterfaceImplementingObj() && 					// en un objeto Freezable
//					!within(FreezableInterfaceAspect) &&					// pero NO dentro de un aspecto FreezableInterfaceAspect
//					this(theObj) && target(theMap);		// this = objeto que hace la llamada al advice / target = objeto que recibe el advice
//	/**
//	 * Advice before: se ejecuta ANTES de establecer el valor de un miembro tipo Map en un objeto Freezable
//	 */
//	before(Freezable theObj,Map theMap): mutatorMethodCallOnFieldImplementingMap(theObj,theMap) {
//		if (theObj.isFrozen()) throw new IllegalStateException("The object " + theObj.getClass().getName() + " state is FROZEN so you cannot put an entry into a Map member!!");
//	}
	
// >> COLECCIONES TIPO Collection ---------------------------------
	// Este POINTCUT NO SIRVE para todos los casos:
	//		Funciona para el caso 2: freezableObj.addIntoCollection(...)
	//		NO funciona para el caso 1: freezableObj.getColField().put(...)
//	pointcut mutatorMethodCallOnFieldImplementingCollection(Freezable theObj,Collection theCol) : 
//					(call(* Collection+.remove(*)) || call(* Collection+.removeAll(*)) || call(* Collection+.retainAll(*)) || call(* Collection+.clear()) ||  
//					call(* Collection+.add(*)) || call(* Collection+.addAll(*))) &&		// método mutator de un mapa
//					freezableInterfaceImplementingObj() &&								// en un objeto Freezable
//					!within(FreezableInterfaceAspect) &&								// pero NO dentro de un aspecto FreezableInterfaceAspect
//					this(theObj) && target(theCol);		// this = objeto que hace la llamada al advice / target = objeto que recibe el advice
//	/**
//	 * Advice before: se ejecuta ANTES de establecer el valor de un miembro tipo Collection en un objeto Freezable
//	 */
//	before(Freezable theObj,java.util.Collection theCol): mutatorMethodCallOnFieldImplementingCollection(theObj,theCol) {
//		if (theObj.isFrozen()) throw new IllegalStateException("The object " + theObj.getClass().getName() + " state is FROZEN so you cannot put a value into a Collection member!!");
//	}
// >> COLECCIONES TIPO ARRAY ---------------------------------------
	/*
	 * ---- NO es posible establecer un joinpoint en el establecimiento de un 
	 *		elemento de un array (hay un bug reportado sobre esto)
//	 * pointcut llamado setOnArrayField que designa un joinpoint al cambiar miembro de tipo array de objetos 
//	 * Freezable
//	 */
//	pointcut setOnArrayField(Freezable theObj,Freezable[] theArray) : 
//					set(Freezable+[] *.*) &&
//					this(theObj) && 
//					args(theArray);
//  /**
//	 * Advice before: se ejecuta ANTES de establecer el valor de un miembro tipo array en un objeto Freezable
//	 */
//	before(Freezable theObj,Freezable[] theArray) : setOnArrayField(theObj,theArray) {
//		if (theObj.isFrozen()) throw new IllegalStateException("The object " + theObj.getClass().getName() + " state is FROZEN so you cannot change an element of an Array member!!");
//    }
}
