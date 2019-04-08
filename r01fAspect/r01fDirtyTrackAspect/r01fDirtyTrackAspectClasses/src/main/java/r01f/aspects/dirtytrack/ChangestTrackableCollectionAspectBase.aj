package r01f.aspects.dirtytrack;

import java.util.Collection;
import java.util.Set;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.collections.dirtytrack.util.ChangesTrackedCollectionMethods;
import r01f.collections.dirtytrack.util.CollectionChangesTrackerImpl;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * Base aspect that converts a type extending Collection into a trackable Collection (ChangesTrackableCollection)
 */
privileged public abstract aspect ChangestTrackableCollectionAspectBase<C extends ChangesTrackableCollection> 
	           			  extends DirtyStateTrackableAspectBase<C> {
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE used to inject a field of type CollectionChangesTracker in charge of 
//  tracking changes in the collection
/////////////////////////////////////////////////////////////////////////////////////////
	private transient CollectionChangesTracker<V> ChangesTrackableCollection._trackingCollectionChangesTracker = new CollectionChangesTrackerImpl<V>();
	
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
//				- fields extending Collection
//				- the collection (add / delete / update) 
// 	NOTE:	It's NOT necessary to include methods from DirtyStateTrackable interface 
//			that will NOT be overridden (ie: getTrackingStatus())
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean ChangesTrackableCollection.isDirty() {
		boolean someMemberDirty = false;		// dirty status of the fields extending Collection
		boolean colDirty = false;				// dirty status of the collection
		
		someMemberDirty = this.getTrackingStatus()._isDirty(this);
		if (!someMemberDirty) colDirty = ChangesTrackedCollectionMethods.isDirty((Collection)this,
																				 _trackingCollectionChangesTracker);
		
		return someMemberDirty | colDirty;
	}
	public DirtyStateTrackable ChangesTrackableCollection.resetDirty() {
		// Reset dirty status of fields of type extending Collection
		this.getTrackingStatus()._resetDirty(this);
		// Reset dirty status of the Collection
		_trackingCollectionChangesTracker.resetDirty();
		
		return (DirtyStateTrackable)this;
	}
	public DirtyStateTrackable ChangesTrackableCollection.startTrackingChangesInState() {
		_trackingCollectionChangesTracker.startTrackingChangesInState();
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  true);
		return (DirtyStateTrackable)this;
	}
	public DirtyStateTrackable ChangesTrackableCollection.stopTrackingChangesInState() {
		_trackingCollectionChangesTracker.stopTrackingChangesInState();
		this.getTrackingStatus()._stopTrackingChangesInState(this,
												 	 		 true);
		return (DirtyStateTrackable)this;
	}
	public DirtyStateTrackable ChangesTrackableCollection.startTrackingChangesInState(final boolean startTrackingInChilds) {
		_trackingCollectionChangesTracker.startTrackingChangesInState(startTrackingInChilds);
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  startTrackingInChilds);
		return (DirtyStateTrackable)this;
	}
	public DirtyStateTrackable ChangesTrackableCollection.startTrackingChangesInState(final boolean startTrackingInChilds,
																	   				  final boolean checkIfOldValueChanges) {
		_trackingCollectionChangesTracker.startTrackingChangesInState(startTrackingInChilds,
															   		  checkIfOldValueChanges);
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  startTrackingInChilds,
												 	 		  checkIfOldValueChanges);
		return (DirtyStateTrackable)this;
	}
	public DirtyStateTrackable ChangesTrackableCollection.stopTrackingChangesInState(final boolean stopTrackingInChilds) {
		_trackingCollectionChangesTracker.stopTrackingChangesInState(stopTrackingInChilds);
		this.getTrackingStatus()._stopTrackingChangesInState(this,
															 stopTrackingInChilds);
		return (DirtyStateTrackable)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE ChangesTrackableCollection interface
//  (see impl of ChangesTrackedCollection)
/////////////////////////////////////////////////////////////////////////////////////////
	public CollectionChangesTracker<V> ChangesTrackableCollection.getChangesTracker() {
		return _trackingCollectionChangesTracker;
	}
	public Set<V> ChangesTrackableCollection.newEntries() {
		return ChangesTrackedCollectionMethods.newEntries((Collection)this,_trackingCollectionChangesTracker);
	}
	public Set<V> ChangesTrackableCollection.removedEntries() {
		return ChangesTrackedCollectionMethods.removedEntries((Collection)this,_trackingCollectionChangesTracker);
	}
	public Set<V> ChangesTrackableCollection.notNewOrRemovedEntries() {
		return ChangesTrackedCollectionMethods.notNewOrRemovedEntries((Collection)this,_trackingCollectionChangesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COLLECTION MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressAjWarnings
    Object around(Collection col,Object value) :
    		    target(@ConvertToDirtyStateTrackable Collection+)
    		 &&	call(public * (Collection+).add(*))	// call add method of a type annotated with @ConvertToDirtyStateTrackable and extending Collection
    		 && args(value)															// add method args
    		 && target(col) {														// the type annotated with @ConvertToDirtyStateTrackable and extending Collection
    	Object outVal = null; 
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)col;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outVal = ChangesTrackedCollectionMethods.add(value,
										 		  		 col,trck.getChangesTracker());
    	} else {
    		outVal = proceed(col,value);
    	}
    	return outVal;
    }
    @SuppressAjWarnings
    boolean around(Collection col,Collection otherCol) : 
    		    target(@ConvertToDirtyStateTrackable Collection+)
    		 &&	call(public * (Collection+).addAll(Collection))	// call addAll method of a type annotated with @ConvertToDirtyStateTrackable and extending Collection
    		 && args(otherCol)																	// addAll method args 
    		 && target(col) {																	// the type annotated with @ConvertToDirtyStateTrackable and extending Collection
    	boolean outResult = true; 
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)col;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outResult = ChangesTrackedCollectionMethods.addAll(otherCol,
										 		  		       col,trck.getChangesTracker());
    	} else {
    		outResult = proceed(col,otherCol);
    	}
    	return outResult;
    }
    @SuppressAjWarnings
    boolean around(Collection col,Object value) :
    		    target(@ConvertToDirtyStateTrackable Collection+)
    		 && call(public * (Collection+).remove(*))									// call remove method of a type annotated with @ConvertToDirtyStateTrackable and extending Collection
    		 && args(value)																// remove method args
    		 && target(col) {															// the type annotated with @ConvertToDirtyStateTrackable and extending Collection
    	boolean outResult = true; 
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)col;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outResult = ChangesTrackedCollectionMethods.remove(value,
										 		  		       col,trck.getChangesTracker());
    	} else {
    		outResult = proceed(col,value);
    	}
    	return outResult;
    }
    @SuppressAjWarnings
    boolean around(Collection col,Collection otherCol) : 
    		    target(@ConvertToDirtyStateTrackable Collection+)
    		 && call(public * (Collection+).removeAll(Collection))								// call removeAll method of a type annotated with @ConvertToDirtyStateTrackable and extending Collection
    		 && args(otherCol)																	// removeAll method args
    		 && target(col) {																	// the type annotated with @ConvertToDirtyStateTrackable and extending Collection
    	boolean outResult = true; 
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)col;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outResult = ChangesTrackedCollectionMethods.removeAll(otherCol,
										 		  		       	  col,trck.getChangesTracker());
    	} else {
    		outResult = proceed(col,otherCol);
    	}
    	return outResult;
    }
    @SuppressAjWarnings
    boolean around(Collection col,Collection otherCol) : 
    	        target(@ConvertToDirtyStateTrackable Collection+)
    		 && call(public * (@ConvertToDirtyStateTrackable Collection+).retainAll(Collection))// call retainAll method of a type annotated with @ConvertToDirtyStateTrackable and extending Collection
    		 && args(otherCol)																	// retainAll method args
    		 && target(col) {																	// the type annotated with @ConvertToDirtyStateTrackable and extending Collection
    	boolean outResult = true; 
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)col;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		outResult = ChangesTrackedCollectionMethods.retainAll(otherCol,
										 		  		       	  col,trck.getChangesTracker());
    	} else {
    		outResult = proceed(col,otherCol);
    	}
    	return outResult;
    } 
    @SuppressAjWarnings
    void around(Collection col) : 
    		 	call(public * (@ConvertToDirtyStateTrackable Collection+).clear())	// call clear method of a type annotated with @ConvertToDirtyStateTrackable and extending Collection
    		 && target(col) {														// the type annotated with @ConvertToDirtyStateTrackable and extending Collection
    	ChangesTrackableCollection trck = (ChangesTrackableCollection)col;
    	if (trck.getTrackingStatus().isThisDirtyTracking()) {
    		ChangesTrackedCollectionMethods.clear(col,trck.getChangesTracker());
    	} else {
    		proceed(col);
    	}
    } 
}
