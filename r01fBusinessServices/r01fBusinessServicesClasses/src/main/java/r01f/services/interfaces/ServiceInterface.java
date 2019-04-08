package r01f.services.interfaces;


/**
 * Marker interface for services
 * It's important to remember that interfaces extending this one MUST be annotated with the
 * appCode and module 
 * <pre class='brush:java'>
 *		@ServiceInterfaceFor(appCode="r01d",module="alias")
 *		public interface R01MServicesForAlias 
 *			     extends ServiceInterface {
 *			... service interface methods...
 *		}
 * </pre>
 */
public interface ServiceInterface {
	/* just a marker interface */
}
