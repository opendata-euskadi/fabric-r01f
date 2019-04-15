package r01f.persistence.search.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.Path;

@Accessors(prefix="_")
public class LuceneSearchModuleConfig 
	 extends SearchModuleConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Index files path
	 */
	@Getter private final Path _indexFilesPath;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LuceneSearchModuleConfig(final Path indexFilesPath) {
		super();
		_indexFilesPath = indexFilesPath;
	}
	public static LuceneSearchModuleConfig storingIndexAt(final Path indexFilexPath) {
		return new LuceneSearchModuleConfig(indexFilexPath);
	}
}
