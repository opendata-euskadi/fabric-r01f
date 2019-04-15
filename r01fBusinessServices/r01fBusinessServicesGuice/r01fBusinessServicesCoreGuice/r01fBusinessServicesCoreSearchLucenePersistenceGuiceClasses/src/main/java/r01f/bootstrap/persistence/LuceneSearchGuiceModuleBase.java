package r01f.bootstrap.persistence;


import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import r01f.inject.HasMoreBindings;
import r01f.persistence.index.IndexManager;
import r01f.persistence.index.IndexerProvider;
import r01f.persistence.index.lucene.LuceneIndexManager;
import r01f.persistence.lucene.LuceneIndex;
import r01f.persistence.search.SearcherProvider;
import r01f.persistence.search.config.LuceneSearchModuleConfig;
import r01f.persistence.search.lucene.LuceneLanguageDependentAnalyzer;
import r01f.types.Path;

/**
 * Base {@link Guice} module for search engine (index / search) bindings
 */
@Slf4j
public abstract class LuceneSearchGuiceModuleBase 
              extends SearchGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected LuceneSearchGuiceModuleBase(final LuceneSearchModuleConfig searchEngineConfig,
										  final Collection<Class<? extends IndexerProvider<?>>> indexerFactoryTypes,
										  final Collection<Class<? extends SearcherProvider<?,?>>> searcherFactoryTypes) {
		super(searchEngineConfig,
			  indexerFactoryTypes,
			  searcherFactoryTypes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public void configure(final Binder binder) {
		// Basic indexers & searchers config
		super.configure(binder);
		
		LuceneSearchModuleConfig cfg = (LuceneSearchModuleConfig)_searchEngineConfig;
		
		// ... Lucene Index
		binder.bind(Analyzer.class)
			  .to(LuceneLanguageDependentAnalyzer.class)
			  .in(Singleton.class);
		binder.bind(LuceneIndex.class)
			  .in(Singleton.class);
		binder.bind(Directory.class)
			  .toInstance(_createLuceneDirectory(cfg.getIndexFilesPath()));			// singleton binding
		
		// Bind the index manager
		binder.bind(IndexManager.class)
			  .to(LuceneIndexManager.class)
			  .in(Singleton.class);
		
	
		// Give chance to sub-types to do more bindings
		if (this instanceof HasMoreBindings) {
			((HasMoreBindings)this).configureMoreBindings(binder);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
////////////////////////////////////////////////////////////////////////////////////////
	private static Directory _createLuceneDirectory(final Path indexFilesPath) {
		try {
			// Ensure the dir exists
			File indexFilesDir = new File(indexFilesPath.asAbsoluteString());
			if (!indexFilesDir.exists()) {
				log.warn("The lucen index dir {} didn't existed so it's created",indexFilesPath);
				indexFilesDir.mkdirs();
			}
			// Create the lucene's FSDDirectory 
			return FSDirectory.open(new File(indexFilesPath.asAbsoluteString()));
		} catch(IOException ioEx) {
			throw new ProvisionException("Could not provide an instance of Lucene Directory at directory " + indexFilesPath + ": " + ioEx.getMessage(),
										 ioEx);
		}
	}
}
