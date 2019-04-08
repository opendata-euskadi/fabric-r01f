package r01f.persistence.lucene;

import java.io.IOException;
import java.util.Set;

import lombok.NoArgsConstructor;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;

import r01f.util.types.collections.CollectionUtils;

import com.google.common.collect.Sets;

/**
 * Utility type that pages across all search results pages to collect all the result {@link Document}s
 * Usage: 
 * <pre class='brush:java'>
 * 		Set<Document> allDocs = LuceneResultsCollector.builder()
 * 															.forQuery(qry)
 * 															.sortedBy(orderFields)
 * 															.searchingWith(indexSearcher)
 * 															.withPageSize(10)
 * 							  						  .build()
 * 															.collectAll();
 * </pre>
 * @param <F>
 * @param <I>
 */
class LuceneResultsCollector {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Query _query;
	private final IndexSearcher _searcher;
	private final int _pageSize;
	private final Sort _sort;
	
	private int _currStartPointer = 0;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private LuceneResultsCollector(final Query query,final Set<SortField> sortFields,
								   final IndexSearcher searcher,
								   final int pageSize) {
		_query = query;
		_searcher = searcher;
		_pageSize = pageSize;
		
		SortField[] theSortFields = null;
		if (CollectionUtils.hasData(sortFields)) theSortFields = CollectionUtils.toArray(sortFields,SortField.class);
		_sort = CollectionUtils.hasData(theSortFields) ? new Sort(theSortFields)
													   : null;
	}
	public static LuceneResultsCollectorBuilder builder() {
		return new LuceneResultsCollectorBuilder();
	}
	@NoArgsConstructor
	static class LuceneResultsCollectorBuilder {
		private Query _query;
		private IndexSearcher _searcher;
		private int _pageSize;
		private Set<SortField> _sortFields;
		
		public LuceneResultsCollectorBuilder forQuery(final Query query) {
			_query = query;
			return this;
		}
		public LuceneResultsCollectorBuilder sortedBy(final Set<SortField> sortFields) {
			_sortFields = sortFields;
			return this;
		}
		public LuceneResultsCollectorBuilder withPageSize(final int pageSize) {
			_pageSize = pageSize;
			return this;
		}
		public LuceneResultsCollectorBuilder searchingWith(final IndexSearcher searcher) {
			_searcher = searcher;
			return this;
		}
		public LuceneResultsCollector build() {
			return new LuceneResultsCollector(_query,_sortFields,
											  _searcher,_pageSize);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return all page documents
	 */
	public Set<Document> collectAll() throws IOException {
		Set<Document> outDocs = null;
		
		// Get first page
		TopDocs topDocs = _topDocs();				// first page
		if (topDocs != null && topDocs.totalHits > 0) {
			outDocs = Sets.newLinkedHashSetWithExpectedSize(topDocs.totalHits);
			
			// Iterate over all the result pages
			while (topDocs != null && CollectionUtils.hasData(topDocs.scoreDocs)) {
				// Put the page in the return set
				int start = _currStartPointer;
				int end = Math.min(_currStartPointer+_pageSize,
								   topDocs.totalHits);
				for (int i=start; i < end; i++) {	// for (ScoreDoc scoredDoc : topDocs.scoreDocs) {
					Document doc = _searcher.doc(topDocs.scoreDocs[i].doc);
					outDocs.add(doc);
				}
				
				// Goto the next page (if it exists)
				_currStartPointer = _currStartPointer + end; 					// next page
				if (_currStartPointer < topDocs.totalHits) {
					topDocs = _topDocs();	
				} else {
					topDocs = null;
				}
			}
		}
		return outDocs;
	}
	private TopDocs _topDocs() throws IOException {
		return _sort != null ? _searcher.search(_query,
								   				_currStartPointer + _pageSize,
								   				_sort)
							 : _searcher.search(_query,
							 					_currStartPointer + _pageSize);
	}
}
 