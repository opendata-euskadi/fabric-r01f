package r01f.persistence.search.lucene;

import org.apache.lucene.analysis.Analyzer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.model.search.SearchFilter;
import r01f.patterns.IsBuilder;
import r01f.persistence.index.document.IndexDocumentFieldConfigSet;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class LuceneSearchQueryBuilder 
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static LuceneSearchQueryBuilderAnalyzerStep forFieldConfigSet(final IndexDocumentFieldConfigSet<?> fieldConfigSet) {
		return new LuceneSearchQueryBuilder() { /* nothing */ }
					.new LuceneSearchQueryBuilderAnalyzerStep(fieldConfigSet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LuceneSearchQueryBuilderAnalyzerStep {
		private final IndexDocumentFieldConfigSet<?> _fieldConfigSet;
		
		public LuceneSearchQueryBuilderUILanguageStep usingLuceneAnalyzer(final Analyzer analyzer) {
			return new LuceneSearchQueryBuilderUILanguageStep(_fieldConfigSet,
															  analyzer);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LuceneSearchQueryBuilderUILanguageStep {
		private final IndexDocumentFieldConfigSet<?> _fieldConfigSet;
		private final Analyzer _luceneAnalyzer;
		
		public LuceneSearchQueryBuilderPredicatesStep withUILanguage(final Language uiLang) {
			return new LuceneSearchQueryBuilderPredicatesStep(_fieldConfigSet,
															  _luceneAnalyzer,
															  uiLang);
		}
		public LuceneSearchQueryBuilderPredicatesStep noUILanguage() {
			return new LuceneSearchQueryBuilderPredicatesStep(_fieldConfigSet,
															  _luceneAnalyzer,
															  null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LuceneSearchQueryBuilderPredicatesStep {
		private final IndexDocumentFieldConfigSet<?> _fieldConfigSet;
		private final Analyzer _luceneAnalyzer;
		private final Language _uiLanguage;
		
		public <F extends SearchFilter> LuceneSearchQuery withFilterType(final Class<F> filterType) {
			return new LuceneSearchQuery(_fieldConfigSet,
										 _luceneAnalyzer,
										 _uiLanguage);
		}
	}
}
