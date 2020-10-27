package r01f.util.types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.BoundType;

import r01f.types.Range;
import r01f.types.Range.RangeDef;

/**
 * Class to avoid java.time dependency in Range class.
 */
@SuppressWarnings("rawtypes")
public class RangeJavaTimeUtils {
	
	public static String getRangeClassesPatternAsString() {
		return LocalDate.class.getName() + "|" +
			   LocalDateTime.class.getName() + "|" +
			   LocalTime.class.getName();
	}
	
	public static boolean isJavaTimeClass(final Class<?> rangeClass) {
		if (rangeClass == LocalDate.class
				|| rangeClass == LocalDateTime.class
				|| rangeClass == LocalTime.class) {
			return true;
		}
		return false;
	}
	
	public static Range<LocalDate> parseLocalDateRange(final String lowerBound,final BoundType lowerBoundType,
													   final String upperBound,final BoundType upperBoundType) {
		Calendar lowerCal = Calendar.getInstance();
		lowerCal.setTime(Dates.fromMillis(Long.parseLong(lowerBound)));
		LocalDate lowerBoundDate = lowerBound != null ? LocalDate.of(lowerCal.get(Calendar.YEAR), lowerCal.get(Calendar.MONTH)+1, lowerCal.get(Calendar.DAY_OF_MONTH))
													  : null;
		Calendar upperCal = Calendar.getInstance();
		upperCal.setTime(Dates.fromMillis(Long.parseLong(upperBound)));
		LocalDate upperBoundDate = upperBound != null ? LocalDate.of(upperCal.get(Calendar.YEAR), upperCal.get(Calendar.MONTH)+1, upperCal.get(Calendar.DAY_OF_MONTH))
													  : null;
		return new Range<LocalDate>(lowerBoundDate,lowerBoundType,
									upperBoundDate,upperBoundType);
	}
	
	public static Range<LocalDateTime> parseLocalDateTimeRange(final String lowerBound,final BoundType lowerBoundType,
															   final String upperBound,final BoundType upperBoundType) {
		Calendar lowerCal = Calendar.getInstance();
		lowerCal.setTime(Dates.fromMillis(Long.parseLong(lowerBound)));
		LocalDateTime lowerBoundDate = lowerBound != null ? LocalDateTime.of(lowerCal.get(Calendar.YEAR), lowerCal.get(Calendar.MONTH)+1, lowerCal.get(Calendar.DAY_OF_MONTH), lowerCal.get(Calendar.HOUR_OF_DAY), lowerCal.get(Calendar.MINUTE), lowerCal.get(Calendar.SECOND), lowerCal.get(Calendar.MILLISECOND)*1000000)
														  : null;
		Calendar upperCal = Calendar.getInstance();
		upperCal.setTime(Dates.fromMillis(Long.parseLong(upperBound)));
		LocalDateTime upperBoundDate = upperBound != null ? LocalDateTime.of(upperCal.get(Calendar.YEAR), upperCal.get(Calendar.MONTH)+1, upperCal.get(Calendar.DAY_OF_MONTH), upperCal.get(Calendar.HOUR_OF_DAY), upperCal.get(Calendar.MINUTE), upperCal.get(Calendar.SECOND), upperCal.get(Calendar.MILLISECOND)*1000000)
														  : null;
		return new Range<LocalDateTime>(lowerBoundDate,lowerBoundType,
										upperBoundDate,upperBoundType);
	}
	private static final transient Pattern LOCAL_TIME_PATTERN = Pattern.compile("([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]):([0-9]{3})");
	private static LocalTime _localTimeFromString(final String str) {
		Matcher m = LOCAL_TIME_PATTERN.matcher(str);
		if (!m.find()) throw new IllegalStateException(str + " is not a valid time: MUST match " + LOCAL_TIME_PATTERN);
		int hour = Integer.parseInt(m.group(1));
		int minutes = Integer.parseInt(m.group(2));
		int seconds = Integer.parseInt(m.group(3));
		int milis = Integer.parseInt(m.group(4));
		return LocalTime.of(hour,minutes,seconds,milis*1000000);
	}
	public static Range<LocalTime> parseLocalTimeRange(final String lowerBound,final BoundType lowerBoundType,
													   final String upperBound,final BoundType upperBoundType) {
		LocalTime lowerBoundDate = lowerBound != null ? _localTimeFromString(lowerBound)
													  : null;
		LocalTime upperBoundDate = upperBound != null ? _localTimeFromString(upperBound)
													  : null;
		return new Range<LocalTime>(lowerBoundDate,lowerBoundType,
									upperBoundDate,upperBoundType);
	}
	
	public static RangeDef toLocalDateBoundStrings(final Range<? extends Comparable> range) {
		Date l = java.sql.Date.valueOf((LocalDate)(range.getLowerBound()));
		Date u = java.sql.Date.valueOf((LocalDate)(range.getUpperBound()));
		String lower = range.getLowerBound() != null ? Long.toString(Dates.asMillis(l)) : null;
		String upper = range.getUpperBound() != null ? Long.toString(Dates.asMillis(u)) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
		
	public static RangeDef toLocalDateTimeBoundStrings(final Range<? extends Comparable> range) {
		Date l = java.sql.Timestamp.valueOf((LocalDateTime)(range.getLowerBound()));
		Date u = java.sql.Timestamp.valueOf((LocalDateTime)(range.getUpperBound()));
		String lower = range.getLowerBound() != null ? Long.toString(Dates.asMillis(l)) : null;
		String upper = range.getUpperBound() != null ? Long.toString(Dates.asMillis(u)) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	
	public static RangeDef toLocalTimeBoundStrings(final Range<? extends Comparable> range) {
		LocalTime l = (LocalTime)(range.getLowerBound());
		LocalTime u = (LocalTime)(range.getUpperBound());
		String lower = l != null ? Strings.customized("{}:{}:{}:{}",
								   Strings.leftPad(Integer.toString(l.get(ChronoField.HOUR_OF_DAY)),2,'0'),
								   Strings.leftPad(Integer.toString(l.get(ChronoField.MINUTE_OF_HOUR)),2,'0'),
								   Strings.leftPad(Integer.toString(l.get(ChronoField.SECOND_OF_MINUTE)),2,'0'),
								   Strings.leftPad(Integer.toString(l.get(ChronoField.MILLI_OF_SECOND)),3,'0'))
								 : null;
		String upper = u != null ? Strings.customized("{}:{}:{}:{}",
								   Strings.leftPad(Integer.toString(u.get(ChronoField.HOUR_OF_DAY)),2,'0'),
								   Strings.leftPad(Integer.toString(u.get(ChronoField.MINUTE_OF_HOUR)),2,'0'),
								   Strings.leftPad(Integer.toString(u.get(ChronoField.SECOND_OF_MINUTE)),2,'0'),
								   Strings.leftPad(Integer.toString(u.get(ChronoField.MILLI_OF_SECOND)),3,'0'))
								 : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	
}
