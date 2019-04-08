package r01f.types.datetime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Dates;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="dayOfWeek")
@GwtIncompatible
@Accessors(prefix="_")
public class DayOfWeek
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<DayOfWeek> {

	private static final long serialVersionUID = 7658275370612790932L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private int _dayOfWeek;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str)
             && Numbers.isInteger(str)
             && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 7;
   }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfWeek(final int dayOfWeek) {
		_set(dayOfWeek);
	}
	public DayOfWeek(final Integer dayOfWeek) {
		_set(dayOfWeek);
	}
	public DayOfWeek(final String month) {
		int m = Integer.parseInt(month);
		_set(m);
	}
	public static DayOfWeek of(final String dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	public static DayOfWeek of(final Date date) {
		return new DayOfWeek(Dates.asCalendar(date).get(Calendar.DAY_OF_WEEK));
	}
	public static DayOfWeek of(final int dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	public static DayOfWeek valueOf(final String dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	public static DayOfWeek fromString(final String dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	public DayOfWeek nextMonth() {
		return _dayOfWeek < 7 ? DayOfWeek.of(_dayOfWeek+1)
							  : DayOfWeek.of(1);
	}
	public DayOfWeek prevMonth() {
		return _dayOfWeek > 1 ? DayOfWeek.of(_dayOfWeek-1)
							  : DayOfWeek.of(7);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private void _set(final int dayOfWeek) {
		Preconditions.checkArgument(dayOfWeek <= 7 || dayOfWeek > 0,"Not a valid day of week");
		_dayOfWeek = dayOfWeek;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_dayOfWeek);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public int asInteger() {
		return _dayOfWeek;
	}
	public int asIntegerStartingOnMonday() {
		return _dayOfWeek == 1 ? 7					// sunday becomes 7
							   : _dayOfWeek - 1;	// monday becomes 1 and so on
	}
	public int asIntegerStartingOnSunday() {
		return _dayOfWeek;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final DayOfWeek other) {
		return _dayOfWeek == other.asInteger();
	}
	public boolean isNOT(final DayOfWeek other) {
		return !this.is(other);
	}
	public boolean isBefore(final DayOfWeek other) {
		return _dayOfWeek < other.asInteger();
	}
	public boolean isAfter(final DayOfWeek other) {
		return _dayOfWeek > other.asInteger();
	}
	public boolean isBeforeOrEqual(final DayOfWeek other) {
		return _dayOfWeek <= other.asInteger();
	}
	public boolean isAfterOrEqual(final DayOfWeek other) {
		return _dayOfWeek >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<DayOfWeek> weekDaysIterable() {
		return new Iterable<DayOfWeek>() {
						@Override
						public Iterator<DayOfWeek> iterator() {
							return new Iterator<DayOfWeek>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												return _curr < 7;
											}
											@Override
											public DayOfWeek next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return DayOfWeek.of(_curr);
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
		if (obj instanceof DayOfWeek) return ((DayOfWeek)obj).getDayOfWeek() == _dayOfWeek;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(_dayOfWeek).hashCode();
	}
	@Override
	public int compareTo(final DayOfWeek other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final DayOfWeek SUNDAY = new DayOfWeek(1);
	public static final DayOfWeek MONDAY = new DayOfWeek(2);
	public static final DayOfWeek TUESDAY = new DayOfWeek(3);
	public static final DayOfWeek WEDNESDAY = new DayOfWeek(4);
	public static final DayOfWeek THURSDAY = new DayOfWeek(5);
	public static final DayOfWeek FRIDAY = new DayOfWeek(6);
	public static final DayOfWeek SATURDAY = new DayOfWeek(7);
}
