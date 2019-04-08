package r01f.services;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableSubType;
import r01f.exceptions.EnrichedThrowableSubTypeWrapper;
import r01f.exceptions.ExceptionSeverity;
import r01f.exceptions.Throwables;
import r01f.model.persistence.PersistenceException;

@Accessors(prefix="_")
public enum ServiceExceptionType
 implements EnrichedThrowableSubType<ServiceExceptionType> {
	SERVER(10+1),
	CLIENT(10+2);
	
	public static final transient int SERVICE = 10;	// change enums if this changes
	
	@Getter private final int _group = SERVICE;
	@Getter private final int _code;
	
	private ServiceExceptionType(final int code) {
		_code = code;
	}
	
	private static EnrichedThrowableSubTypeWrapper<ServiceExceptionType> WRAPPER = EnrichedThrowableSubTypeWrapper.create(ServiceExceptionType.class); 
	
	public static ServiceExceptionType from(final int errorCode) {
		return WRAPPER.from(SERVICE,errorCode);
	}
	public static ServiceExceptionType from(final int groupCode,final int errorCode) {
		if (groupCode != SERVICE) throw new IllegalArgumentException(Throwables.message("The group code for a {} MUST be {}",
																					    PersistenceException.class,SERVICE));
		return WRAPPER.from(SERVICE,errorCode);
	}
	public static ServiceExceptionType from(final Throwable th) {
		ServiceExceptionType outType = null;
		if (th instanceof IllegalArgumentException) {
			outType = CLIENT;
		} else {
			outType = SERVER;
		}
		return outType;
	}
	@Override
	public boolean is(final int group,final int code) {
		return WRAPPER.is(this,
						  group,code);
	}
	public boolean is(final int code) {
		return this.is(SERVICE,code);
	}
	@Override
	public boolean isIn(final ServiceExceptionType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final ServiceExceptionType el) {
		return WRAPPER.is(this,el);
	}
	public boolean isServerError() {
		return this == SERVER;
	}
	public boolean isClientError() {
		return !this.isServerError();
	}
	@Override
	public ExceptionSeverity getSeverity() {
		ExceptionSeverity outSeverity = null;
		switch(this) {
		case SERVER:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		case CLIENT:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		default:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		}
		return outSeverity;
	}
}