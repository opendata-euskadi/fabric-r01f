package r01f.rest;

import java.util.Date;

import r01f.util.types.Dates;

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
public class RESTDateParam {
	private final Date _theDate;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTDateParam(final String dateMillisStr) {
		Long millis = Long.parseLong(dateMillisStr);
		_theDate = Dates.fromMillis(millis);
	}
	public RESTDateParam(final Date date) {
		_theDate = date;
	}
	public Date asDate() {
		return _theDate;
	}
}
