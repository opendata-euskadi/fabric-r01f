package r01f.types;

import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Dates;
import r01f.util.types.Strings;

/**
 * Wraps a Guava {@link com.google.common.collect.Range} in order to be serializable
 * If
 * <ul>
 * 		<li>"[" or "]" represents a lower or upper bound where the bound itself is included in the range</li>
 * 		<li>"(" or ")" represents a lower or upper bound where the bound itself is EXCLUDED from the range</li>
 * <li>
 * <pre>
 * 		(a..b)		open(C, C)
 * 		[a..b]		closed(C, C)
 * 		[a..b)		closedOpen(C, C)
 * 		(a..b]		openClosed(C, C)
 * 		(a..+oo)	greaterThan(C)
 * 		[a..+oo)	atLeast(C)
 * 		(-oo..b)	lessThan(C)
 * 		(-oo..b]	atMost(C)
 * 		(-oo..+oo)	all()
 * </pre>
 *
 * Usage:
 * <pre class='brush:java'>
 *		// Create an integer range
 *		Range<Integer> intRange = Range.open(2,3);
 *
 *		// Serialize to string
 *		String intRangeStr = intRange.toString();
 *
 *		// Convert back to range from String representation
 *		intRange = Range.parse(intRangeStr,Integer.class);
 * </pre>
 * @param <T>
 */
