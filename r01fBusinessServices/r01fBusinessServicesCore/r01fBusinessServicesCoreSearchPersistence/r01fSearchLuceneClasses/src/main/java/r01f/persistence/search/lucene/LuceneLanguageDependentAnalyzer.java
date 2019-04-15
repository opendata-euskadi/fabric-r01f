package r01f.persistence.search.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;

import r01f.exceptions.Throwables;
import r01f.locale.Language;
import r01f.persistence.index.document.IndexDocumentFieldConfig;
import r01f.persistence.index.document.IndexDocumentFieldConfigSetByIndexableObjectType;
import r01f.persistence.index.document.IndexDocumentFieldID;
import r01f.persistence.lucene.LuceneConstants;
import r01f.util.types.Strings;

/**
 * Custom Lucene analyzer that
 * <ol>
 * 		<li>does the standard filtering</li>
 * 		<li>converts to lower-case</li>
 * 		<li>remove stop-words (language dependant)</li>
 * 		<li>change accents with it's no-accent equivalent</li>
 * </ol>
 */
public class LuceneLanguageDependentAnalyzer 
	 extends Analyzer {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides access to {@link IndexDocumentFieldConfig} by fieldId
	 * Beware that the only input available is the fieldId so maybe ALL indexable type's fields
	 * is traversed to find the config
	 */
	private final IndexDocumentFieldConfigSetByIndexableObjectType _fieldConfigSetsByObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LuceneLanguageDependentAnalyzer() {
		_fieldConfigSetsByObjectType = new IndexDocumentFieldConfigSetByIndexableObjectType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("resource")
	protected TokenStreamComponents createComponents(final String fieldIdStr,
													 final Reader reader) {
		IndexDocumentFieldID fieldId = IndexDocumentFieldID.forId(fieldIdStr);
		
		Tokenizer source = new ClassicTokenizer(LuceneConstants.VERSION,
												reader);
		
		// standard filter
	    TokenStream standardFilterStream = new StandardFilter(LuceneConstants.VERSION,
	    													  source);
	    // lower-case
	    TokenStream lowerCaseFilterStream = new LowerCaseFilter(LuceneConstants.VERSION,
	    														standardFilterStream);
	    // stop-words filter... this is language dependant
	    TokenStream stopFilterStream = null;
	    IndexDocumentFieldConfig<?> fieldCfg = _fieldConfigSetsByObjectType.getConfigOrNullFor(fieldId);
	    if (fieldCfg == null) throw new IllegalStateException(Throwables.message("The field {} is NOT configured at the {} instance provided to the {} analyzer. Please check that the corresponding {} instance is provided (if using guice look at [SearchGuiceModule]#getLuceneAnalyzer() guice module method)",
	    																		 fieldId,IndexDocumentFieldConfig.class.getSimpleName(),LuceneLanguageDependentAnalyzer.class,LuceneLanguageDependentAnalyzer.class));
	    if (fieldCfg.isLanguageDependent()) {
		    Language lang = Language.fromName(IndexDocumentFieldID.dynamicDimensionPointFromFieldId(fieldId));
		    if (lang == null) throw new IllegalStateException(Strings.customized("Could NOT analyze field with id={}: it was suppossed to be language dependent but it's NOT",
		    																	 fieldId));
		    CharArraySet stopWords = null;
		    switch (lang) {
			case SPANISH:
				stopWords = SpanishAnalyzer.getDefaultStopSet();
				break;
			case BASQUE:
				stopWords = BasqueAnalyzer.getDefaultStopSet();
				break;
			case FRENCH:
				stopWords = FrenchAnalyzer.getDefaultStopSet();
				break;
			case ENGLISH:
				stopWords = EnglishAnalyzer.getDefaultStopSet();
				break;
			case DEUTCH:
				stopWords = DutchAnalyzer.getDefaultStopSet();
				break;
			case ANY:
			default:
				stopWords = SpanishAnalyzer.getDefaultStopSet();
				break;
			}
		    stopFilterStream = new StopFilter(LuceneConstants.VERSION,
		    								  lowerCaseFilterStream,
		    								  stopWords);
	    } else {
	    	//throw new IllegalStateException("The " + LuceneLanguageDependantAnalyzer.class + " should ONLY be used on language dependant fields!");
	    }
	    // change accents for it's no-accent equivalent
	    TokenStream asciiFoldingFilterStream = stopFilterStream != null ? new ASCIIFoldingFilter(stopFilterStream)
	    																: new ASCIIFoldingFilter(lowerCaseFilterStream);
	    
	    TokenStream finalStream = asciiFoldingFilterStream;
	    
	    // return
	    return new TokenStreamComponents(source,finalStream);
	}
}
