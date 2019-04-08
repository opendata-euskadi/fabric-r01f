package r01f.model.search;

import java.io.Serializable;

import com.google.common.annotations.GwtIncompatible;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.Strings;

@MarshallType(as="searchFilterCriteriaString")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class SearchFilterAsCriteriaString
  implements CanBeRepresentedAsString,
  			 Serializable {
	
	private static final long serialVersionUID = 3196015344923111354L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="filter",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _filter;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR  & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static SearchFilterAsCriteriaString of(final String criteriaStr) {
		return new SearchFilterAsCriteriaString(criteriaStr);
	}
	public static SearchFilterAsCriteriaString of(final SearchFilter filter) {
		return filter.toCriteriaString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the filter contains data
	 */
	public boolean hasData() {
		return Strings.isNOTNullOrEmpty(_filter);
	}
	@Override
	public String asString() {
		return _filter;
	}
	/**
	 * @return the filter url encoded
	 */
	@GwtIncompatible
	public String asStringUrlEncoded() {
		return StringEncodeUtils.urlEncodeNoThrow(_filter)
								.toString();
	}
	@Override
	public String toString() {
		return this.asString();
	}
}
