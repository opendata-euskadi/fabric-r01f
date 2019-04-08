package r01f.model.persistence;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableSubType;
import r01f.exceptions.EnrichedThrowableSubTypeWrapper;
import r01f.exceptions.ExceptionSeverity;
import r01f.exceptions.Throwables;

/**
 * Persistence error codes
 */
@Accessors(prefix="_")
public enum PersistenceErrorType 
 implements EnrichedThrowableSubType<PersistenceErrorType> {
	
	SERVER_ERROR						(100 + 1),		// a server error (ie pool exhausted, no connection, etc)
	CLIENT_CANNOT_CONNECT_SERVER		(100 + 2),		// the client cannot reach the server
	ENTITY_NOT_FOUND					(100 + 3),		// the requested entity was not found
	RELATED_REQUIRED_ENTITY_NOT_FOUND	(100 + 4),		// a related required entity was not found
	ENTITY_ALREADY_EXISTS				(100 + 5),		// some entity to be created already exists
	ENTITY_NOT_VALID					(100 + 6),		// the entity is not valid: it cannot be persisted
	OPTIMISTIC_LOCKING_ERROR			(100 + 7),		// the entity to be persisted was modified by other client/thread so the version stored at the db does NOT match the one sent by client
	ILLEGAL_STATUS						(100 + 8),		// the entity to be persisted is in an illegal status in the db so the persistence operation cannot continue
	BAD_REQUEST_DATA					(100 + 9),		// the request data is not enougth to execute the persistence operation
	UNKNOWN								(100);
	
	public static final transient int PERSISTENCE = 100;	// change enums if this changes
	
	@Getter private final int _group = PERSISTENCE;
	@Getter private final int _code;
	
	private PersistenceErrorType(final int code) {
		_code = code;
	}
	
	private static EnrichedThrowableSubTypeWrapper<PersistenceErrorType> WRAPPER = EnrichedThrowableSubTypeWrapper.create(PersistenceErrorType.class); 
	
	public static PersistenceErrorType from(final int errorCode) {
		return WRAPPER.from(PERSISTENCE,errorCode);
	}
	public static PersistenceErrorType from(final int groupCode,final int errorCode) {
		if (groupCode != PERSISTENCE) throw new IllegalArgumentException(Throwables.message("The group code for a {} MUST be {}",
																							PersistenceException.class,PERSISTENCE));
		return WRAPPER.from(PERSISTENCE,errorCode);
	}
	public static PersistenceErrorType fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	@Override
	public boolean is(final int group,final int code) {
		return WRAPPER.is(this,
						  group,code);
	}
	public boolean is(final int code) {
		return this.is(PERSISTENCE,code);
	}
	@Override
	public boolean isIn(final PersistenceErrorType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final PersistenceErrorType el) {
		return WRAPPER.is(this,el);
	}
	public boolean isServerError() {
		return this == SERVER_ERROR;
	}
	public boolean isClientError() {
		return !this.isServerError();
	}
	@Override
	public ExceptionSeverity getSeverity() {
		ExceptionSeverity outSeverity = null;
		switch(this) {
		case BAD_REQUEST_DATA:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case ENTITY_ALREADY_EXISTS:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case ENTITY_NOT_FOUND:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case ENTITY_NOT_VALID:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case ILLEGAL_STATUS:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case OPTIMISTIC_LOCKING_ERROR:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case RELATED_REQUIRED_ENTITY_NOT_FOUND:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case SERVER_ERROR:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		case CLIENT_CANNOT_CONNECT_SERVER:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		case UNKNOWN:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		default:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		}
		return outSeverity;
	}
}