@Immutable
@MarshallType(as="range")
@Accessors(prefix="_")
@NoArgsConstructor
@SuppressWarnings("rawtypes")
public class Range<T extends Comparable<? super T>>
  implements CanBeRepresentedAsString,
  			 Serializable {

	private static final long serialVersionUID = -5779835775897731838L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
////////////////////////////////////////////////////////////////////////////////////////
	@GwtIncompatible
	private static final Pattern RANGE_PATTERN = Pattern.compile("(?:\\(|\\[)(.+)?\\.\\.(.+)?(?:\\)|\\])");

/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The range lower bound
	 */
	@Getter private T _lowerBound;
	/**
	 * The range upper bound
	 */
	@Getter private T _upperBound;
	/**
	 * The lower bound type
	 */
	@Getter private BoundType _lowerBoundType;
	/**
	 * The upper bound type
	 */
	@Getter private BoundType _upperBoundType;

/////////////////////////////////////////////////////////////////////////////////////////
//  NON-SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField @NotDirtyStateTrackable
	private transient com.google.common.collect.Range<T> _range;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T extends Comparable<? super T>> Range<T> wrap(final com.google.common.collect.Range<T> range) {
		return new Range<T>(range);
	}
	public Range(final com.google.common.collect.Range<T> range) {
		_range = range;
		_upperBound = range.hasUpperBound() ? range.upperEndpoint() : null;
		_lowerBound = range.hasLowerBound() ? range.lowerEndpoint() : null;

		_upperBoundType = range.hasUpperBound() ? range.upperBoundType() : BoundType.OPEN;
		_lowerBoundType = range.hasLowerBound() ? range.lowerBoundType() : BoundType.OPEN;
	}
	public Range(final T lower,
				 final T upper) {
		this(lower,BoundType.CLOSED,
			 upper,BoundType.CLOSED);
	}
	public Range(final T lower,final BoundType lowerBoundType,
				 final T upper,final BoundType upperBoundType) {
		// store the lower and upper bounds
		_lowerBound = lower;
		_upperBound = upper;

		_lowerBoundType = lowerBoundType;
		_upperBoundType = upperBoundType;

		// Create the delegate
		if (_lowerBound != null && _upperBound != null) {
			if (lowerBoundType == BoundType.OPEN && upperBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.open(_lowerBound,_upperBound);
			} else if (lowerBoundType == BoundType.OPEN && upperBoundType == BoundType.CLOSED) {
				_range = com.google.common.collect.Range.openClosed(_lowerBound,_upperBound);
			} else if (lowerBoundType == BoundType.CLOSED && upperBoundType == BoundType.CLOSED) {
				_range = com.google.common.collect.Range.closed(_lowerBound,_upperBound);
			} else if (lowerBoundType == BoundType.CLOSED && upperBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.closedOpen(_lowerBound,_upperBound);
			} else {
				throw new IllegalArgumentException("Both lower and upper bound types MUST be provided!");
			}
		} else if (_lowerBound != null) {
			if (lowerBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.greaterThan(_lowerBound);
			} else {
				_range = com.google.common.collect.Range.atLeast(_lowerBound);
			}
		} else if (_upperBound != null) {
			if (upperBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.lessThan(_upperBound);
			} else {
				_range = com.google.common.collect.Range.atMost(_upperBound);
			}
		} else {
			throw new IllegalArgumentException("Cannot create range, at least lower or upper bound SHOULD be not null");
		}
	}
	public static <T extends Comparable<? super T>> Range<T> range(final T lower,final BoundType lowerBoundType,
																   final T upper,final BoundType upperBoundType) {
		return new Range<T>(lower,lowerBoundType,
						 	upper,upperBoundType);
	}
	/**
	 * Creates an OPEN range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> open(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in an open Range");
		return new Range<T>(lower,BoundType.OPEN,
							upper,BoundType.OPEN);
	}
	/**
	 * Creates an OPEN on the lower end and CLOSED on the upper end range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> openClosed(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in an open-closed Range");
		return new Range<T>(lower,BoundType.OPEN,
							upper,BoundType.CLOSED);
	}
	/**
	 * Creates an CLOSED range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> closed(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in a closed Range");
		return new Range<T>(lower,BoundType.CLOSED,
							upper,BoundType.CLOSED);
	}
	/**
	 * Creates an CLOSED on the lower end and OPEN on the upper end range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> closedOpen(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in an closed-open Range");
		return new Range<T>(lower,BoundType.CLOSED,
							upper,BoundType.OPEN);
	}
	/**
	 * Creates a greaterThan (>) range from its lower bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> greaterThan(final T lower) {
		if (lower == null) throw new IllegalArgumentException("lower bound must be not null in an greaterThan Range");
		return new Range<T>(lower,BoundType.OPEN,
							null,null);
	}
	/**
	 * Creates a atLeast (>=) range from its lower bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> atLeast(final T lower) {
		if (lower == null) throw new IllegalArgumentException("lower bound must be not null in an atLeast Range");
		return new Range<T>(lower,BoundType.CLOSED,
							null,null);
	}
	/**
	 * Creates a lessThan (<) range from the upper bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> lessThan(final T upper) {
		if (upper == null) throw new IllegalArgumentException("upper bound must be not null in an lessThan Range");
		return new Range<T>(null,null,
							upper,BoundType.OPEN);
	}
	/**
	 * Creates a atMost (<=) range from the upper bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> atMost(final T upper) {
		if (upper == null) throw new IllegalArgumentException("upper bound must be not null in an atMost Range");
		return new Range<T>(null,null,
							upper,BoundType.CLOSED);
	}
	/**
	 * Creates a atMost (<=) range from the upper bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> all() {
		return new Range<T>(null,null,
							null,null);
	}
	/**
	 * Unsafe parse a range from its textual representation like lowerBound..upperBound
	 * It's unsafe since it can throw a {@link ClassCastException} if the dataType arg is not a {@link Comparable} type
	 * @param rangeStr
	 * @param dataType
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	@GwtIncompatible
	public static Range<?> unsafeParse(final String rangeStr,
									   final Class<?> dataType) {
		Class<? extends Comparable> comparableDataType = (Class<? extends Comparable>)dataType;
		return Range.parse(rangeStr,
						   comparableDataType);
	}
	/**
	 * Parses a range from its textual representation like lowerBound..upperBound
	 * @param rangeStr
	 * @param dataType
	 */
	@SuppressWarnings("unchecked")
	@GwtIncompatible
	public static <T extends Comparable<? super T>> Range<T> parse(final String rangeStr,
							 	 	  					   		   final Class<T> dataType) {
		Object outRange = null;

		// Errores de compilacion:  Incomparable Types
		//[javac] /softbase_ejie/aplic/r01fb/tmp/compileLib/r01fbClasses/src/r01f/types/Range.java:407:
		//incomparable types: java.lang.Class<T> and java.lang.Class<java.util.Date>
	    // [javac] 		if (dataType == java.util.Date.class || dataType == java.sql.Date.class) {

		Class<?> java_util_Date_class = java.util.Date.class;
		Class<?> java_time_LocalTime = java.time.LocalTime.class;
		Class<?> java_time_LocalDate = java.time.LocalDate.class;
		Class<?> java_time_LocalDateTime = java.time.LocalDateTime.class;
		Class<?> java_sql__Date_class = java.sql.Date.class;
		Class<?> joda_LocalTime = org.joda.time.LocalTime.class;
		Class<?> joda_LocalDate = org.joda.time.LocalDate.class;
		Class<?> joda_LocalDateTime = org.joda.time.LocalDateTime.class;
		Class<?> java_lang_Byte_class = Byte.class;
		Class<?> java_lang_Integer_class = Integer.class;
		Class<?> java_lang_Short_class = Short.class;
		Class<?> java_lang_Long_class = Long.class;
		Class<?> java_lang_Double_class = Double.class;
		Class<?> java_lang_Float_class = Float.class;


		RangeDef bounds = _parseBounds(rangeStr);
		if (dataType == java_util_Date_class|| dataType == java_sql__Date_class) {
			outRange = _parseDateRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
									   bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_LocalDate) {
			outRange = _parseLocalDateRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
											bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_LocalDateTime) {
			outRange = _parseLocalDateTimeRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
												bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_LocalTime) {
			outRange = _parseLocalTimeRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
											bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == joda_LocalDate) {
			outRange = _parseJodaLocalDateRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
												bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == joda_LocalDateTime) {
			outRange = _parseJodaLocalDateTimeRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
													bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == joda_LocalTime) {
			outRange = _parseJodaLocalTimeRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
												bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Byte_class) {
			outRange = _parseByteRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
									   bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Integer_class) {
			outRange = _parseIntRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
									  bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Short_class) {
			outRange = _parseShortRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
										bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Long_class) {
			outRange = _parseLongRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
									   bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType ==java_lang_Double_class) {
			outRange = _parseDoubleRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
										 bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Float_class) {
			outRange = _parseFloatRange(bounds.getLowerBound(),bounds.getLowerBoundType(),
										bounds.getUpperBound(),bounds.getUpperBoundType());
		} else {
			throw new IllegalArgumentException("Type " + dataType + " is NOT supported in Range");
		}
		return  (Range<T>)outRange;
	}

	@GwtIncompatible
	private static RangeDef _parseBounds(final String rangeStr) {
		RangeDef outBounds = null;
		Matcher m = RANGE_PATTERN.matcher(rangeStr);
		if (m.matches()) {
			outBounds = new RangeDef();
			if (m.groupCount() == 2) {
				if (rangeStr.startsWith("(") || rangeStr.startsWith("..")) {
					outBounds.setLowerBoundType(BoundType.OPEN);
				} else if (rangeStr.startsWith("[")) {
					outBounds.setLowerBoundType(BoundType.CLOSED);
				} else {
					throw new IllegalArgumentException(rangeStr + " is NOT a valid range string representation!");
				}
				if (rangeStr.endsWith(")") || rangeStr.endsWith("..")) {
					outBounds.setUpperBoundType(BoundType.OPEN);
				} else if (rangeStr.endsWith("]")) {
					outBounds.setUpperBoundType(BoundType.CLOSED);
				} else {
					throw new IllegalArgumentException(rangeStr + " is NOT a valid range string representation!");
				}
				outBounds.setLowerBound(m.group(1));
				outBounds.setUpperBound(m.group(2));
			} else if (rangeStr.startsWith("(..") || rangeStr.startsWith("..")) {
				outBounds.setLowerBoundType(BoundType.OPEN);
				outBounds.setUpperBound(m.group(1));
			} else if (rangeStr.startsWith("[..")) {
				outBounds.setLowerBoundType(BoundType.CLOSED);
				outBounds.setUpperBound(m.group(1));
			} else if (rangeStr.endsWith("..)") || rangeStr.endsWith("..")) {
				outBounds.setUpperBoundType(BoundType.OPEN);
				outBounds.setLowerBound(m.group(1));
			} else if (rangeStr.endsWith("..]")) {
				outBounds.setUpperBoundType(BoundType.CLOSED);
				outBounds.setLowerBound(m.group(1));
			} else {
				throw new IllegalArgumentException(rangeStr + " is NOT a valid range!");
			}
		} else {
			throw new IllegalArgumentException("The range string representation: " + rangeStr + " does NOT match the pattern " + RANGE_PATTERN.pattern());
		}
		return outBounds;
	}
	private static Range<Date> _parseDateRange(final String lowerBound,final BoundType lowerBoundType,
											   final String upperBound,final BoundType upperBoundType) {
		Date lowerBoundDate = lowerBound != null ? Dates.fromMillis(Long.parseLong(lowerBound)) : null;
		Date upperBoundDate = upperBound != null ? Dates.fromMillis(Long.parseLong(upperBound)) : null;
		return new Range<Date>(lowerBoundDate,lowerBoundType,
							   upperBoundDate,upperBoundType);
	}
	
	private static Range<java.time.LocalDate> _parseLocalDateRange(final String lowerBound,final BoundType lowerBoundType,
																   final String upperBound,final BoundType upperBoundType) {
		Calendar lowerCal = Calendar.getInstance();
		lowerCal.setTime(Dates.fromMillis(Long.parseLong(lowerBound)));
		java.time.LocalDate lowerBoundDate = lowerBound != null ? java.time.LocalDate.of(lowerCal.get(Calendar.YEAR), lowerCal.get(Calendar.MONTH), lowerCal.get(Calendar.DAY_OF_MONTH))
																: null;
		Calendar upperCal = Calendar.getInstance();
		upperCal.setTime(Dates.fromMillis(Long.parseLong(upperBound)));
		java.time.LocalDate upperBoundDate = upperBound != null ? java.time.LocalDate.of(upperCal.get(Calendar.YEAR), upperCal.get(Calendar.MONTH), upperCal.get(Calendar.DAY_OF_MONTH))
																: null;
		return new Range<java.time.LocalDate>(lowerBoundDate,lowerBoundType,
											  upperBoundDate,upperBoundType);
	}
	private static Range<java.time.LocalDateTime> _parseLocalDateTimeRange(final String lowerBound,final BoundType lowerBoundType,
																		   final String upperBound,final BoundType upperBoundType) {
		Calendar lowerCal = Calendar.getInstance();
		lowerCal.setTime(Dates.fromMillis(Long.parseLong(lowerBound)));
		java.time.LocalDateTime lowerBoundDate = lowerBound != null ? java.time.LocalDateTime.of(lowerCal.get(Calendar.YEAR), lowerCal.get(Calendar.MONTH), lowerCal.get(Calendar.DAY_OF_MONTH), lowerCal.get(Calendar.HOUR_OF_DAY), lowerCal.get(Calendar.MINUTE), lowerCal.get(Calendar.SECOND), lowerCal.get(Calendar.MILLISECOND)*1000000)
																	: null;
		Calendar upperCal = Calendar.getInstance();
		upperCal.setTime(Dates.fromMillis(Long.parseLong(upperBound)));
		java.time.LocalDateTime upperBoundDate = upperBound != null ? java.time.LocalDateTime.of(upperCal.get(Calendar.YEAR), upperCal.get(Calendar.MONTH), upperCal.get(Calendar.DAY_OF_MONTH), upperCal.get(Calendar.HOUR_OF_DAY), upperCal.get(Calendar.MINUTE), upperCal.get(Calendar.SECOND), upperCal.get(Calendar.MILLISECOND)*1000000)
																	: null;
		return new Range<java.time.LocalDateTime>(lowerBoundDate,lowerBoundType,
							   			upperBoundDate,upperBoundType);
	}
	private static Range<java.time.LocalTime> _parseLocalTimeRange(final String lowerBound,final BoundType lowerBoundType,
																   final String upperBound,final BoundType upperBoundType) {
		Calendar lowerCal = Calendar.getInstance();
		lowerCal.setTime(Dates.fromMillis(Long.parseLong(lowerBound)));
		java.time.LocalTime lowerBoundDate = lowerBound != null ? java.time.LocalTime.of(lowerCal.get(Calendar.HOUR_OF_DAY), lowerCal.get(Calendar.MINUTE), lowerCal.get(Calendar.SECOND), lowerCal.get(Calendar.MILLISECOND)*1000000)
																: null;
		Calendar upperCal = Calendar.getInstance();
		upperCal.setTime(Dates.fromMillis(Long.parseLong(upperBound)));
		java.time.LocalTime upperBoundDate = upperBound != null ? java.time.LocalTime.of(upperCal.get(Calendar.HOUR_OF_DAY), upperCal.get(Calendar.MINUTE), upperCal.get(Calendar.SECOND), upperCal.get(Calendar.MILLISECOND)*1000000)
																: null;
		return new Range<java.time.LocalTime>(lowerBoundDate,lowerBoundType,
							   			upperBoundDate,upperBoundType);
	}
	
	private static Range<LocalDate> _parseJodaLocalDateRange(final String lowerBound,final BoundType lowerBoundType,
															 final String upperBound,final BoundType upperBoundType) {
		LocalDate lowerBoundDate = lowerBound != null ? new LocalDate(Dates.fromMillis(Long.parseLong(lowerBound))) : null;
		LocalDate upperBoundDate = upperBound != null ? new LocalDate(Dates.fromMillis(Long.parseLong(upperBound))) : null;
		return new Range<LocalDate>(lowerBoundDate,lowerBoundType,
							   		upperBoundDate,upperBoundType);
	}
	private static Range<LocalDateTime> _parseJodaLocalDateTimeRange(final String lowerBound,final BoundType lowerBoundType,
																	 final String upperBound,final BoundType upperBoundType) {
		LocalDateTime lowerBoundDate = lowerBound != null ? new LocalDateTime(Dates.fromMillis(Long.parseLong(lowerBound))) : null;
		LocalDateTime upperBoundDate = upperBound != null ? new LocalDateTime(Dates.fromMillis(Long.parseLong(upperBound))) : null;
		return new Range<LocalDateTime>(lowerBoundDate,lowerBoundType,
							   			upperBoundDate,upperBoundType);
	}
	private static Range<LocalTime> _parseJodaLocalTimeRange(final String lowerBound,final BoundType lowerBoundType,
															 final String upperBound,final BoundType upperBoundType) {
		LocalTime lowerBoundDate = lowerBound != null ? _localTimeFromString(lowerBound) : null;
		LocalTime upperBoundDate = upperBound != null ? _localTimeFromString(upperBound) : null;
		return new Range<LocalTime>(lowerBoundDate,lowerBoundType,
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
		return new LocalTime(hour,minutes,seconds,milis);
	}
	private static Range<Byte>  _parseByteRange(final String lowerBound,final BoundType lowerBoundType,
											    final String upperBound,final BoundType upperBoundType) {
		Byte lowerBoundByte = lowerBound != null ? Byte.valueOf(lowerBound) : null;
		Byte upperBoundByte = upperBound != null ? Byte.valueOf(upperBound) : null;
		return new Range<Byte>(lowerBoundByte,lowerBoundType,
							   upperBoundByte,upperBoundType);
	}
	private static Range<Integer>  _parseIntRange(final String lowerBound,final BoundType lowerBoundType,
											   	  final String upperBound,final BoundType upperBoundType) {
		Integer lowerBoundInt = lowerBound != null ? Integer.valueOf(lowerBound) : null;
		Integer upperBoundInt = upperBound != null ? Integer.valueOf(upperBound) : null;
		return new Range<Integer>(lowerBoundInt,lowerBoundType,
							      upperBoundInt,upperBoundType);
	}
	private static Range<Short>  _parseShortRange(final String lowerBound,final BoundType lowerBoundType,
											   	  final String upperBound,final BoundType upperBoundType) {
		Short lowerBoundShort = lowerBound != null ? Short.valueOf(lowerBound) : null;
		Short upperBoundShort = upperBound != null ? Short.valueOf(upperBound) : null;
		return new Range<Short>(lowerBoundShort,lowerBoundType,
							    upperBoundShort,upperBoundType);
	}
	private static Range<Long>  _parseLongRange(final String lowerBound,final BoundType lowerBoundType,
											    final String upperBound,final BoundType upperBoundType) {
		Long lowerBoundLong = lowerBound != null ? Long.valueOf(lowerBound) : null;
		Long upperBoundLong = upperBound != null ? Long.valueOf(upperBound) : null;
		return new Range<Long>(lowerBoundLong,lowerBoundType,
							   upperBoundLong,upperBoundType);
	}
	private static Range<Double>  _parseDoubleRange(final String lowerBound,final BoundType lowerBoundType,
											   		final String upperBound,final BoundType upperBoundType) {
		Double lowerBoundDouble = lowerBound != null ? Double.valueOf(lowerBound) : null;
		Double upperBoundDouble = upperBound != null ? Double.valueOf(upperBound) : null;
		return new Range<Double>(lowerBoundDouble,lowerBoundType,
							     upperBoundDouble,upperBoundType);
	}
	private static Range<Float>  _parseFloatRange(final String lowerBound,final BoundType lowerBoundType,
											   	  final String upperBound,final BoundType upperBoundType) {
		Float lowerBoundFloat = lowerBound != null ? Float.valueOf(lowerBound) : null;
		Float upperBoundFloat = upperBound != null ? Float.valueOf(upperBound) : null;
		return new Range<Float>(lowerBoundFloat,lowerBoundType,
							    upperBoundFloat,upperBoundType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the data type of the range
	 */
	public Class<T> getDataType() {
		return Range.guessDataType(this);
	}
	/**
	 * @return the range as a Google Guava {@link com.google.common.collect.Range}
	 */
	public com.google.common.collect.Range<T> asGuavaRange() {
		return _range;
	}
	/**
	 * As a collection of the values between the range. Must provide a class implementing {@link import com.google.common.collect.DiscreteDomain}<T>
	 * @param discreteDomain
	 * @return
	 */
	public Collection<T> asCollectionUsingDiscreteDomain(final DiscreteDomain<T> discreteDomain) {
		return ContiguousSet.create(this.asGuavaRange(),discreteDomain);
	}
	@Override
	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		RangeDef rangeDef = null;
		Class<T> dataType = Range.guessDataType(this);
		/// Compilation errors:  Incomparable Types
		//[javac] /softbase_ejie/aplic/r01fb/tmp/compileLib/r01fbClasses/src/r01f/types/Range.java:407:
		//incomparable types: java.lang.Class<T> and java.lang.Class<java.util.Date>
		// [javac] 		if (dataType == java.util.Date.class || dataType == java.sql.Date.class) {

		Class<?> java_util_Date_class = java.util.Date.class;
		Class<?> java_time_LocalTime = java.time.LocalTime.class;
		Class<?> java_time_LocalDate = java.time.LocalDate.class;
		Class<?> java_time_LocalDateTime = java.time.LocalDateTime.class;
		Class<?> java_sql_Date_class = java.sql.Date.class;
		Class<?> joda_LocalTime = org.joda.time.LocalTime.class;
		Class<?> joda_LocalDate = org.joda.time.LocalDate.class;
		Class<?> joda_LocalDateTime = org.joda.time.LocalDateTime.class;
		Class<?> java_lang_Byte_class = Byte.class;
		Class<?> java_lang_Integer_class = Integer.class;
		Class<?> java_lang_Short_class = Short.class;
		Class<?> java_lang_Long_class = Long.class;
		Class<?> java_lang_Double_class = Double.class;
		Class<?> java_lang_Float_class = Float.class;

		if (dataType == java_util_Date_class || dataType == java_sql_Date_class) {
			rangeDef = _toDateBoundStrings(this);
		} else if (dataType == java_time_LocalDate) {
			rangeDef = _toLocalDateBoundStrings(this);
		} else if (dataType == java_time_LocalDateTime) {
			rangeDef = _toLocalDateTimeBoundStrings(this);
		} else if (dataType == java_time_LocalTime) {
			rangeDef = _toLocalTimeBoundStrings(this);
		} else if (dataType == joda_LocalDate) {
			rangeDef = _toJodaLocalDateBoundStrings(this);
		} else if (dataType == joda_LocalDateTime) {
			rangeDef = _toJodaLocalDateTimeBoundStrings(this);
		} else if (dataType == joda_LocalTime) {
			rangeDef = _toJodaLocalTimeBoundStrings(this);
		} else if (dataType == java_lang_Byte_class) {
			rangeDef = _toByteBoundStrings(this);
		} else if (dataType == java_lang_Integer_class) {
			rangeDef = _toIntegerBoundStrings(this);
		} else if (dataType == java_lang_Short_class) {
			rangeDef = _toShortBoundStrings(this);
		} else if (dataType == java_lang_Long_class ) {
			rangeDef = _toLongBoundStrings(this);
		} else if (dataType == java_lang_Double_class ) {
			rangeDef = _toDoubleBoundStrings(this);
		} else if (dataType == java_lang_Float_class) {
			rangeDef = _toFloatBoundStrings(this);
		} else {
			throw new IllegalArgumentException("Type " + dataType + " is NOT supported in Range");
		}
		String outStr = Strings.customized("{}{}..{}{}",
							   (rangeDef.getLowerBoundType() == BoundType.CLOSED ? "[" : "("),(rangeDef.getLowerBound() != null ? rangeDef.getLowerBound() : ""),
							   (rangeDef.getUpperBound() != null ? rangeDef.getUpperBound() : ""),(rangeDef.getUpperBoundType() == BoundType.CLOSED ? "]" : ")"));
		return outStr;
	}


	private static RangeDef _toDateBoundStrings(final Range<? extends Comparable> range) {
		Date l = (java.util.Date)(range.getLowerBound());
		Date u = (java.util.Date)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Long.toString(Dates.asMillis(l)) : null;
		String upper = range.getUpperBound() != null ? Long.toString(Dates.asMillis(u)) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	
	private static RangeDef _toLocalDateBoundStrings(final Range<? extends Comparable> range) {
		Date l = java.sql.Date.valueOf((java.time.LocalDate)(range.getLowerBound()));
		Date u = java.sql.Date.valueOf((java.time.LocalDate)(range.getUpperBound()));
		String lower = range.getLowerBound() != null ? Long.toString(Dates.asMillis(l)) : null;
		String upper = range.getUpperBound() != null ? Long.toString(Dates.asMillis(u)) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toLocalDateTimeBoundStrings(final Range<? extends Comparable> range) {
		Date l = java.sql.Timestamp.valueOf((java.time.LocalDateTime)(range.getLowerBound()));
		Date u = java.sql.Timestamp.valueOf((java.time.LocalDateTime)(range.getUpperBound()));
		String lower = range.getLowerBound() != null ? Long.toString(Dates.asMillis(l)) : null;
		String upper = range.getUpperBound() != null ? Long.toString(Dates.asMillis(u)) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toLocalTimeBoundStrings(final Range<? extends Comparable> range) {
		java.time.LocalTime l = (java.time.LocalTime)(range.getLowerBound());
		java.time.LocalTime u = (java.time.LocalTime)(range.getUpperBound());
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
	
	private static RangeDef _toJodaLocalDateBoundStrings(final Range<? extends Comparable> range) {
		LocalDate l = (LocalDate)(range.getLowerBound());
		LocalDate u = (LocalDate)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Long.toString(Dates.asMillis(l.toDate())) : null;
		String upper = range.getUpperBound() != null ? Long.toString(Dates.asMillis(u.toDate())) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toJodaLocalDateTimeBoundStrings(final Range<? extends Comparable> range) {
		LocalDateTime l = (LocalDateTime)(range.getLowerBound());
		LocalDateTime u = (LocalDateTime)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Long.toString(Dates.asMillis(l.toDate())) : null;
		String upper = range.getUpperBound() != null ? Long.toString(Dates.asMillis(u.toDate())) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toJodaLocalTimeBoundStrings(final Range<? extends Comparable> range) {
		LocalTime l = (LocalTime)(range.getLowerBound());
		LocalTime u = (LocalTime)(range.getUpperBound());
		String lower = l != null ? Strings.customized("{}:{}:{}:{}",
								   Strings.leftPad(Integer.toString(l.getHourOfDay()),2,'0'),
								   Strings.leftPad(Integer.toString(l.getMinuteOfHour()),2,'0'),
								   Strings.leftPad(Integer.toString(l.getSecondOfMinute()),2,'0'),
								   Strings.leftPad(Integer.toString(l.getMillisOfSecond()),3,'0'))
								 : null;
		String upper = u != null ? Strings.customized("{}:{}:{}:{}",
								   Strings.leftPad(Integer.toString(u.getHourOfDay()),2,'0'),
								   Strings.leftPad(Integer.toString(u.getMinuteOfHour()),2,'0'),
								   Strings.leftPad(Integer.toString(u.getSecondOfMinute()),2,'0'),
								   Strings.leftPad(Integer.toString(u.getMillisOfSecond()),3,'0'))
								 : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toByteBoundStrings(final Range<? extends Comparable> range) {
		Byte l = (Byte)(range.getLowerBound());
		Byte u = (Byte)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Byte.toString(l) : null;
		String upper = range.getUpperBound() != null ? Byte.toString(u) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toIntegerBoundStrings(final Range<? extends Comparable> range) {
		Integer l = (Integer)(range.getLowerBound());
		Integer u = (Integer)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Integer.toString(l) : null;
		String upper = range.getUpperBound() != null ? Integer.toString(u) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toShortBoundStrings(final Range<? extends Comparable> range) {
		Short l = (Short)(range.getLowerBound());
		Short u = (Short)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Short.toString(l) : null;
		String upper = range.getUpperBound() != null ? Short.toString(u) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toLongBoundStrings(final Range<? extends Comparable> range) {
		Long l = (Long)(range.getLowerBound());
		Long u = (Long)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Long.toString(l) : null;
		String upper = range.getUpperBound() != null ? Long.toString(u) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toDoubleBoundStrings(final Range<? extends Comparable> range) {
		Double l = (Double)(range.getLowerBound());
		Double u = (Double)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Double.toString(l) : null;
		String upper = range.getUpperBound() != null ? Double.toString(u) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toFloatBoundStrings(final Range<? extends Comparable> range) {
		Float l = (Float)(range.getLowerBound());
		Float u = (Float)(range.getUpperBound());
		String lower = range.getLowerBound() != null ? Float.toString(l) : null;
		String upper = range.getUpperBound() != null ? Float.toString(u) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> Class<T> guessDataType(final Range<T> range) {
		Class<T> outDataType = null;
		if (range.getLowerBound() != null) {
			outDataType = (Class<T>)range.getLowerBound().getClass();
		} else if (range.getUpperBound() != null) {
			outDataType = (Class<T>)range.getUpperBound().getClass();
		} else {
			throw new IllegalStateException("NO lower or upper bound set!");
		}
		return outDataType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	private static class RangeDef {
		@Getter @Setter private String _lowerBound;
		@Getter @Setter private BoundType _lowerBoundType;
		@Getter @Setter private String _upperBound;
		@Getter @Setter private BoundType _upperBoundType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (object instanceof com.google.common.collect.Range) {
			com.google.common.collect.Range<?> otherGuavaRange = (com.google.common.collect.Range<?>)object;
			return _range != null ? _range.equals(otherGuavaRange)
								  : false;
		}
		else if (object instanceof Range) {
			Range<?> otherRange = (Range<?>)object;
			return _range != null ? otherRange.asGuavaRange() != null ? _range.equals(otherRange.asGuavaRange())
																	  : true
								  : false;
		}
		else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return _range.hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Guava Range DELEGATED
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasLowerBound() {
		return _range.hasLowerBound();
	}
	public T lowerEndpoint() {
		return _range.lowerEndpoint();
	}
	public BoundType lowerBoundType() {
		return _range.lowerBoundType();
	}
	public boolean hasUpperBound() {
		return _range.hasUpperBound();
	}
	public T upperEndpoint() {
		return _range.upperEndpoint();
	}
	public BoundType upperBoundType() {
		return _range.upperBoundType();
	}
	public boolean isEmpty() {
		return _range.isEmpty();
	}
	public boolean contains(final T value) {
		return _range.contains(value);
	}
	public boolean containsAll(final Iterable<? extends T> values) {
		return _range.containsAll(values);
	}
	public boolean encloses(final com.google.common.collect.Range<T> otherRange) {
		return _range.encloses(otherRange);
	}
	public boolean isConnected(final com.google.common.collect.Range<T> otherRange) {
		return _range.isConnected(otherRange);
	}
	public com.google.common.collect.Range<T> intersection(final com.google.common.collect.Range<T> connectedRange) {
		return _range.intersection(connectedRange);
	}
	public com.google.common.collect.Range<T> span(final com.google.common.collect.Range<T> other) {
		return _range.span(other);
	}
	public com.google.common.collect.Range<T> canonical(final DiscreteDomain<T> domain) {
		return _range.canonical(domain);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Predicate DELEGATED
/////////////////////////////////////////////////////////////////////////////////////////
//	public Predicate<T> and(final Predicate<? super T> otherPred) {
//		return _range.and(otherPred);
//	}
//	public boolean test(final T otherPred) {
//		return _range.test(otherPred);
//	}
//	public Predicate<T> negate() {
//		return _range.negate();
//	}
//	public Predicate<T> or(final Predicate<? super T> otherPred) {
//		return _range.or(otherPred);
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final <T extends Comparable<? super T>> Comparator<Range<T>> createComparatorByLowerBound() {
		return new Comparator<Range<T>>() {
						@Override
						public int compare(final Range<T> r1,final Range<T> r2) {
							if (r1 == null && r2 == null) return 0;
							if (r1 != null && r2 == null) return -1;
							if (r1 == null && r2 != null) return 1;
							T l1 = r1.getLowerBound();
							T l2 = r2.getLowerBound();
							T u1 = r1.getUpperBound();
							T u2 = r2.getUpperBound();
							if (l1 == null && l2 == null) {
								if (u1 == null && u2 == null) return 0;
								if (u1 != null && u2 == null) return -1;// 2 do not have upper bound
								if (u1 == null && u2 != null) return 1;	// 1 do not have upper bound
								return u1.compareTo(u2);
							}
							if (l1 != null && l2 == null) return -1;	// 2 do not have lower bound
							if (l1 == null && l2 != null) return 1;		// 1 do not have lower bound
							return l1.compareTo(l2);
						}
			    };
	}
}
