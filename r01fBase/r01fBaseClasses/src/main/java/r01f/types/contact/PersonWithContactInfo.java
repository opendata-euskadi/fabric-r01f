package r01f.types.contact;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.summary.Summary;

@ConvertToDirtyStateTrackable
@MarshallType(as="personWithContact")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class PersonWithContactInfo
  implements Serializable,
		     HasSummaryFacet {
	
	private static final long serialVersionUID = 1530908840360246971L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Person data: name, surname, etc
	 */
	@MarshallField(as="person")
	@Getter @Setter private Person<? extends PersonID> _person;
	/**
	 * Contact Info
	 */
	@MarshallField(as="contactInfo")
	@Getter @Setter private ContactInfo _contactInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHOS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		Summary summary = _person != null ? _person.asSummarizable()
															.getSummary()
										  : null;
		return SummarizableBuilder.summarizableFrom(summary);
	}
}
