package r01f.services.delegates.persistence.search;

import r01f.model.search.SearchFilter;
import r01f.securitycontext.SecurityContext;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResults.ObjectValidationResultNOK;
import r01f.validation.ObjectValidationResults.ObjectValidationResultOK;

/**
 * This interface is intended to be used at {@link SearchServicesDelegateBase} sub-types that validates
 * the filter BEFORE it's executed
 * @param <M>
 */
public interface ValidatesSearchFilter<F extends SearchFilter > {
	/**
	 * Validates the search filter BEFORE being executed
	 * If the filter is NOT valid, it MUST return a {@link ObjectValidationResultNOK} that encapsulates the reason
	 * If the filter is valid, it MUST return a {@link ObjectValidationResultOK}
	 * @param securityContext
	 * @param modelObj
	 * @return a {@link ObjectValidationResult}
	 */
	public abstract ObjectValidationResult<F> validateSearchFilter(final SecurityContext securityContext,
															 	   final F filter);
}
