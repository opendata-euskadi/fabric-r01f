package r01f.persistence.lucene;


public class LuceneIndexForLucene43 {
/////////////////////////////////////////////////////////////////////////////////////////
//// 	FIELDS (see http://blog.mikemccandless.com/2011/11/near-real-time-readers-with-lucenes.html)
/////////////////////////////////////////////////////////////////////////////////////////
//	private final IndexWriter _indexWriter;
//	private final TrackingIndexWriter _trackingIndexWriter;
//	private final NRTManager _searchManager;
//	
/////////////////////////////////////////////////////////////////////////////////////////
////  
/////////////////////////////////////////////////////////////////////////////////////////
//	LuceneNRTReopenThread _reopenThread = null;
//	private long _reopenToken;	// index update/delete methods returned token
//	
/////////////////////////////////////////////////////////////////////////////////////////
//// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Constructor based on an instance of the type responsible of the lucene index persistence
//	 */
//	public LuceneIndexForLucene43(final Directory luceneDirectory,
//					   final Analyzer analyzer) {
//		try {
//			// Create the indexWriter
//			_indexWriter = new IndexWriter(luceneDirectory,
//										   new IndexWriterConfig(LuceneConstants.VERSION,
//										   					  	 analyzer));
//			_trackingIndexWriter = new NRTManager.TrackingIndexWriter(_indexWriter);
//			// Create the SearchManager to exec the search
//			_searchManager = new NRTManager(_trackingIndexWriter,
//											new SearcherFactory(),
//											true);
//			
//			// Open the thread in charge of re-open the index to allow it to see real-time changes
//			// 		The index is refreshed every 60sc when nobody is waiting 
//			//		and every 100 millis whenever is someone waiting (see search method)
//			// (see http://lucene.apache.org/core/4_3_0/core/org/apache/lucene/search/NRTManagerReopenThread.html)
//			_reopenThread = new LuceneNRTReopenThread(_searchManager,
//													  60.0,		// when there is nobody waiting
//													  0.1);		// when there is someone waiting
//			_reopenThread.startReopening();
//			
//		} catch (IOException ioEx) {
////			if (luceneDirectory instanceof JdbcDirectory) {
////				throw new IllegalStateException("The BBDD table for the lucene index could not be created: " + ioEx.getMessage(),ioEx);	
////			} else {
//				throw new IllegalStateException("Lucene index could not be created: " + ioEx.getMessage());
////			}
//		}
//	}
/////////////////////////////////////////////////////////////////////////////////////////
////	FINALIZADOR
/////////////////////////////////////////////////////////////////////////////////////////
//	@Override
//	protected void finalize() throws Throwable {
//		this.close();
//		super.finalize();
//	}
//	/**
//	 * Closes every index
//	 */
//	public void close() {
//		try {
//			// stop the index reader re-open thread
//			_reopenThread.stopReopening();
//			_reopenThread.interrupt();
//
//			// Close the search manager
//			_searchManager.close();
//			
//			// Close the indexWriter, commiting everithing that's pending
//			_indexWriter.commit();
//			_indexWriter.close();
//			
//		} catch(IOException ioEx) {
//			log.error("Error while closing lucene index: {}",ioEx.getMessage(),
//											 		   	     ioEx);
//		}
//	}
/////////////////////////////////////////////////////////////////////////////////////////
////	REOPEN-THREAD: Thread in charge of re-open the IndexReader to have access to the 
////				   latest IndexWriter changes
/////////////////////////////////////////////////////////////////////////////////////////
//	private class LuceneNRTReopenThread
//	      extends NRTManagerReopenThread {
//		
//		volatile boolean _finished = false;
//		
//		public LuceneNRTReopenThread(final NRTManager manager,
//									 final double targetMaxStaleSec,final double targetMinStaleSec) {
//			super(manager, targetMaxStaleSec, targetMinStaleSec);
//			this.setName("NRT Reopen Thread");
//			this.setPriority(Math.min(Thread.currentThread().getPriority()+2, 
//									  Thread.MAX_PRIORITY));
//			this.setDaemon(true);
//		}
//		public synchronized  void startReopening() {
//			_finished = false;
//			this.start();
//		}
//		public synchronized void stopReopening() {
//			_finished = true;
//		}
//		@Override
//		public void run() {
//			while (!_finished) {
//				super.run();
//			}
//		}
//	}
/////////////////////////////////////////////////////////////////////////////////////////
////	
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Index a Lucene document
//	 * @param doc the document to be indexed
//	 */
//	public void index(final Document doc) {	
//		// Indexar en lucene
//		try {
//			_reopenToken = _trackingIndexWriter.addDocument(doc);
//			log.debug("document indexed in lucene");
//		} catch(IOException ioEx) {
//			log.error("Error while in Lucene index operation: {}",ioEx.getMessage(),
//											   		              ioEx);
//		} finally {
//			try {
//				_indexWriter.commit();
//			} catch (IOException ioEx) {
//				log.error("Error while commiting changes to Lucene index: {}",ioEx.getMessage(),
//												 		   					  ioEx);
//			}
//		}
//	}
//	/**
//	 * Updates the index info for a lucene document
//	 * @param doc the document to be indexed
//	 */
//	public void reIndex(final Term recordIdTerm,
//						final Document doc) {	
//		// Indexar en lucene
//		try {
//			_reopenToken = _trackingIndexWriter.updateDocument(recordIdTerm, 
//													   		   doc);
//			log.debug("{} document re-indexed in lucene",recordIdTerm.text());
//		} catch(IOException ioEx) {
//			log.error("Error in lucene re-indexing operation: {}",ioEx.getMessage(),
//											 		              ioEx);
//		} finally {
//			try {
//				_indexWriter.commit();
//			} catch (IOException ioEx) {
//				log.error("Error while commiting changes to Lucene index: {}",ioEx.getMessage(),
//												 		   					  ioEx);
//			}
//		}
//	}
//	/**
//	 * Unindex a lucene document
//	 * @param idTerm term used to locate the document to be unindexed
//	 * 				 IMPORTANT! the term must filter only the document and only the document
//	 * 						    otherwise all matching docs will be unindexed
//	 */
//	public void unIndex(final Term idTerm) {
//		try {
//			_reopenToken = _trackingIndexWriter.deleteDocuments(idTerm);
//			log.debug("{}={} term matching records un-indexed from lucene",idTerm.field(),
//																		   idTerm.text());
//		} catch(IOException ioEx) {
//			log.error("Error in un-index lucene operation: {}",ioEx.getMessage(),
//											 		   		   ioEx);			
//		} finally {
//			try {
//				_indexWriter.commit(); 
//			} catch (IOException ioEx) {
//				log.error("Error while commiting changes to Lucene index: {}",ioEx.getMessage(),
//												 		   					  ioEx);
//			}
//		}
//	}
//	/**
//	 * Delete all lucene index docs
//	 */
//	public void truncate() {
//		try {
//			_reopenToken = _trackingIndexWriter.deleteAll();
//			log.warn("lucene index truncated");
//		} catch(IOException ioEx) {
//			log.error("Error truncating lucene index: {}",ioEx.getMessage(),
//											 		   	  ioEx);			
//		} finally {
//			try {
//				_indexWriter.commit(); 
//			} catch (IOException ioEx) {
//				log.error("Error truncating lucene index: {}",ioEx.getMessage(),
//												 			  ioEx);
//			}
//		}
//	}
/////////////////////////////////////////////////////////////////////////////////////////
////	COUNT-SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Count the number of results returned by a search against the lucene index
//	 * @param qry the query
//	 * @return
//	 */
//	public long count(final Query qry) {
//		long outCount = 0;
//		try {
//			_searchManager.waitForGeneration(_reopenToken);		// wait untill the index is re-opened
//			IndexSearcher searcher = _searchManager.acquire();
//			try {
//				TopDocs docs = searcher.search(qry,0);
//				if (docs != null) outCount = docs.totalHits;
//				log.debug("count-search executed against lucene index returning {}",outCount);
//			} finally {
//				_searchManager.release(searcher);
//			}
//		} catch (IOException ioEx) {
//			log.error("Error re-opening the index {}",ioEx.getMessage(),
//													  ioEx);
//		}
//		return outCount;
//	}
//	/**
//	 * Executes a search query
//	 * @param qry the query to be executed
//	 * @param sortFields the search query criteria
//	 * @param firstResultItemOrder the order number of the first element to be returned
//	 * @param numberOfResults number of results to be returnee
//	 * @return a page of search results
//	 */
//	public LucenePageResults search(final Query qry,Set<SortField> sortFields,
//						  			final int firstResultItemOrder,final int numberOfResults) {
//		LucenePageResults outDocs = null;
//		try {
//			_searchManager.waitForGeneration(_reopenToken);	// wait until the index is re-opened for the last update
//			IndexSearcher searcher = _searchManager.acquire();
//			try {
//				// sort crieteria
//				SortField[] theSortFields = null;
//				if (CollectionUtils.hasData(sortFields)) theSortFields = CollectionUtils.toArray(sortFields,SortField.class);
//				Sort theSort = CollectionUtils.hasData(theSortFields) ? new Sort(theSortFields)
//																   	  : null;
//				// number of results to be returned
//				int theNumberOfResults = firstResultItemOrder + numberOfResults;
//				
//				// Exec the search (if the sort criteria is null, they're not used)
//				TopDocs scoredDocs = theSort != null ? searcher.search(qry,
//													 	 			   theNumberOfResults,
//													 	 			   theSort)
//													 : searcher.search(qry,
//															 		   theNumberOfResults);
//				log.debug("query {} {} executed against lucene index: returned {} total items, {} in this page",qry.toString(),
//																												(theSort != null ? theSort.toString() : ""),
//																					 						    scoredDocs != null ? scoredDocs.totalHits : 0,
//																					 						    scoredDocs != null ? scoredDocs.scoreDocs.length : 0);
//				outDocs = LucenePageResults.create(searcher,
//												   scoredDocs,
//												   firstResultItemOrder,numberOfResults);
//			} finally {
//				_searchManager.release(searcher);
//			}
//		} catch (IOException ioEx) {
//			log.error("Error freeing the searcher {}",ioEx.getMessage(),
//													  ioEx);
//		}
//		return outDocs;
//	}
/////////////////////////////////////////////////////////////////////////////////////////
////	INDEX MAINTEINANCE
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Mergest the lucene index segments into one
//	 * (this should NOT be used, only rarely for index mainteinance)
//	 */
//	public void optimize() {
//		try {
//			_indexWriter.forceMerge(1);
//			log.debug("Lucene index merged into one segment");
//		} catch (IOException ioEx) {
//			log.error("Error optimizing lucene index {}",ioEx.getMessage(),
//													     ioEx);
//		}
//	}
}
