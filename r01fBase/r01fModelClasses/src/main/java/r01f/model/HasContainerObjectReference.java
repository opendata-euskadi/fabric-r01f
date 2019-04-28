package r01f.model;



/**
 * An interface for objects that maintains a reference to the object that contain them
 * ie
 * <pre class='brush:java'>
 * 		// The container type
 * 		public class MyContainerType {
 * 			private MyContainedType _instance;
 * 		}
 * 		// The contained type that maintains a reference to the container
 * 		public class MyContainedType 
 * 		  implements HasContainerObjectReference<MyContainerType> {
 * 			private MyContainerType _container;
 * 		}
 * </pre>
 * 
 * This type is usually used to allow {@link Marshaller} objects set a link between contained and container objects
 * when de-serializing objects
 * 
 * @param <T>
 */
public interface HasContainerObjectReference<T> {
	/**
	 * @return the container
	 */
	public T getContainer();
}
