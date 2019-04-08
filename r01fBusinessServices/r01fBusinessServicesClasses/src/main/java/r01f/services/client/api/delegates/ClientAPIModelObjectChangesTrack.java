package r01f.services.client.api.delegates;

import java.util.Collection;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.types.dirtytrack.DirtyTrackAdapter;
import r01f.util.types.collections.CollectionUtils;

public class ClientAPIModelObjectChangesTrack {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <R> void startTrackingChangesOnLoaded(final R record) {
		_startTrackingChangesOn(record,
								false);	// not a new record
	}
	public static <R> void startTrackingChangesOnSaved(final R record) {
		_startTrackingChangesOn(record,
								false);	// not a new record
	}
	public static <R> void startTrackingChangesOnDeleted(final R record) {
		_startTrackingChangesOn(record,
								true);	// now it's a new record in the case the client tries to save it again
	}
	public static <R> void startTrackingChangesOnNew(final R record) {
		_startTrackingChangesOn(record,
								true);	// it's a new record 		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <R> void startTrackingChangesOnLoaded(final Collection<R> record) {
		_startTrackingChangesOn(record,
								false);	// not a new record
	}
	public static <R> void startTrackingChangesOnSaved(final Collection<R> record) {
		_startTrackingChangesOn(record,
								false);	// not a new record
	}
	public static <R> void startTrackingChangesOnDeleted(final Collection<R> record) {
		_startTrackingChangesOn(record,
								true);	// now it's a new record in the case the client tries to save it again
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static <R> void _startTrackingChangesOn(final R record,
										   		    final boolean newRecord) {
		// Sets loaded record tracking status (not new, not dirty...)
		if (record != null
		 && record instanceof DirtyStateTrackable) {
			DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(record);
			trackable.resetDirty();									// The object's state is clean... no modifications
			trackable.startTrackingChangesInState(true);			// Start tracking changes in state
			trackable.getTrackingStatus().setThisNew(newRecord);
		}
	}
	private static <R> void _startTrackingChangesOn(final Collection<R> records,
										   		  	final boolean newRecords) {
		// Sets loaded records tracking status (not new, not dirty...)
		if (CollectionUtils.hasData(records)) {
			for (R record : records) {
				_startTrackingChangesOn(record,
										newRecords);		// are new records 
			}
		}
	}
}
