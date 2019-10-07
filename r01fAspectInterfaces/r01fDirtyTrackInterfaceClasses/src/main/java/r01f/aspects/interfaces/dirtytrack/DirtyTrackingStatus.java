package r01f.aspects.interfaces.dirtytrack;

import java.io.Serializable;


/**
 * Data about an object's tracking  {@link DirtyStateTrackable}
 */
public interface DirtyTrackingStatus 
		 extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//  Is THIS object new?
/////////////////////////////////////////////////////////////////////////////////////////	
	public void setThisNew(boolean newObj);
	public boolean isThisNew();
/////////////////////////////////////////////////////////////////////////////////////////
//  Changes in THIS object's state
/////////////////////////////////////////////////////////////////////////////////////////	
	public void setThisDirty(boolean thisDirty);
	public boolean isThisDirty();
/////////////////////////////////////////////////////////////////////////////////////////
//  Is THIS object being tracked?
/////////////////////////////////////////////////////////////////////////////////////////	
	public void setThisDirtyTracking(boolean dirtyTrack);
	public boolean isThisDirtyTracking();
/////////////////////////////////////////////////////////////////////////////////////////
//  How this object state changes are being controlled?
/////////////////////////////////////////////////////////////////////////////////////////
	public void setThisCheckIfValueChanges(boolean check);
	public boolean isThisCheckIfValueChanges();
	
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS TO DELEGATE FROM THE ASPECTS 
/////////////////////////////////////////////////////////////////////////////////////////
	public void _resetDirty(DirtyStateTrackable trck);
	public boolean _isThisDirty(DirtyStateTrackable trck);
	public boolean _isDirty(DirtyStateTrackable trck);
	public void _startTrackingChangesInState(DirtyStateTrackable trck);
	public void _stopTrackingChangesInState(DirtyStateTrackable trck);
	public void _startTrackingChangesInState(DirtyStateTrackable trck,
											 boolean startTrackingInChilds);
	public void _startTrackingChangesInState(DirtyStateTrackable trck,
											 boolean startTrackingInChilds,
											 boolean checkIfOldValueChanges);
	public void _stopTrackingChangesInState(DirtyStateTrackable trck,
											boolean stopTrackingInChilds);
}
