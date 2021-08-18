package r01f.validation;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResults.ObjectValidationErrorSourceID;
import r01f.validation.ObjectValidationResults.ObjectValidationResultNOK;
import r01f.validation.ObjectValidationResults.ObjectValidationResultOK;

/**
 * Builder for {@link ObjectValidationResult} implementing types: {@link ObjectValidationResultNOK} and {@link ObjectValidationResultOK}
 * <pre class='brush:java'>
 * 		ObjectValidationResultNOK validNOK = ObjectValidationResultBuilder.on(modelObj)
 * 																					.isNotValidBecause("blah blah");
 * 		ObjectValidationResultOK validOK = ObjectValidationResultBuilder.on(modelObj)
 * 																				  .isValid();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ObjectValidationResultBuilder 
  		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <M> ObjectValidationResultNOK<M> isNotValidBecauseNull(final String reason,final Object... args) {
		return new ObjectValidationResultNOK<>(null,
											   Strings.customized(reason,args),
											   null);	// no error sources
	}
	public static <M> ObjectValidationResultNOK<M> isNotValidBecauseNull() {
		return ObjectValidationResultBuilder.isNotValidBecauseNull("Not valid because the object is null");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <M> ObjectValidationResultBuilderStep<M> on(final M Object) {
		return new ObjectValidationResultBuilder() {/* ignore */}
						.new ObjectValidationResultBuilderStep<>(Object);
	}
	@RequiredArgsConstructor
	public final class ObjectValidationResultBuilderStep<M> {
		private final M _modelObj;
		private int _errorCode = -1;
		
		public ObjectValidationResultOK<M> isValid() {
			return new ObjectValidationResultOK<>(_modelObj);
		}
		public ObjectValidationResultBuilderStep<M> notValidwithErrorCode(final int code) {
			_errorCode = code;
			return this;
		}
		public ObjectValidationResultNOK<M> isNotValidBecause(final Collection<ObjectValidationErrorSourceID> errorSources,
															  final String reason,final Object... args) {
			return new ObjectValidationResultNOK<>(_modelObj,
												   _errorCode,
												   Strings.customized(reason,args),
												   errorSources);		
		}
		public ObjectValidationResultNOK<M> isNotValidBecause(final ObjectValidationErrorSourceID errorSource,
															  final String reason,final Object... args) {
			return new ObjectValidationResultNOK<>(_modelObj,
												   _errorCode,
												   Strings.customized(reason,args),
												   errorSource != null ? Lists.newArrayList(errorSource) : null);
		}
		public ObjectValidationResultNOK<M> isNotValidBecause(final String reason,final Object... args) {
			return new ObjectValidationResultNOK<>(_modelObj,
												   _errorCode,Strings.customized(reason,args),
												   null);		// no error sources
		}
		public ObjectValidationResultNOK<M> isNotValidBecause(final ObjectValidationResultNOK<?> otherValid) {
			return new ObjectValidationResultNOK<>(_modelObj,
												   _errorCode,
												   Strings.customized("Error={} on {} object",
																   	  otherValid.getReason(),
																   	  otherValid.getValidatedObject() != null ? otherValid.getValidatedObject().getClass() : "null"),
												   otherValid.getErrorSources());
		}
		public ObjectValidationResult<M> combine(@SuppressWarnings("unchecked") final Validates<M>... validators) {
			Collection<ObjectValidationResult<M>> validResults = null;
			if (CollectionUtils.hasData(validators)) {
				validResults = FluentIterable.from(validators)
									.transform(new Function<Validates<M>,ObjectValidationResult<M>>() {
														@Override
														public ObjectValidationResult<M> apply(final Validates<M> validates) {
															return validates.validate(_modelObj);
														}
											   })
									.toList();
			}
			return this.combine(validResults);
		}
		/**
		 * Combines the result of individual validations
		 * @param validations
		 * @return
		 */
		public ObjectValidationResult<M> combine(@SuppressWarnings("unchecked") final ObjectValidationResult<M>... validations) {
			if (CollectionUtils.isNullOrEmpty(validations)) return this.isValid();
			return this.combine(FluentIterable.from(validations)
										.filter(new Predicate<ObjectValidationResult<M>>() {
														@Override
														public boolean apply(final ObjectValidationResult<M> valid) {
															return valid != null;
														}
												})
										.toList());
		}
		/**
		 * Combines the result of individual validations
		 * @param validations
		 * @return
		 */
		public ObjectValidationResult<M> combine(final Collection<ObjectValidationResult<M>> validations) {
			ObjectValidationResult<M> outValid = this.isValid();	// by default is valid
			if (CollectionUtils.hasData(validations)) {
				for (ObjectValidationResult<M> valid : validations) {
					if (valid == null) continue;
					if (valid.isNOTValid()) {
						outValid = this.isNotValidBecause(valid.asNOKValidationResult());
						break;
					}
				}
			}
			return outValid;
		}
	}
}
