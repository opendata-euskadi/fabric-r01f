package r01f.types;

import java.io.Serializable;

import lombok.Getter;
import r01f.util.types.Strings;

/**
 * Encapsulates an error description
 * It's usefull when a function call must return an error description if an error was detected or null if no error was risen
 * <pre class='brush:java'>
 * 		public ValidationError myFunc() {
 * 			// ... do some validation
 * 			return ValidationError.about(someEntity)
 * 								  .withMessage("some validation was not successful");
 * 		}
 * </pre>
 * It's even possible to use placeholders with vars:
 * <pre class='brush:java'>
 * 		public ValidationError myFunc() {
 * 			// ... do some validation
 * 			return ValidationError.about(someEntity)
 * 								  .withMessage("some validation about {} was not successful",
 * 										  	   someEntity.getClass().getName());
 * 		}
 * </pre>
 */
public class ValidationError<E>
  implements Serializable {

	private static final long serialVersionUID = 9135154765893707362L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private E _theEntity;
	@Getter private String _theError;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	private ValidationError(final E entity) {
		_theEntity = entity;
	}
	public static <E> ValidationError<E> about(final E theEntity) {
		return new ValidationError<E>(theEntity);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public ValidationError<E> withMessage(final String theError) {
		_theError = theError;
		return this;
	}
	public ValidationError<E> withMessage(final String theError,final Object... vars) {
		_theError = Strings.customized(theError,
					       			   vars);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public String asString() {
		return _theError;
	}
	@Override
	public String toString() {
		return _theError;
	}
}
