package r01f.types.datetime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="secondOfMinute")
@GwtIncompatible
@Accessors(prefix="_")
public class SecondOfMinute
  implements Serializable,
  			 CanBeRepresentedAsString,
  			 Comparable<SecondOfMinute> {

	private static final long serialVersionUID = 5446927630549647342L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private int _secondOfMinute;
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
	public SecondOfMinute(final int secondOfMinute) {
		_set(secondOfMinute);
	}
	public SecondOfMinute(final Integer secondOfMinute) {
		_set(secondOfMinute);
	}
	public SecondOfMinute(final String month) {
		int m = Integer.parseInt(month);
		_set(m);
	}
	public static SecondOfMinute of(final String secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
	public static SecondOfMinute of(final Date date) {
		return new SecondOfMinute(Dates.asCalendar(date).get(Calendar.SECOND));
	}
	public static SecondOfMinute of(final int secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
	public static SecondOfMinute valueOf(final String secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
	public static SecondOfMinute fromString(final String secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private void _set(final int secondOfMinute) {
		Preconditions.checkArgument(secondOfMinute < 60 || secondOfMinute >= 0,"Not a valid second of minute");
		_secondOfMinute = secondOfMinute;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(_secondOfMinute);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public int asInteger() {
		return _secondOfMinute;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final SecondOfMinute other) {
		return _secondOfMinute == other.asInteger();
	}
	public boolean isNOT(final SecondOfMinute other) {
		return !this.is(other);
	}
	public boolean isBefore(final SecondOfMinute other) {
		return _secondOfMinute < other.asInteger();
	}
	public boolean isAfter(final SecondOfMinute other) {
		return _secondOfMinute > other.asInteger();
	}
	public boolean isBeforeOrEqual(final SecondOfMinute other) {
		return _secondOfMinute <= other.asInteger();
	}
	public boolean isAfterOrEqual(final SecondOfMinute other) {
		return _secondOfMinute >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<SecondOfMinute> yearMonthsIterable() {
		return new Iterable<SecondOfMinute>() {
						@Override
						public Iterator<SecondOfMinute> iterator() {
							return new Iterator<SecondOfMinute>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												return _curr < 60;
											}
											@Override
											public SecondOfMinute next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return SecondOfMinute.of(_curr);
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
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public SecondOfMinute nextSecondOfMinute() {
		if (_secondOfMinute == 59) return SecondOfMinute.of(0);
		return SecondOfMinute.of(_secondOfMinute + 1);
	}
	public SecondOfMinute prevSecondOfMinute() {
		if (_secondOfMinute == 0) return SecondOfMinute.of(59);
		return SecondOfMinute.of(_secondOfMinute - 1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof SecondOfMinute) return ((SecondOfMinute)obj).getSecondOfMinute() == _secondOfMinute;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(_secondOfMinute).hashCode();
	}
	@Override
	public int compareTo(final SecondOfMinute other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
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
	public static DiscreteDomain<SecondOfMinute> DISCRETE_DOMAIN = new DiscreteDomain<SecondOfMinute>() {
																		@Override
																		public SecondOfMinute next(final SecondOfMinute val) {
																			if (val.is(SecondOfMinute.of(59))) throw new IllegalArgumentException();
																			return val.nextSecondOfMinute();
																		}
																		@Override
																		public SecondOfMinute previous(final SecondOfMinute val) {
																			if (val.is(SecondOfMinute.of(0))) throw new IllegalArgumentException();
																			return val.prevSecondOfMinute();
																		}
																		@Override
																		public long distance(final SecondOfMinute start,final SecondOfMinute end) {
																			return end.asInteger() - start.asInteger();
																		}
																 };
	public static ContiguousSet<SecondOfMinute> createContiguousSetOf(final Range<SecondOfMinute> range) {
		return SecondOfMinute.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<SecondOfMinute> createContiguousSetOf(final com.google.common.collect.Range<SecondOfMinute> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		return ContiguousSet.create(range,
								    DISCRETE_DOMAIN);		
	}
}
