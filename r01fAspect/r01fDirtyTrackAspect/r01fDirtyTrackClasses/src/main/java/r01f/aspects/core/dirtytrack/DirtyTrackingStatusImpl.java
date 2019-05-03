package r01f.aspects.core.dirtytrack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.core.dirtytrack.DirtyStateTrackingUtils.DirtyStatusModifier;
import r01f.aspects.core.dirtytrack.DirtyStateTrackingUtils.DirtyTrackingStatusModifier;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableMap;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.reflection.ReflectionUtils;

/**
 * Tracking status container
 * @see {@link DirtyStateTrackable}
 */
@Slf4j
@NoArgsConstructor
public class DirtyTrackingStatusImpl 
  implements DirtyTrackingStatus {

	private static final long serialVersionUID = -4833831535002335824L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private boolean _thisNew = false;					// it's a new object
	private boolean _thisDirty = false;					// has THIS object changed? (NO MATTER the dependent objects has changed)
	private boolean _thisDirtyTracking = false;			// are changes being tracked?
	private boolean _thisCheckIfValueChanges = false;	// Sets how a field change is detected
					 	 								//	- _trckCheckIfValueChanges=true --> when a field changes, the new value is compared with the previous one
					 	 								//										... the field is marked as dirty ONLY if the value has changed
						 								//	  									(used at joinpoint before fieldSet) 
						 								//	- _trckCheckIfValueChanges=false -> When a field changes, the new value is NOT compared with the previous one
						 								//	  									... the field is allways considered dirty, no matter if the value has NOT changed
						 								//	  									(used at joinpiont after fieldSet)
/////////////////////////////////////////////////////////////////////////////////////////
//  THIS OBJECT NEW
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setThisNew(final boolean newObj) {
		_thisNew = newObj;
	}
	@Override
	public boolean isThisNew() {
		return _thisNew;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  THIS OBJECT DIRTY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setThisDirty(final boolean thisDirty) {
		_thisDirty = thisDirty;
	}
	@Override
	public boolean isThisDirty() {
		return _thisDirty;
	}
	@Override
	public void setThisDirtyTracking(final boolean dirtyTrack) {
		_thisDirtyTracking = dirtyTrack;
	}
	@Override
	public boolean isThisDirtyTracking() {
		return _thisDirtyTracking;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setThisCheckIfValueChanges(final boolean check) {
		_thisCheckIfValueChanges = check;
	}
	@Override
	public boolean isThisCheckIfValueChanges() {
		return _thisCheckIfValueChanges;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void _resetDirty(final DirtyStateTrackable trck) {
		_thisDirty = false;
		_thisNew = false;
		DirtyStatusModifier.resetDirtyStatus(trck,true);
	}
	@Override
	public boolean _isThisDirty(final DirtyStateTrackable trck) {
		// If changes are not being tracked... do not check anything else, return false
		if (!_thisDirtyTracking) return false;
		boolean outDirty = false;
		if (_thisDirty) {
			// if this object is dirty... it worth nothing to check anything else
			outDirty = true;
 		} else {
 			// if this object is NOT dirty... one of it's COMPOSITION related objects could be
 			outDirty = DirtyStateTrackingUtils.isThisObjectDirty(trck);
 		}
		return outDirty;
	}
	@Override
	public boolean _isDirty(final DirtyStateTrackable trck) {
		// If changes are not being tracked, do nothing
		if (!_thisDirtyTracking) return false;
		
		boolean outDirty = false;
		if (_thisDirty) {
			// if this object is dirty... it worth nothing to check anything else
			outDirty = true;
		} else {
			// if this object is NOT dirty... check related objects (all of them -opposed to _isThisDirty)
			outDirty = DirtyStateTrackingUtils.isObjectDirty(trck);		
		}
		return outDirty;
	}
	@Override
	public void _startTrackingChangesInState(final DirtyStateTrackable trck) {
		DirtyTrackingStatusModifier.startTrackingChangesInState(trck,
																false,
																true);
	}
	@Override
	public void _stopTrackingChangesInState(final DirtyStateTrackable trck) {
		DirtyTrackingStatusModifier.stopTrackingChangesInState(trck,
															   true);
	}
	@Override
	public void _startTrackingChangesInState(final DirtyStateTrackable trck,
											 final boolean startTrackingInChilds) {
		DirtyTrackingStatusModifier.startTrackingChangesInState(trck,
																false,
																startTrackingInChilds);
	}
	@Override
	public void _startTrackingChangesInState(final DirtyStateTrackable trck,
											 final boolean startTrackingInChilds,
											 final boolean checkIfOldValueChanges) {
		_thisCheckIfValueChanges = checkIfOldValueChanges;
		DirtyTrackingStatusModifier.startTrackingChangesInState(trck,
																checkIfOldValueChanges,
																startTrackingInChilds);
	}
	@Override
	public void _stopTrackingChangesInState(final DirtyStateTrackable trck,
											final boolean stopTrackingInChilds) {
		DirtyTrackingStatusModifier.stopTrackingChangesInState(trck,
															   stopTrackingInChilds);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ASPECT ADVICE
/////////////////////////////////////////////////////////////////////////////////////////
	public static void _beforeSetMember(final DirtyStateTrackable trck,final Field field,final Object newValue) {
		// Do not track transient fields
		if (!_dirtyTrackableField(field)) return;
		
		// When the old value is compared with the new value to check if a change has taken place
		// 		trck.getTrackingStatus().isThisCheckIfValueChanges() = true
		// If the old and new values are NOT compared the _afterSetMember() method is where the dirty status is set
		if (trck.getTrackingStatus().isThisDirtyTracking() && trck.getTrackingStatus().isThisCheckIfValueChanges()) {		// changes are being tracked: check if the value has changed from the prev value			
			// Get the previous value using reflection
			Object prevValue = ReflectionUtils.fieldValue(trck,field,false);
			
			// See if it has changed from it's previous value
			if (prevValue == null && newValue == null) {
				// It has NOT changed
			} if (prevValue != null && newValue == null) {
				log.trace("[DirtyTracking]: change detected at field {} of {}",field.getName(),trck.getWrappedObject().getClass());
				trck.getTrackingStatus().setThisDirty(true);
			} else if (prevValue == null && newValue != null) {
				log.trace("[DirtyTracking]: change detected at field {} of {}",field.getName(),trck.getWrappedObject().getClass());
				trck.getTrackingStatus().setThisDirty(true);
			} else if (prevValue != null && newValue != null) {		// check for equality
				if (!trck.getTrackingStatus().isThisDirty()) {		// ... but ONLY change if it was NOT dirty
					log.trace("[DirtyTracking]: change detected at field {} of {}",field.getName(),trck.getWrappedObject().getClass());
					trck.getTrackingStatus().setThisDirty( !prevValue.equals(newValue) );																							
				}
																																	
			}			
		} 
	}
	/**
	 * Advice after: AFTER setting a field value
	 */
	public static void _afterSetMember(final DirtyStateTrackable trck,final Field field,final Object newValue) {
		// Do not track transient fields
		if (!_dirtyTrackableField(field)) return;
		
		// The new instance can be a DirtyStateTrackable instace created "outside" the parent object AFTER a call to startTrackingChanges
		// ... so the tracking attrs might not have been set 
		if (newValue != null && newValue instanceof DirtyStateTrackable) {
			DirtyStateTrackable newValueAsTrackable = ((DirtyStateTrackable)newValue);			
			if (newValueAsTrackable.getTrackingStatus().isThisDirtyTracking() != trck.getTrackingStatus().isThisDirtyTracking()
			 || newValueAsTrackable.getTrackingStatus().isThisCheckIfValueChanges() != trck.getTrackingStatus().isThisCheckIfValueChanges()) {
				DirtyTrackingStatusModifier.startTrackingChangesInState(newValueAsTrackable,
																		trck.getTrackingStatus().isThisDirtyTracking(),
																		trck.getTrackingStatus().isThisCheckIfValueChanges());
			}
		} 
		// The field probably has changed (a setter method has been called)... a comparison between the old and new field value is not done
		// so we assume that the value has changed...
		//		trck.getTrackingStatus().isThisCheckIfValueChanges() = false
		// if the new value has to be compared with the old value to determine if a change has occur the _beforeSetMember() method 
		// should be called
		if (trck.getTrackingStatus().isThisDirtyTracking() && !trck.getTrackingStatus().isThisCheckIfValueChanges()) {		// changes are being tracked
			log.trace("[DirtyTracking]: change detected at field {} of {}",field.getName(),trck.getWrappedObject().getClass());			
			trck.getTrackingStatus().setThisDirty(true);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAP / Collection setting (Map / Collection)
// 	When a collection / map is set, it's transparently changed to a wrapped collection / map
// 	that changes all changes inside the collection / map
//	NOTE the advices could have been defined as
// 				Map around() : get(Map+ DirtyStateTrackable+.*) 
// 	... but an error is raised whtn the Collection is an object that extends Collection
// 		  Ej: 
// 				public class MyObj extends HashMap.
/////////////////////////////////////////////////////////////////////////////////////////
	public static LanguageTexts _arroundLanguageTextsFieldGet(final DirtyStateTrackable trck,final Field field,final LanguageTexts theLangTexts) {
		if (theLangTexts == null) return null;
		
		// if it's NOT a map-backed LanguageTexts instance... do nothing
		if (!(theLangTexts instanceof LanguageTextsMapBacked)) return theLangTexts;
		
		// do not track transient fields
		if (!_dirtyTrackableField(field)) return theLangTexts;
    	
    	return DirtyStateTrackingUtils.wrapLanguageTextsToTrackable(trck,field,theLangTexts);
    }
	public static <K,V> Object _arroundMapFieldGet(final DirtyStateTrackable trck,final Field field,final Map<K,V> theMap) {
		if (theMap == null) return null;
		
		// if it's already a ChangesTrackableMap instance do nothing
		if (theMap instanceof ChangesTrackableMap) return theMap;
		
		// do not track transient fields
		if (!_dirtyTrackableField(field)) return theMap;

    	return DirtyStateTrackingUtils.wrapMapToTrackable(trck,field,theMap);
    }
	public static <V> Object _arroundCollectionFieldGet(final DirtyStateTrackable trck,final Field field,final Collection<V> theCol) {
		if (theCol == null) return null;
		
		// if it's already a ChangesTrackableCollection instance do nothing
		if (theCol instanceof ChangesTrackableCollection) return theCol;
		
		// do nothing on transient fields
		if (!_dirtyTrackableField(field)) return theCol;
    	Object outWrappedCol = DirtyStateTrackingUtils.wrapCollectionToTrackable(trck,field,theCol);
    	return outWrappedCol;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Return true if the field is trackable
     * @param field 
     * @return 
     */
    private static boolean _dirtyTrackableField(final Field field) {
		boolean tranzient = Modifier.isTransient(field.getModifiers());
		boolean ztatic = Modifier.isStatic(field.getModifiers());
		boolean notDirtyStateTrackable = field.isAnnotationPresent(NotDirtyStateTrackable.class);
		// Return true if none of the prev conditions is met
		return !tranzient && !ztatic && !notDirtyStateTrackable;
    }

}
