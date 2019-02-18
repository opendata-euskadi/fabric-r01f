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
public class PersonBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  ID
/////////////////////////////////////////////////////////////////////////////////////////
	public static <ID extends PersonID> PersonBuilderNameStep<ID> createPersonWithId(final ID id) {
		Person<ID> person = new Person<ID>();
		person.setId(id);
		return new PersonBuilder() {/* nothing */}
						.new PersonBuilderNameStep<ID>(person) {/* nothing */};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NAME, SURNAME & SALUTATION
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonBuilderNameStep<ID extends PersonID> {
		private final Person<ID> _person;
		
		public PersonBuilderSurnamesStep<ID> withName(final String name) {
			_person.setName(name);
			return new PersonBuilderSurnamesStep<ID>(_person) {/* nothing */};
		}
		public PersonBuilderSurnamesStep<ID> noName() {
			return new PersonBuilderSurnamesStep<ID>(_person) {/* nothing */};
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonBuilderSurnamesStep<ID extends PersonID> {
		private final Person<ID> _person;
		
		public PersonBuilderSalutationStep<ID> withSurname(final String surname1) {
			_person.setSurname1(surname1);
			return new PersonBuilderSalutationStep<ID>(_person) {/* nothing */};
		}
		public PersonBuilderSalutationStep<ID> withSurnames(final String surname1,final String surname2) {
			_person.setSurname1(surname1);
			_person.setSurname2(surname2);
			return new PersonBuilderSalutationStep<ID>(_person) {/* nothing */};
		}
		public PersonBuilderSalutationStep<ID> noSurnames() {
			return new PersonBuilderSalutationStep<ID>(_person) {/* nothing */};
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonBuilderSalutationStep<ID extends PersonID> {
		private final Person<ID> _person;
		
		public PersonBuilderPrefLangStep<ID> useSalutation(final PersonSalutation salutation) {
			_person.setSalutation(salutation);
			return new PersonBuilderPrefLangStep<ID>(_person) {/* nothing */};
		}
		public PersonBuilderPrefLangStep<ID> noSalutation() {
			return new PersonBuilderPrefLangStep<ID>(_person) {/* nothing */};
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonBuilderPrefLangStep<ID extends PersonID> {
		private final Person<ID> _person;
		
		public PersonBuilderDetailsStep<ID> preferredLanguage(final Language lang) {
			_person.setPreferredLang(lang);
			return new PersonBuilderDetailsStep<ID>(_person) {/* nothing */};
		}
		public PersonBuilderDetailsStep<ID> noPreferredLanguage() {
			return new PersonBuilderDetailsStep<ID>(_person) {/* nothing */};
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonBuilderDetailsStep<ID extends PersonID> {
		private final Person<ID> _person;
		
		public PersonBuilderBuildStep<ID> withDetails(final String details) {
			_person.setDetails(details);
			return new PersonBuilderBuildStep<ID>(_person) {/* nothing */ };
		}
		public PersonBuilderBuildStep<ID> noDetails() {
			return new PersonBuilderBuildStep<ID>(_person) {/* nothing */ };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class PersonBuilderBuildStep<ID extends PersonID> {
		private final Person<ID> _person;
		
		public Person<ID> build() {
			return _person;
		}
	}
}
