package r01f.exceptions;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Exception severity
 */
@GwtIncompatible
@Accessors(prefix="_")
public enum ExceptionSeverity {
	DEFAULT		(0), 
	RECOVERABLE	(1),
	FATAL		(2),
	DEVELOPER	(3);
	
	@Getter int _level;
	private ExceptionSeverity(final int level) {
		_level = level;
	}
	public boolean isMoreSeriousThan(final ExceptionSeverity other) {
		return other != null ? this.getLevel() > other.getLevel()
							 : false;
	}
}
