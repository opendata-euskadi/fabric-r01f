package r01f.persistence.index.document;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Using this type, the options the {@link IndexDocumentFieldConfig} values can be extended and overridden
 */
@MarshallType(as="indexDocumentFieldIndexOptions")
@Accessors(prefix="_")
@NoArgsConstructor
public class IndexDocumentFieldIndexOptions {
	/**
	 * Is the field indexed value tokenized?
	 */
	@MarshallField(as="tokenized")
	@Getter @Setter private boolean _tokenized;
	/**
	 * Boosting value
	 */
	@MarshallField(as="boost",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private float _boost = 1.0f;
	/**
	 * Norms values are omitted or not
	 * <pre>
	 * NOTE:	Norms are a value computed at index-time by combining the boosting values for the field and document
	 * 			Norms are loaded in memory as a floating-point at search-time in order to compute the relevance, so they uses memory space 
	 * </pre> 
	 */
	@MarshallField(as="omitNorms")
	@Getter @Setter private boolean _omitingNorms;
	/**
	 * Sets if the terms are stored "as is" in the index to get for example
	 * <ul>
	 * 		<li>Greater speed when highlighting matched terms in the search results</li>
	 * 		<li>related-terms searching</li>
	 * 		<li>automated categorization</li>
	 * </ul>
	 */
	@MarshallField(as="termVectorsStoring")
	@Getter @Setter private IndexDocumentFieldTypeTermVectorsStoringOptionsOverride _termVectorsStoring;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static final IndexDocumentFieldIndexOptions create() {
		IndexDocumentFieldIndexOptions outOptsOverride = new IndexDocumentFieldIndexOptions();
		outOptsOverride.notStoringTermVectors();
		return outOptsOverride;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public void merge(final IndexDocumentFieldIndexOptions other) {
		if (!_tokenized) _tokenized = other.isTokenized();
		if (!_omitingNorms) _omitingNorms = other.isOmitingNorms();
		if (_boost == 1.0f) _boost = other.getBoost();
		if (other.getTermVectorsStoring() != null) {
			if (_termVectorsStoring != null) {
				_termVectorsStoring.merge(other.getTermVectorsStoring());
			} else {
				_termVectorsStoring = other.getTermVectorsStoring();
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public IndexDocumentFieldIndexOptions tokenized() {
		_tokenized = true;
		return this;
	}
	public IndexDocumentFieldIndexOptions notTokenized() {
		_tokenized = false;
		return this;
	}
	public IndexDocumentFieldIndexOptions withBoost(final float boost) {
		_boost = boost;
		return this;
	}
	public IndexDocumentFieldIndexOptions withoutNorms() {
		_omitingNorms = true;
		return this;
	}
	public IndexDocumentFieldIndexOptions withNorms() {
		_omitingNorms = false;
		return this;
	}
	public IndexDocumentFieldIndexOptions notStoringTermVectors() {
		_termVectorsStoring = IndexDocumentFieldTypeTermVectorsStoringOptionsOverride.create()
																		  			 .notStored();
		return this;
	}
	public IndexDocumentFieldIndexOptions storingTermVectorsWithoutPositionsOrOffsets() {
		_termVectorsStoring = IndexDocumentFieldTypeTermVectorsStoringOptionsOverride.create()
																		  			 .store()
																		  			 .notIncludingTermOffsets()
																		  			 .notIncludingTermPositions();
		return this;
	}
	public IndexDocumentFieldIndexOptions storingTermVectorsWithPositions() {
		_termVectorsStoring = IndexDocumentFieldTypeTermVectorsStoringOptionsOverride.create()
																		  			 .store()
																		  			 .notIncludingTermOffsets()
																		  			 .includingTermPositions();
		return this;
	}
	public IndexDocumentFieldIndexOptions storingTermVectorsWithPositionsAndOffsets() {
		_termVectorsStoring = IndexDocumentFieldTypeTermVectorsStoringOptionsOverride.create()
																		  			 .store()
																		  			 .includingTermOffsets()
																		  			 .includingTermPositions();
		return this;
	}
}
