package r01f.aspects.dirtytrack;

import r01f.aspects.core.dirtytrack.DirtyTrackingStatusImpl;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;


/**
 * Aspect that makes posible to detect changes in an object's state: all state changes are tracked and recorded 
 * Usage:
 * [1]: Annotate the type with @ConvertToDirtyStateTrackable
 * 				@ConvertToDirtyStateTrackable
 * 				public class MyTrackableObj {
 * 				}
 * 				MyTrackableObj obj = new MyTrackableObj();
 * 				obj.setXX   <-- this change is NOT taken into account since changes are NOT being tracked
 * [2]: Start tracking changes
 * 				assertFalse(DirtyTrackAdapter.adapt(obj)
 * 											 .isDirty());
 * 				DirtyTrackAdapter.adapt(obj)
 * 								 .startTrackingChanges();
 * [2]: Change the object's state 
 * 				MyTrackableObj obj = new MyTrackableObj();
 * 				obj.setXX
 * 				obj.setYY
 * [3]: Check if there's any change
 * 				assertTrue(DirtyTrackAdapter.adapt(obj)
 * 											.isDirty());	<-- Must be true since obj state has changed
 */
privileged public aspect DirtyStateTrackableAspect  
                 extends DirtyStateTrackableAspectBase<DirtyStateTrackable>  {
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE to "inject" a DirtyTrackingStatusImpl field
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Dirty status control
	 */
	private transient DirtyTrackingStatus DirtyStateTrackable._trackingStatus = new DirtyTrackingStatusImpl();
	
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE inject DirtyStateTrackable interface
/////////////////////////////////////////////////////////////////////////////////////////
	public DirtyTrackingStatus DirtyStateTrackable.getTrackingStatus() {
		return _trackingStatus;
	}
	public boolean DirtyStateTrackable.isThisDirty() {
		return this.getTrackingStatus()._isThisDirty(this);
	}
	public boolean DirtyStateTrackable.isDirty() {
		return this.getTrackingStatus()._isDirty(this);
	}
	public DirtyStateTrackable DirtyStateTrackable.touch() {
		this.getTrackingStatus().setThisDirty(true);
		return this;
	}
	public DirtyStateTrackable DirtyStateTrackable.setNew() {
		this.getTrackingStatus().setThisNew(true);
		return this;
	}
	public DirtyStateTrackable DirtyStateTrackable.resetDirty() {
		this.getTrackingStatus()._resetDirty(this);
		return this;
	}
	public DirtyStateTrackable DirtyStateTrackable.stopTrackingChangesInState() {
		this.getTrackingStatus()._stopTrackingChangesInState(this,
												 	 		 true);	
		return this;
	}
	public DirtyStateTrackable DirtyStateTrackable.startTrackingChangesInState() {
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  true);
		return this;
	}
	public DirtyStateTrackable DirtyStateTrackable.startTrackingChangesInState(final boolean startTrackingInChilds) {
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  startTrackingInChilds);
		return this;
	}
	public DirtyStateTrackable DirtyStateTrackable.startTrackingChangesInState(final boolean startTrackingInChilds,
																			   final boolean checkIfOldValueChanges) {
		this.getTrackingStatus()._startTrackingChangesInState(this,
												 	 		  startTrackingInChilds,
												 	 		  checkIfOldValueChanges);
		return this;
	}
	public DirtyStateTrackable DirtyStateTrackable.stopTrackingChangesInState(final boolean stopTrackingInChilds) {
		this.getTrackingStatus()._stopTrackingChangesInState(this,
															 stopTrackingInChilds);
		return this;
	}
	@SuppressWarnings("unchecked")
	public <T> T DirtyStateTrackable.getWrappedObject() {
		return (T)this;
	}
}
