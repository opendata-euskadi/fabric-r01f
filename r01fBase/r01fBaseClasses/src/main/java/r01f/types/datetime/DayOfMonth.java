package r01f.types.datetime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Dates;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="dayOfMonth")
@GwtIncompatible
@Accessors(prefix="_")
public class DayOfMonth
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<DayOfMonth> {

	private static final long serialVersionUID = 7658275370612790932L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final int _dayOfMonth;

/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "(0[1-9]|[12]\\d|3[01])";	// do not match 32, etc
	public static final String REGEX_NOCAPTURE = "(?:0[1-9]|[12]\\d|3[01])";
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str)
             && Numbers.isInteger(str)
             && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 31;
   }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfMonth(final int dayOfMonth) {
		_check(dayOfMonth);
		_dayOfMonth = dayOfMonth;
	}
	public DayOfMonth(final Integer dayOfMonth) {
		_check(dayOfMonth);
		_dayOfMonth = dayOfMonth;
	}
	public DayOfMonth(final String dayOfMonthStr) {
		int dayOfMonth = Integer.parseInt(dayOfMonthStr);
		_check(dayOfMonth);
		_dayOfMonth = dayOfMonth;
	}
	public static DayOfMonth of(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth of(final Date date) {
		return new DayOfMonth(Dates.asCalendar(date).get(Calendar.DAY_OF_MONTH));
	}
	public static DayOfMonth of(final LocalDate date) {
		return new DayOfMonth(date.getDayOfMonth());
	}
	public static DayOfMonth of(final DateTime date) {
		return new DayOfMonth(date.getDayOfMonth());
	}
	public static DayOfMonth of(final int dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth valueOf(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth from(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth fromString(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth now() {
		return DayOfMonth.of(new Date());
	}
	private static void _check(final int dayOfMonth) {
		Preconditions.checkArgument(dayOfMonth <= 31 || dayOfMonth > 0,"Not a valid day of month");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_dayOfMonth);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return _dayOfMonth;
	}
	public boolean isValidAt(final Year year,final MonthOfYear monthOfYear) {
		return DayOfMonth.isValidDayOfMonth(year,monthOfYear,this);
	}
	public static boolean isValidDayOfMonth(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		LocalDate localDate = new LocalDate(year.asInteger(),monthOfYear.asInteger(),1);
		int dayOfMonthMaxValue = localDate.dayOfMonth().getMaximumValue();
		return dayOfMonthMaxValue <= dayOfMonth.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final DayOfMonth other) {
		return _dayOfMonth == other.asInteger();
	}
	public boolean isNOT(final DayOfMonth other) {
		return !this.is(other);
	}
	public boolean isBefore(final DayOfMonth other) {
		return _dayOfMonth < other.asInteger();
	}
	public boolean isAfter(final DayOfMonth other) {
		return _dayOfMonth > other.asInteger();
	}
	public boolean isBeforeOrEqual(final DayOfMonth other) {
		return _dayOfMonth <= other.asInteger();
	}
	public boolean isAfterOrEqual(final DayOfMonth other) {
		return _dayOfMonth >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<DayOfMonth> dayOfMonthIterableOf(final Year year,
															final MonthOfYear monthOfYear) {
		return new Iterable<DayOfMonth>() {
						@Override
						public Iterator<DayOfMonth> iterator() {
							return new Iterator<DayOfMonth>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												LocalDate endOfMonth = new LocalDate(year.asInteger(),monthOfYear.asInteger(),1)
																				.dayOfMonth().withMaximumValue();
												return _curr < endOfMonth.getDayOfMonth();
											}
											@Override
											public DayOfMonth next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return DayOfMonth.of(_curr);
											}
											@Override
											public void remove() {
												throw new UnsupportedOperationException();
											}
								   };
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof DayOfMonth) return ((DayOfMonth)obj).getDayOfMonth() == _dayOfMonth;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(_dayOfMonth).hashCode();
	}
	@Override
	public int compareTo(final DayOfMonth other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
