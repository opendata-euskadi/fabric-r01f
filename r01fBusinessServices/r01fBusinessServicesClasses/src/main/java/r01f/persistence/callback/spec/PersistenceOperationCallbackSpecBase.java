package r01f.persistence.callback.spec;

import lombok.experimental.Accessors;

@Accessors(prefix="_")
abstract class PersistenceOperationCallbackSpecBase
    implements PersistenceOperationCallbackSpec {
	
	private static final long serialVersionUID = 6637078046538568391L;
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	public PersistenceOperationCallbackSpecBase() {
		// default no-args constructor
	}
}
