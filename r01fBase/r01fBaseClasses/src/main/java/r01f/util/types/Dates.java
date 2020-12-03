
package r01f.util.types;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.types.datetime.MonthOfYear;
import r01f.util.types.locale.Languages;

/**
 * Date utils
 * (see http://www.odi.ch/prog/design/datetime.php)
 */
public abstract class Dates {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String ES_DEFAULT_FORMAT = "dd/MM/yyyy";
	public static final String EU_DEFAULT_FORMAT = "yyyy/MM/dd";
	public static final String EPOCH = "MMM dd yyyy HH:mm:ss.SSS zzz";
	public static final String DEFAULT_FORMAT = ES_DEFAULT_FORMAT;
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final Map<Language,String> DATE_FORMATS_BY_LANG = Maps.toMap(Lists.newArrayList(Language.BASQUE,Language.SPANISH),
																			   new Function<Language,String>() {
																						@Override
																						public String apply(final Language lang) {
																							String outFormat = null;
																							switch(lang) {
																							case BASQUE:
																							case ENGLISH:
																								outFormat = "yyyy/MM/dd";
																								break;
																							case SPANISH:
																								outFormat = "dd/MM/yyyy";
																								break;
																							default:
																								outFormat = "yyyy/MM/dd";
																							}
																							return outFormat;
																						}
																			   });
	public static final Map<Language,String> DATE_HOURS_FORMATS_BY_LANG = Maps.toMap(Lists.newArrayList(Language.BASQUE,Language.SPANISH),
																			   new Function<Language,String>() {
																						@Override
																						public String apply(final Language lang) {
																							String outFormat = null;
																							switch(lang) {
																							case BASQUE:
																							case ENGLISH:
																								outFormat = "yyyy/MM/dd HH:mm:ss";
																								break;
																							case SPANISH:
																								outFormat = "dd/MM/yyyy HH:mm:ss";
																								break;
																							default:
																								outFormat = "yyyy/MM/dd HH:mm:ss";
																							}
																							return outFormat;
																						}
																			   });
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the time now
	 */
	@GwtIncompatible
	public static Date now() {
		return Calendar.getInstance().getTime();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if an object is a java.util.Date or a java.sql.Date
	 * @param obj
	 * @return
	 */
	public static <T> boolean isDate(final T obj) {
		return obj instanceof java.util.Date || obj instanceof java.sql.Date;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EPOCH see http://www.epochconverter.com/
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the epoch time
	 */
	public static long epochTimeStamp() {
		return System.currentTimeMillis();
	}
	/**
	 * Returns an epoch timestamp as a String human readable like "MMM dd yyyy HH:mm:ss.SSS zzz"
	 * @param epochTimeStamp
	 * @return
	 */
	@GwtIncompatible
	public static String epochTimeStampAsString(final long epochTimeStamp) {
		String date = new SimpleDateFormat(Dates.EPOCH)
								.format(new Date(epochTimeStamp*1000));
		return date;
	}
	/**
	 * Returns an epoch timetamp from it's human radable representation like "MMM dd yyyy HH:mm:ss.SSS zzz"
	 * @param epochTimeStampAsString
	 * @return
	 */
	@GwtIncompatible
	public static long epochTimeStampFromString(final String epochTimeStampAsString) {
		long epoch = 0;
		try {
			epoch = new SimpleDateFormat(Dates.EPOCH)
								.parse(epochTimeStampAsString).getTime() / 1000;
		} catch (ParseException parseEx) {
			parseEx.printStackTrace(System.out);
		}
		return epoch;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  AUX METODOS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the date format pattern depending on the provided language
	 * @param lang the language
	 * @param langFormats a map with the language patterns
	 * @return
	 */
	public static String langFormat(final Language lang,final Map<Language,String> langFormats) {
		String fmt = null;
		if (langFormats != null) {
			if (lang != null) fmt = langFormats.get(lang);
			if (fmt == null) fmt = langFormats.get(Language.DEFAULT);
			if (fmt == null) fmt = langFormats.get(Language.ENGLISH);	// english by default
		}
		if (fmt == null) fmt = DEFAULT_FORMAT;
		return fmt;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONVERSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 * Returns the Date as milis
	 * @param date
	 * @return
	 */
	public static long asMillis(final Date date) {
		return Dates.asEpochTimeStamp(date);
	}
	/**
	 * Return a Date as an epoch timeStamp
	 * @param date
	 * @return
	 */
	public static long asEpochTimeStamp(final Date date) {
		if (date == null) return Long.MIN_VALUE;
		return date.getTime();
	}
	/**
	 * Returns the Date as a Calendar
	 * @param date the Date
	 * @return the returned Calendar
	 */
	@GwtIncompatible
	public static GregorianCalendar asCalendar(final Date date) {
		if (date == null) return null;
		GregorianCalendar outCal = new GregorianCalendar();
		outCal.setTime(date);
		return outCal;
	}
	/**
	 * Returns the Date as a {@link Timestamp}
	 * @param date
	 * @return
	 */
	public static Timestamp asSqlTimestamp(final Date date) {
		if (date == null) return null;
		Timestamp outTS = new Timestamp(date.getTime());
		return outTS;
	}
	/**
	 * Returns a date from it's milis representation
	 * @param milis formato numrico
	 * @return string dd/mm/yyyy
	 */
	public static Date fromMillis(final long milis) {
		return Dates.fromEpochTimeStamp(milis);
	}
	/**
	 * Returns a date from it's epoch timestamp representation
	 * @param epochTimeStamp
	 * @return
	 */
	public static Date fromEpochTimeStamp(final long epochTimeStamp) {
		return new Date(epochTimeStamp);
	}
	/**
	 * Returns a {@link Date} from a calendar
	 * @param cal
	 * @return
	 */
	@GwtIncompatible
	public static Date fromCalendar(final Calendar cal) {
		if (cal == null) return null;
		return cal.getTime();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FORMAT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a date formated as ISO8601 (yyyy-MM-dd'T'HH:mm'Z') GMT (greenwich meridian time) / UTC (coordinate universal time) time
	 * (see http://www.timeanddate.com/time/gmt-utc-time.html and http://stackoverflow.com/questions/3914404/how-to-get-current-moment-in-iso-8601-format
	 *	  http://www.odi.ch/prog/design/datetime.php)
	 * @param date
	 * @return
	 */
	@GwtIncompatible
	public static String formatAsISO8601(final Date date) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat(Dates.ISO8601);
		df.setTimeZone(tz);
		String outISODate = df.format(date);
		return outISODate;
	}
	@GwtIncompatible
	public static String formatAsUTC(final Date date) {
		return Dates.formatAsISO8601(date);
	}
	/**
	 * Returns a date formated as epoch default format "MMM dd yyyy HH:mm:ss.SSS zzz"
	 * @param date
	 * @return
	 */
	@GwtIncompatible
	public static String formatAsEpochTimeStamp(final Date date) {
		return Dates.epochTimeStampAsString(date.getTime());
	}
	/**
	 * Gets the Date formated
	 * The format pattern can contain
	 * <pre>
	 *	  y -> Year
	 *	  M -> Month
	 *	  d -> Day
	 * </pre>
	 * It's also possible to return the milis formated date if the format param = milis
	 * @param date
	 * @param fmt
	 * @return
	 */
	@GwtIncompatible
	public static String format(final Date date,final String fmt) {
		return Dates.format(date,fmt,Locale.getDefault());
	}
	@GwtIncompatible
	public static String format(final LocalDate localDate,final String fmt) {
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		return Dates.format(date,fmt,Locale.getDefault());
	}
	@GwtIncompatible
	public static String format(final Range<Date> dateRange,final String fmt) {
		if (dateRange == null) return ".. - ..";
		return Strings.customized("{} - {}",
								  dateRange.hasLowerBound() ? Dates.format(dateRange.lowerEndpoint(),fmt) : "..",
								  dateRange.hasUpperBound() ? Dates.format(dateRange.upperEndpoint(),fmt) : "..");
	}
	@GwtIncompatible
	public static String format(final r01f.types.Range<Date> dateRange,final String fmt) {
		return Dates.format(dateRange != null ? dateRange.asGuavaRange() : null,
							fmt);
	}
	/**
	 * Formats a milis given date
	 * @param milis
	 * @param fmt
	 * @return
	 */
	@GwtIncompatible
	public static String format(final long milis,final String fmt) {
		Date date = Dates.fromMillis(milis);
		return Dates.format(date,fmt);
	}
	/**
	 * Gets the Date formated
	 * The format pattern can contain
	 * <pre>
	 *	  y -> Year
	 *	  M -> Month
	 *	  d -> Day
	 * </pre>
	 * @param date
	 * @param fmt
	 * @param locale format language. For Locale.English the timeZone is set to GMT (RSS uses this).
	 * @return
	 */
	@GwtIncompatible
	public static String format(final Date date,final String fmt,
								final Locale locale) {
		if (date == null) return null;

		String theFmt = Strings.isNullOrEmpty(fmt) ? DEFAULT_FORMAT : fmt; 		// Dates default format
		boolean isISO = theFmt.equalsIgnoreCase("iso")
					 || theFmt.equalsIgnoreCase("iso8601")
					 || theFmt.equalsIgnoreCase("utc");

		if (theFmt.equalsIgnoreCase("millis") || theFmt.equalsIgnoreCase("milis")) {	// millis bug WTF!
			return Long.toString(date.getTime());
		} else if (theFmt.equalsIgnoreCase("seconds")) {
			return Long.toString(date.getTime() / 1000L);
		} else if (theFmt.equalsIgnoreCase("epoch")) {
			theFmt = Dates.EPOCH;		// "MMM dd yyyy HH:mm:ss.SSS zzz"
		} else if (isISO) {
			theFmt = Dates.ISO8601;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(theFmt,locale);
		// Adjust to UTC for ISO time
		if (isISO) formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		// For the English Locale, the timeZone MUST be GMT
		if (locale.equals(Locale.ENGLISH)) {
			// Change the timezone to GMT
			TimeZone zone = formatter.getTimeZone();
			final int msInMin = 60000;
			final int minInHr = 60;
			int minutes = zone.getOffset(date.getTime()) / msInMin;
			int hours = minutes / minInHr;
			zone = TimeZone.getTimeZone( "GMT Time" + (hours >= 0 ? "+" : "") + hours + ":" + minutes);
			formatter.setTimeZone( zone );
		}
		return formatter.format(date);
	}
	/**
	 * Gets the date formated depending on the language
	 * @param date
	 * @param lang
	 * @param langFormats map with the language-dependent date formats
	 * @return
	 */
	@GwtIncompatible
	public static String format(final Date date,final Language lang,
								final Map<Language,String> langFormats) {
		String fmt = Dates.langFormat(lang,langFormats);
		return Dates.format(date,fmt);
	}
	public static DateLangFormat formatterFor(final Language lang) {
		DateLangFormat outFormat = DateLangFormat.DEFAULT;
		switch (lang) {
		case SPANISH:
			outFormat = DateLangFormat.SPANISH;
			break;
		case BASQUE:
			outFormat = DateLangFormat.BASQUE;
			break;
		case ENGLISH:
			outFormat = DateLangFormat.ENGLISH;
			break;
		default:
			outFormat = DateLangFormat.DEFAULT;
		}
		return outFormat;
	}
	/**
	 * Language-dependent formats
	 */
	@Accessors(prefix="_")
	@AllArgsConstructor
	public enum DateLangFormat {
		DEFAULT	(Language.DEFAULT,"dd/MM/yyyy"),
		SPANISH	(Language.SPANISH,"dd/MM/yyyy"),
		BASQUE	(Language.BASQUE,"yyyy/MM/dd"),
		ENGLISH	(Language.ENGLISH,"yyyy/MM/dd");

		@Getter private final Language _lang;
		@Getter private final String _dateFormat;
		@Getter private final String _timeToMinFormat = "HH:mm";
		@Getter private final String _timeTimeToSecFormat = "HH:mm:ss";

		public static DateLangFormat of(final Language lang) {
			DateLangFormat outFormat = null;
			switch (lang) {
			case SPANISH:
				outFormat = SPANISH;
				break;
			case BASQUE:
				outFormat = BASQUE;
				break;
			case ENGLISH:
				outFormat = ENGLISH;
				break;
			default:
				outFormat = DEFAULT;
			}
			return outFormat;
		}
		
		public String formatDate(final Date date) {
			return Dates.format(date,
								_dateFormat);
		}
		public String formatDateRange(final Range<Date> dateRange) {
			return Dates.format(dateRange,
								_dateFormat);
		}
		public String formatDateRange(final r01f.types.Range<Date> dateRange) {
			return this.formatDateRange(dateRange != null ? dateRange.asGuavaRange() : null);
		}
		public String formatDateWithTimeToMinutes(final Date date) {
			return Dates.format(date,
								_dateFormat + " " + _timeToMinFormat);
		}
		public String formatDateWithTimeToSeconds(final Date date) {
			return Dates.format(date,
								_dateFormat + " " + _timeTimeToSecFormat);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	FORMAT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a date from it's ISO representation
	 * @param dateStr
	 * @return
	 */
	@GwtIncompatible
	public static Date fromISO8601FormattedString(final String dateStr) {
		return Dates.fromFormatedString(dateStr,"iso");
	}
	@GwtIncompatible
	public static Date fromUTC(final String dateStr) {
		return Dates.fromFormatedString(dateStr,"iso");
	}
	/**
	 * Gets a date from it's string representation
	 * It returns null if the provided String representation cannot be parsed to a Date
	 * If the format parameter is "milis" or "millis" it assumes that the date string is in milliseconds
	 * If the format parameter is "epoch" it assumes that the date string is in epoch timestamp format
	 * If the format parameter is "seconds" it assumes that the date string is in seconds
	 * If the format parameter is "iso", "iso8601" or "utc" it assumes that the date string is in iso/utc format
	 * @param dateStr
	 * @param format
	 * @return
	 */
	@GwtIncompatible @SuppressWarnings("null")
	public static Date fromFormatedString(final String dateStr,final String format) {
		if (dateStr == null) return null;
		String theDateStr = new String(dateStr);

		String fmt = (format == null ) ? null
									   : new String(format);
		if (Strings.isNullOrEmpty(fmt)) fmt = DEFAULT_FORMAT;
		if (Strings.isNullOrEmpty(theDateStr)) return new Date();

		boolean isISO = fmt.equalsIgnoreCase("iso")
					 || fmt.equalsIgnoreCase("utc")
					 || fmt.equalsIgnoreCase("iso8601")
					 || fmt.equalsIgnoreCase(Dates.ISO8601);
		if (isISO) {
			// java 6 does NOT supports iso8601 date formatting... resorting to joda time
			// see
			DateTimeFormatter jtParser = ISODateTimeFormat.dateTimeParser();
			return jtParser.parseDateTime(theDateStr).toDate();
		}

		if ((fmt.equalsIgnoreCase("millis") || fmt.equalsIgnoreCase("milis")) && Numbers.isLong(dateStr)) {		// bug with millis WTF!
			return new Date( Long.parseLong(dateStr) );
		} else if (fmt.equalsIgnoreCase("seconds") && Numbers.isLong(dateStr)) {
			return new Date( Long.parseLong(dateStr)*1000L );
		} else if (fmt.equalsIgnoreCase("epoch")) {
			fmt = Dates.EPOCH;		// "MMM dd yyyy HH:mm:ss.SSS zzz"
		}
		SimpleDateFormat formatter = new SimpleDateFormat(fmt);
		formatter.setLenient(true);	// strict format
		ParsePosition pos = new ParsePosition(0);
		Date outDate = formatter.parse(theDateStr,pos);

		return outDate;
	}
	/**
	 * Gests a date from it's string language-dependent representation
	 * @param dateStr
	 * @param lang
	 * @param langFormats the language date formats
	 * @return the parsed date of null if the date cannot be parsed
	 */
	@GwtIncompatible
	public static java.util.Date fromLanguageFormatedString(final String dateStr,final Language lang,
															final Map<Language,String> langFormats) {
		if (dateStr == null) return null;
		String fmt = Dates.langFormat(lang,langFormats);
		return fromFormatedString(dateStr,fmt);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Reformats a date as string to another format
	 * @param dateStr the date in the source format
	 * @param sourceFormat the source format
	 * @param targetFormat the target format
	 * @return
	 */
	@GwtIncompatible
	public static String reformat(final String dateStr,
								  final String sourceFormat,final String targetFormat) {
		String theOldFmt = Strings.isNullOrEmpty(sourceFormat) ? DEFAULT_FORMAT : sourceFormat;
		String theNewFmt = Strings.isNullOrEmpty(targetFormat) ? DEFAULT_FORMAT : targetFormat;
		String theDateStr = Strings.isNullOrEmpty(dateStr) ? Dates.format(new Date(),theOldFmt) : dateStr;

		Date newDate = Dates.fromFormatedString(theDateStr,theOldFmt);
		return Dates.format(newDate,theNewFmt);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FORMATTING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the current date as a String
	 * @param language El lenguaje 0=Castellano, 1=Euskara
	 * @return La fecha actual como una cadena formateada segn el lenguaje
	 */
	@GwtIncompatible
	public static String currentDate(final Language language) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"),Languages.getLocale(language));
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		String outDate = null;
		switch (language) {
			case SPANISH:
				// Lunes, 25 de abril de 1995
				outDate = new StringBuilder()
								 .append(Dates.getDayOfWeekName(dayOfWeek,Language.SPANISH)).append(", ")
								 .append(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))).append(" de ")
								 .append(getMonthName(monthOfYear,Language.SPANISH)).append(" de ")
								 .append(Integer.toString(year)).toString();
				break;
			case BASQUE:
				outDate = new StringBuilder()
								 .append(Dates.getDayOfWeekName(dayOfWeek,Language.BASQUE)).append(", ")
								 .append(Integer.toString(year)).append("-ko ")
								 .append(getMonthName(monthOfYear,Language.BASQUE)).append("ren ")
								 .append(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))).toString();
				break;
			case ENGLISH:
				// 2012 Monday, April the 1st
				throw new IllegalArgumentException("english language... not implemented!");
			case FRENCH:
				throw new IllegalArgumentException("frech language... not implemented!");
			case DEUTCH:
				throw new IllegalArgumentException("english language... not implemented!");
			case ANY:
				throw new IllegalArgumentException("unknown language... not implemented!");
			default:
				outDate = day + "/" + monthOfYear + "/" + year;

		}
		return outDate;
	}
	public static String getDayOfWeekName(final int dayOfWeek,final Language language) {
		String outDayName = null;
		switch(language) {
			case SPANISH:
				outDayName = _getDayOfWeekInCastellano(dayOfWeek);
				break;
			case BASQUE:
				outDayName = _getDayOfWeekInEuskera(dayOfWeek);
				break;
			case ENGLISH:
				outDayName = _getDayOfWeekInEnglish(dayOfWeek);
				break;
			case FRENCH:
				throw new IllegalArgumentException("frech language... not implemented!");
			case DEUTCH:
				throw new IllegalArgumentException("english language... not implemented!");
			case ANY:
				throw new IllegalArgumentException("unknown language... not implemented!");
			default:
				outDayName = "";
		}
		return outDayName;
	}
	public static String getMonthName(final MonthOfYear month,final Language language) {
		return Dates.getMonthName(month.asInteger(),language);
	}
	public static String getMonthName(final int month,final Language language) {
		String outMonthName = null;
		switch(language) {
			case SPANISH:
				outMonthName = _getMonthNameInCastellano(month);
				break;
			case BASQUE:
				outMonthName = _getMonthNameInEuskera(month);
				break;
			case ENGLISH:
				outMonthName = _getMonthNameInEnglish(month);
				break;
			case FRENCH:
				throw new IllegalArgumentException("frech language... not implemented!");
			case DEUTCH:
				throw new IllegalArgumentException("english language... not implemented!");
			case ANY:
				throw new IllegalArgumentException("unknown language... not implemented!");
			default:
				outMonthName = "";
		}
		return outMonthName;
	}
	private static String _getDayOfWeekInEuskera(final int dayOfWeek) {
		String outDayOfWeek = null;
		switch(dayOfWeek) {
			case Calendar.SUNDAY:   outDayOfWeek = "Igandea";	break;
			case Calendar.MONDAY:   outDayOfWeek = "Astelehena";break;
			case Calendar.TUESDAY:  outDayOfWeek = "Asteartea";	break;
			case Calendar.WEDNESDAY:outDayOfWeek = "Asteazkena";break;
			case Calendar.THURSDAY: outDayOfWeek = "Osteguna";	break;
			case Calendar.FRIDAY:   outDayOfWeek = "Ostirala";	break;
			case Calendar.SATURDAY: outDayOfWeek = "Larunbata";	break;
			default:				outDayOfWeek = "";
		}
		return outDayOfWeek;
	}
	private static String _getDayOfWeekInCastellano(final int dayOfWeek) {
		String outDayOfWeek = null;
		switch(dayOfWeek) {
			case Calendar.SUNDAY:   outDayOfWeek = "Domingo";	break;
			case Calendar.MONDAY:   outDayOfWeek = "Lunes";		break;
			case Calendar.TUESDAY:  outDayOfWeek = "Martes";	break;
			case Calendar.WEDNESDAY:outDayOfWeek = "Miércoles";	break;
			case Calendar.THURSDAY: outDayOfWeek = "Jueves";	break;
			case Calendar.FRIDAY:   outDayOfWeek = "Viernes";	break;
			case Calendar.SATURDAY: outDayOfWeek = "Sábado";	break;
			default:				outDayOfWeek = "";
		}
		return outDayOfWeek;
	}
	private static String _getDayOfWeekInEnglish(final int dayOfWeek) {
		String outDayOfWeek = null;
		switch(dayOfWeek) {
			case Calendar.SUNDAY:   outDayOfWeek = "Sunday";	break;
			case Calendar.MONDAY:   outDayOfWeek = "Monday";	break;
			case Calendar.TUESDAY:  outDayOfWeek = "Tuesday";	break;
			case Calendar.WEDNESDAY:outDayOfWeek = "Wednesday";	break;
			case Calendar.THURSDAY: outDayOfWeek = "Thursday";	break;
			case Calendar.FRIDAY:   outDayOfWeek = "Friday";	break;
			case Calendar.SATURDAY: outDayOfWeek = "Saturday";	break;
			default:				outDayOfWeek = "";
		}
		return outDayOfWeek;
	}
	private static String _getMonthNameInEuskera(final int month) {
		String outMonthName = null;
		switch(month) {
			case Calendar.JANUARY:  outMonthName = "Urtarrila"; break;
			case Calendar.FEBRUARY: outMonthName = "Otsaila";  break;
			case Calendar.MARCH:	outMonthName = "Martxoa";  break;
			case Calendar.APRIL:	outMonthName = "Aprila";   break;
			case Calendar.MAY:	  outMonthName = "Maiatza";  break;
			case Calendar.JUNE:	 outMonthName = "Ekaina";   break;
			case Calendar.JULY:	 outMonthName = "Uztaila";  break;
			case Calendar.AUGUST:   outMonthName = "Abuztua";  break;
			case Calendar.SEPTEMBER:outMonthName = "Iraila";   break;
			case Calendar.OCTOBER:  outMonthName = "Urria";	break;
			case Calendar.NOVEMBER: outMonthName = "Azaroa";   break;
			case Calendar.DECEMBER: outMonthName = "Abendua";  break;
			default:				outMonthName = "";
		}
		return outMonthName;
	}
	private static String _getMonthNameInCastellano(final int month) {
		String outMonthName = null;
		switch(month) {
			case Calendar.JANUARY:  outMonthName = "Enero";	 break;
			case Calendar.FEBRUARY: outMonthName = "Febrero";   break;
			case Calendar.MARCH:	outMonthName = "Marzo";	 break;
			case Calendar.APRIL:	outMonthName = "Abril";	 break;
			case Calendar.MAY:	  outMonthName = "Mayo";	  break;
			case Calendar.JUNE:	 outMonthName = "Junio";	 break;
			case Calendar.JULY:	 outMonthName = "Julio";	 break;
			case Calendar.AUGUST:   outMonthName = "Agosto";	break;
			case Calendar.SEPTEMBER:outMonthName = "Septiembre";break;
			case Calendar.OCTOBER:  outMonthName = "Octubre";   break;
			case Calendar.NOVEMBER: outMonthName = "Noviembre"; break;
			case Calendar.DECEMBER: outMonthName = "Diciembre"; break;
			default:				outMonthName = "";
		}
		return outMonthName;
	}
	private static String _getMonthNameInEnglish(final int month) {
		String outMonthName = null;
		switch(month) {
			case Calendar.JANUARY:  outMonthName = "January";	 break;
			case Calendar.FEBRUARY: outMonthName = "February";   break;
			case Calendar.MARCH:	outMonthName = "March";	 break;
			case Calendar.APRIL:	outMonthName = "April";	 break;
			case Calendar.MAY:	  outMonthName = "May";	  break;
			case Calendar.JUNE:	 outMonthName = "June";	 break;
			case Calendar.JULY:	 outMonthName = "July";	 break;
			case Calendar.AUGUST:   outMonthName = "August";	break;
			case Calendar.SEPTEMBER:outMonthName = "September";break;
			case Calendar.OCTOBER:  outMonthName = "October";   break;
			case Calendar.NOVEMBER: outMonthName = "November"; break;
			case Calendar.DECEMBER: outMonthName = "December"; break;
			default:				outMonthName = "";
		}
		return outMonthName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Swift a date to its maximun, ie if the given date is  2197/03/25 11:44:00
	 * it'll be swifteed to 2017/03/25 23:59:999
	 * @param
	 * @return
	 */
	@GwtIncompatible
	public static Date rollDateToMaximum(final Date date) {
		Calendar theCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"),new Locale("es","ES"));
		theCal.setTime(date);
		return Dates.rollCalendarToMaximum(theCal).getTime();
	}
	/**
	 * Swift a date to its minimum, ie if the given date is  2197/03/25 11:44:00
	 * it'll be swifteed to 2017/03/25 00:00:000
	 * @param date
	 * @return
	 */
	@GwtIncompatible
	public static Date rollDateToMinimum(final Date date) {
		Calendar theCal = Calendar.getInstance();
		theCal.setTime(date);
		return Dates.rollCalendarToMinimum(theCal).getTime();
	}
	/**
	 * Swift a date to its maximun, ie if the given date is  2197/03/25 11:44:00
	 * it'll be swifteed to 2017/03/25 23:59:999
	 * @param theCal la fecha
	 * @return otra fecha en el ultimo mili
	 */
	@GwtIncompatible
	public static Calendar rollCalendarToMaximum(final Calendar theCal) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"),new Locale("es","ES"));
		cal.setTime(theCal.getTime());
		cal.set(Calendar.HOUR_OF_DAY,cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE,cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.MILLISECOND,cal.getActualMaximum(Calendar.MILLISECOND));
		return cal;
	}
	/**
	 * Swift a date to its minimum, ie if the given date is  2197/03/25 11:44:00
	 * it'll be swifteed to 2017/03/25 23:59:999
	 * @param theCal
	 * @return
	 */
	@GwtIncompatible
	public static Calendar rollCalendarToMinimum(final Calendar theCal) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(theCal.getTime());
		cal.set(Calendar.HOUR_OF_DAY,cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE,cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.MILLISECOND,cal.getActualMinimum(Calendar.MILLISECOND));
		return cal;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static long DAY_MILIS = 24 * 60 * 60 * 1000;
	/**
	 * Returns a Joda-Time's {@link Interval} object with a date day start and day end
	 * @param date
	 * @return
	 */
	@GwtIncompatible
	public static Interval dayIntervalOf(final Date date) {
		DateTime dayStart = Dates.dayStartOf(date);
		DateTime nextDayStart = Dates.nextDayStartOf(date);
		Interval dateInterval = new Interval(dayStart,nextDayStart);
		return dateInterval;
	}
	/**
	 * Returns the first day datetime (00:00:01)
	 * @param date
	 * @return
	 */
	@GwtIncompatible
	public static DateTime dayStartOf(final Date date) {
		DateTime dateTime = new DateTime(date,
										 DateTimeZone.getDefault());
		DateTime dayStart = dateTime.withTimeAtStartOfDay();
		return dayStart;
	}
	/**
	 * Returns the first day datetime (00:00:01)
	 * @param date
	 * @return
	 */
	@GwtIncompatible
	public static DateTime nextDayStartOf(final Date date) {
		DateTime dateTime = new DateTime(date,
										 DateTimeZone.getDefault());
		DateTime dayEnd = dateTime.plusDays(1)
								  .withTimeAtStartOfDay();
		return dayEnd;
	}
	/**
	 * Returns the first instant of a week given it's number within a year
	 * @param year
	 * @param weekOfYear
	 * @return
	 */
	@GwtIncompatible
	public static DateTime weekFirstInstant(final int year,final int weekOfYear) {
		Partial weekFirstInstant = new Partial(
				   new DateTimeFieldType[] {DateTimeFieldType.weekyear(),DateTimeFieldType.weekOfWeekyear(),DateTimeFieldType.dayOfWeek(),
				   							DateTimeFieldType.hourOfDay(),DateTimeFieldType.minuteOfHour(),DateTimeFieldType.secondOfMinute(),DateTimeFieldType.millisOfSecond()},
											new int[] {year,weekOfYear,1,	// first day of week
													   0,0,0,1});			// first mili of week
		return weekFirstInstant.toDateTime(new DateTime());
	}
	/**
	 * Returns the first instant of a week given it's number within a year
	 * @param year
	 * @param weekOfYear
	 * @return
	 */
	@GwtIncompatible
	public static DateTime weekLastInstant(final int year,final int weekOfYear) {
		return Dates.weekFirstInstant(year,weekOfYear)
						.plusWeeks(1)
						.minusMillis(2);
	}
	/**
	 * Returns the first instant of a month
	 * @param year
	 * @param monthOfYear
	 * @return
	 */
	@GwtIncompatible
	public static DateTime monthFirstInstant(final int year,final int monthOfYear) {
		Partial monthFirstInstant = new Partial(
				   new DateTimeFieldType[] {DateTimeFieldType.weekyear(),DateTimeFieldType.monthOfYear(),DateTimeFieldType.dayOfMonth(),
				   							DateTimeFieldType.hourOfDay(),DateTimeFieldType.minuteOfHour(),DateTimeFieldType.secondOfMinute(),DateTimeFieldType.millisOfSecond()},
											new int[] {year,monthOfYear,1,	// first day of month
													   0,0,0,1});			// first mili of month
		return monthFirstInstant.toDateTime(new DateTime());
	}
	/**
	 * Returns the first instant of a month
	 * @param year
	 * @param monthOfYear
	 * @return
	 */
	@GwtIncompatible
	public static DateTime monthLastInstant(final int year,final int monthOfYear) {
		return Dates.monthFirstInstant(year,monthOfYear)
						.plusMonths(1)
						.minusMillis(2);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RANGES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a Range object for a day
	 * @param date
	 * @return
	 */
	public static Range<Date> dayDateRangeOf(final Date date) {
		Interval dayInterval = Dates.dayIntervalOf(date);
		return Range.closed(dayInterval.getStart().toDate(),dayInterval.getEnd().toDate());
	}
}
