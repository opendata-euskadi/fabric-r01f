package r01f.persistence.db;

import r01f.model.ModelObject;
import r01f.model.ModelObjectRef;
import r01f.model.PersistableModelObject;
import r01f.securitycontext.SecurityContext;

/**
 * For a dependent model object, this interface encapsulates the methods that 
 * transfers the {@link ModelObject}'s parent reference to a given {@link DBEntity}
 * (used when creating or updating a {@link DBEntity} from a {@link ModelObject})
 * @param <P>
 * @param <DB>
 */
public interface TransfersParentModelObjectRefToDBEntity<P extends PersistableModelObject<?>,
														 DB extends DBEntity> {
	public <PR extends ModelObjectRef<P>> void setDBEntityFieldsForParentModelObjectRef(final SecurityContext securityContext,
												 	  								    final PR parentRef,final DB dbEntity);
}
