package r01f.filestore.api.hdfs;

import java.io.IOException;

import javax.security.sasl.SaslException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import lombok.extern.slf4j.Slf4j;
import r01f.filestore.api.FileStoreChecksDelegate;

@Slf4j
abstract class HDFSFileStoreAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected static final int AUTH_CREDENTIAL_REFRESH_RETRY_NUM = 2;		// try 2 times to refresh the auth credential
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
	private final HDFSFileSystemProvider _fsProvider;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	 HDFSFileStoreAPIBase(final HDFSFileSystemProvider fsProvider) {
		 _fsProvider = fsProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////	 
	public HDFSFileSystemProvider getHDFSFileSystemProvider() {
		return _fsProvider;
	}
	public FileSystem getHDFSFileSystem() throws IOException {
		return _fsProvider.getHDFSFileSystem();
	}
	public Configuration getHDFSConfiguration() {
		return _fsProvider.getHDFSConfiguration();
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
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles an {@link IOException} so it checks if the root cause is that the 
	 * [kerberos] credential has expired and has to be refreshed
	 * If the ticket is refreshed, the method returns true so the operation 
	 * can be retried
	 * @param io
	 * @return true if the 
	 */
	protected static boolean _isCredentialExpiredError(final IOException io) {
		if (io.getCause() == null || !(io.getCause() instanceof IOException)) return false;
		
		Throwable rootCause = io.getCause().getCause();
		boolean isExpiredCredentialException = rootCause != null 
											&& rootCause instanceof SaslException;
		if (isExpiredCredentialException) {
			log.warn("[refresh hdfs user credential]: CAUSE ticket expired: {}",io.getMessage());
		} else {
			if (log.isTraceEnabled()) log.trace("[refresh hdfs user credential]: NO NEED to refresh credential > IOException has another root cause!");
		}
		return isExpiredCredentialException; 
	}
	protected synchronized void _refreshAuthCredential() {
		
	}
}
