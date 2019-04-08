package r01f.types.datetime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.LocalTime;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Dates;
import r01f.util.types.Strings;

@MarshallType(as="timeHourMinutes")
@GwtIncompatible
@Accessors(prefix="_")
public class Time
  implements Serializable,
  			 Comparable<Time>,
  			 CanBeRepresentedAsString {

	private static final long serialVersionUID = -7084816234257337127L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final transient Pattern TIME_PATTERN = Pattern.compile("([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])(?::([0-5][0-9]))?" +	// HH:MM:ss
																		  "(\\s)?(?i)(am|pm)?");											// am|pm

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="hour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _hourOfDay;

	@MarshallField(as="minuteOfHour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _minuteOfHour;

	@MarshallField(as="secondOfMinute",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _secondOfMinute;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Time(@MarshallFrom("hour") final int hour,@MarshallFrom("minuteOfHour") final int minuteOfHour,@MarshallFrom("secondOfMinute") final int secondOfMinute) {
		Preconditions.checkArgument(hour >= 0,"Not a valid hour");
		Preconditions.checkArgument(minuteOfHour >= 0,"Not a valid minute of hour value");
		Preconditions.checkArgument(secondOfMinute >= 0,"Not a valid second of minute value");
		_hourOfDay = hour;
		_minuteOfHour = minuteOfHour;
		_secondOfMinute = secondOfMinute;
	}
	public Time(final HourOfDay hour,final MinuteOfHour minutes,final SecondOfMinute sencond) {
		this(hour.asInteger(),minutes.asInteger(),sencond.asInteger());
	}
	public Time(final String timeStr) {
		Matcher m = TIME_PATTERN.matcher(timeStr);
		if (!m.find()) throw new IllegalStateException(timeStr + " is not a valid time: MUST match " + TIME_PATTERN);
		int hour = Integer.parseInt(m.group(1));
		int minutes = Integer.parseInt(m.group(2));
		int seconds = Strings.isNOTNullOrEmpty(m.group(3)) ? Integer.parseInt(m.group(3)) : 0;
		String ampm = m.group(4);

		if (Strings.isNOTNullOrEmpty(ampm)
		 && ampm.equalsIgnoreCase("PM")
		 && hour < 12) {
			hour = 12 + hour;
		}
		_hourOfDay = hour;
		_minuteOfHour = minutes;
		_secondOfMinute = seconds;
	}
	public static Time of(final String time) {
		return new Time(time);
	}
	public static Time of(final Date date) {
		Calendar cal = Dates.asCalendar(date);
		return new Time(cal.get(Calendar.HOUR),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
	}
	public static Time of(final LocalTime time) {
		return new Time(time.getHourOfDay(),time.getMinuteOfHour(),time.getSecondOfMinute());
	}
	public static Time of(final int hour,final int minutes,final int seconds) {
		return new Time(hour,minutes,seconds);
	}
	public static Time valueOf(final String time) {
		return new Time(time);
	}
	public static Time fromString(final String time) {
		return new Time(time);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public LocalTime asLocalTime() {
		return new LocalTime(_hourOfDay,_minuteOfHour,_secondOfMinute);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Strings.customized("{}:{}:{}",
								  String.format("%02d",_hourOfDay),
								  String.format("%02d",_minuteOfHour),
								  String.format("%02d",_secondOfMinute));
	}
	@Override
	public String asString() {
		return this.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isBefore(final Time other) {
		return this.asLocalTime().isBefore(other.asLocalTime());
	}
	public boolean isAfter(final Time other) {
		return this.asLocalTime().isAfter(other.asLocalTime());
	}
	public boolean isBeforeOrEqual(final Time other) {
		LocalTime thisLT = this.asLocalTime();
		LocalTime otherLT = other.asLocalTime();
		return thisLT.isBefore(otherLT)
			|| thisLT.isEqual(otherLT);
	}
	public boolean isAfterOrEqual(final Time other) {
		LocalTime thisLT = this.asLocalTime();
		LocalTime otherLT = other.asLocalTime();
		return thisLT.isBefore(otherLT)
			|| thisLT.isEqual(otherLT);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof Time) {
			Time other = (Time)obj;
			return this.getHourOfDay() == other.getHourOfDay()
				&& this.getMinuteOfHour() == other.getMinuteOfHour()
				&& this.getSecondOfMinute() == other.getSecondOfMinute();
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_hourOfDay,_minuteOfHour,_secondOfMinute);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int compareTo(final Time other) {
		LocalTime thisLT = this.asLocalTime();
		LocalTime otherLT = other.asLocalTime();
		return thisLT.compareTo(otherLT);
	}
}
