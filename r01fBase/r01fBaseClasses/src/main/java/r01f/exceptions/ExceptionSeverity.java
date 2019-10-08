package r01f.exceptions;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.util.types.collections.CollectionUtils;

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
	public boolean is(final ExceptionSeverity other) {
		return this == other;
	}
	public boolean isNOT(final ExceptionSeverity other) {
		return !this.is(other);
	}
	public boolean isIn(final ExceptionSeverity... other) {
		if (CollectionUtils.isNullOrEmpty(other)) return false;
		boolean outIs = false;
		for (ExceptionSeverity s : other) {
			if (this == s) {
				outIs = true;
				break;
			}
		}
		return outIs;
	}
	public boolean isNOTIn(final ExceptionSeverity... other) {
		return !this.isIn(other);
	}
}
