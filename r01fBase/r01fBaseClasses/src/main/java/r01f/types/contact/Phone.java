package r01f.types.contact;

import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.exceptions.Throwables;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="phone")
@Immutable
@Accessors(prefix="_")
@NoArgsConstructor
public class Phone
	 extends ValidatedContactMeanBase {

	private static final long serialVersionUID = 2718728842252439399L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@GwtIncompatible("User regex")
	public static final java.util.regex.Pattern VALID_PHONE_COUNTRY_CODE = java.util.regex.Pattern.compile("\\+[0-9]{2}");

	@GwtIncompatible("User regex")
	public static final java.util.regex.Pattern VALID_PHONE_FORMAT_PATTERN = java.util.regex.Pattern.compile("(" + VALID_PHONE_COUNTRY_CODE + ")?" +
																			 								 "([0-9]{9})");

	/*
	 * This is not used because in GWT is not possible to use inner classes.
	 */
//	@GwtIncompatible(value = "Atributte not compatible for GWT")
//	private r01f.patterns.Memoized<Boolean> _valid = new MemoizedPhoneValidator();

/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public Phone(final String phone) {
		super(phone);
	}
	public static Phone of(final String phone) {
		return Strings.isNOTNullOrEmpty(phone) ? new Phone(phone)
											   : null;
	}
	public static Phone valueOf(final String phone) {
		return Phone.of(phone);
	}
	public static Phone create(final String phone) {
		return Phone.of(phone);
	}

	@GwtIncompatible("User regex")
	public static Phone createValidating(final String phone) {
		if (!Phone.create(phone).isValid()) throw new IllegalArgumentException("Not a valid phone number!!");
		return Phone.of(phone);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean validate(final String phone) {
		return new PhoneValidator()
						.validate(new Phone(phone))
						.isValid();
	}
	@Override
	public boolean isValid() {
		return new PhoneValidator()
						.validate(this)
						.isValid();
	}
	@Override
	public String asString() {
		return Strings.isNOTNullOrEmpty(this.getId()) ? _sanitize(this.getId())	// remove all non-numeric or + characters
													  : null;
	}
	@Override
	public String toString() {
		return this.asString();
	}
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		} else if (other instanceof Phone) {
			Phone otherPhone = (Phone)other;
			return _sanitize(this.getId()).equals(otherPhone.toString());
		} else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return _sanitize(this.getId()).hashCode();
	}
	@GwtIncompatible("User regex")
	public String asStringWithoutCountryCode() {
		String outPhone = null;
		java.util.regex.Matcher m = VALID_PHONE_FORMAT_PATTERN.matcher(this.asStringEnsuringCountryCode("+00"));	// this will throw an exception if the phone number is invalid
		outPhone = m.find() ? m.group(2)	// phone (without country code)
								   : null;		// imposible
		return outPhone;
	}

	/**
	 * Returns the phone ensuring that it's prefixed with a provided country code
	 * it it's NOT already present
	 * @param defaultCountryCode
	 */
	@GwtIncompatible("User regex")
	public String asStringEnsuringCountryCode(final String defaultCountryCode) {
		String outPhone = null;
		String theDefaultCountryCode = !defaultCountryCode.startsWith("+") ? "+" + defaultCountryCode : defaultCountryCode;
		com.google.common.base.Preconditions.checkArgument(theDefaultCountryCode.length() == 3 && VALID_PHONE_COUNTRY_CODE.matcher(theDefaultCountryCode).find(),
														   "The provided default phone country code %s is NOT valid",defaultCountryCode);
		java.util.regex.Matcher m = VALID_PHONE_FORMAT_PATTERN.matcher(this.asString());
		if (m.find()) {
			String countryCode = null;
			String phoneNumber = null;
			countryCode = m.group(1);
			phoneNumber = m.group(2);
			if (Strings.isNullOrEmpty(countryCode)) countryCode = theDefaultCountryCode;

			//Note : Cannot use @Sl4j for GWT.....
			 if (countryCode != null && !countryCode.equals(theDefaultCountryCode)) {
				 org.slf4j.LoggerFactory.getLogger(this.getClass()).info("The phone {} has a country code={} which does NOT match the provided default country code={}: {} will be returned",
						 												 this.asString(),countryCode,theDefaultCountryCode,countryCode);
			}
			outPhone = countryCode + phoneNumber;
		} else {
			throw new IllegalStateException(Throwables.message("The phone number does NOT have a valid format: {}",
															   VALID_PHONE_FORMAT_PATTERN));
		}
		return outPhone;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODOS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sanitizes the phone number removing all non-numeric or +
	 * characters
	 * @param phoneAsString
	 * @return
	 */

	private static String _sanitize(final String phoneAsString) {
		String outPhone = phoneAsString.replaceAll("[^0-9^\\+]","");
		return outPhone;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STATIC COLLECTION UTIL METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public static String phonesAsString(final Collection<Phone> phones) {
		return ValidatedContactMean.colToString(phones);
	}
	/**
	 * Compose phone collection from string phones separated by comma, semicolon or space character.
	 * @param splitedPhonesStr
	 * @return phones collection or null if empty.
	 */
	public static Collection<ContactPhone> phonesCollectionFromString(final String splitedPhonesStr) {
		Collection<String> phones = _split(splitedPhonesStr);
		if (CollectionUtils.isNullOrEmpty(phones)) return null;
		return FluentIterable.from(phones)
							 .transform(str -> Phone.of(str))
							 .transform(phone -> ContactPhone.createToBeUsedFor(ContactInfoUsage.ANY)
									 						 .withNumber(phone))
							 .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,Phone> FROM_STRING_TRANSFORM = new Function<String,Phone>() {
																				@Override
																				public Phone apply(final String phone) {
																					return Phone.of(phone);
																				}
																	   };
	public static final Function<Phone,String> TO_STRING_TRANSFORM = new Function<Phone,String>() {
																			@Override
																			public String apply(final Phone phone) {
																				return phone.asString();
																			}
																  	   };
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// INNER CLASS FOR PHONE VALIDATIONS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//	public class MemoizedPhoneValidator extends r01f.patterns.Memoized<Boolean> {
//
//		private static final long serialVersionUID = 7868130344629476045L;
//
//		@Override
//		@GwtIncompatible(value="METHOD")
//		protected Boolean supply() {
//			return VALID_PHONE_FORMAT_PATTERN.matcher(Phone.this.asString()).find();
//		}
//
//	}



}
