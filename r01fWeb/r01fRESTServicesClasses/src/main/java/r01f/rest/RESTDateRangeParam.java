package r01f.rest;

import java.util.Date;

import r01f.types.Range;

/**
 * Since a Jersey resource method param received from the query string (@QueryParam) or from the resource path (@PathParam)
 * can only be maped to a custom type if this type has:
 * <ul>
 * 	<li>A constructor from a String</li>
 *  <li>A valueOf(String) method<li>
 * </ul>
 * ... and {@link Date} type do not fall into any of theese categories, a custom type must be used
 * (see http://codahale.com/what-makes-jersey-interesting-parameter-classes/)
 */
public class RESTDateRangeParam {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final Range<Date> _theDateRange;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTDateRangeParam(final String dateRangeStr) {
		_theDateRange = Range.parse(dateRangeStr,Date.class);
	}
	public RESTDateRangeParam(final Range<Date> dateRange) {
		_theDateRange = dateRange;
	}
	public Range<Date> asDateRange() {
		return _theDateRange;
	}
}
