package r01f.model.search.query;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.AscendingDescending;
import r01f.util.types.Strings;

@MarshallType(as="searchResultsOrdering")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class SearchResultsOrdering 
  implements Serializable {

	private static final long serialVersionUID = 2415352790939093983L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FieldID _fieldId;
	
	@MarshallField(as="direction",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AscendingDescending _direction;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static SearchResultsOrdering from(final FieldID fieldId,final AscendingDescending direction) {
		return new SearchResultsOrdering(fieldId,direction);
	}
	public static SearchResultsOrdering ascending(final FieldID fieldId) {
		return new SearchResultsOrdering(fieldId,AscendingDescending.ASCENDING);
	}
	public static SearchResultsOrdering descending(final FieldID fieldId) {
		return new SearchResultsOrdering(fieldId,AscendingDescending.DESCENDING);
	}
	private static final Pattern STR_SERIALIZED_PATTERN = Pattern.compile("([^(]+)(\\([^)]+\\))");
	public static SearchResultsOrdering valueOf(final String str) {
		Matcher m = STR_SERIALIZED_PATTERN.matcher(str);
		if (m.find()) {
			String fieldId = m.group(1);
			String direction = m.group(2);
			return new SearchResultsOrdering(FieldID.forId(fieldId),
											 AscendingDescending.fromCode(direction));
		}
		throw new IllegalArgumentException(str + " does NOT matches the " + SearchResultsOrdering.class.getSimpleName() +
										   " serialized pattern: " + STR_SERIALIZED_PATTERN);
	}
	public static SearchResultsOrdering fromString(final String str) {
		return SearchResultsOrdering.valueOf(str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public String toOrderingString() {
		return Strings.customized("{}({})",
								  _fieldId,_direction.getCode());
	}
}
