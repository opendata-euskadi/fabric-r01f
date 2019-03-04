package r01f.types.datetime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
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
	public Year nextYear() {
		return Year.of(_year+1);
	}
	public Year prevYear() {
		return Year.of(_year-1);
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
	/**
	 * Creates a new Year from this year minus the given number of years
	 * @param years
	 * @return
	 */
	public Year minus(final int years) {
		return Year.of(_year - years);
	}
	/**
	 * Creates a new Year from this year minus the given number of years
	 * @param years
	 * @return
	 */
	public Year plus(final int years) {
		return Year.of(_year + years);
	}
}
