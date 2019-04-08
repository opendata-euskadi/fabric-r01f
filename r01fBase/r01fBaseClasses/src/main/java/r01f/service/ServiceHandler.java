package r01f.service;

/**
 * Interface used to expose start() and stop() methods for a service in a way that it can be controlled
 * externally
 * see  https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree
 * 
 * Usually this is used with JPA's PersistenceService: 
 * <ul>
 * 		<li>At DBGuiceModuleBase type, PersistenceService is initialized and it's a ServiceHandler is binded</li>
 * 		<li>At ServletContextListenerBase type, the start() and stop() life-cycle control methods are called</li>
 * </ul>
 */
public interface ServiceHandler {
	public void start();
	public void stop();
}
