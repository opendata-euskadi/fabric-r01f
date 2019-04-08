package r01f.aspects.dirtytrack;

import java.util.Map;
import java.util.Set;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableMap;
import r01f.collections.dirtytrack.util.ChangesTrackedMapMethods;
import r01f.collections.dirtytrack.util.CollectionChangesTrackerImpl;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * Converts a type implementing Map into a changes trackable Map
 */
privileged public aspect ChangestTrackableMapAspect 
	  			 extends DirtyStateTrackableAspectBase<ChangesTrackableMap> {
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE used to inject a field of type CollectionChangesTracker in charge of 
//  tracking changes in the Map
/////////////////////////////////////////////////////////////////////////////////////////
	private transient CollectionChangesTracker<K> ChangesTrackableMap._trackingMapChangesTracker = new CollectionChangesTrackerImpl<K>();
	
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE used to inject a field of type DirtyTrackingStatusImpl
//	It's NOT necessary to inject field _trackingStatus since when injecting 
//	ChangesTrackableCollection interface, the DirtyStateTrackable interface is
// 	also injected because 
//		ChangesTrackableCollection extends DirtyStateTrackable
// 	so DirtyStateTrackableAspect applies and _trackingStatus is injected
/////////////////////////////////////////////////////////////////////////////////////////
//	DirtyTrackingStatus DirtyStateTrackable._trackingStatus = new DirtyTrackingStatusImpl();
	
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE: interface DirtyStateTrackable -> Overridden methods of DirtyStateTrackable
//												 interface
//	NOTE: 	DirtyStateTrackable interface are overridden since to check the dirty status
//			the things to check are:
//				- fields extending Map
//				- the Map (add / delete / update) 
// 	NOTE:	It's NOT necessary to include methods from DirtyStateTrackable interface 
//			that will NOT be overridden (ie: getTrackingStatus())
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked","rawtypes" })
	public boolean ChangesTrackableMap.isDirty() {
		boolean someMemberDirty = false;		// dirty status of the fields extending Map
		boolean mapDirty = false;				// dirty status of the collection
		
		someMemberDirty = this.getTrackingStatus()._isDirty(this);
		if (!someMemberDirty) mapDirty = ChangesTrackedMapMethods.isDirty((Map)this,
																		  _trackingMapChangesTracker);
		
		return someMemberDirty | mapDirty;
	}
	public DirtyStateTrackable ChangesTrackableMap.resetDirty() {
		// Reset dirty status of fields of type extending Collection
		this.getTrackingStatus()._resetDirty(this);
		// Reset dirty status of the Collection
		_trackingMapChangesTracker.resetDirty();
		
		return this;
	}
	public DirtyStateTrackable ChangesTrackableMap.startTrackingChangesInState() {
		_trackingMapChangesTracker.startTrackingChangesInState();
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  true);
		return this;
	}
	public DirtyStateTrackable ChangesTrackableMap.startTrackingChangesInState(final boolean startTrackingInChilds) {
		_trackingMapChangesTracker.startTrackingChangesInState(startTrackingInChilds);
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  startTrackingInChilds);
		return this;
	}
	public DirtyStateTrackable ChangesTrackableMap.startTrackingChangesInState(final boolean startTrackingInChilds,
																			   final boolean checkIfOldValueChanges) {
		_trackingMapChangesTracker.startTrackingChangesInState(startTrackingInChilds,
															   checkIfOldValueChanges);
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  startTrackingInChilds,
												 	 		  checkIfOldValueChanges);
		return this;
	}
	public DirtyStateTrackable ChangesTrackableMap.stopTrackingChangesInState() {
		_trackingMapChangesTracker.stopTrackingChangesInState();
		this.getTrackingStatus()._stopTrackingChangesInState(this,
												 	 		 true);
		return this;
	}
	public DirtyStateTrackable ChangesTrackableMap.stopTrackingChangesInState(final boolean stopTrackingInChilds) {
		_trackingMapChangesTracker.stopTrackingChangesInState(stopTrackingInChilds);
		this.getTrackingStatus()._stopTrackingChangesInState(this,
															 stopTrackingInChilds);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE ChangesTrackableMap interface
//  (see ChangesTrackedMap)
/////////////////////////////////////////////////////////////////////////////////////////
	public CollectionChangesTracker<K> ChangesTrackableMap.getChangesTracker() {
		return _trackingMapChangesTracker;
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public Set<K> ChangesTrackableMap.newKeys() {
		return ChangesTrackedMapMethods.newKeys((Map)this,_trackingMapChangesTracker);
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public Set<K> ChangesTrackableMap.removedKeys() {
		return ChangesTrackedMapMethods.removedKeys((Map)this,_trackingMapChangesTracker);
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public Set<K> ChangesTrackableMap.notNewOrRemovedKeys() {
		return ChangesTrackedMapMethods.notNewOrRemovedKeys((Map)this,_trackingMapChangesTracker);
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public Map<K,V> ChangesTrackableMap.newEntries() {
		return ChangesTrackedMapMethods.newEntries((Map)this,_trackingMapChangesTracker);
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public Map<K,V> ChangesTrackableMap.notNewOrRemovedEntries() {
		return ChangesTrackedMapMethods.notNewOrRemovedEntries((Map)this,_trackingMapChangesTracker);
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public String ChangesTrackableMap.debugInfo() {
		return ChangesTrackedMapMethods.debugInfo((Map)this,_trackingMapChangesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAP MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressAjWarnings
    Object around(Map map,Object key,Object value) : 
    			target(@ConvertToDirtyStateTrackable Map+)
    	     && call(public * (Map+).put(*,*))										// call the put method of a @ConvertToDirtyStateTrackable type extending map
    		 && args(key,value)														// put mothods args
    		 && target(map) {														// the Map-extending type annotated with @ConvertToDirtyStateTrackable
    	Object outVal = null; 
    	ChangesTrackableMap trck = (ChangesTrackableMap)map;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outVal = ChangesTrackedMapMethods.put(key,value,
										 		  map,trck.getChangesTracker());
    	} else {
    		outVal = proceed(map,key,value);
    	}
    	return outVal;
    }
    @SuppressAjWarnings
	Object around(Map map,Object key) :
    			target(@ConvertToDirtyStateTrackable Map+)
    		 && call(public * (Map+).remove(*))										// call the remove method of a @ConvertToDirtyStateTrackable type extending map
    		 && args(key)															// remove method args
    		 && target(map) {														// the Map-extending type annotated with @ConvertToDirtyStateTrackable
		Object outVal = null; 
    	ChangesTrackableMap trck = (ChangesTrackableMap)map;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outVal = ChangesTrackedMapMethods.remove(key,
													 map,trck.getChangesTracker());
    	} else {
    		outVal = proceed(map,key);
    	}
    	return outVal;
	}
	@SuppressAjWarnings
	void around(Map map,java.util.Map otherMap) : 
		        target(@ConvertToDirtyStateTrackable Map+)
    		 && call(public * (Map+).putAll(java.util.Map))							// call the putAll method of a @ConvertToDirtyStateTrackable type extending map
    		 && args(otherMap)														// putAll method args
    		 && target(map) {														// the Map-extending type annotated with @ConvertToDirtyStateTrackable
    	ChangesTrackableMap trck = (ChangesTrackableMap)map;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
			ChangesTrackedMapMethods.putAll(otherMap,
											map,trck.getChangesTracker());
    	}
	}
	@SuppressAjWarnings
	void around(Map map) : 
    			target(@ConvertToDirtyStateTrackable Map+)
    		 && call(public * (Map+).clear())										// call the clear method of a @ConvertToDirtyStateTrackable type extending map
    		 && target(map) {														// the Map-extending type annotated with @ConvertToDirtyStateTrackable
    	ChangesTrackableMap trck = (ChangesTrackableMap)map;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		ChangesTrackedMapMethods.clear(map,trck.getChangesTracker());
    	}
	}
}
