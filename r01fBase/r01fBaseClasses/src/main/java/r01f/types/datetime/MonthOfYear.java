package r01f.types.datetime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@MarshallType(as="monthOfYear")
@GwtIncompatible
@Accessors(prefix="_")
public class MonthOfYear
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<MonthOfYear> {

	private static final long serialVersionUID = 7658275370612790932L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private int _monthOfYear;

/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "(0?[1-9]|1[012])";		// does not match 13,,,
	public static final String REGEX_NOCAPTURE = "(?:0?[1-9]|1[012])";
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str)
             && Numbers.isInteger(str)
             && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 12;
   }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public MonthOfYear(final int month) {
		_set(month);
	}
	public MonthOfYear(final String month) {
		int m = Integer.parseInt(month);
		_set(m);
	}
	public static MonthOfYear of(final String monthOfYear) {
		return new MonthOfYear(monthOfYear);
	}
	public static MonthOfYear of(final Date date) {
		return new MonthOfYear(Dates.asCalendar(date).get(Calendar.MONTH)+1);
	}
	public static MonthOfYear of(final LocalDate date) {
		return new MonthOfYear(date.getMonthOfYear());
	}
	public static MonthOfYear of(final DateTime date) {
		return new MonthOfYear(date.getMonthOfYear());
	}
	public static MonthOfYear of(final int monthOfYear) {
		return new MonthOfYear(monthOfYear);
	}
	public static MonthOfYear valueOf(final String monthOfYear) {
		return new MonthOfYear(monthOfYear);
	}
	public static MonthOfYear from(final String monthOfYear) {
		return new MonthOfYear(monthOfYear);
	}
	public static MonthOfYear fromString(final String monthOfYear) {
		return new MonthOfYear(monthOfYear);
	}
	public static MonthOfYear now() {
		return MonthOfYear.of(new Date());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private void _set(final int month) {
		Preconditions.checkArgument(month <= 12 || month > 0,"Not a valid month");
		_monthOfYear = month;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_monthOfYear);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return _monthOfYear;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final MonthOfYear other) {
		return _monthOfYear == other.asInteger();
	}
	public boolean isNOT(final MonthOfYear other) {
		return !this.is(other);
	}
	public boolean isBefore(final MonthOfYear other) {
		return _monthOfYear < other.asInteger();
	}
	public boolean isAfter(final MonthOfYear other) {
		return _monthOfYear > other.asInteger();
	}
	public boolean isBeforeOrEqual(final MonthOfYear other) {
		return _monthOfYear <= other.asInteger();
	}
	public boolean isAfterOrEqual(final MonthOfYear other) {
		return _monthOfYear >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<MonthOfYear> yearMonthsIterable() {
		return new Iterable<MonthOfYear>() {
						@Override
						public Iterator<MonthOfYear> iterator() {
							return new Iterator<MonthOfYear>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												return _curr < 12;
											}
											@Override
											public MonthOfYear next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return MonthOfYear.of(_curr);
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
		if (obj instanceof MonthOfYear) return ((MonthOfYear)obj).getMonthOfYear() == _monthOfYear;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(_monthOfYear).hashCode();
	}
	@Override
	public int compareTo(final MonthOfYear other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public MonthOfYear minus(final int num) {
		int newVal = _monthOfYear - num;
		if (newVal <= 0) newVal = 12 - Math.abs(newVal);
		return MonthOfYear.of(newVal);
	}
	public MonthOfYear plus(final int num) {
		int newVal = _monthOfYear + num;
		if (newVal > 12) newVal = 12 - newVal;
		return MonthOfYear.of(newVal);
	}
	public MonthOfYear nextMonth() {
		return _monthOfYear < 12 ? MonthOfYear.of(_monthOfYear+1)
								 : MonthOfYear.of(1);
	}
	public MonthOfYear prevMonth() {
		return _monthOfYear > 1 ? MonthOfYear.of(_monthOfYear-1)
								: MonthOfYear.of(12);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final MonthOfYear JANUARY = new MonthOfYear(1);
	public static final MonthOfYear FEBRUARY = new MonthOfYear(2);
	public static final MonthOfYear MARCH = new MonthOfYear(3);
	public static final MonthOfYear APRIL = new MonthOfYear(4);
	public static final MonthOfYear MAY = new MonthOfYear(5);
	public static final MonthOfYear JUNE = new MonthOfYear(6);
	public static final MonthOfYear JULY = new MonthOfYear(7);
	public static final MonthOfYear AUGUST = new MonthOfYear(8);
	public static final MonthOfYear SEPTEMBER = new MonthOfYear(9);
	public static final MonthOfYear OCTOBER = new MonthOfYear(10);
	public static final MonthOfYear NOVEMBER = new MonthOfYear(11);
	public static final MonthOfYear DECEMBER = new MonthOfYear(12);

	public static final Collection<MonthOfYear> MONTHS_OF_YEAR = Lists.newArrayList(JANUARY,FEBRUARY,MARCH,APRIL,MAY,JUNE,JULY,AUGUST,SEPTEMBER,OCTOBER,NOVEMBER,DECEMBER);

	public static final MonthOfYear MONTH1 = JANUARY;
	public static final MonthOfYear MONTH2 = FEBRUARY;
	public static final MonthOfYear MONTH3 = MARCH;
	public static final MonthOfYear MONTH4 = APRIL;
	public static final MonthOfYear MONTH5 = MAY;
	public static final MonthOfYear MONTH6 = JUNE;
	public static final MonthOfYear MONTH7 = JULY;
	public static final MonthOfYear MONTH8 = AUGUST;
	public static final MonthOfYear MONTH9 = SEPTEMBER;
	public static final MonthOfYear MONTH10 = OCTOBER;
	public static final MonthOfYear MONTH11 = NOVEMBER;
	public static final MonthOfYear MONTH12 = DECEMBER;

/////////////////////////////////////////////////////////////////////////////////////////
//	GUAVA DISCRETE DOMAIN                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the months within the given range
	 * @param range
	 * @return
	 */
	public static Collection<MonthOfYear> monthsOfYearWithin(final Range<MonthOfYear> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		
		Collection<MonthOfYear> monthsOfYear = new ArrayList<>(); 
		MonthOfYear currMonthOfYear = range.upperEndpoint();
		MonthOfYear lowerMonthOfYear = range.lowerEndpoint();
		while(currMonthOfYear.isAfter(lowerMonthOfYear)) {
			monthsOfYear.add(currMonthOfYear);
			currMonthOfYear = currMonthOfYear.minus(1);
		}
		return monthsOfYear;
	}
	/**
	 * Guava's {@link DiscreteDomain} used to create a {@link Set} of {@link Year}s
	 * <pre class='brush:java'>
	 * 		ContiguousSet<Year> years = ContiguousSet.create(Range.closed(Year.of(1960),Year.now()),
															 Year.DISCRETE_DOMAIN);
	 * </pre>
	 */
	public static DiscreteDomain<MonthOfYear> DISCRETE_DOMAIN = new DiscreteDomain<MonthOfYear>() {
																		@Override
																		public MonthOfYear next(final MonthOfYear val) {
																			if (val.is(MonthOfYear.DECEMBER)) throw new IllegalArgumentException();
																			return val.nextMonth();
																		}
																		@Override
																		public MonthOfYear previous(final MonthOfYear val) {
																			if (val.is(MonthOfYear.JANUARY)) throw new IllegalArgumentException();
																			return val.nextMonth();
																		}
																		@Override
																		public long distance(final MonthOfYear start,final MonthOfYear end) {
																			return end.asInteger() - start.asInteger();
																		}
																 };
	public static ContiguousSet<MonthOfYear> createContiguousSetOf(final Range<MonthOfYear> range) {
		return MonthOfYear.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<MonthOfYear> createContiguousSetOf(final com.google.common.collect.Range<MonthOfYear> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		return ContiguousSet.create(range,
								    DISCRETE_DOMAIN);		
	}
}
