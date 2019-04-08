package r01f.aspects.freezable;

import r01f.aspects.interfaces.freezable.ConvertToFreezable;
import r01f.aspects.interfaces.freezable.Freezable;




/**
 * Inyecta el interfaz {@link Freezable} y su comportamiento definidos en el aspecto {@link FreezableInterfaceAspect}
 * a todas las clases anotadas con {@link ConvertToFreezable}
 * 
 * NOTA: 	Ver {@link ConvertToFreezableAnnotationAspect}
 * 
 * Para hacer que un tipo implemente el interfaz {@link Freezable} hay que:
 *		- PASO 1: Establecer que todas las clases anotadas con @ConvertToFreezable implementen
 *				  el interfaz {@link Freezable}
 *						declare parents: @ConvertToFreezable * implements Freezable;
 *		- PASO 2: Crear el interfaz {@link Freezable} (ver aspecto {@link r01f.aspects.dirtytrack.DirtyStateTrackableInterfaceAspect})
 *		- PASO 3: Implementar los pointcuts específicos para el interfaz {@link Freezable}
 *
 * IMPORTANTE!!	En este aspecto se implementa el PASO 1 y en el aspecto {@link FreezableInterfaceAspect}
 * 				se implementan el PASO 2 y PASO 3
 */
privileged public aspect ConvertToFreezableAnnotationAspect { 
	/**
	 * PASO 1: Hacer que todos los tipos anotados con @ConvertToFreezable implementen
	 * 		   el interfaz Freezable (cuyo comportamiento está en {@link FreezableInterfaceAspect})
	 */
	declare parents: @ConvertToFreezable * implements Freezable;

}
