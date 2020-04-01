package r01f.util.types;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import r01f.util.types.locale.Languages;

/**
 * Date to text converter for a given locale and in different formats.
 * StringToDateWithDayNameConverter.convertToText(Dates.now(), Languages.SPANISH);
 * 	convertToText:Miércoles, 1 de abril
 * 	convertToText:Apirilaren 1, Osteguna
 *
 * StringToDateWithDayNameConverter.convertToDayOfWeek(Dates.now(), Languages.SPANISH);
 *  convertToDayOfWeek:Miércoles 1
 *  convertToDayOfWeek:Osteguna 1
 *
 * StringToDateWithDayNameConverter.convertToTextWithYear(Dates.now(), Languages.SPANISH);
 * 	convertToTextWithYear:Miércoles, 1 de abril del 2020
 *  convertToTextWithYear:2020ko Apirilaren 1, Osteguna
 *
 * StringToDateWithDayNameConverter.convetToMonthYearFormat(Dates.now(), Languages.SPANISH);
 * 	convetToMonthYearFormat:Abril del 2020
 *  convetToMonthYearFormat:2020ko Apirila
 *
 * StringToDateWithDayNameConverter.convetToWeekYearFormat(Dates.now(), Languages.SPANISH);
 * 	convetToWeekYearFormat:Semana 14 del 2020
 *  convetToWeekYearFormat:2020ko 14 astea
 *
 * StringToDateWithDayNameConverter.convetToMonthYearSortFormat(Dates.now(), Languages.SPANISH);
 * 	convetToMonthYearSortFormat:Abr 2020
 *  convetToMonthYearSortFormat:2020 Api
 *
 * StringToDateWithDayNameConverter.convertToDate("Miércoles, 1 de abril", Languages.SPANISH);
 * 	convertToDate:Wed Apr 01 00:00:00 CET 1970
 *  convertToDate:null
 *
 */
@Slf4j
public abstract class StringToDateWithDayNameConverter {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////

