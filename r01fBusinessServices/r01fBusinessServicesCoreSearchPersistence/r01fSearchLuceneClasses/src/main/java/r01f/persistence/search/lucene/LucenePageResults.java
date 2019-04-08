package r01f.persistence.search.lucene;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import r01f.util.types.collections.CollectionUtils;

/**
 * Encapsula los resultados devueltos por lucene para una página
 * <pre>
 * NOTA:	Lucene como resultado de una búsqueda devuelve un objeto TopDocs
 * 			que contiene un array de objetos {@link ScoreDoc} cada uno de los cuales
 * 			contiene doc que es un int identificador del registro devuelto.
 * 			Para obtener el documento hay que utilizar el metodo {@link IndexSearcher}#doc(int) 
 * </pre> 
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
@Slf4j
public class LucenePageResults {
/////////////////////////////////////////////////////////////////////////////////////////
//	ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter 		private final int _pageSize;
	@Getter @Setter private int _totalHits;
	@Getter @Setter private Set<Document> _documents;
/////////////////////////////////////////////////////////////////////////////////////////
//	METODOS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a document
	 * @param doc 
	 */
	public void addDocument(final Document doc) {
		if (_documents == null) _documents = new LinkedHashSet<Document>(_pageSize);
		_documents.add(doc);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORIA
/////////////////////////////////////////////////////////////////////////////////////////
	public static LucenePageResults create(final IndexSearcher searcher,
										   final TopDocs topDocs,
										   final int firstResultItemOrder,final int numberOfResults) throws IOException {
		LucenePageResults outResults = new LucenePageResults(numberOfResults);
		if (topDocs != null) {
			outResults.setTotalHits(topDocs.totalHits);
			if (CollectionUtils.hasData(topDocs.scoreDocs)) {
				long start = firstResultItemOrder;
				long end = Math.min(firstResultItemOrder+numberOfResults,topDocs.totalHits);
				
				log.debug("Lucene Page Results: start/end={}/{} -> total: {}, numberOfResults: {}, topDocs.size: {}",
						  start,end,
						  topDocs.totalHits,
						  numberOfResults,
						  topDocs.scoreDocs.length);
						  
				for (long i=start; i < end; i++) {	// for (ScoreDoc scoredDoc : topDocs.scoreDocs) {
					Document doc = searcher.doc(topDocs.scoreDocs[(int)i].doc);
					outResults.addDocument(doc);
				}
			}
		}
		return outResults;
	}
}
