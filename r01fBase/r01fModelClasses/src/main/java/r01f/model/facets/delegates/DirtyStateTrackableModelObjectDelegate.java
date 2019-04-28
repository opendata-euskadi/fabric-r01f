package r01f.model.facets.delegates;

import com.google.common.annotations.GwtIncompatible;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.facets.delegates.FacetDelegateBase;
import r01f.model.ModelObject;
import r01f.model.facets.DirtyStateTrackableModelObject;
import r01f.types.dirtytrack.DirtyTrackAdapter;

/**
 * The delegate object that implements the {@link DirtyStateTrackableModelObject} interface
 * @param <M>
 */
@GwtIncompatible
public class DirtyStateTrackableModelObjectDelegate<M extends ModelObject> 
	 extends FacetDelegateBase<M>
  implements DirtyStateTrackableModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DirtyStateTrackableModelObjectDelegate(final M modelObject) {
		super(modelObject);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DirtyStateTrackableModelObject interface methods
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public DirtyTrackingStatus getTrackingStatus() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		return trackable.getTrackingStatus();
	}
	@Override
	public boolean isThisDirty() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		return trackable.isThisDirty();
	}
	@Override
	public boolean isDirty() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		return trackable.isDirty();
	}
	@Override
	public DirtyStateTrackable resetDirty() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.resetDirty();
		return this;
	}
	@Override
	public DirtyStateTrackable touch() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.touch();	
		return this;
	}
	@Override
	public DirtyStateTrackable setNew() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.setNew();
		return this;	
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds,
														   final boolean checkIfOldValueChanges) {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.startTrackingChangesInState(startTrackingInChilds,
											  checkIfOldValueChanges);
		return this;
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds) {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.startTrackingChangesInState(startTrackingInChilds);
		return this;
	}
	@Override
	public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds) {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.stopTrackingChangesInState(stopTrackingInChilds);
		return this;
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.startTrackingChangesInState();
		return this;
	}
	@Override
	public DirtyStateTrackable stopTrackingChangesInState() {
		DirtyStateTrackable trackable = DirtyTrackAdapter.adapt(this.getModelObject());
		trackable.stopTrackingChangesInState();
		return this;
	}
	@Override @SuppressWarnings("unchecked")
	public <T> T getWrappedObject() {
		return (T)this.getModelObject();
	}
}
