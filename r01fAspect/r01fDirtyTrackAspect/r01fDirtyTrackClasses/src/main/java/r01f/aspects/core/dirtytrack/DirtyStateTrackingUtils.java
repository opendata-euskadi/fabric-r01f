package r01f.aspects.core.dirtytrack;


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.core.util.ObjectsHierarchyModifier;
import r01f.aspects.core.util.ObjectsHierarchyModifier.StateModifierFunction;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.dirtytrack.ChangesTrackedCollection;
import r01f.collections.dirtytrack.ChangesTrackedList;
import r01f.collections.dirtytrack.ChangesTrackedMap;
import r01f.collections.dirtytrack.ChangesTrackedSet;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableMap;
import r01f.locale.LanguageTexts;
import r01f.reflection.ReflectionUtils;
import r01f.types.annotations.CompositionRelated;

@Slf4j
class DirtyStateTrackingUtils {
	/**
	 * Wraps a {@link Map}-type field so all changes made are tracked
	 * @param trckContainer 
	 * @param mapField 
	 * @parma map
	 * @return 
	 */
	static <K,V,T extends Map<K,V>> Object wrapMapToTrackable(final DirtyStateTrackable trckContainer,
															  final Field mapField,
															  final T map) {
    	// If all this happens:
    	//		a.- It's a trackable object and changes are being tracked 
    	//		b.- It's NOT a ChangesTrackableMap
    	// change the object for a trackable one
		Object outMap = null;
		if (trckContainer.getTrackingStatus() == null) {
			log.error("The Map tracking status is null");
			outMap = map;
		} else if (trckContainer.getTrackingStatus().isThisDirtyTracking() && !(map instanceof ChangesTrackableMap)) {
    		// Ensure the field is a Map
    		boolean isMap = Map.class.isAssignableFrom(mapField.getType());
    		if (!isMap) throw new IllegalArgumentException("The Map field " + mapField.getDeclaringClass().getName() + "." + mapField.getName() + " (" + mapField.getType() + ") type is NOT a java.util.Map (the interface), so it cannot be converted to a ChangesTrackableMap.");
    		
    		// Change the field instance for a ChangesTrackableMap
    		ChangesTrackableMap<K,V> changesTracked = _wrapMap(map);
    		changesTracked.getTrackingStatus().setThisDirtyTracking(trckContainer.getTrackingStatus().isThisDirtyTracking());		// pasarle el estado de tracking al nuevo mapa
    		ReflectionUtils.setFieldValue(trckContainer,mapField,changesTracked,false);

    		outMap = changesTracked;
    	} else if (!trckContainer.getTrackingStatus().isThisDirtyTracking() && map instanceof ChangesTrackableMap) {
    		// Changes are NOT being tracked but the Map was substituted by a ChangesTrackableMap: just return the map
    		outMap = map;
    	} else {
    		// Changes are being tracked and the Map was already substituted by a ChangesTrackableMap
    		outMap = map;
    	}
    	return outMap;
	}
	/**
	 * Wraps a {@link Collection}-type field so all changes made are tracked
	 * @param trckContainer 
	 * @param colField
	 * @param col 
	 * @return 
	 */
	static <V,T extends Collection<V>> Object wrapCollectionToTrackable(final DirtyStateTrackable trckContainer,
																		final Field colField,
																		final T col) {
    	// If all this happens:
    	//		a.- It's a trackable object and changes are being tracked 
    	//		b.- It's NOT a ChangesTrackableCollection
    	// change the object for a trackable one
		Object outCol = null;
    	if (trckContainer.getTrackingStatus().isThisDirtyTracking() && !(col instanceof ChangesTrackableCollection)) {
    		// Changes are being tracked but the collection has not already been substituted 
    		// Ensure the field is a Collection
    		boolean isCollectionOrSet = Collection.class.isAssignableFrom(colField.getType()) || 
    									Set.class.isAssignableFrom(colField.getType()) ||
    									List.class.isAssignableFrom(colField.getType());
    		if (!isCollectionOrSet) throw new IllegalArgumentException("The Collection/List/Set field " + colField.getDeclaringClass().getName() + "." + colField.getName() + " (" + colField.getType() + ") type is NOT a java.util.Collection/java.util.List/java.util.Set (the interface), so it cannot be converted to a ChangesTrackableCollection.");
    		
    		// Change the field instance for a ChangesTrackableCollection
    		ChangesTrackableCollection<V> changesTracked = _wrapCollection(col);
    		changesTracked.getTrackingStatus().setThisDirtyTracking(trckContainer.getTrackingStatus().isThisDirtyTracking());		// pasarle el estado de tracking a la nueva colecci�n
    		ReflectionUtils.setFieldValue(trckContainer,colField,changesTracked,false);
    		outCol = changesTracked;
    	} else if (!trckContainer.getTrackingStatus().isThisDirtyTracking() && col instanceof ChangesTrackableCollection) {
    		// Changes are NOT being tracked but the collection was substituted by a ChangesTrackableCollection: just return the col
    		outCol = col;
    	} else {
    		// Changes are being tracked and the Map was already substituted by a ChangesTrackableCollection
    		outCol = col;
    	}
    	return outCol;
	}
	/**
	 * Wraps a {@link LanguageTexts}-type field so all changes made are tracked
	 * @param trckContainer 
	 * @param langTextsField
	 * @param languageTexts 
	 * @return 
	 */
	static LanguageTexts wrapLanguageTextsToTrackable(final DirtyStateTrackable trckContainer,
													  final Field langTextsField,
													  final LanguageTexts languageTexts) {
		return null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COLLECTION / MAP WRAPPERS (V1)
//	Works for "simple" Map/Collection, ie:
//	public class MyObj {
//		private Map<String,String> _myMap;	<-- _myMap can be changed for a ChangesTrackedMap
//	}											
/////////////////////////////////////////////////////////////////////////////////////////
	private static <K,V,T extends Map<K,V>> ChangesTrackedMap<K,V> _wrapMap(final T map) {
		ChangesTrackedMap<K,V> outWrappedMap = new ChangesTrackedMap<K,V>(map);
		return outWrappedMap;
	}	
	private static <V,T extends Collection<V>> ChangesTrackedCollection<V> _wrapCollection(final T col) {
		ChangesTrackedCollection<V> outWrappedCol = null;
		if (col instanceof List) {
			outWrappedCol = new ChangesTrackedList<V>(col);
		} else if (col instanceof Set) {
			outWrappedCol = new ChangesTrackedSet<V>(col);
		} else {
			outWrappedCol = new ChangesTrackedCollection<V>(col);
		}
		return outWrappedCol;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//	Utility that checks if an obejct is dirty by walking through all it's fields
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isThisObjectDirty(final DirtyStateTrackable trck) {
		return _isObjectDirty(trck,false);
	}
	public static boolean isObjectDirty(final DirtyStateTrackable trck) {
		return _isObjectDirty(trck,true);
	}
	private static boolean _isObjectDirty(final DirtyStateTrackable trck,final boolean checkDependants) {
		String dirtyMemberName = null;
		boolean outDirty = trck.getTrackingStatus().isThisDirty();
		if (outDirty) dirtyMemberName = "some not complex member"; 
		if (!outDirty) {
			// Check the child DirtyStateTrackable objects
			// BEWARE: [1] an infinite loop could be started if the child object mantains a reference to the father object
			//		   [2] sometimes the field is NOT declared with a DirtyStateTrackable type
			//			   @ConverToDirtyStateTrackable
			//			   public class SomeTrackableType {
			//					private MyType _myType;		<--- MyType is NOT a DirtyStateTrackable subtype... so it's NOT detected as a DirtyStateTrackable object
			//			   }
			//			   The solution is to annotate the field with @ConvertToDirtyStateTrackable 
			//			   @ConverToDirtyStateTrackable
			//			   public class SomeTrackableType {
			//					@ConvertToDirtyStateTrackable	<-- now MyType is recognized as a DirtyStateTrackable instance
			//					private MyType _myType;		
			//			   }
			for (Field f : ReflectionUtils.allFields(trck.getClass())) {
				if (f.isAnnotationPresent(NotDirtyStateTrackable.class)) continue;					// do not process @NotDirtyStateTrackable-annotated fields (infinite loop)
				if (!checkDependants && !f.isAnnotationPresent(CompositionRelated.class)) continue;	// if only THIS object is being checked, do NOT check complex members NOT annotated with @CompositionRelated
				
				Object value = ReflectionUtils.fieldValue(trck,f,false);
				if (value == null) continue;
								
				boolean isTrackable = _isTrackable(f.getType());				// check if the field-defined type is trackable
				if (!isTrackable) isTrackable = _isTrackable(value.getClass());	// the real type of the field (the field can be an interface) can be trackable
				if (!isTrackable) continue;

				//log.trace("\t-dirtyChecking of map field {} {}.{}",trck.getClass().getName(),f.getName(),value.getClass().getName());
				
				// Maps
				if (ReflectionUtils.isSubClassOf(f.getType(),Map.class)) {
					if (ReflectionUtils.isImplementing(value.getClass(),ChangesTrackableMap.class) 
					 && ((ChangesTrackableMap<?,?>)value).isDirty()) {
						dirtyMemberName = f.getName();
						outDirty = true;
						break;
					}
				}
				// Collections
				else if (ReflectionUtils.isSubClassOf(f.getType(),Collection.class)) {
					if (ReflectionUtils.isImplementing(value.getClass(),ChangesTrackableCollection.class) 
					 && ((ChangesTrackableCollection<?>)value).isDirty()) {
						dirtyMemberName = f.getName();
						outDirty = true;
						break;
					}
				}
				// complex objects
				else {					
					if (((DirtyStateTrackable)value).isDirty()) {
						dirtyMemberName = f.getName();
						outDirty = true;
						break;
					}
				}
			}	// for
		}
		if (outDirty) log.debug("/DirtyTracking/: an instance of {} is detected to be dirty ({})",
								trck.getClass().getName(),dirtyMemberName);
		return outDirty;
	}
	private static boolean _isTrackable(final Class<?> type) {
		return ReflectionUtils.isSubClassOf(type,DirtyStateTrackable.class)	// DirtyStateTrackable fields
			   ||
			   type.isAnnotationPresent(ConvertToDirtyStateTrackable.class);			// or fields annotated with @ConvertToDirtyStateTrackable
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Changes the tacking status of objects 
/////////////////////////////////////////////////////////////////////////////////////////
	static final Predicate<Field> _fieldAcceptCriteria = new Predicate<Field>() {
																@Override
																public boolean apply(final Field f) {
																	if (f.getDeclaringClass().getPackage().getName().startsWith("java")) return false;	
																	if (f.getDeclaringClass().getPackage().getName().startsWith("com.google")) return false;
																	if (f.getName().startsWith("ajc$")) return false; 
																	if (f.getName().startsWith("_tracking")) return false;
																	return true;
																}
														};
	/**
	 * Modifies an object's status
	 */
	static class DirtyStatusModifier {
		@NoArgsConstructor
		private static class DirtyStatusResetFunction 
			      implements StateModifierFunction<DirtyStateTrackable> {
			
			@Override
			public void changeState(final DirtyStateTrackable obj) {
				obj.getTrackingStatus().setThisDirty(false);	// The object is NOT dirty
				obj.getTrackingStatus().setThisNew(false);		// The object is NOT new
			}
		}
		/**
		 * Sets the dirty status of an object and optionally of it's descendants
		 * @param trackableObj 
		 * @param changeAlsoChilds true if the descendants dirty track status must be changed
		 */
		@SuppressWarnings("serial")
		public static void resetDirtyStatus(final DirtyStateTrackable trackableObj,
										  	final boolean changeAlsoChilds) {
			ObjectsHierarchyModifier.<DirtyStateTrackable>changeObjectHierarchyState(trackableObj,new TypeToken<DirtyStateTrackable>() {/* nothing */},
																		   			 new DirtyStatusResetFunction(),
																		   			 changeAlsoChilds,
																		   			 _fieldAcceptCriteria);
		}
	}
	static class DirtyTrackingStatusModifier {
		private static class DirtyTrackingStatusModifierFunction 
		          implements StateModifierFunction<DirtyStateTrackable> {
			
			private final boolean _track;
			private 	  boolean _checkIfOldValueChanges;
			
			public DirtyTrackingStatusModifierFunction(final boolean track) {
				_track = track;
			}
			public DirtyTrackingStatusModifierFunction(final boolean track,
													   final boolean checkIfOldValueChanges) {
				_track = track;
				_checkIfOldValueChanges = checkIfOldValueChanges;
			}
			@Override
			public void changeState(final DirtyStateTrackable trck) {
				trck.getTrackingStatus()
					.setThisDirtyTracking(_track);
				if (trck instanceof ChangesTrackableCollection) {
					((ChangesTrackableCollection<?>)trck).getChangesTracker()
														 .startTrackingChangesInState(true,_checkIfOldValueChanges);
				} else if (trck instanceof ChangesTrackableMap) {
					((ChangesTrackableMap<?,?>)trck).getChangesTracker().startTrackingChangesInState(true,_checkIfOldValueChanges);
				} 
				if (_track) trck.getTrackingStatus()
								.setThisCheckIfValueChanges(_checkIfOldValueChanges);		// ONLY changed when start tracking
			}																				// ... so it has NO effect when called from stopTrackingChangesInState
		}
		/**
		 * Sets the dirty status of an object and optionally of it's descendants
		 * @param trackableObj 
		 * @param checkIfOldValueChanges Sets how to detect if a field has changed
	 	 *									- _trckCheckIfValueChanges=true --> when a field changes, the new value is compared with the previous one
	 	 *																		... the field is marked as dirty ONLY if the value has changed
		 *									  									(used at joinpoint before fieldSet) 
		 *									- _trckCheckIfValueChanges=false -> When a field changes, the new value is NOT compared with the previous one
		 *									  									... the field is allways considered dirty, no matter if the value has NOT changed
		 *									  									(used at joinpiont after fieldSet)
		 */
		@SuppressWarnings("serial")
		public static void startTrackingChangesInState(final DirtyStateTrackable trackableObj,
													   final boolean checkIfOldValueChanges,
													   final boolean startTrackingInChilds) {
			ObjectsHierarchyModifier.<DirtyStateTrackable>changeObjectHierarchyState(trackableObj,new TypeToken<DirtyStateTrackable>() {/* nothing */},
																		   			 new DirtyTrackingStatusModifierFunction(true,checkIfOldValueChanges),
																		   			 startTrackingInChilds,
																		   			 _fieldAcceptCriteria);
		}
		/**
		 * Stops tracking 
		 * @param trackableObj
		 */
		@SuppressWarnings("serial")
		public static void stopTrackingChangesInState(DirtyStateTrackable trackableObj,
													  final boolean stopTrackingInChilds) {
			ObjectsHierarchyModifier.<DirtyStateTrackable>changeObjectHierarchyState(trackableObj,new TypeToken<DirtyStateTrackable>() {/* nothing */},
																		   			 new DirtyTrackingStatusModifierFunction(false),
																		   			 stopTrackingInChilds,
																		   			 _fieldAcceptCriteria);
		}
	}
}
