package r01f.types;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

/**
 * Represents some time interval o lapse
 * Usage:
 * <pre class='brush:java'>
 * 		TimeLapse timeLapse = TimeLapse.createFor("5s");
 * 		long milis = timeLapse.get();
 * </pre>
 */
@MarshallType(as="timeLapse")
public class TimeLapse
  implements CanBeRepresentedAsString,
  			 Serializable {

	private static final long serialVersionUID = 8201041020863160970L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The time lapse in milliseconds
	 */
	private final long _timeLapse;
	/**
	 * The {@link TimeUnit} used to define the lapse
	 */
	private final TimeUnit _definedBy;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public TimeLapse(final long millis) {
		this(millis,TimeUnit.MILLISECONDS);
	}
	public TimeLapse(final long millis,final TimeUnit unit) {
		_timeLapse = millis;
		_definedBy = unit;
	}
	@GwtIncompatible("uses regexp")
	public TimeLapse(final String timeSpec) {
		MillisAndUnit millisAndUnit = _parseTimeLapseSpec(timeSpec);
		_timeLapse = millisAndUnit.millis;
		_definedBy = millisAndUnit.unit;
	}
	@GwtIncompatible("uses regexp")
	public static TimeLapse valueOf(final String timeSpec) {
		return new TimeLapse(timeSpec);
	}
	/**
	 * Creates a {@link TimeLapse} from milis
	 * @param millis the milis
	 * @return
	 */
	public static TimeLapse createFor(final long milis) {
		return new TimeLapse(milis);
	}
	/**
	 * Creates a {@link TimeLapse} from units
	 * @param units
	 * @param unit
	 * @return
	 */
	public static TimeLapse createFor(final long millis,final TimeUnit unit) {
		return new TimeLapse(millis,unit);
	}
	/**
	 * Creates a {@link TimeLapse} from a textual spec like some of the following
	 * <ul>
	 * 		<li>1d for one day</li>
	 * 		<li>1h for one hour</li>
	 * 		<li>30m for 30 minutes</li>
	 * 		<li>100s for 100 seconds</li>
	 * </ul>
	 * @param timeSpec the spec
	 * @return
	 */
	@GwtIncompatible("uses regexp")
	public static TimeLapse createFor(final String timeSpec) {
		MillisAndUnit millisAndUnit = _parseTimeLapseSpec(timeSpec);
		return new TimeLapse(millisAndUnit.millis,millisAndUnit.unit);
	}
	/**
	 * Creates a {@link TimeLapse} from a textual spec like some of the following
	 * <ul>
	 * 		<li>1d for one day</li>
	 * 		<li>1h for one hour</li>
	 * 		<li>30m for 30 minutes</li>
	 * 		<li>100s for 100 seconds</li>
	 * </ul>
	 * @param timeSpec the spec
	 * @return
	 */
	@GwtIncompatible("uses regexp")
	public static TimeLapse of(final String timeSpec) {
		return TimeLapse.createFor(timeSpec);
	}
	public static TimeLapse of(final long millis) {
		return new TimeLapse(millis);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public long asMilis() {
		return _timeLapse;
	}
	@Override
	public String asString() {
		return _toString(_timeLapse,_definedBy);
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASH CODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof TimeLapse)) return false;
		
		TimeLapse other = (TimeLapse)obj;
		return _timeLapse == other.asMilis();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_timeLapse);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _toString(final long millis,final TimeUnit unit) {
		String unitStr = "";	// millis
		long lapse = 0;
		switch(unit) {
		case DAYS:
			unitStr = "d";
			lapse = millis / (24l * 60l * 60l * 1000l);
			break;
		case HOURS:
			unitStr = "h";
			lapse = millis / (60l * 60l * 1000l);
			break;
		case MINUTES:
			unitStr = "m";
			lapse = millis / (60l * 1000l);
			break;
		case SECONDS:
			unitStr = "s";
			lapse = millis / (1000l);
			break;
		case MILLISECONDS:
			unitStr = "";
			break;
		case MICROSECONDS:
			throw new IllegalArgumentException();
		case NANOSECONDS:
			throw new IllegalArgumentException();
		default:
			break;
		}
		return Strings.customized("{}{}",
							 	  lapse,unitStr);
	}
	@RequiredArgsConstructor
	private static class MillisAndUnit {
		private final long millis;
		private final TimeUnit unit;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	@GwtIncompatible
	private static final Pattern TIMELAPSE_PATTERN = Pattern.compile("\\s*([0-9]+)\\s*(s|m|h|d)?\\s*");
	
	@GwtIncompatible("uses regexp")
	static MillisAndUnit _parseTimeLapseSpec(final String periodSpec) {
		long outMillis = -1;
		TimeUnit outUnit = TimeUnit.MILLISECONDS;
		Matcher m = TIMELAPSE_PATTERN.matcher(periodSpec);
		if (m.matches()) {
			long periodValue = Long.parseLong(m.group(1));
			String unitStr = m.groupCount() == 2 ? m.group(2) : null;
			if (Strings.isNullOrEmpty(unitStr)) {
				outMillis = periodValue;							// millis
				outUnit = TimeUnit.MILLISECONDS;				
			} else if (unitStr.equalsIgnoreCase("s")) {
				outMillis = periodValue * 1000l;					// 1 sg = 1000 millis
				outUnit = TimeUnit.SECONDS;
			} else if (unitStr.equalsIgnoreCase("m")) {
				outMillis = periodValue * 60l * 1000l;				// 1 min = 60 sg = 60 * 1000 millis
				outUnit = TimeUnit.MINUTES;
			} else if (unitStr.equalsIgnoreCase("h")) {
				outMillis = periodValue * 60l * 60l * 1000l;		// 1 h = 60 min = 60 * 60 * 1000 millis
				outUnit = TimeUnit.HOURS;
			}  else if (unitStr.equalsIgnoreCase("d")) {
				outMillis = periodValue * 24l * 60l * 60l * 1000l;	// 1 d = 24h = 24 * 60 * 60 * 1000 millis
				outUnit = TimeUnit.DAYS;
			}
		} else if (Numbers.isLong(periodSpec)) {
			outMillis = Long.parseLong(periodSpec);
		}
		return new MillisAndUnit(outMillis,outUnit);
	}
}