	private static final String[] basquedays = {"Igandea", "Astelehena", "Asteartea", "Asteazkena", "Osteguna", "Ostirala", "Larunbata"};
	private static final String[] basqueMonths = {"Urtarrila", "Otsaila", "Martxoa", "Apirila", "Maiatza", "Ekaina", "Uztaila", "Abustua", "Iraila", "Urria", "Azaroa", "Abendua"};
	private static final String[] basqueMonthsSort = {"Urt", "Ots", "Mar", "Api", "Mai", "Eka", "Uzt", "Abu", "Ira", "Urr", "Aza", "Abe"};
	private static final String _semanaEs = "semana";
	private static final String _semanaEu = "astea";
	private static final String _semanaEn = "week";

/////////////////////////////////////////////////////////////////////////////////////////
//	FORMAT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private static DateFormat _getFormat(final Locale loc) {
		SimpleDateFormat  f = new SimpleDateFormat("EEEE, d 'de' MMMM", loc);
		f.setLenient(false);
		return f;
	}

	private static DateFormat _getFormatDayOfWeek(final Locale loc) {
		SimpleDateFormat  f = new SimpleDateFormat("EEEE d", loc);
		f.setLenient(false);
		return f;
	}

	private static DateFormat _getFormatWithYear(final Locale loc) {
		SimpleDateFormat  f = new SimpleDateFormat("EEEE, d 'de' MMMM 'del' yyyy", loc);
		f.setLenient(false);
		return f;
	}

	private static DateFormat _getMonthYearFormat(final Locale loc) {
		SimpleDateFormat  f = new SimpleDateFormat("MMMM 'del' yyyy", loc);
		f.setLenient(false);
		return f;
	}

	private static DateFormat _getMonthYearSortFormat(final Locale loc) {
		SimpleDateFormat  f = new SimpleDateFormat("MMM yyyy", loc);
		f.setLenient(false);
		return f;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public static Date convertToDate(final String dateText,
							  		 final Locale locale) {

		if (Strings.isNullOrEmpty(dateText) || locale == null) {
			log.warn("Could not convert null to Date, or locale is null.!!!");
			throw new IllegalArgumentException("Could not convert null to Date, or locale is null!!!");
		}

		String dateTrimmed = dateText.trim();
		ParsePosition parsePosition = new ParsePosition(0);
		Date parsedValue = _getFormat(locale).parse(dateTrimmed, parsePosition);

		if (parsePosition.getIndex() != dateTrimmed.length()) {
			log.warn("Could not convert {} to Date", dateTrimmed);
			return null;
		} else {
			return parsedValue;
		}
	}

	public static String convertToText(final Date theDate,
									   final Locale locale) {
		log.info("StringToDateWithDayNameConverter.convertToText the date {} in locale {} to text.", theDate, locale);

		if (theDate == null || locale == null) {
			log.warn("Could not convert null date to text, or locale is null.!!!");
			throw new IllegalArgumentException("Could not convert null date to text, or locale is null.!!!.");
		}

		if (Languages.BASQUE.equals(locale)) {

			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(theDate);

			StringBuilder sb = new StringBuilder();
			sb.append(Strings.capitalizeFirstLetter(basqueMonths[cal.get(Calendar.MONTH)] + "ren"))
				.append(" ")
				.append(cal.get(Calendar.DAY_OF_MONTH))
				.append(", ")
				.append(Strings.capitalizeFirstLetter(basquedays[cal.get(Calendar.DAY_OF_WEEK)]));

			return sb.toString();
		} else {
			return Strings.capitalizeFirstLetter(_getFormat(locale).format(theDate));
		}
	}

	public static String convertToDayOfWeek(final Date theDate,
									   		final Locale locale) {
		log.info("StringToDateWithDayNameConverter.convertToDayOfWeek the date {} in locale {} to day of week text.", theDate, locale);

		if (theDate == null || locale == null) {
			log.warn("Could not convert null date to text, or locale is null.!!!");
			throw new IllegalArgumentException("Could not convert null date to text, or locale is null.!!!.");
		}

		if (Languages.BASQUE.equals(locale)) {

			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(theDate);

			StringBuilder sb = new StringBuilder();
			sb.append(Strings.capitalizeFirstLetter(basquedays[cal.get(Calendar.DAY_OF_WEEK)]))
				.append(" ")
				.append(cal.get(Calendar.DAY_OF_MONTH));

			return sb.toString();
		} else {
			return Strings.capitalizeFirstLetter(_getFormatDayOfWeek(locale).format(theDate));
		}
	}

	public static String convertToTextWithYear(final Date theDate,
											   final Locale locale) {
		log.info("StringToDateWithDayNameConverter.convertToTextWithYear the date {} in locale {} to text.", theDate, locale);

		if (theDate == null || locale == null) {
			log.warn("Could not convert null date to text, or locale is null.!!!");
			throw new IllegalArgumentException("Could not convert null date to text, or locale is null.!!!.");
		}

		if (Languages.BASQUE.equals(locale)) {

			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(theDate);

			StringBuilder sb = new StringBuilder();
			sb.append(cal.get(Calendar.YEAR))
				.append("ko ")
				.append(Strings.capitalizeFirstLetter(basqueMonths[cal.get(Calendar.MONTH)] + "ren"))
				.append(" ")
				.append(cal.get(Calendar.DAY_OF_MONTH))
				.append(", ")
				.append(Strings.capitalizeFirstLetter(basquedays[cal.get(Calendar.DAY_OF_WEEK)]));

			return sb.toString();

		} else {
			return Strings.capitalizeFirstLetter(_getFormatWithYear(locale).format(theDate));
		}
	}

	public static String convetToMonthYearFormat(final Date theDate,
										  		 final Locale locale) {
		log.info("StringToDateWithDayNameConverter.convetToMonthYearFormat the date {} in locale {} to text.", theDate, locale);

		if (theDate == null || locale == null) {
			log.warn("Could not convert null date to text, or locale is null.!!!");
			throw new IllegalArgumentException("Could not convert null date to text, or locale is null.!!!.");
		}

		if (Languages.BASQUE.equals(locale)) {

			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(theDate);

			StringBuilder sb = new StringBuilder();
			sb.append(cal.get(Calendar.YEAR))
				.append("ko ")
				.append(Strings.capitalizeFirstLetter(basqueMonths[cal.get(Calendar.MONTH)]));

			return sb.toString();

		} else {
			return Strings.capitalizeFirstLetter(_getMonthYearFormat(locale).format(theDate));
		}
	}

	public static String convetToWeekYearFormat(final Date theDate,
										 		final Locale locale) {
		log.info("StringToDateWithDayNameConverter.convetToWeekYearFormat the date {} in locale {} to text.", theDate, locale);

		if (theDate == null || locale == null) {
			log.warn("Could not convert null date to text, or locale is null.!!!");
			throw new IllegalArgumentException("Could not convert null date to text, or locale is null.!!!.");
		}

		if (Languages.BASQUE.equals(locale)) {

			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(theDate);

			StringBuilder sb = new StringBuilder();
			sb.append(cal.get(Calendar.YEAR))
				.append("ko ")
				.append(cal.get(Calendar.WEEK_OF_YEAR))
				.append(" ")
				.append(_semanaEu);

			return sb.toString();

		} else {

			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(theDate);

			StringBuilder sb = new StringBuilder();
			if (Languages.SPANISH.equals(locale)) {
				sb.append(Strings.capitalizeFirstLetter(_semanaEs))
					.append(" ");
			} else {
				sb.append(Strings.capitalizeFirstLetter(_semanaEn))
					.append(" ");
			}
			sb.append(cal.get(Calendar.WEEK_OF_YEAR));

			if (Languages.SPANISH.equals(locale)) {
				sb.append(" del ");
			} else {
				sb.append(" of ");
			}

			sb.append(cal.get(Calendar.YEAR));

			return sb.toString();
		}
	}

	public static String convetToMonthYearSortFormat(final Date theDate,
											  		 final Locale locale) {
		log.info("StringToDateWithDayNameConverter.convetToMonthYearSortFormat the date {} in locale {} to text.", theDate, locale);

		if (theDate == null || locale == null) {
			log.warn("Could not convert null date to text, or locale is null.!!!");
			throw new IllegalArgumentException("Could not convert null date to text, or locale is null.!!!.");
		}

		if (Languages.BASQUE.equals(locale)) {

			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(theDate);

			StringBuilder sb = new StringBuilder();
			sb.append(cal.get(Calendar.YEAR))
				.append(" ")
				.append(Strings.capitalizeFirstLetter(basqueMonthsSort[cal.get(Calendar.MONTH)]));

			return sb.toString();

		} else {
			return Strings.capitalizeFirstLetter(_getMonthYearSortFormat(locale).format(theDate));
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	MAIN
/////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		System.out.println("--------------- CASTELLANO -----------------------------");
		System.out.println("convertToText:" + StringToDateWithDayNameConverter.convertToText(Dates.now(), Languages.SPANISH));
		System.out.println("convertToDayOfWeek:" + StringToDateWithDayNameConverter.convertToDayOfWeek(Dates.now(), Languages.SPANISH));
		System.out.println("convertToTextWithYear:" + StringToDateWithDayNameConverter.convertToTextWithYear(Dates.now(), Languages.SPANISH));
		System.out.println("convetToMonthYearFormat:" + StringToDateWithDayNameConverter.convetToMonthYearFormat(Dates.now(), Languages.SPANISH));
		System.out.println("convetToWeekYearFormat:" + StringToDateWithDayNameConverter.convetToWeekYearFormat(Dates.now(), Languages.SPANISH));
		System.out.println("convetToMonthYearSortFormat:" + StringToDateWithDayNameConverter.convetToMonthYearSortFormat(Dates.now(), Languages.SPANISH));
		System.out.println("convertToDate:" + StringToDateWithDayNameConverter.convertToDate(StringToDateWithDayNameConverter.convertToText(Dates.now(), Languages.SPANISH), Languages.SPANISH));

		System.out.println("--------------- EUSKARA -----------------------------");
		System.out.println("convertToText:" + StringToDateWithDayNameConverter.convertToText(Dates.now(), Languages.BASQUE));
		System.out.println("convertToDayOfWeek:" + StringToDateWithDayNameConverter.convertToDayOfWeek(Dates.now(), Languages.BASQUE));
		System.out.println("convertToTextWithYear:" + StringToDateWithDayNameConverter.convertToTextWithYear(Dates.now(), Languages.BASQUE));
		System.out.println("convetToMonthYearFormat:" + StringToDateWithDayNameConverter.convetToMonthYearFormat(Dates.now(), Languages.BASQUE));
		System.out.println("convetToWeekYearFormat:" + StringToDateWithDayNameConverter.convetToWeekYearFormat(Dates.now(), Languages.BASQUE));
		System.out.println("convetToMonthYearSortFormat:" + StringToDateWithDayNameConverter.convetToMonthYearSortFormat(Dates.now(), Languages.BASQUE));
		System.out.println("convertToDate:" + StringToDateWithDayNameConverter.convertToDate(StringToDateWithDayNameConverter.convertToText(Dates.now(), Languages.BASQUE), Languages.BASQUE));

	}
}
