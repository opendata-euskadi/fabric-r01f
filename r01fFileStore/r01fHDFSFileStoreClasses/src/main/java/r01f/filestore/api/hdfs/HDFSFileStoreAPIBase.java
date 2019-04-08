package r01f.filestore.api.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import r01f.filestore.api.FileStoreChecksDelegate;

abstract class HDFSFileStoreAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Common checkings
	 */
	protected FileStoreChecksDelegate _check;
	/**
	 * Generic class to access and manage HDFS files/directories.
	 * How it works:
	 * 		FileSystem reads an stream by accessing blocks in sequence order.
	 * 		to do so, it gets the blocks information from NameNode and then 
	 * 		open, read and close blocks one by one.
	 * 
	 * FileSystem uses FSDataOutputStream and FSDataInputStream to write and read the contents in stream.
	 * Hadoop provides various implementation of FileSystem as described below:
	 * - DistributedFileSystem: To access HDFS File in distributed environment
	 * - LocalFileSystem: To access HDFS file in Local system
	 * - FTPFileSystem: To access HDFS file FTP client
	 * - WebHdfsFileSystem: To access HDFS file over the web
	 */
	protected final FileSystem _fs;
	/**
	 * Configuration class that stores the Hadoop config needed by FileSystem type.
	 * It loads the core-site and core-default.xml files using the class loader
	 * and keeps Hadoop configuration information such as fs.defaultFS, fs.default.name etc.
	 */
	 protected final Configuration _conf;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	 HDFSFileStoreAPIBase(final Configuration conf) throws IOException {
		_conf = conf;
		_fs = FileSystem.get(_conf);
	 }
	 HDFSFileStoreAPIBase(final FileSystem fs) throws IOException {
		 _fs = fs;
		_conf = fs.getConf();
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Path r01fPathToHDFSPath(final r01f.types.Path path) {
		return new Path(path.asAbsoluteString());
	}
	protected static r01f.types.Path hdfsPathToR01FPath(final Path path) {
		Path hdfsPath = Path.getPathWithoutSchemeAndAuthority(path);
		return r01f.types.Path.from(hdfsPath);
	}
}
