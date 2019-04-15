package r01f.persistence.db;

import javax.persistence.EntityManager;

/**
 * Interface for the types that holds an {@link EntityManager}
 */
public interface HasEntityManager {
	/**
	 * @return an {@link EntityManager}
	 */
	public EntityManager getEntityManager();
}
