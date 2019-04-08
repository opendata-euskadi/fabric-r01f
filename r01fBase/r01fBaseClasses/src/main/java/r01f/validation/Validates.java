package r01f.validation;

/**
 * Facet for model objects that contains self validation logic:
 * <pre class='brush:java'>
 * 		public class MySelfValidatedType
 * 		  implements ModelObject,
 * 					 Validates<MySelfValidatedType> {
 * 			...
 *			@Override
 *			public ModelObjectValidationResult<MySelfValidatedType> validate(final MySelfValidatedType obj) {
 *				if (obj.getOid() == null) {
 *					return ModelObjectValidationResultBuilder.on(obj)
 *															 .isNotValidBecause("The oid MUST NOT be null");
 *				}
 *				return ModelObjectValidationResultBuilder.on(obj)
 *														 .isValid();
 *			}
 * 		}
 * </pre>
 * @param <T>
 */
public interface Validates<T> {

	public ObjectValidationResult<T> validate(final T obj);
}
