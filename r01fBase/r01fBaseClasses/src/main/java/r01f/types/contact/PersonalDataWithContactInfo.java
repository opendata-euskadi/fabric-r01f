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
public class PersonalDataWithContactInfo
  implements Serializable,
  			 HasSummaryFacet {

	private static final long serialVersionUID = 5346229482920059868L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Person data: name, surname, etc
	 */
	@MarshallField(as="personalData")
	@Getter @Setter private PersonalData _personalData;
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
		Summary summary = _personalData != null ? _personalData.asSummarizable()
															.getSummary()
										  : null;
		return SummarizableBuilder.summarizableFrom(summary);
	}

}
