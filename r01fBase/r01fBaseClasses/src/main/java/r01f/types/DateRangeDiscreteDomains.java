package r01f.types;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.common.collect.DiscreteDomain;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;

@Accessors(prefix="_")
public enum DateRangeDiscreteDomains
      implements  EnumExtended<DateRangeDiscreteDomains>{
///////////////////////////////////////////////////////////////////////////////////////////////////
// SOME DATE RANGE DAYLY DOMAIN DEFAULT IMPLS.
///////////////////////////////////////////////////////////////////////////////////////////////////
	DATE_RANGE_DAYLY_DISCRETE_DOMAIN(new DiscreteDomain<Date>() {
												@Override
												public Date next(final Date value) {
													return new DateTime(value).plusDays(1).toDate();
												}
												@Override
												public Date previous(final Date value) {
													return new DateTime(value).minusDays(1).toDate();
												}
												@Override
												public long distance(final Date start,final  Date end) {
													return Days.daysBetween(new DateTime(start), new DateTime(end)).getDays();
												}
											});

	;
	@Getter final DiscreteDomain<Date> _discreteDomain;
///////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////////////////////////
	DateRangeDiscreteDomains(final DiscreteDomain<Date> discreteDomain){
		_discreteDomain = discreteDomain;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////
// SOME METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isIn(DateRangeDiscreteDomains... els){
		return false;
	}
	@Override
	public boolean is(DateRangeDiscreteDomains el) {
		return false;
	}
}



