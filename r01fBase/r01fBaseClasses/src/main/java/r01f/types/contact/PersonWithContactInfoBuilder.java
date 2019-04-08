package r01f.types.contact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class PersonWithContactInfoBuilder 
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersonWithContactInfoBuilderPersonStep create() {
		return new PersonWithContactInfoBuilder() { /* nothing */ }
						.new PersonWithContactInfoBuilderPersonStep(new PersonWithContactInfo());
	}
	public static PersonWithContactInfo create(final Person<? extends PersonID> person,
											   final ContactInfo contactInfo) {
		return new PersonWithContactInfo(person,
										 	 contactInfo);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersonWithContactInfoBuilderPersonStep {
		private final PersonWithContactInfo _modelObj;
		
		public PersonWithContactInfoBuilderContactStep noPerson() {
			return new PersonWithContactInfoBuilderContactStep(_modelObj);
		}
		public PersonWithContactInfoBuilderContactStep forPerson(final Person<? extends PersonID> person) {
			_modelObj.setPerson(person);
			return new PersonWithContactInfoBuilderContactStep(_modelObj);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersonWithContactInfoBuilderContactStep {
		private final PersonWithContactInfo _modelObj;
		
		public PersonWithContactInfoBuilderBuildStep noContactInfo() {
			return new PersonWithContactInfoBuilderBuildStep(_modelObj);
		}
		public PersonWithContactInfoBuilderBuildStep withContactInfo(final ContactInfo contactInfo) {
			_modelObj.setContactInfo(contactInfo);
			return new PersonWithContactInfoBuilderBuildStep(_modelObj);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersonWithContactInfoBuilderBuildStep {
		private final PersonWithContactInfo _modelObj;
		
		public PersonWithContactInfo build() {
			return _modelObj;
		}
	}
}
