package r01f.types.contact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.IsBuilder;

/**
 * Builder for {@link Person} objects
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class PersonalDataBuilder
  implements IsBuilder {

/////////////////////////////////////////////////////////////////////////////////////////
//  NAME
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersonalDataBuilderSurnamesStep withName(final String name) {
		PersonalData person = new PersonalData();
		person.setName(name);
		return new PersonalDataBuilder() {/* nothing */}
						.new PersonalDataBuilderSurnamesStep(person) {/* nothing */};
	}
	public static PersonalDataBuilderSurnamesStep noName() {
		PersonalData person = new PersonalData();
		return new PersonalDataBuilder() {/* nothing */}
						.new PersonalDataBuilderSurnamesStep(person) {/* nothing */};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NAME, SURNAME & SALUTATION
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonalDataBuilderSurnamesStep {
		private final PersonalData _person;

		public PersonalDataBuilderSalutationStep withSurname(final String surname1) {
			_person.setSurname1(surname1);
			return new PersonalDataBuilderSalutationStep(_person) {/* nothing */};
		}
		public PersonalDataBuilderSalutationStep withSurnames(final String surname1,final String surname2) {
			_person.setSurname1(surname1);
			_person.setSurname2(surname2);
			return new PersonalDataBuilderSalutationStep(_person) {/* nothing */};
		}
		public PersonalDataBuilderSalutationStep noSurnames() {
			return new PersonalDataBuilderSalutationStep(_person) {/* nothing */};
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonalDataBuilderSalutationStep {
		private final PersonalData _person;

		public PersonalDataBuilderPrefLangStep useSalutation(final PersonSalutation salutation) {
			_person.setSalutation(salutation);
			return new PersonalDataBuilderPrefLangStep(_person) {/* nothing */};
		}
		public PersonalDataBuilderPrefLangStep noSalutation() {
			return new PersonalDataBuilderPrefLangStep(_person) {/* nothing */};
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonalDataBuilderPrefLangStep {
		private final PersonalData _person;

		public PersonalDataBuilderDetailsStep preferredLanguage(final Language lang) {
			_person.setPreferredLang(lang);
			return new PersonalDataBuilderDetailsStep(_person) {/* nothing */};
		}
		public PersonalDataBuilderDetailsStep noPreferredLanguage() {
			return new PersonalDataBuilderDetailsStep(_person) {/* nothing */};
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonalDataBuilderDetailsStep {
		private final PersonalData _person;

		public PersonalDataBuilderBuildStep withDetails(final String details) {
			_person.setDetails(details);
			return new PersonalDataBuilderBuildStep(_person) {/* nothing */ };
		}
		public PersonalDataBuilderBuildStep noDetails() {
			return new PersonalDataBuilderBuildStep(_person) {/* nothing */ };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonalDataBuilderBuildStep {
		private final PersonalData _person;

		public PersonalData build() {
			return _person;
		}
	}
}
