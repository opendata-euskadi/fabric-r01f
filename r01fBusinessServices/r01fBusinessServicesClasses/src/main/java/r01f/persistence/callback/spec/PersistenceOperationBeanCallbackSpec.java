package r01f.persistence.callback.spec;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.persistence.callback.PersistenceOperationCallback;

@MarshallType(as="beanCallbackSpec")
@Accessors(prefix="_")
public class PersistenceOperationBeanCallbackSpec
     extends PersistenceOperationCallbackSpecBase {

	private static final long serialVersionUID = -169301594778012524L;
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="implType")
	@Getter @Setter private Class<? extends PersistenceOperationCallback> _implType;
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceOperationBeanCallbackSpec() {
		// default no-args constructor
	}
	public PersistenceOperationBeanCallbackSpec(final Class<? extends PersistenceOperationCallback> callbackType) {
		_implType = callbackType;
	}
}
