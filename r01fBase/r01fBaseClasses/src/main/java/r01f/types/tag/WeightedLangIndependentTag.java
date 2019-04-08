package r01f.types.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="weightedTag")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class WeightedLangIndependentTag
  implements Comparable<WeightedLangIndependentTag> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
  	@MarshallField(as="tag",
  				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _tag;

  	@MarshallField(as="weight",
  				   whenXml=@MarshallFieldAsXml(attr=true))
  	@Getter @Setter private int _weight;
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
  	@Override
	public int compareTo(final WeightedLangIndependentTag other) {
	    if (other == null) return 1;

	    int outComp = 0;
	    if (this.getTag() != null && other.getTag() != null) {
	    	int tagComp = this.getTag().compareTo(other.getTag());
	    	outComp = tagComp == 0 ? Integer.valueOf(this.getWeight()).compareTo(Integer.valueOf(other.getWeight()))
	    						   : tagComp;
	    } else if (this.getTag() != null && other.getTag() == null) {
	    	outComp = 1;
	    } else if (this.getTag() == null && other.getTag() != null) {
	    	outComp = -1;
	    } else {
	    	outComp = Integer.valueOf(this.getWeight()).compareTo(Integer.valueOf(other.getWeight()));
	    }
		return outComp;
	}
}
