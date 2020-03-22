package r01f.types.datetime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="year")
@GwtIncompatible
@Accessors(prefix="_")
@NoArgsConstructor
public class Year
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<Year> {

	private static final long serialVersionUID = 7658275370612790932L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private int _year;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "(19\\d{2}|20\\d{2})";
	public static final String REGEX_NOCAPTURE = "(?:19\\d{2}|20\\d{2})";
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str)
                && Numbers.isInteger(str);
   }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public Year(final int year) {
		_set(year);
	}
	public Year(final Integer year) {
		_set(year);
	}
	public Year(final String year) {
		_set(Integer.parseInt(year));
	}
	public static Year of(final String year) {
		return new Year(year);
	}
	public static Year of(final Date date) {
		return new Year(Dates.asCalendar(date).get(Calendar.YEAR));
	}
	public static Year of(final LocalDate date) {
		return new Year(date.getYear());
	}
	public static Year of(final DateTime date) {
		return new Year(date.getYear());
	}
	public static Year of(final int year) {
		return new Year(year);
	}
	public static Year now() {
		return Year.of(new Date());
	}
	public static Year valueOf(final String year) {
		return new Year(year);
	}
	public static Year from(final String year) {
		return new Year(year);
	}
	public static Year fromString(final String year) {
		return new Year(year);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private void _set(final int year) {
		Preconditions.checkArgument(year >= 0,"Not a valid day year");
		_year = year;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_year);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public int asInteger() {
		return _year;
	}
	public String asStringInCentury() {
		return this.toString().substring(this.toString().length()-2,
										 this.toString().length());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final Year other) {
		return _year == other.asInteger();
	}
	public boolean isNOT(final Year other) {
		return !this.is(other);
	}
	public boolean isBefore(final Year other) {
		return _year < other.asInteger();
	}
	public boolean isAfter(final Year other) {
		return _year > other.asInteger();
	}
	public boolean isBeforeOrEqual(final Year other) {
		return _year <= other.asInteger();
	}
	public boolean isAfterOrEqual(final Year other) {
		return _year >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof Year) return ((Year)obj).getYear() == _year;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(_year).hashCode();
	}
	@Override
	public int compareTo(final Year other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public Year minus(final int years) {
		return Year.of(_year - years);
	}
	public Year plus(final int years) {
		return Year.of(_year + years);
	}
	public Year nextYear() {
		return this.plus(1);
	}
	public Year prevYear() {
		return this.minus(1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the years within the given range
	 * @param range
	 * @return
	 */
	public static Collection<Year> yearsWithin(final Range<Year> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.getUpperBound().isBefore(range.getUpperBound())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		
		Collection<Year> years = new ArrayList<>(); 
		Year currYear = range.upperEndpoint();
		Year lowerLimitYear = range.lowerEndpoint();
		while (currYear.isAfter(lowerLimitYear)) {
			years.add(currYear);
			currYear = currYear.minus(1);
		}
		return years;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GUAVA DISCRETE DOMAIN                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guava's {@link DiscreteDomain} used to create a {@link Set} of {@link Year}s
	 * <pre class='brush:java'>
	 * 		ContiguousSet<Year> years = ContiguousSet.create(Range.closed(Year.of(1960),Year.now()),
															 Year.DISCRETE_DOMAIN);
	 * </pre>
	 */
	public static DiscreteDomain<Year> DISCRETE_DOMAIN = new DiscreteDomain<Year>() {
																@Override
																public Year next(final Year val) {
																	return val.nextYear();
																}
																@Override
																public Year previous(final Year val) {
																	return val.prevYear();
																}
																@Override
																public long distance(final Year start,final Year end) {
																	return end.asInteger() - start.asInteger();
																}
														 };
	public static ContiguousSet<Year> createContiguousSetOf(final Range<Year> range) {
		return Year.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<Year> createContiguousSetOf(final com.google.common.collect.Range<Year> range) {
		return ContiguousSet.create(range,
								    DISCRETE_DOMAIN);		
	}
} 
