package r01f.aspects.core.util;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

import r01f.aspects.interfaces.dirtytrack.ForceTransientToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableLazyCollection;
import r01f.collections.lazy.LazyCollection;
import r01f.collections.lazy.LazyMap;
import r01f.reflection.ReflectionUtils;

/**
 * Utility used to change a field in an object hierarchy
 * It uses reflection to crawl the object hierarchy and change a field 
 */
public class ObjectsHierarchyModifier {
/////////////////////////////////////////////////////////////////////////////////////////////////
// 	FUNCTION
/////////////////////////////////////////////////////////////////////////////////////////////////
	public static interface StateModifierFunction<T> {
		public void changeState(T obj);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static <T> void changeObjectHierarchyState(final T obj,final TypeToken<T> typeRef,
													  final StateModifierFunction<T> modifierFunction,
													  final boolean changeChildsState,
													  final Predicate<Field> fieldAcceptCriteria) {
		if (obj == null) return;
		
		// Crawl if it's an object implementing typeRef
		if (ReflectionUtils.isImplementing(obj.getClass(),typeRef.getRawType())) {
			modifierFunction.changeState(obj);
		}
		
		// Update childs
		Field[] fields = ReflectionUtils.allFields(obj.getClass());
		if (fields == null) return;
		
		for (Field f : fields) {
			// Ignored fields
			if (Modifier.isTransient(f.getModifiers()) && !f.isAnnotationPresent(ForceTransientToDirtyStateTrackable.class)) continue;	// do not process transient fields
			if (Modifier.isStatic(f.getModifiers())) continue;																			// do not process static fields
			if (f.isAnnotationPresent(NotDirtyStateTrackable.class)) continue;															// do not process @NotDirtyStateTrackable annotated fields (otherwise it enters an infinite loop)
			if (fieldAcceptCriteria != null && !fieldAcceptCriteria.apply(f)) continue;
			
			// Get the field value... if null do nothind
			Object fValue = ReflectionUtils.fieldValue(obj,f,true);
			if (fValue == null) continue;
			
			// --- Primitive types
			if (f.getType().isPrimitive() || ReflectionUtils.isFinalImmutable(f.getType())) continue;
			
			// System.out.println("::::::::>" + obj.getClass().getName() + "." + f.getName() + " (" + fValue.getClass() + ") ");
			
			// --- complex types
			if (changeChildsState
			 && ReflectionUtils.isImplementing(fValue.getClass(),typeRef.getRawType())) {		// BEWARE!! check if it implements the IMPLEMENTATION and NOT the interface
				T fzChild = (T)fValue;																			
				ObjectsHierarchyModifier.changeObjectHierarchyState(fzChild,typeRef,		// Recursive 
																	modifierFunction,changeChildsState,
																	fieldAcceptCriteria);
				
			}
			
			// --- CollectionS
			// a) Maps
			if (changeChildsState && CollectionUtils.isMap(f.getType())) {
				Map<?,?> theMap = (Map<?,?>)fValue;
				// Update the field at every LOADED element of the map
				Collection<?> loadedValues = theMap instanceof LazyMap ? ((LazyMap<?,?>)theMap).loadedValues()	// ONLY update the loaded elements
																 	   : theMap.values();					
				for (Object v : loadedValues) {
					if (v == null) continue;
					if (ReflectionUtils.isImplementing(v.getClass(),typeRef.getRawType())) {	// Update only elements implementing T
						ObjectsHierarchyModifier.changeObjectHierarchyState((T)v,typeRef,	// Recursive call
												   							modifierFunction,changeChildsState,
												   							fieldAcceptCriteria);
					}
				}
			}
			// b) collections
			else if (changeChildsState && CollectionUtils.isCollection(f.getType())) {
				Collection<?> theCol = (Collection<?>)fValue;
				// Update the field at every LOADED element of the collection 
				Collection<?> loadedValues = theCol instanceof LazyCollection ? ((ChangesTrackableLazyCollection<?>)theCol).loadedValues()	// ONLY update the loaded elements
																			  : theCol;
				for (Object v : loadedValues) {
					if (v == null) continue;
					if (ReflectionUtils.isImplementing(v.getClass(),typeRef.getRawType())) {	// Update only elements implementing T
						ObjectsHierarchyModifier.changeObjectHierarchyState((T)v,typeRef,	// Recursive call
												   							modifierFunction,changeChildsState,
												   							fieldAcceptCriteria);
					} 
				}
			}
			// c) Array
			else if (changeChildsState && CollectionUtils.isArray(f.getType())) {
				if (ReflectionUtils.isImplementing(f.getType().getComponentType(),typeRef.getRawType())) {	// Update only elements implementing T
					int length = Array.getLength(fValue);
				    for (int i = 0; i < length; i ++) { 
				        Object v = Array.get(fValue,i);
				        if (v == null) continue; 
						ObjectsHierarchyModifier.changeObjectHierarchyState((T)v,typeRef,				// Recursive call
								   			  	   							modifierFunction,changeChildsState,
								   			  	   							fieldAcceptCriteria);
				    }
				}
			} 
		} // for fields
	}
}
