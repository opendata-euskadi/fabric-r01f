package r01f.persistence.index.document;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Sets options about storing or not storing the field's term vector
 * The terms vector of a field can be stored in order to:
 * <ul>
 * 		<li>Get greater speed when highlighting matching-terms in search results</li>
 * 		<li>search by related terms</li>
 * 		<li>automated documents categorization</li>
 * </ul>
 */
@MarshallType(as="termVectorsStoringOptions")
@Accessors(prefix="_")
@NoArgsConstructor
public class IndexDocumentFieldTypeTermVectorsStoringOptionsOverride {
	/**
	 * Sets if each term and it's occurrences is stored 
	 */
	@MarshallField(as="store",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _enabled = false;
	/**
	 * Include the term payload (if this option is seted includeTermPositions option must be also enabled)
	 */
	@MarshallField(as="includeTermPayloads",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _includeTermPayLoads;
	/**
	 * Sets if the term position is stored or not 
	 */
	@MarshallField(as="includeTermPositions",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _includeTermPositions = false;
	/**
	 * Sets if the offset of each term is stored (init and end positions)
	 */
	@MarshallField(as="includeTermOffsets",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _includeTermOffsets = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static IndexDocumentFieldTypeTermVectorsStoringOptionsOverride create() {
		return new IndexDocumentFieldTypeTermVectorsStoringOptionsOverride();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public void merge(IndexDocumentFieldTypeTermVectorsStoringOptionsOverride options) {
		if (!_enabled) _enabled = options.isEnabled();
		if (!_includeTermPayLoads) _includeTermPayLoads = options.isIncludeTermPayLoads();
		if (!_includeTermPositions) _includeTermPositions = options.isIncludeTermPositions();
		if (!_includeTermOffsets) _includeTermOffsets = options.isIncludeTermOffsets();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride store() {
		_enabled = true;
		return this;
	}
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride notStored() {
		_enabled = false;
		_includeTermPositions = false;
		_includeTermOffsets = false;
		return this;
	}
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride includingTermPayloads() {
		_enabled = true;
		_includeTermPositions = true;	// es obligatorio si se incluyen los payloads
		_includeTermPayLoads = true;
		return this;
	}
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride notIncludingTermPayloads() {
		_enabled = true;
		_includeTermPayLoads = false;
		return this;
	}
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride includingTermPositions() {
		_enabled = true;
		_includeTermPositions = true;
		return this;
	}
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride notIncludingTermPositions() {
		_enabled = true;
		_includeTermPositions = false;
		return this;
	}
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride includingTermOffsets() {
		_enabled = true;
		_includeTermOffsets = true;
		return this;
	}
	public IndexDocumentFieldTypeTermVectorsStoringOptionsOverride notIncludingTermOffsets() {
		_enabled = true;
		_includeTermOffsets = false;
		return this;
	}
}
