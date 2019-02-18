package r01f.types.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Url;

@MarshallType(as="weightedTag")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class WeightedLangDependentTag
  implements Comparable<WeightedLangDependentTag>,
  			 HasLanguage {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
  	@MarshallField(as="tag",
  				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _tag;

   	@MarshallField(as="lang",
  				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _language;

  	@MarshallField(as="weight",
  				   whenXml=@MarshallFieldAsXml(attr=true))
  	@Getter @Setter private int _weight;

  	@MarshallField(as="url",
  				   whenXml=@MarshallFieldAsXml(attr=true))
  	@Getter @Setter private Url _url;
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
  	@Override
	public int compareTo(final WeightedLangDependentTag other) {
	    if (other == null) return 1;

    	int tagComp = this.getTag() != null && other.getTag() != null
    							? this.getTag().compareTo(other.getTag())
    							: this.getTag() != null && other.getTag() == null
    									? 1
    									: this.getTag() == null && other.getTag() != null
    											? -1
    											: 0;		// both null
    	int langComp = this.getLanguage() != null && other.getLanguage() != null
    							? this.getLanguage().compareTo(other.getLanguage())
    							: this.getLanguage() != null && other.getLanguage() == null
    									? 1
    									: this.getLanguage() == null && other.getLanguage() != null
    											? -1
    											: 0;		// both langs null
    	int weightComp = Integer.valueOf(this.getWeight()).compareTo(Integer.valueOf(other.getWeight()));

    	int urlComp = this.getUrl() != null && other.getUrl() != null
    						? this.getUrl().is(other.getUrl())
    								? 0
    								: -1
    						: this.getUrl() != null && other.getUrl() == null
	    							? 1
	    							: this.getUrl() == null && other.getUrl() != null
		    								? -1
		    								: 0; //noth null


    	int outComp = 0;
    	if (tagComp == 0 && langComp == 0 && urlComp == 0) {
    		outComp = weightComp;
    	} else if (tagComp == 0 && urlComp == 0 && langComp != 0) {
    		outComp = langComp;
    	} else if (tagComp != 0 && langComp == 0 && urlComp == 0) {
    		outComp = tagComp;
    	} else if (tagComp == 0 && langComp == 0 && urlComp != 0) {
    		outComp = urlComp;
    	} else if (tagComp != 0 && langComp != 0  && urlComp != 0) {
    		outComp = tagComp;
    	} else {
    		outComp = weightComp;
    	}
		return outComp;
	}
}
