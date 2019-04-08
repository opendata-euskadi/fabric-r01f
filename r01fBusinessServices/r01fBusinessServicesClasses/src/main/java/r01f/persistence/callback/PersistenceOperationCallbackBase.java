package r01f.persistence.callback;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.model.persistence.PersistenceOperationResult;

@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class PersistenceOperationCallbackBase 
  		   implements PersistenceOperationCallback,
  		   			  Serializable {
	
	private static final long serialVersionUID = -4720498929631480441L;
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter protected PersistenceOperationResult _persistenceOperationResult;
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	ACCESSOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasSucceeded() {
		return _persistenceOperationResult.hasSucceeded();
	}
	@Override
	public boolean hasFailed() {
		return _persistenceOperationResult.hasFailed();
	}
}
