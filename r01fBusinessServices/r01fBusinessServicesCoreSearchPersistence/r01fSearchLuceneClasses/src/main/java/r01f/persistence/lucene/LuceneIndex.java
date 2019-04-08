package r01f.persistence.lucene;

import java.io.Closeable;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.google.common.base.Stopwatch;

import lombok.extern.slf4j.Slf4j;
import r01f.persistence.search.lucene.LucenePageResults;
import r01f.util.types.collections.CollectionUtils;

@Singleton
@Slf4j
public class LuceneIndex 
  implements Closeable {
/////////////////////////////////////////////////////////////////////////////////////////
// 	PRIVATE STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Analyzer _luceneAnalyzer;
	
	private final IndexWriter _indexWriter;
	private final TrackingIndexWriter _trackingIndexWriter;
	private final ReferenceManager<IndexSearcher> _indexSearcherReferenceManager;
	private final ControlledRealTimeReopenThread<IndexSearcher> _indexSearcherReopenThread;
	
	private long _reopenToken;		// index update/delete methods returned token
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public LuceneIndex(final Directory luceneDirectory,
					   final Analyzer analyzer) {
		_luceneAnalyzer = analyzer;
		try {
			// [1]: Create the indexWriter
			_indexWriter = new IndexWriter(luceneDirectory,
										   new IndexWriterConfig(LuceneConstants.VERSION,
										   					  	 analyzer));
			
			// [2a]: Create the TrackingIndexWriter to track changes to the delegated previously created IndexWriter 
			_trackingIndexWriter = new TrackingIndexWriter(_indexWriter);
			
			// [2b]: Create an IndexSearcher ReferenceManager to safely share IndexSearcher instances across
			//		 multiple threads
			_indexSearcherReferenceManager = new SearcherManager(_indexWriter,
																 true,
																 null);
			
			// [3]: Create the ControlledRealTimeReopenThread that reopens the index periodically having into 
			//		account the changes made to the index and tracked by the TrackingIndexWriter instance
			// 		The index is refreshed every 60sc when nobody is waiting 
			//		and every 100 millis whenever is someone waiting (see search method)
			//		(see http://lucene.apache.org/core/4_3_0/core/org/apache/lucene/search/NRTManagerReopenThread.html)
			_indexSearcherReopenThread = new ControlledRealTimeReopenThread<IndexSearcher>(_trackingIndexWriter,
																			 		       _indexSearcherReferenceManager,
																			 		       60.00,	// when there is nobody waiting
																			 		       0.1); 	// when there is someone waiting
			_indexSearcherReopenThread.start();	// start the refresher thread
		} catch (IOException ioEx) {
			throw new IllegalStateException("Lucene index could not be created: " + ioEx.getMessage());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FINALIZE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ANALYZER
/////////////////////////////////////////////////////////////////////////////////////////
	public Analyzer getAnalyzer() {
		return _luceneAnalyzer;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INDEX MAINTEINANCE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Opens the index
	 */
	@SuppressWarnings("static-method")
	public void open() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void close() {
		try {
			// stop the index reader re-open thread
			_indexSearcherReopenThread.interrupt();
			_indexSearcherReopenThread.close();
			
			// Close the indexWriter, committing everything that's pending
			_indexWriter.commit();
			_indexWriter.close();
			
		} catch(IOException ioEx) {
			log.error("Error while closing lucene index: {}",ioEx.getMessage(),
											 		   	     ioEx);
		}
	}
	/**
	 * Merges the lucene index segments into one
	 * (this should NOT be used, only rarely for index mainteinance)
	 */
	public void optimize() {
		try {
			_indexWriter.forceMerge(1);
			log.debug("Lucene index merged into one segment");
		} catch (IOException ioEx) {
			log.error("Error optimizing lucene index {}",ioEx.getMessage(),
													     ioEx);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Index a Lucene document
	 * @param doc the document to be indexed
	 */
	public void index(final Document doc) {	
		try {
			_reopenToken = _trackingIndexWriter.addDocument(doc);
			log.debug("document indexed in lucene");
		} catch(IOException ioEx) {
			log.error("Error while in Lucene index operation: {}",ioEx.getMessage(),
											   		              ioEx);
		} finally {
			try {
				_indexWriter.commit();
			} catch (IOException ioEx) {
				log.error("Error while commiting changes to Lucene index: {}",ioEx.getMessage(),
												 		   					  ioEx);
			}
		}
	}
	/**
	 * Updates the index info for a lucene document
	 * @param doc the document to be indexed
	 */
	public void reIndex(final Term recordIdTerm,
						final Document doc) {	
		try {
			_reopenToken = _trackingIndexWriter.updateDocument(recordIdTerm, 
													   		   doc);
			log.debug("{} document re-indexed in lucene",recordIdTerm.text());
		} catch(IOException ioEx) {
			log.error("Error in lucene re-indexing operation: {}",ioEx.getMessage(),
											 		              ioEx);
		} finally {
			try {
				_indexWriter.commit();
			} catch (IOException ioEx) {
				log.error("Error while commiting changes to Lucene index: {}",ioEx.getMessage(),
												 		   					  ioEx);
			}
		}
	}
	/**
	 * Unindex a lucene document
	 * @param idTerm term used to locate the document to be unindexed
	 * 				 IMPORTANT! the term must filter only the document and only the document
	 * 						    otherwise all matching docs will be unindexed
	 */
	public void unIndex(final Term idTerm) {
		try {
			_reopenToken = _trackingIndexWriter.deleteDocuments(idTerm);
			log.debug("{}={} term matching records un-indexed from lucene",idTerm.field(),
																		   idTerm.text());
		} catch(IOException ioEx) {
			log.error("Error in un-index lucene operation: {}",ioEx.getMessage(),
											 		   		   ioEx);			
		} finally {
			try {
				_indexWriter.commit(); 
			} catch (IOException ioEx) {
				log.error("Error while commiting changes to Lucene index: {}",ioEx.getMessage(),
												 		   					  ioEx);
			}
		}
	}
	/**
	 * Delete all lucene index docs
	 */
	public void truncate() {
		try {
			_reopenToken = _trackingIndexWriter.deleteAll();
			log.warn("lucene index truncated");
		} catch(IOException ioEx) {
			log.error("Error truncating lucene index: {}",ioEx.getMessage(),
											 		   	  ioEx);			
		} finally {
			try {
				_indexWriter.commit(); 
			} catch (IOException ioEx) {
				log.error("Error truncating lucene index: {}",ioEx.getMessage(),
												 			  ioEx);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COUNT-SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Count the number of results returned by a search against the lucene index
	 * @param qry the query
	 * @return
	 */
	public int count(final Query qry) {
		assert(qry != null);
		
		Stopwatch stopWatch = Stopwatch.createStarted();
		
		int outCount = 0;
		try {
			_indexSearcherReopenThread.waitForGeneration(_reopenToken);		// wait untill the index is re-opened
			IndexSearcher searcher = _indexSearcherReferenceManager.acquire();
			try {
				TopDocs docs = searcher.search(qry,0);
				if (docs != null) outCount = docs.totalHits;
				log.debug("count-search executed against lucene index returning {}",outCount);
			} finally {
				_indexSearcherReferenceManager.release(searcher);
			}
		} catch (IOException ioEx) {
			log.error("Error re-opening the index {}",ioEx.getMessage(),
													  ioEx);
		} catch (InterruptedException intEx) {
			log.error("The index writer periodically re-open thread has stopped",intEx.getMessage(),
																				 intEx);
		}
		
		log.info("Lucene query (elapsed time: {} milis): {}",NumberFormat.getNumberInstance(Locale.getDefault()).format(stopWatch.elapsed(TimeUnit.MILLISECONDS)),qry);		
		stopWatch.stop();
		
		return outCount;
	}
	/**
	 * Executes a search query
	 * @param qry the query to be executed
	 * @param sortFields the search query criteria
	 * @param firstResultItemOrder the order number of the first element to be returned
	 * @param numberOfResults number of results to be returned
	 * @return a page of search results
	 */
	public LucenePageResults search(final Query qry,Set<SortField> sortFields,
						  			final int firstResultItemOrder,final int numberOfResults) {
		assert(qry != null);
		
		Stopwatch stopWatch = Stopwatch.createStarted();
		
		LucenePageResults outDocs = null;
		try {
			_indexSearcherReopenThread.waitForGeneration(_reopenToken);	// wait until the index is re-opened for the last update
			IndexSearcher searcher = _indexSearcherReferenceManager.acquire();
			try {
				// sort criteria
				SortField[] theSortFields = null;
				if (CollectionUtils.hasData(sortFields)) theSortFields = CollectionUtils.toArray(sortFields,SortField.class);
				Sort theSort = CollectionUtils.hasData(theSortFields) ? new Sort(theSortFields)
																   	  : null;
				// number of results to be returned
				long theNumberOfResults = firstResultItemOrder + numberOfResults;
				
				// Exec the search (if the sort criteria is null, they're not used)
				TopDocs scoredDocs = theSort != null ? searcher.search(qry,
													 	 			   (int)theNumberOfResults,
													 	 			   theSort)
													 : searcher.search(qry,
															 		   (int)theNumberOfResults);
				log.debug("query {} {} executed against lucene index: returned {} total items, {} in this page",
						  qry.toString(),
						  (theSort != null ? theSort.toString() : ""),
						  scoredDocs != null ? scoredDocs.totalHits : 0,
						  scoredDocs != null ? scoredDocs.scoreDocs.length : 0);
				outDocs = LucenePageResults.create(searcher,
												   scoredDocs,
												   firstResultItemOrder,numberOfResults);
			} finally {
				_indexSearcherReferenceManager.release(searcher);
			}
		} catch (IOException ioEx) {
			log.error("Error freeing the searcher {}",ioEx.getMessage(),
													  ioEx);
		} catch (InterruptedException intEx) {
			log.error("The index writer periodically re-open thread has stopped",intEx.getMessage(),
																				 intEx);
		}
		
		log.info("Lucene query (elapsed time: {} milis): {}",NumberFormat.getNumberInstance(Locale.getDefault()).format(stopWatch.elapsed(TimeUnit.MILLISECONDS)),qry);
		stopWatch.stop();
		
		return outDocs;
	}
	/**
	 * Return all documents matching a query
	 * @param qry
	 * @param sortFields
	 * @return
	 */
	public Set<Document> searchAll(final Query qry,Set<SortField> sortFields) {
		assert(qry != null);
		
		Stopwatch stopWatch = Stopwatch.createStarted();
		
		Set<Document> outDocs = null;
		try {
			_indexSearcherReopenThread.waitForGeneration(_reopenToken);	// wait until the index is re-opened for the last update
			IndexSearcher searcher = _indexSearcherReferenceManager.acquire();
			try {
				outDocs = LuceneResultsCollector.builder()
													.forQuery(qry)
													.sortedBy(sortFields)
													.searchingWith(searcher)
													.withPageSize(50)
												.build()
													.collectAll();
			} finally {
				_indexSearcherReferenceManager.release(searcher);
			}
		} catch(IOException ioEx) {
			log.error("Error freeing the searcher {}",ioEx.getMessage(),
													  ioEx);
		} catch (InterruptedException intEx) {
			log.error("The index writer periodically re-open thread has stopped",intEx.getMessage(),
																				 intEx);
		}
		
		log.info("Lucene query (elapsed time: {} milis): {}",NumberFormat.getNumberInstance(Locale.getDefault()).format(stopWatch.elapsed(TimeUnit.MILLISECONDS)),qry);
		stopWatch.stop();
		
		return outDocs;
	}
}
