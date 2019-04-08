package r01f.persistence.search.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.persistence.index.Indexer;
import r01f.persistence.index.document.IndexDocumentFieldConfigSet;
import r01f.persistence.search.Searcher;

/**
 * Encapsulates search config data, for example:
 * <ul>
 * 		<li>The indexed document definition: the document's fields: {@link IndexDocumentFieldConfigSet}</li>
 * 		<li>The indexer: the type that stores data at the index: {@link Indexer}</li>
 * 		<li>The searcher: the type that retrieves data from the index: {@link Searcher}</li>
 * </ul>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class SearchModuleConfig 
  implements ContainsConfigData {
	// just extend if needed
}
