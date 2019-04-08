package r01f.objectstreamer;

/**
 * Interface for the types that holds a model object's {@link Marshaller}
 */
public interface HasMarshaller {
	/**
	 * @return a model object's {@link Marshaller}
	 */
	public Marshaller getModelObjectsMarshaller();
}
