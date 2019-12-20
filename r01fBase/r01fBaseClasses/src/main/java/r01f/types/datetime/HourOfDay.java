package r01f.types.datetime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Dates;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="hourOfDay")
@GwtIncompatible
@Accessors(prefix="_")
public class HourOfDay
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<HourOfDay> {

	private static final long serialVersionUID = 8445517567471520680L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private int _hourOfDay;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "([0-9]|0[0-9]|1[0-9]|2[0-3])";
	public static final String REGEX_NOCAPTURE = "(?:[0-9]|0[0-9]|1[0-9]|2[0-3])";
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str)
             && Numbers.isInteger(str)
             && Integer.parseInt(str) >= 0 && Integer.parseInt(str) < 24;
   }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public HourOfDay(final int hourOfDay) {
		_set(hourOfDay);
	}
	public HourOfDay(final Integer hourOfDay) {
		_set(hourOfDay);
	}
	public HourOfDay(final String month) {
		int m = Integer.parseInt(month);
		_set(m);
	}
	public static HourOfDay of(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay of(final Date date) {
		return new HourOfDay(Dates.asCalendar(date).get(Calendar.HOUR_OF_DAY));
	}
	public static HourOfDay of(final DateTime date) {
		return new HourOfDay(date.getHourOfDay());
	}
	public static HourOfDay of(final LocalTime time) {
		return new HourOfDay(time.getHourOfDay());
	}
	public static HourOfDay of(final int hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay valueOf(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay fromString(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay now() {
		return HourOfDay.of(new Date());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private void _set(final int hourOfDay) {
		Preconditions.checkArgument(hourOfDay < 24 || hourOfDay >= 0,"Not a valid hour of day");
		_hourOfDay = hourOfDay;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_hourOfDay);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return _hourOfDay;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final HourOfDay other) {
		return _hourOfDay == other.asInteger();
	}
	public boolean isNOT(final HourOfDay other) {
		return !this.is(other);
	}
	public boolean isBefore(final HourOfDay other) {
		return _hourOfDay < other.asInteger();
	}
	public boolean isAfter(final HourOfDay other) {
		return _hourOfDay > other.asInteger();
	}
	public boolean isBeforeOrEqual(final HourOfDay other) {
		return _hourOfDay <= other.asInteger();
	}
	public boolean isAfterOrEqual(final HourOfDay other) {
		return _hourOfDay >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<HourOfDay> dayHoursIterable() {
		return _dayHoursIterableWithin(0,24);
	}
	public static Iterable<HourOfDay> dayHoursIterableFrom(final HourOfDay start) {
		return _dayHoursIterableWithin(start.asInteger(),24);
	}
	public static Iterable<HourOfDay> dayHoursIterableTo(final HourOfDay end) {
		return _dayHoursIterableWithin(0,end.asInteger());
	}
	public static Iterable<HourOfDay> dayHoursIterableWithin(final HourOfDay start,final HourOfDay end) {
		return _dayHoursIterableWithin(start.asInteger(),end.asInteger());
	}
	private static Iterable<HourOfDay> _dayHoursIterableWithin(final int start,final int end) {
		return new Iterable<HourOfDay>() {
						@Override
						public Iterator<HourOfDay> iterator() {
							return new Iterator<HourOfDay>() {
											private int _curr = start - 1;

											@Override
											public boolean hasNext() {
												return _curr < (end - 1);
											}
											@Override
											public HourOfDay next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return HourOfDay.of(_curr);
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
		if (obj instanceof HourOfDay) return ((HourOfDay)obj).getHourOfDay() == _hourOfDay;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(_hourOfDay).hashCode();
	}
	@Override
	public int compareTo(final HourOfDay other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
