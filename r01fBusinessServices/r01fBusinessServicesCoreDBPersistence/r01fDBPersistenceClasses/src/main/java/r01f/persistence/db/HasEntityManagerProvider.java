package r01f.persistence.db;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import r01f.guids.CommonOIDs.TenantID;
import r01f.securitycontext.SecurityContext;


/**
 * Interface for objects that holds an {@link EntityManager}{@link Provider} 
 */
public interface HasEntityManagerProvider {
	/**
	 * @return an entity manager provider
	 */
	public Provider<EntityManager> getEntityManagerProvider();
	/**
	 * Uses the {@link EntityManager} {@link Provider} to get a fresh new instance
	 * {@link EntityManager} instance
	 * @return
	 */
	public EntityManager getFreshNewEntityManager();
	/**
	 * Uses the {@link EntityManager} {@link Provider} to get a fresh new instance for a given tenant
	 * @param tenantId
	 * @return
	 */
	public EntityManager getFreshNewEntityManager(final TenantID tenantId);
	/**
	 * Uses the {@link EntityManager} {@link Provider} to get a fresh new instance for a given securityContext
	 * @param securityContext
	 * @return
	 */
	public EntityManager getFreshNewEntityManager(final SecurityContext securityContext);
}
