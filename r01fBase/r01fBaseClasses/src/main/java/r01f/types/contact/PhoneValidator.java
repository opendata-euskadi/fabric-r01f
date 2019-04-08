package r01f.types.contact;

import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

     class PhoneValidator 
implements Validates<Phone> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<Phone> validate(final Phone phone) {
		if (Phone.VALID_PHONE_FORMAT_PATTERN.matcher(phone.asString()).find()) return ObjectValidationResultBuilder.on(phone)
																												   .isValid();
		return ObjectValidationResultBuilder.on(phone)
											.isNotValidBecause("The phone does NOT match the pattern: " + Phone.VALID_PHONE_FORMAT_PATTERN);
	}
}
