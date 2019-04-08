package r01f.model.persistence;

import java.util.Date;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDOK;
import r01f.model.persistence.PersistenceErrorType;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.DBEntityToModelObjectTransformerBuilder;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.types.url.Url;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.validation.ObjectValidationResults.ObjectValidationResultNOK;

@GwtIncompatible
public class CRUDResultForSingleEntityBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
	public static class CRUDResultBuilderForErrorStep<T> {
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		protected final PersistenceRequestedOperation _requestedOp;
		
		public CRUDResultBuilderForErrorAboutStep<T> because(final Throwable th) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
							    				th);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
												 			 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> because(final CRUDError<?> otherCRUDError) {
			CRUDError<T> err = new CRUDError<T>(_entityType,
												otherCRUDError);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseClientCannotConnectToServer(final Url serverUrl) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
											    _entityType,
							    				Strings.customized("Cannot connect to server at {}",serverUrl),PersistenceErrorType.CLIENT_CANNOT_CONNECT_SERVER);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseServerError(final String errData,final Object... vars) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
											    _entityType,
												Strings.customized(errData,vars),PersistenceErrorType.SERVER_ERROR);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseClientError(final PersistenceErrorType errorType,
																		final String msg,final Object... vars) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
												Strings.customized(msg,vars),errorType);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseClientBadRequest(final String msg,final Object... vars) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
												Strings.customized(msg,vars),PersistenceErrorType.BAD_REQUEST_DATA);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseClientBadRequest(final ObjectValidationResultNOK<?> valid) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
												valid.getReason(),PersistenceErrorType.BAD_REQUEST_DATA);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseClientRequestedEntityWasNOTFound() {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
							   					PersistenceErrorType.ENTITY_NOT_FOUND);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseRequiredRelatedEntityWasNOTFound(final String msg,final Object... vars) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
												Strings.customized(msg,vars),PersistenceErrorType.RELATED_REQUIRED_ENTITY_NOT_FOUND);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);		
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
	public static class CRUDResultBuilderForErrorAboutStep<T> { 
		protected final SecurityContext _securityContext;
		protected final CRUDError<T> _err;
		
		public CRUDError<T> build() {
			return _err;
		}
		public CRUDResultBuilderForErrorExtErrorCodeStep<T> about(final String meta,final String value) {
			_err.addTargetEntityIdInfo(meta,value);
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																    _err);
		}
		public <O extends OID> CRUDResultBuilderForErrorExtErrorCodeStep<T> about(final O entityOid) {
			if (entityOid  != null) {
				_err.addTargetEntityIdInfo("oid",entityOid.asString());
			}
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																    _err);
		}
		public CRUDResultBuilderForErrorExtErrorCodeStep<T> about(final T entity) {
			_err.setTargetEntity(entity);
			if (entity instanceof HasOID) {
				HasOID<?> hasOid = (HasOID<?>)entity;
				_err.addTargetEntityIdInfo("oid",(hasOid.getOid() != null ? hasOid.getOid().asString() : "null"));
			}
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																	_err);
		}
		public CRUDResultBuilderForErrorExtErrorCodeStep<T> about(final VersionIndependentOID oid) {
			_err.addTargetEntityIdInfo("versionIndependentOid",oid.asString());
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																	_err);
		}
		public CRUDResultBuilderForErrorExtErrorCodeStep<T> about(final VersionIndependentOID oid,final VersionOID version) {
			_err.addTargetEntityIdInfo("versionIndependentOid",oid.asString());
			_err.addTargetEntityIdInfo("version",version.asString());
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																	_err);
		}
		public CRUDResultBuilderForErrorExtErrorCodeStep<T> about(final VersionIndependentOID oid,final Date date) {
			_err.addTargetEntityIdInfo("versionIndependentOid",oid.asString());
			_err.addTargetEntityIdInfo("date",Dates.epochTimeStampAsString(date.getTime()));
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																	_err);
		}
		public CRUDResultBuilderForErrorExtErrorCodeStep<T> aboutWorkVersion(final VersionIndependentOID oid) {
			_err.addTargetEntityIdInfo("versionIndependentOid",oid.asString());
			_err.addTargetEntityIdInfo("version","workVersion");
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																    _err);
		}
		public CRUDResultBuilderForErrorExtErrorCodeStep<T> about(final VersionIndependentOID oid,final Object version) {
			_err.addTargetEntityIdInfo("versionIndependentOid",oid.asString());
			if (version instanceof Date) { 
				_err.addTargetEntityIdInfo("date",Dates.epochTimeStampAsString(((Date)version).getTime()));
			} else if (version instanceof VersionOID) {
				_err.addTargetEntityIdInfo("version",((VersionOID)version).asString());
			} else if (version instanceof String || version == null) {
				_err.addTargetEntityIdInfo("version","workVersion");	
			}
			return new CRUDResultBuilderForErrorExtErrorCodeStep<T>(_securityContext,
																	_err);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
	public static class CRUDResultBuilderForErrorExtErrorCodeStep<T> { 
		protected final SecurityContext _securityContext;
		protected final CRUDError<T> _err;
		
		public CRUDError<T> buildWithExtendedErrorCode(final int extErrCode) {
			_err.setExtendedErrorCode(extErrCode);
			return _err;
		}
		public CRUDError<T> build() {
			return _err;
		}
	}
	private abstract static class PersistenceCRUDResultBuilderForMutatorErrorBase<T>
			              extends CRUDResultBuilderForErrorStep<T> {
		public PersistenceCRUDResultBuilderForMutatorErrorBase(final SecurityContext securityContext,
								   	   				   		   final Class<T> entityType,
								   	   				   		   final PersistenceRequestedOperation reqOp) {
			super(securityContext,
				  entityType,
				  reqOp);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseOptimisticLockingError() {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
							    				PersistenceErrorType.OPTIMISTIC_LOCKING_ERROR);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseClientSentEntityValidationErrors(final ObjectValidationResultNOK<T> validNOK) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
							    				validNOK.getReason(),PersistenceErrorType.ENTITY_NOT_VALID);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
	}
	public static class CRUDResultBuilderForCreateError<T>
		 extends PersistenceCRUDResultBuilderForMutatorErrorBase<T> {
		public CRUDResultBuilderForCreateError(final SecurityContext securityContext,
										  	   final Class<T> entityType) {
			super(securityContext,
				  entityType,
				  PersistenceRequestedOperation.CREATE);
		}
		public CRUDResultBuilderForCreateError(final SecurityContext securityContext,
								  	   		   final Class<T> entityType,
								  	   		   final PersistenceRequestedOperation reqOp) {
			super(securityContext,
				  entityType,
				  reqOp);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseClientRequestedEntityAlreadyExists() {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
											    PersistenceErrorType.ENTITY_ALREADY_EXISTS);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
	}
	public static class CRUDResultBuilderForUpdateError<T>
		 extends PersistenceCRUDResultBuilderForMutatorErrorBase<T> {

		public CRUDResultBuilderForUpdateError(final SecurityContext securityContext,
										  	   final Class<T> entityType) {
			super(securityContext,
				  entityType,
				  PersistenceRequestedOperation.UPDATE);
		}
		public CRUDResultBuilderForErrorAboutStep<T> becauseTargetEntityWasInAnIllegalStatus(final String msg,final Object... vars) {
			CRUDError<T> err = new CRUDError<T>(_requestedOp,
												_entityType,
												Strings.customized(msg,vars),PersistenceErrorType.ILLEGAL_STATUS);
			return new CRUDResultBuilderForErrorAboutStep<T>(_securityContext,
															 err);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUCCESS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
	public static class CRUDResultBuilderForOKStep<T> {
		
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		protected final PersistenceRequestedOperation _requestedOp;
		protected final PersistencePerformedOperation _performedOp;
		
		public CRUDOK<T> entity(final T entity) {
			CRUDOK<T> outPersistenceOpResult = new CRUDOK<T>(_requestedOp,_performedOp,
															 _entityType,
											 				 entity);
			return outPersistenceOpResult;			
		}
		public <DB extends DBEntity> CRUDResultBuilderForOKTransformerStep<DB,T> dbEntity(final DB dbEntity) {			
			return new CRUDResultBuilderForOKTransformerStep<DB,T>(_securityContext,
															 	   _entityType,
																   _requestedOp,_performedOp,
																   dbEntity);	
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
	public static class CRUDResultBuilderForOKTransformerStep<DB extends DBEntity,
															  T> {
		protected final SecurityContext _securityContext;
		protected final Class<T> _entityType;
		protected final PersistenceRequestedOperation _requestedOp;
		protected final PersistencePerformedOperation _performedOp;
		protected final DB _dbEntity;
		
		public <M extends PersistableModelObject<? extends OID>> CRUDOK<M> transformedToModelObjectUsing(final TransformsDBEntityIntoModelObject<DB,M> dbEntityToModelObjectTransformer) {			
			return this.transformedToModelObjectUsing(DBEntityToModelObjectTransformerBuilder.createFor(_securityContext,
																										dbEntityToModelObjectTransformer));
		}
		@SuppressWarnings("unchecked")
		public <M extends PersistableModelObject<? extends OID>> CRUDOK<M> transformedToModelObjectUsing(final Function<DB,M> transformer) {
			Function<DB,M> dbEntityToModelObjConverter = DBEntityToModelObjectTransformerBuilder.createFor(_securityContext,
																										   transformer);
			M obj = dbEntityToModelObjConverter.apply(_dbEntity);
			CRUDOK<M> outPersistenceOpResult = new CRUDOK<M>(_requestedOp,_performedOp,
															 (Class<M>)_entityType,
				 						     				 obj);
			return outPersistenceOpResult;
		}
	}
}