package r01f.types.contact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class PersonalDataWithContactInfoBuilder
		   implements IsBuilder {

/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersonalDataWithContactInfoBuilderPersonalDataStep create() {
		return new PersonalDataWithContactInfoBuilder() { /* nothing */ }
						.new PersonalDataWithContactInfoBuilderPersonalDataStep(new PersonalDataWithContactInfo());
	}
	public static PersonalDataWithContactInfo create(final PersonalData person,
											   final ContactInfo contactInfo) {
		return new PersonalDataWithContactInfo(person,
										 	 contactInfo);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersonalDataWithContactInfoBuilderPersonalDataStep {
		private final PersonalDataWithContactInfo _modelObj;

		public PersonalDataWithContactInfoBuilderContactStep noPersonalData() {
			return new PersonalDataWithContactInfoBuilderContactStep(_modelObj);
		}
		public PersonalDataWithContactInfoBuilderContactStep forPersonalData(final PersonalData person) {
			_modelObj.setPersonalData(person);
			return new PersonalDataWithContactInfoBuilderContactStep(_modelObj);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersonalDataWithContactInfoBuilderContactStep {
		private final PersonalDataWithContactInfo _modelObj;

		public PersonalDataWithContactInfoBuilderBuildStep noContactInfo() {
			return new PersonalDataWithContactInfoBuilderBuildStep(_modelObj);
		}
		public PersonalDataWithContactInfoBuilderBuildStep withContactInfo(final ContactInfo contactInfo) {
			_modelObj.setContactInfo(contactInfo);
			return new PersonalDataWithContactInfoBuilderBuildStep(_modelObj);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersonalDataWithContactInfoBuilderBuildStep {
		private final PersonalDataWithContactInfo _modelObj;

		public PersonalDataWithContactInfo build() {
			return _modelObj;
		}
	}
}
