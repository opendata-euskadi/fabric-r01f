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

@MarshallType(as="minuteOfHour")
@GwtIncompatible
@Accessors(prefix="_")
public class MinuteOfHour
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<MinuteOfHour> {

	private static final long serialVersionUID = -6492644548389808923L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private int _minuteOfHour;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "([0-5][0-9])";
	public static final String REGEX_NOCAPTURE = "[0-5][0-9]";
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str)
             && Numbers.isInteger(str)
             && Integer.parseInt(str) >= 0 && Integer.parseInt(str) < 60;
   }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public MinuteOfHour(final int minuteOfHour) {
		_set(minuteOfHour);
	}
	public MinuteOfHour(final Integer minuteOfHour) {
		_set(minuteOfHour);
	}
	public MinuteOfHour(final String month) {
		int m = Integer.parseInt(month);
		_set(m);
	}
	public static MinuteOfHour of(final String minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	public static MinuteOfHour of(final Date date) {
		return new MinuteOfHour(Dates.asCalendar(date).get(Calendar.MINUTE));
	}
	public static MinuteOfHour of(final DateTime date) {
		return new MinuteOfHour(date.getMinuteOfHour());
	}
	public static MinuteOfHour of(final LocalTime time) {
		return new MinuteOfHour(time.getMinuteOfHour());
	}
	public static MinuteOfHour of(final int minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	public static MinuteOfHour valueOf(final String minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	public static MinuteOfHour fromString(final String minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	public static MinuteOfHour now() {
		return MinuteOfHour.of(new Date());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private void _set(final int minuteOfHour) {
		Preconditions.checkArgument(minuteOfHour < 60 || minuteOfHour >= 0,"Not a valid minute of hour");
		_minuteOfHour = minuteOfHour;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_minuteOfHour);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return _minuteOfHour;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final MinuteOfHour other) {
		return _minuteOfHour == other.asInteger();
	}
	public boolean isNOT(final MinuteOfHour other) {
		return !this.is(other);
	}
	public boolean isBefore(final MinuteOfHour other) {
		return _minuteOfHour < other.asInteger();
	}
	public boolean isAfter(final MinuteOfHour other) {
		return _minuteOfHour > other.asInteger();
	}
	public boolean isBeforeOrEqual(final MinuteOfHour other) {
		return _minuteOfHour <= other.asInteger();
	}
	public boolean isAfterOrEqual(final MinuteOfHour other) {
		return _minuteOfHour >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<MinuteOfHour> hourMinutesIterable() {
		return new Iterable<MinuteOfHour>() {
						@Override
						public Iterator<MinuteOfHour> iterator() {
							return new Iterator<MinuteOfHour>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												return _curr < 60;
											}
											@Override
											public MinuteOfHour next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return MinuteOfHour.of(_curr);
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
		if (obj instanceof MinuteOfHour) return ((MinuteOfHour)obj).getMinuteOfHour() == _minuteOfHour;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(_minuteOfHour).hashCode();
	}
	@Override
	public int compareTo(final MinuteOfHour other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
