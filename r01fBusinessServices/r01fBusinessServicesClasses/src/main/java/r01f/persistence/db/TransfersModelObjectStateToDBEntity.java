package r01f.persistence.db;

import r01f.model.ModelObject;
import r01f.model.PersistableObject;
import r01f.securitycontext.SecurityContext;

/**
 * Transfers the {@link PersistableObject}'s state to a given {@link DBEntity}
 * (used when creating or updating a {@link DBEntity} from a {@link ModelObject})
 * @param <M>
 * @param <DB>
 */
public interface TransfersModelObjectStateToDBEntity<M extends PersistableObject,
													 DB extends DBEntity> {
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final M modelObj,final DB dbEntity);
}
