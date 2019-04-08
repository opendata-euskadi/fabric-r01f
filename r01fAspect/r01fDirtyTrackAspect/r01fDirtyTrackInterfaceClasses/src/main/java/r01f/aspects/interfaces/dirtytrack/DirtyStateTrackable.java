package r01f.aspects.interfaces.dirtytrack;




/**
 * Interface that states that an object's state is trackable, ie, every class implementing
 * this interface can be asked for changes into it's state. 
 */
public interface DirtyStateTrackable {
/////////////////////////////////////////////////////////////////////////////////////////
//	PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the dirty status of THIS object but when computing the dirty status, NOT every dependant objects
	 * are taken into account (despite what's happening in the isDirty() method); only fields annotated with @CompositionRelation 
	 * dirty status are computed 
	 * @return the dirty status of THIS object since startTrackingChangesInState method was called
	 */
	public boolean isThisDirty();
	/**
	 * Returns the dirty status of the object taking into account every dependant object when computing de dirty status
	 * (despite what's happening in the isThisDirty() method); 
	 * @return true if the state of this object or one of its dependant object has been modified since startTrackingChangesInState method was called
	 */
	public boolean isDirty();
	/**
	 * @return tracking info
	 */
	public DirtyTrackingStatus getTrackingStatus();
	/**
	 * "Touches" the model object setting it's state trackable's dirty status as true
	 * so when saving the object it'll be detected as modified 
	 */
	public DirtyStateTrackable touch();
	/**
	 * Sets the object as NEW so it'll be created when persisted
	 */
	public DirtyStateTrackable setNew();
	/**
	 * Resets the dirty status (sets the object as NOT modified so it'll NOT be persisted when a save() method is called)
	 */
	public DirtyStateTrackable resetDirty();
	/**
	 * Starts tracking the changes in the object's state
	 * @param startTrackingInChilds true if the dependant object's have to be controlled
	 * @param checkIfOldValueChanges states how a member change has to be computed
	 * 									checkIfOldValueChanges=true --> when changing a field, it looks for a change in the previous value respecting the new value
     *							  										(the joinpoint is at before fieldSet) 
	 * 									checkIfOldValueChanges=false -> when changing a field, it doesn't look for a change in the previous value respecting the new value
	 * 																	it simply consider that when a set in a field is called, a change has occured, not taking into
	 * 																	account whether the value has been changed
	 *									  								(the joinpiont is at after fieldSet)
	 */
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds,
														   final boolean checkIfOldValueChanges);
	/**
	 * Starts tracking the changes in the object's state
	 * @param startTrackingInChilds true if the dependant object's have to be controlled
	 */
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds);
	/**
	 * Stops the state modify operations checking
	 * @param startTrackingInChilds true if the dependant object's have to be controlled
	 */
	public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds);
	/**
	 * Starts tracking the changes in the object's state
	 */
	public DirtyStateTrackable startTrackingChangesInState();
	/**
	 * Stops tracking the changes in the object's state
	 */
	public DirtyStateTrackable stopTrackingChangesInState();
	/**
	 * @return the underlying object
	 */
	public <T> T getWrappedObject();
}
