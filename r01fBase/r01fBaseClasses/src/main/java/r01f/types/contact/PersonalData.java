package r01f.types.contact;

import java.io.Serializable;
import java.util.Date;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.FullTextSummarizable;
import r01f.facets.FullTextSummarizable.HasFullTextSummaryFacet;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryStringBacked;
import r01f.util.types.Strings;

/**
 * Person contact data
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="personalData")
@Accessors(prefix="_")
@NoArgsConstructor
public class PersonalData
  implements Serializable,
  			 HasSummaryFacet,
  			 HasFullTextSummaryFacet {

	private static final long serialVersionUID = -4090115487051906413L;

/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersonID _personId;
	/**
	 * Name
	 */
	@MarshallField(as="name",escape=true)
	@Getter @Setter protected String _name;
	/**
	 * Surname or first name
	 */
	@MarshallField(as="firstName",escape=true)
	@Getter @Setter protected String _surname1;
	/**
	 * Second name
	 */
	@MarshallField(as="secondName",escape=true)
	@Getter @Setter protected String _surname2;
	/**
	 * Mr, Ms, Doc, etc
	 */
	@MarshallField(as="salutation",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersonSalutation _salutation;
	/**
	 * Preferred language
	 */
	@MarshallField(as="preferredLang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Language _preferredLang;
	/**
	 * The person gender
	 */
	@MarshallField(as="gender",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersonGender _gender;
	/**
	 * Birth date 
	 */
	@MarshallField(as="birthDate",dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Date _birthDate;
	/**
	 * Details about the person
	 */
	@MarshallField(as="details",escape=true)
	@Getter @Setter protected String _details;
/////////////////////////////////////////////////////////////////////////////////////////
//  HasSummary
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return SummarizableBuilder.summarizableFrom(_composeSummary());
	}
	@Override
	public FullTextSummarizable asFullTextSummarizable() {
		return SummarizableBuilder.fullTextSummarizableFrom(this.asSummarizable());
	}
	/**
	 * Returns the surname composing the surname1 and surname2
	 * @return
	 */
	public String getSurname() {
		String outSurname = null;
		if (Strings.isNOTNullOrEmpty(_surname1) && Strings.isNOTNullOrEmpty(_surname2)) {
			outSurname = Strings.customized("{} {}",_surname1,_surname2);
		}
		else if (Strings.isNOTNullOrEmpty(_surname1)) {
			outSurname = _surname1;
		}
		else if (Strings.isNOTNullOrEmpty(_surname2)) {
			outSurname = _surname2;
		}
		return outSurname;
	}
	private Summary _composeSummary() {
		SummaryStringBacked outSummary = null;

		String surname = this.getSurname();

		if (Strings.isNOTNullOrEmpty(surname) && Strings.isNOTNullOrEmpty(_name)) {
			outSummary = SummaryStringBacked.of(Strings.customized("{}, {}",
																   surname,_name));
		}
		else if (Strings.isNOTNullOrEmpty(surname)) {
			outSummary = SummaryStringBacked.of(surname);
		}
		else if (Strings.isNOTNullOrEmpty(_name)) {
			outSummary = SummaryStringBacked.of(_name);
		}
		else {
			outSummary = SummaryStringBacked.of(Strings.customized("--NO summary for {}--",PersonalData.class));
		}
		return outSummary;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof PersonalData)) return false;

		PersonalData otherPerson = (PersonalData)obj;

		boolean nameEq = _name != null ? _name.equals(otherPerson.getName()) ? true : false
									   : true;	// both null
		boolean surname1Eq = _surname1 != null ? _surname1.equals(otherPerson.getSurname1()) ? true : false
											   : true;	// both null
		boolean surname2Eq = _surname2 != null ? _surname2.equals(otherPerson.getSurname1()) ? true : false
											   : true;	// both null
		boolean salutEq = _salutation != null ? _salutation.equals(otherPerson.getSalutation()) ? true : false
											   : true;	// both null
		boolean langEq = _preferredLang != null ? _preferredLang.equals(otherPerson.getPreferredLang()) ? true : false
											    : true;	// both null
		boolean genderEq = _gender != null ? _gender.equals(otherPerson.getGender()) ? true : false
										   : true;	// both null
		boolean detailsEq = _details != null ? _details.equals(otherPerson.getDetails()) ? true : false
										     : true;	// both null
		return  nameEq &&  surname1Eq && surname2Eq
						&& salutEq && langEq
						&& genderEq
						&& detailsEq;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_name,_surname1,_surname2,
								_salutation,_preferredLang,
								_gender,
								_details);
	}
}
