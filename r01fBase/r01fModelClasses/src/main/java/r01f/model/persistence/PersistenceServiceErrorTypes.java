package r01f.model.persistence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.exceptions.ExceptionSeverity;
import r01f.model.services.COREServiceErrorOrigin;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceErrorTypes;

/**
 * Persistence error codes
 */
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class PersistenceServiceErrorTypes 
			  extends COREServiceErrorTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	// the client cannot reach the server
	public static COREServiceErrorType CLIENT_CANNOT_CONNECT_SERVER = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.CLIENT)
																			  .withName("CLIENT_CANNOT_CONNECT_SERVER")
																			  .coded(100,1)
																			  .severity(ExceptionSeverity.FATAL)
																			  .build();
	// the requested entity was not found
	public static COREServiceErrorType ENTITY_NOT_FOUND = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.CLIENT)
																			  .withName("ENTITY_NOT_FOUND")
																			  .coded(100,2)
																			  .severity(ExceptionSeverity.RECOVERABLE)
																			  .build();
	// a related required entity was not found
	public static COREServiceErrorType RELATED_REQUIRED_ENTITY_NOT_FOUND = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.CLIENT)
																			  .withName("RELATED_REQUIRED_ENTITY_NOT_FOUND")
																			  .coded(100,3)
																			  .severity(ExceptionSeverity.RECOVERABLE)
																			  .build();
	// some entity to be created already exists
	public static COREServiceErrorType ENTITY_ALREADY_EXISTS = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.CLIENT)
																			  .withName("ENTITY_ALREADY_EXISTS")
																			  .coded(100,4)
																			  .severity(ExceptionSeverity.RECOVERABLE)
																			  .build();	
	// the entity is not valid: it cannot be persisted
	public static COREServiceErrorType ENTITY_NOT_VALID = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.CLIENT)
																			  .withName("ENTITY_NOT_VALID")
																			  .coded(100,5)
																			  .severity(ExceptionSeverity.RECOVERABLE)
																			  .build();
	// the entity to be persisted was modified by other client/thread so the version stored at the db does NOT match the one sent by client
	public static COREServiceErrorType OPTIMISTIC_LOCKING_ERROR = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.SERVER)
																			  .withName("OPTIMISTIC_LOCKING_ERROR")
																			  .coded(100,6)
																			  .severity(ExceptionSeverity.RECOVERABLE)
																			  .build();	
	// the entity to be persisted is in an illegal status in the db so the persistence operation cannot continue
	public static COREServiceErrorType ILLEGAL_STATUS = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.SERVER)
																			  .withName("ILLEGAL_STATUS")
																			  .coded(100,7)
																			  .severity(ExceptionSeverity.FATAL)
																			  .build();
}