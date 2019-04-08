package r01f.validation;

import r01f.validation.ObjectValidationResults.ObjectValidationResultNOK;
import r01f.validation.ObjectValidationResults.ObjectValidationResultOK;

public interface ObjectValidationResult<M> {
	/**
	 * @return the validated model object
	 */
	public M getValidatedObject();
	/**
	 * @return true if the model object is valid
	 */
	public boolean isValid();
	/**
	 * @return true if the model object is NOT valid
	 */
	public boolean isNOTValid();
	/**
	 * @return a {@link ObjectValidationResultOK} if the model object is valid or a {@link ClassCastException} if the model object is NOT valid
	 */
	public ObjectValidationResultOK<M> asOKValidationResult();
	/**
	 * @return a {@link ObjectValidationResultNOK} if the model object is NOT valid or a {@link ClassCastException} if the model object is valid
	 */
	public ObjectValidationResultNOK<M> asNOKValidationResult();
}