package r01f.persistence.lucene;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import r01f.types.Path;


/**
 * @link Directory} provider
 * <ul>
 * 		<li>FileSystem</li>
 * 		<li>JdbDirectory (only Lucene 4)</li>
 * </ul>
 */
public class LuceneDirectoryProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//	FileSystem
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds a {@link Directory} impl FileSystem based
	 * (lucene index is stored at a FileSystem location)
	 * @param indexFilesPath filesystem path where lucene index is stored
	 * @return
	 */
	public Directory provideFileSystemDirectory(final Path indexFilesPath) throws IOException {
		return this.provideFileSystemDirectory(indexFilesPath.asString());
	}
	/**
	 * Builds a {@link Directory} impl FileSystem based
	 * (lucene index is stored at a FileSystem location)
	 * @param indexFilesPath filesystem path where lucene index is stored
	 * @return
	 */
	@SuppressWarnings("static-method")
	public Directory provideFileSystemDirectory(final String indexFilesPath) throws IOException {
		Directory outDir = FSDirectory.open(new File(indexFilesPath));
		return outDir;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Jdbc (only lucene 3)
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Builds a {@link Directory} impl DB based
//	 * (lucene index is stored as DB tables)
//	 * <b>BEWARE!!</b>
//	 * This impl uses Commet lib for lucene that was DISCONTINUED by 2009 
//	 * ... so it DOES NOT supports Lucene4
//	 * @param dataSource DB dataSoucre
//	 * @param dialect DB dialect (oracle/mySql)
//	 * @param tableName DB table name storing lucene indes
//	 * @return the {@link Directory} impl
//	 */
//	public Directory provideJdbcDirectory(final DataSource dataSource,
//										  final Dialect dialect,
//										  final String tableName) throws IOException {
//		return new LuceneJdbcDirectory(dataSource,
//									   dialect,
//									   tableName);
//	}
//	import java.io.IOException;
//	
//	import javax.inject.Inject;
//	import javax.sql.DataSource;
//	
//	import org.apache.lucene.store.Directory;
//	import org.apache.lucene.store.jdbc.JdbcDirectory;
//	import org.apache.lucene.store.jdbc.JdbcDirectorySettings;
//	import org.apache.lucene.store.jdbc.JdbcStoreException;
//	import org.apache.lucene.store.jdbc.dialect.Dialect;
//	import org.apache.lucene.store.jdbc.support.JdbcTable;
//	
//	public class LuceneJdbcDirectory 
//	     extends JdbcDirectory {
//	/////////////////////////////////////////////////////////////////////////////////////////
//	//	FIELDS
//	/////////////////////////////////////////////////////////////////////////////////////////
//		/**
//		 * DataSource
//		 */
//		private DataSource _dataSource;
//	/////////////////////////////////////////////////////////////////////////////////////////
//	//	CONSTRUCTORS
//	/////////////////////////////////////////////////////////////////////////////////////////
//		public LuceneJdbcDirectory(final DataSource dataSource,final Dialect dialect,
//								   final String tableName) {
//			super(dataSource, dialect, tableName);
//		}
//		public LuceneJdbcDirectory(final DataSource dataSource,final Dialect dialect,final JdbcDirectorySettings settings,
//								   final String tableName) {
//			super(dataSource, dialect, settings, tableName);
//		}
//		public LuceneJdbcDirectory(final DataSource dataSource,final JdbcDirectorySettings settings,
//								   final String tableName) throws JdbcStoreException {
//			super(dataSource, settings, tableName);
//		}
//		public LuceneJdbcDirectory(final DataSource dataSource,
//								   final JdbcTable table) {
//			super(dataSource, table);
//		}
//		public LuceneJdbcDirectory(final DataSource dataSource,
//								   final String tableName)throws JdbcStoreException {
//			super(dataSource, tableName);
//		}
//	/////////////////////////////////////////////////////////////////////////////////////////
//	//	METODOS OVERRIDEN
//	/////////////////////////////////////////////////////////////////////////////////////////
//		@Override
//		public String[] listAll() throws IOException {
//			return super.list();		// Simply delegate
//		}
//	}
	
//	import org.apache.lucene.store.Directory;
//	import org.apache.lucene.store.jdbc.JdbcDirectory;
//	import org.apache.lucene.store.jdbc.dialect.Dialect;
//	import org.apache.lucene.store.jdbc.dialect.MySQLDialect;
//	import org.apache.lucene.store.jdbc.dialect.OracleDialect;
//	
//	import r01f.exceptions.Throwables;
//	import r01f.types.enums.Enums;
//	
//	public class LuceneJdbcDirectoryDialectProvider {
//	/////////////////////////////////////////////////////////////////////////////////////////
//	//	
//	/////////////////////////////////////////////////////////////////////////////////////////
//		/**
//		 * Returs a {@link Dialect} to be used with {@link JdbcDirectory}
//		 * @param dialect
//		 * @return
//		 */
//		public Dialect provideDialect(final LuceneJdbcDirectoryDialect dialect) {
//			Dialect outDialect = null;
//			switch(dialect) {
//			case ORACLE:
//				outDialect = new OracleDialect();
//				break;
//			case MYSQL:
//				outDialect = new MySQLDialect();
//				break;
//			default:
//				outDialect = new MySQLDialect();
//				break;
//			}
//			return outDialect;
//		}
//		/**
//		 * Returns a {@link Dialect} to be used with {@link JdbcDirectory}
//		 * @param dialectStr
//		 * @return
//		 */
//		public Dialect provideDialect(final String dialectStr) {
//			LuceneJdbcDirectoryDialect dialect = Enums.of(LuceneJdbcDirectoryDialect.class)
//													  .fromName(dialectStr);
//			if (dialect == null) throw new IllegalArgumentException(Throwables.message("The Lucene JdbcDirectory Dialect provider '{}' is NOT valid. The valid options are {}",
//																					   dialectStr,LuceneJdbcDirectoryDialect.values()));
//			return this.provideDialect(dialect);
//		}
//	/////////////////////////////////////////////////////////////////////////////////////////
//	//	
//	/////////////////////////////////////////////////////////////////////////////////////////
//		private enum LuceneJdbcDirectoryDialect {
//			ORACLE,
//			MYSQL;
//		}
//	}
}
