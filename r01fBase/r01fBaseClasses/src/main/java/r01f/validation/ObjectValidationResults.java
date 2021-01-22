package r01f.validation;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

/**
 * Model object validation result
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ObjectValidationResults {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	static class ObjectValidationResultBase<M> 
	  implements ObjectValidationResult<M> {
		@Getter private final M _validatedObject;
		@Getter private final boolean _valid;
		
		@Override
		public boolean isNOTValid() {
			return !_valid;
		}
		@Override
		public ObjectValidationResultOK<M> asOKValidationResult() {
			return (ObjectValidationResultOK<M>)this;
		}
		@Override
		public ObjectValidationResultNOK<M> asNOKValidationResult() {
			return (ObjectValidationResultNOK<M>)this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static class ObjectValidationResultOK<M> 
				extends ObjectValidationResultBase<M> {
		ObjectValidationResultOK(final M modelObject) {
			super(modelObject,
				  true);	// it's valid
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	public static class ObjectValidationResultNOK<M> 
				extends ObjectValidationResultBase<M> {
		@Getter private final int _code;			// a code
		@Getter private final String _reason;
		@Getter private final Collection<ObjectValidationErrorSourceID> _errorSources; 
		
		ObjectValidationResultNOK(final M modelObject,
								  final int code,final String reason,
								  final Collection<ObjectValidationErrorSourceID> errorSources) {
			super(modelObject,
				  false);	// it's NOT valid
			_code = code;
			_reason = reason;
			_errorSources = errorSources;
		}
		
		ObjectValidationResultNOK(final M modelObject,
								  final String reason,
								  final Collection<ObjectValidationErrorSourceID> errorSources) {
			this(modelObject,
				 -1,reason,			// no error code
				 errorSources);
		}
		public ObjectValidationErrorSourceID getSingleExpectedErrorSource() {
			return CollectionUtils.hasData(_errorSources) ? CollectionUtils.<ObjectValidationErrorSourceID>pickOneAndOnlyElement(_errorSources,
																																 "There's more than a single error source at the validation result!!")
														  : null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="objValidationErrorSourceId")
	@Immutable
	public static class ObjectValidationErrorSourceID
	            extends OIDBaseMutable<String> { 	// normally this should extend OIDBaseImutable BUT it MUST have a default no-args constructor to be serializable
		private static final long serialVersionUID = -4305395894867558460L;
		public ObjectValidationErrorSourceID() {
			/* default no args constructor for serialization purposes */
		}
		public ObjectValidationErrorSourceID(final String id) {
			super(id);
		}
		public static ObjectValidationErrorSourceID forId(final String id) {
			return new ObjectValidationErrorSourceID(id);
		}
		public static ObjectValidationErrorSourceID valueOf(final String id) {
			return ObjectValidationErrorSourceID.forId(id);
		}
		public static ObjectValidationErrorSourceID fromString(final String id) {
			return ObjectValidationErrorSourceID.forId(id);
		}
	}
	
}
