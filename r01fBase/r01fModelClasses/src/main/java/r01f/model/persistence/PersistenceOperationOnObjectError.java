package r01f.model.persistence;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceMethod;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.types.collections.CollectionUtils;


@Accessors(prefix="_")
public abstract class PersistenceOperationOnObjectError<T>
       		  extends PersistenceOperationExecError<T>
    	   implements PersistenceOperationOnObjectResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE DATA
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The type of the entity subject of the requested operation 
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _objectType;
	/**
	 * Some data about the requested operation target entity such as it's oid
	 */
	@MarshallField(as="requestedOperationTarget")
	@Getter @Setter protected Map<String,String> _requestedOperationTargetEntityIdInfo;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceOperationOnObjectError(final COREServiceMethod reqOp) {
		super(reqOp);
	}
	public PersistenceOperationOnObjectError(final PersistenceRequestedOperation reqOp) {
		super(reqOp);
	}
	@SuppressWarnings("unchecked")
	public PersistenceOperationOnObjectError(final Class<?> entityType,
											 final PersistenceRequestedOperation reqOp) {
		super(reqOp);
		_objectType = (Class<T>)entityType;
	}
	@SuppressWarnings("unchecked")
	PersistenceOperationOnObjectError(final Class<?> entityType,
									  final PersistenceRequestedOperation reqOp,
		 				 			  final Throwable th) {
		super(reqOp,
			  th);
		_objectType = (Class<T>)entityType;
	}
	@SuppressWarnings("unchecked")
	PersistenceOperationOnObjectError(final Class<?> entityType,
									  final PersistenceRequestedOperation reqOp,
						 			  final COREServiceErrorType errorType,
						 			  final Throwable th) {
		super(reqOp,
			  errorType,
			  th);		// no exception
		_objectType = (Class<T>)entityType;
	}
	@SuppressWarnings("unchecked")
	PersistenceOperationOnObjectError(final Class<?> entityType,
									  final PersistenceRequestedOperation reqOp,
						 			  final String errMsg) {
		super(reqOp,
			  errMsg);
		_objectType = (Class<T>)entityType;
	}
	@SuppressWarnings("unchecked")
	PersistenceOperationOnObjectError(final Class<?> entityType,
									  final PersistenceRequestedOperation reqOp,
									  final COREServiceErrorType errType,
						 			  final String errMsg) {
		super(reqOp,
			  errType,
			  errMsg);
		_objectType = (Class<T>)entityType;
	}
	@SuppressWarnings("unchecked")
	public <E extends PersistenceOperationExecError<?>> PersistenceOperationOnObjectError(final Class<?> entityType,
																						  final PersistenceRequestedOperation reqOp,
																	 					  final E otherError) {
		super(reqOp,
			  otherError);
		_objectType = (Class<T>)entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void addTargetEntityIdInfo(final String field,final String value) {
		if (_requestedOperationTargetEntityIdInfo == null) _requestedOperationTargetEntityIdInfo = Maps.newHashMap();
		_requestedOperationTargetEntityIdInfo.put(field,value);
	}
	/**
	 * @return any info about the target entity such as it's oid 
	 */
	public String getTargetEntityIdInfo() {
		String outIdInfo = null;
		if (CollectionUtils.hasData(_requestedOperationTargetEntityIdInfo)) {
			StringBuilder sb = new StringBuilder();
			for (Iterator<Map.Entry<String,String>> meIt =_requestedOperationTargetEntityIdInfo.entrySet().iterator(); meIt.hasNext(); ) {
				Map.Entry<String,String> me = meIt.next();
				sb.append(me.getKey())
				  .append("=")
				  .append(me.getValue());
				if (meIt.hasNext()) sb.append(", ");
			}
			outIdInfo = sb.toString();
		}
		return outIdInfo != null ? outIdInfo : "unknown";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if it was an error due to the client sending a version number that does NOT match the db-stored one (see Optimistic Locking)
	 */
	public boolean wasBecauseAnOptimisticLockingError() {
		return _errorType.is(PersistenceServiceErrorTypes.OPTIMISTIC_LOCKING_ERROR);
	}
	/**
	 * @return true if it was a client bad request due to the requested entity was NOT found
	 */
	public boolean wasBecauseClientRequestedEntityWasNOTFound() {
		return _errorType.is(PersistenceServiceErrorTypes.ENTITY_NOT_FOUND);
	}
	/**
	 * @return true if it was a client bad request due to the requested entity already exists and a create operation was issued
	 */
	public boolean wasBecauseClientRequestedEntityAlreadyExists() {
		return _errorType.is(PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS);
	}
	/**
	 * @return true if it was a client bad request due to a required related entity was NOT found
	 */
	public boolean wasBecauseClientRequestedEntityRequiredRelatedEntityNOTFound() {
		return _errorType.is(PersistenceServiceErrorTypes.RELATED_REQUIRED_ENTITY_NOT_FOUND);
	}
	/**
	 * @return true if it was a client bad request due to some validation error in the entity
	 */
	public boolean wasBecauseClientRequestedEntityValidationErrors() {
		return _errorType.is(PersistenceServiceErrorTypes.ENTITY_NOT_VALID);
	}
	/**
	 * @return true if it was because the entity's persisted status is NOT valid 
	 */
	public boolean wasBecauseClientRequestedEntityWasInAnIllegalStatus() {
		return _errorType.is(PersistenceServiceErrorTypes.ILLEGAL_STATUS);
	}
}
