package r01f.validation;

/**
 * An interface for objects that self validates
 * @param <T>
 */
public interface SelfValidates<T> {
	public ObjectValidationResult<T> validate();
}
