package r01f.persistence.db;

import r01f.model.ModelObject;
import r01f.securitycontext.SecurityContext;

/**
 * Interface for types that transforms a {@link DBEntity} into a {@link ModelObject}
 * (used when loading a {@link ModelObject} from a {@link DBEntity})
 * @param <DB>
 * @param <M>
 */
public interface TransformsDBEntityIntoModelObject<DB extends DBEntity,
												   M extends ModelObject> {
	/**
	 * Builds a {@link ModelObject} from this {@link DBEntity} data
	 * @param securityContext
	 * @param dbEntity
	 * @return a model object
	 */
	public abstract M dbEntityToModelObject(final SecurityContext securityContext,
										    final DB dbEntity);
}
