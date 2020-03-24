package r01f.collections.lazy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.dirtytrack.util.CollectionChangesTrackerImpl;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

@Accessors(prefix="_")
@Slf4j
@RequiredArgsConstructor
     class LazyChangesTrackerBase<V> 
implements DirtyStateTrackable {
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATED CHANGES TRACKER
/////////////////////////////////////////////////////////////////////////////////////////
	@NotDirtyStateTrackable
	@Getter protected final CollectionChangesTracker<V> _changesTracker = new CollectionChangesTrackerImpl<V>();

	@Override public DirtyTrackingStatus getTrackingStatus() {		return _changesTracker.getTrackingStatus();		}
	@Override public <T> T getWrappedObject() {						return _changesTracker.<T>getWrappedObject();		}
	@Override public boolean isThisDirty() {	return _changesTracker.isThisDirty();		}
	@Override public boolean isDirty() {		return _changesTracker.isDirty();			}
	@Override public DirtyStateTrackable touch() {			return _changesTracker.touch();			}
	@Override public DirtyStateTrackable setNew() {			return _changesTracker.setNew();		}
	@Override public DirtyStateTrackable resetDirty() {		return _changesTracker.resetDirty();	}
	@Override public DirtyStateTrackable startTrackingChangesInState() {	return _changesTracker.startTrackingChangesInState();	}
	@Override public DirtyStateTrackable stopTrackingChangesInState() {		return _changesTracker.stopTrackingChangesInState();	}
	@Override public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds) {		return _changesTracker.startTrackingChangesInState(startTrackingInChilds);	}
	@Override public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds,
																	 final boolean checkIfOldValueChanges) {	return _changesTracker.startTrackingChangesInState(startTrackingInChilds,
																			 																					   checkIfOldValueChanges);	}
	@Override public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds) {	return _changesTracker.stopTrackingChangesInState(stopTrackingInChilds);	}
	
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	protected static DirtyStateTrackable _asDirtyStateTrackable(final Object o) {
		try {
			return (DirtyStateTrackable)o;
		} catch (ClassCastException ccEx) {
			log.error("{} canot be cast-ed to {}: maybe it's not annotated as @{} or maybe weaving is not in use, add -javaagent:aspectjweaver.jar to the VM start command",
					  o.getClass(),DirtyStateTrackable.class,ConvertToDirtyStateTrackable.class.getSimpleName());
			throw ccEx;
		}
	}
}
