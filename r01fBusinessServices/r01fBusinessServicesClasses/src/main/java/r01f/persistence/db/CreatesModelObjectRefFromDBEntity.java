package r01f.persistence.db;

import r01f.model.ModelObjectRef;
import r01f.model.PersistableModelObject;
import r01f.securitycontext.SecurityContext;

/**
 * For a dependent model object, this interface encapsulates the method that 
 * creates a {@link ModelObjectRef} from the {@link DBEntity} info
 * @param <DB>
 * @param <M>
 */
public interface CreatesModelObjectRefFromDBEntity<DB extends DBEntity,M extends PersistableModelObject<?>> {
	public <MR extends ModelObjectRef<M>> MR createModelObjectRefFrom(final SecurityContext securityContext,
																	  final M modelObj);
}
