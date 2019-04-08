package r01f.model.persistence;

import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.OID;
import r01f.guids.OIDs;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.patterns.IsBuilder;
import r01f.persistence.db.DBEntity;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;

/**
 * Builder type for {@link FindOIDsResult}-implementing types:
 * <ul>
 * 		<li>A successful FIND operation result: {@link FindOIDsOK}</li>
 * 		<li>An error on a FIND operation execution: {@link FindOIDsError}</li>
 * </ul>
 * If the find operation execution was successful and oids are returned:
 * <pre class='brush:java'>
 * 		FindOIDsOK<MyEntityOID> opOK = FindOIDsResultBuilder.using(securityContext)
 * 											    		    .on(MyEntity.class)
 * 												  	   			.foundEntitiesWithOids(myEntityOids);
 * </pre>
 * If an error is raised while executing an entity find operation:
 * <pre class='brush:java'>
 * 		FindError<MyEntityOID> opError = FindOIDsResultBuilder.using(securityContext)
 * 													   		  .on(MyEntity.class)
 * 														   			.errorFindingOids()
 * 																		.causedBy(error);
 * </pre>
 */
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FindOIDsResultBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final HasTypesMetaData _hasTypesMetaData = TypeMetaDataInspector.singleton();
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static FindOIDsResultBuilderEntityStep using(final SecurityContext securityContext) {
		return new FindOIDsResultBuilder() {/* ignore */}
						.new FindOIDsResultBuilderEntityStep(securityContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class FindOIDsResultBuilderEntityStep {
		private final SecurityContext _securityContext;
		
		public FindOIDsResultBuilderOperationStep on(final Class<? extends PersistableModelObject<? extends OID>> entityType) {
			return new FindOIDsResultBuilderOperationStep(_securityContext,
														  entityType);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class FindOIDsResultBuilderOperationStep {
		protected final SecurityContext _securityContext;
		protected final Class<? extends PersistableModelObject<? extends OID>> _modelObjType;

		
		//  --------- ERROR
		public FindOIDsResultBuilderForError errorFindingOids() {
			return new FindOIDsResultBuilderForError(_securityContext,
													 _modelObjType);	
		}
		// ---------- SUCCESS FINDING 
		public <DB extends DBEntity,O extends PersistableObjectOID> FindOIDsOK<O> foundDBEntities(final Collection<DB> dbEntities,
															 					 				  final Function<DB,O> transformFunction) {
			FindOIDsOK<O> outFoundOids = new FindOIDsOK<O>();
			outFoundOids.setModelObjectType(_modelObjType);
			outFoundOids.setRequestedOperation(PersistenceRequestedOperation.FIND);
			outFoundOids.setPerformedOperation(PersistencePerformedOperation.FOUND);
			outFoundOids.setOperationExecResult(FluentIterable.from(dbEntities)
															  .transform(transformFunction)
															  .toList());	
			return outFoundOids;
		}
		@SuppressWarnings("unchecked")
		public <O extends PersistableObjectOID> FindOIDsOK<O> foundEntitiesWithOids(final Collection<O> oids) {
			Class<? extends PersistableModelObject<O>> modelObjType = (Class<? extends PersistableModelObject<O>>)_modelObjType;
			Class<O> oidType = _guessOidType(_modelObjType);
			
			FindOIDsOK<O> outFoundOids = new FindOIDsOK<O>(modelObjType,oidType);
			outFoundOids.setRequestedOperation(PersistenceRequestedOperation.FIND);
			outFoundOids.setPerformedOperation(PersistencePerformedOperation.FOUND);
			outFoundOids.setOperationExecResult(oids);	
			return outFoundOids;
		}
		@SuppressWarnings("unchecked")
		public <O extends PersistableObjectOID> FindOIDsOK<O> noEntityFound() {
			Class<? extends PersistableModelObject<O>> modelObjType = (Class<? extends PersistableModelObject<O>>)_modelObjType;
			Class<O> oidType = _guessOidType(_modelObjType);
			
			FindOIDsOK<O> outFoundOids = new FindOIDsOK<O>(modelObjType,oidType);
			outFoundOids.setRequestedOperation(PersistenceRequestedOperation.FIND);
			outFoundOids.setPerformedOperation(PersistencePerformedOperation.FOUND);
			outFoundOids.setOperationExecResult(Lists.<O>newArrayList());	// no data found
			return outFoundOids;
		}
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class FindOIDsResultBuilderForError {
		protected final SecurityContext _securityContext;
		protected final Class<? extends PersistableModelObject<? extends OID>> _modelObjType;
		
		@SuppressWarnings("unchecked")
		public <O extends PersistableObjectOID> FindOIDsError<O> causedBy(final Throwable th) {
			Class<? extends PersistableModelObject<O>> modelObjType = (Class<? extends PersistableModelObject<O>>)_modelObjType;
			Class<O> oidType = _guessOidType(_modelObjType);
			
			return new FindOIDsError<O>(modelObjType,oidType,
										th);
		}
		@SuppressWarnings("unchecked")
		public <O extends PersistableObjectOID> FindOIDsError<O> causedBy(final String cause) {
			Class<? extends PersistableModelObject<O>> modelObjType = (Class<? extends PersistableModelObject<O>>)_modelObjType;
			Class<O> oidType = _guessOidType(_modelObjType);
			
			return new FindOIDsError<O>(modelObjType,oidType,
										cause,
										PersistenceErrorType.SERVER_ERROR);
		}
		@SuppressWarnings("unchecked")
		public <O extends PersistableObjectOID> FindOIDsError<O> causedByClientBadRequest(final String msg,final Object... vars) {
			Class<? extends PersistableModelObject<O>> modelObjType = (Class<? extends PersistableModelObject<O>>)_modelObjType;
			Class<O> oidType = _guessOidType(_modelObjType);
			
			FindOIDsError<O> outError = new FindOIDsError<O>(modelObjType,oidType,
											     	 		 Strings.customized(msg,vars),			// the error message
											     	 		 PersistenceErrorType.BAD_REQUEST_DATA);	// is a client error?
			return outError;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private <O extends PersistableObjectOID> Class<O> _guessOidType(final Class<? extends PersistableModelObject<? extends OID>> modelObjType) {
		// [1] - Try to guess the oid type using the type info
		Class<O> oidType = OIDs.oidTypeOrNullFor(modelObjType);
		// [2] - Try to guess the oid type using the metadata info
		if (oidType == null) oidType = (Class<O>)_hasTypesMetaData.getTypeMetaDataFor(modelObjType)
														 .findFieldByIdOrThrow(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID)
														  		.getRawFieldType();
		return oidType;
	}
}
