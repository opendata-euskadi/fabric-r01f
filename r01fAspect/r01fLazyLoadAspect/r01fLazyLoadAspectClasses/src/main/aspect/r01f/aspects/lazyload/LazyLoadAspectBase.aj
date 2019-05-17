package r01f.aspects.lazyload; 

import java.lang.reflect.Field;
import java.util.Map;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.FieldSignature;

import r01f.aspects.core.lazyload.LazyLoadUtils;
import r01f.aspects.core.util.ObjectsHierarchyModifier;
import r01f.aspects.interfaces.lazyload.LazyCompleteLoad;
import r01f.aspects.interfaces.lazyload.LazyLoadCapable;
import r01f.collections.lazy.LazyMap;
import r01f.types.lazy.LazyLoaded;


/**
 * Aspect that transforms an object's field into a {@link LazyLoaded} field
 * This aspect can be enabled annotating the lazy-loaded field with @LazyLoadCapable
 */
privileged public abstract aspect LazyLoadAspectBase<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  POINTCUT
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Abstract pointcut to select types to apply this aspect
	 */
	public abstract pointcut typesContainingLazilyLoadedFields();
	
	/**
	 * Pointcut used to avoid activating the lazy load advide at {@link ObjectsHierarchyModifier} type
	 */
	pointcut objectsHierarchyModifierMethodExecution() : execution(* ObjectsHierarchyModifier.*(..));
	pointcut notInObjectsHierarchyModifierFlow() : !cflow(objectsHierarchyModifierMethodExecution()) && !within(LazyLoadAspectBase+);
///////////////////////////////////////////////////////////////////////////////////////////////////
// 	LAZY LOAD EN TIPOS COMPLEJOS
///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressAjWarnings
    Object around() : 
    			typesContainingLazilyLoadedFields() 	// types not applyng LazyLoad
    		 && notInObjectsHierarchyModifierFlow()		// not in an ObjectsHierarchyModifier method
    		 && get(@LazyLoadCapable !Map+ T+.*) {		// any not Map member annotated with LazyLoadCapable
    	// Get the joinpoint info
    	Object container = thisJoinPoint.getTarget();
		FieldSignature fs = (FieldSignature)thisJoinPoint.getSignature();
		Field f = fs.getField();						// Field que se est� accediendo
		
		// Get the member's object
    	Object lazyLoaded = proceed();
    	
    	// If the object was not loaded, load it
    	if (lazyLoaded == null) {
    		lazyLoaded = LazyLoadUtils.loadTypeFieldLazily(container,f);
    		//System.out.println("The field " + container.getClass().getName() + "." +  f.getName() + " (" + f.getType() + ") @LazyLoadCapable and is NOT loaded!!!");
    	} else {
    		//System.out.println("The field " + container.getClass().getName() + "." +  f.getName() + " (" + f.getType() + ") @LazyLoadCapable and is already loaded!!!");
    	}
    	return lazyLoaded;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
// 	LAZY LOAD AT MAPS
///////////////////////////////////////////////////////////////////////////////////////////////////
	/**
     * If the Map were not loaded... load it
     */
    @SuppressAjWarnings
    Map around() : 
    		typesContainingLazilyLoadedFields() 	// types not applyng LazyLoad
    	 && notInObjectsHierarchyModifierFlow()		// not in an ObjectsHierarchyModifier method
    	 && get(@LazyLoadCapable Map+ T+.*) {		// any not Map member annotated with LazyLoadCapable
    	
    	// Get the joinpoint info
    	Object container = thisJoinPoint.getTarget();
		FieldSignature fs = (FieldSignature)thisJoinPoint.getSignature();
		Field f = fs.getField();					// Field being accessed
		
		// Ensure that the field is a Map, NOT a Map implementation because if this is the case
		// the map cannot be lazily loaded
		if (!f.getType().isAssignableFrom(Map.class)) throw new IllegalArgumentException("The Map field " + container.getClass().getName() + "." + f.getName() + " (" + f.getType() + ") type is NOT a java.util.Map (the interface), so it cannot be converted to a LazilyLoaded Map.");
		
		// Get the underlyng Map
    	Map lazyMap = proceed();
    	
    	// If the map instance is null there are two options to load it:
    	//	- The map entries are loaded as they are requested: the underlying Map is replaced with a LazyMap
    	//	- All the Map elements are loaded the first time the map is accessed (the Map is annotated with @LazyCompleteLoad): The Map is NOT replaced with a LazyMap; it's simply loaded
    	boolean hasToLoad = (lazyMap == null);
    	if (lazyMap != null && f.getAnnotation(LazyCompleteLoad.class) == null) hasToLoad = !(lazyMap instanceof LazyMap);
    	
    	if (hasToLoad) {    		
    		lazyMap = (Map)LazyLoadUtils.loadTypeFieldLazily(container,f);
    		//System.out.println("The field " + container.getClass().getName() + "." +  f.getName() + " (" + f.getType() + ") @LazyLoadCapable and is NOT loaded!!!");
    	} else {
    		//System.out.println("The field " + container.getClass().getName() + "." +  f.getName() + " (" + f.getType() + ") @LazyLoadCapable and is already loaded!!!");
    	}
    	return lazyMap;
    }
}