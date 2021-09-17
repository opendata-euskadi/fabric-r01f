package r01f.filestore.api.hdfs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileID;
import r01f.file.FileProperties;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.types.TimeLapse;
import r01f.util.types.Strings;

/**
 * [1] - Build a <pre>Configuration</pre> object that sets where in the classpath
 * 		 the core-site.xml & hdfs-site.xml files resides.
 *
 * 		SIMPLE authentication mode (must be enabled in core-site.xml).
 *
 * 		 	<pre class='brush:java'>
 *				Configuration conf = new Configuration();
 *				conf.addResource("hadoop/core-site.xml");
 *				conf.addResource("hadoop/hdfs-site.xml");
 *			</pre>
 *
 *		KERBEROS authentication mode (must be enabled in core-site.xml):
 *			<pre class='brush:java'>
 *				System.setProperty("java.security.krb5.realm", DOMINIODOMINIO.toUpperCase());
 *				System.setProperty("java.security.krb5.kdc", U);
 *
 *				Configuration conf = newConfiguration();
 *				conf.addResource("hadoop/core-site.xml");
 *				conf.addResource("hadoop/hdfs-site.xml");
 *				conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
 *				conf.set("fs.webhdfs.impl", org.apache.hadoop.hdfs.web.WebHdfsFileSystem.class.getName());
 *				conf.set("hadoop.security.authentication", "kerberos");
 *				conf.set("fs.defaultFS", URL_WEBHDFS);
 *				conf.set("dfs.namenode.kerberos.principal.pattern", "nn/*@" + DOMINIO.toUpperCase());
 *
 *				UserGroupInformation.setConfiguration(conf);
 *				UserGroupInformation.loginUserFromKeytab(user, keytab);
 *
 *				FileSystemfs = FileSystem.get(conf);
 *			</pre>
 *
 * [2] - Just create the api
 * 		 	<pre class='brush:java'>
 *				HDFSFileStoreAPI api = new HDFSFileStoreAPI(conf);
 * 			</pre>
 *
 * For local testing (use the hdfs api to access the local file system):
 * 	[1] Copy winutils from http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe to HADOOP_HOME/bin
 * 	[2] Set at core-site.xml
 * 			<pre class='brush:xml'>
 * 				   <property>
 * 					  <name>fs.defaultFS</name>
 *	   				  <value>file:///</value>
 *				   </property>
 *			</pre>
 *
 *
 * see: http://hadoop.apache.org/docs/current/
 *
 * Hadoop HDFS basic commands.
 * It's important to know URI and Path usage:
 * 		Hadoop's URI file location in HDFS > hdfs://host:port/location to access file through FileSystem.
 *
 * Code below shows how to create URI:
 * <pre class='brush: java'>
 * 		hdfs://localhost:9000/user/joe/TestFile.txt
 *  	URI uri = URI.create ("hdfs://host: port/path");
 * </pre>
 *
 * Path object resolves the OS dependency in URI e.g. Windows uses \\path whereas linux uses //.
 * It's also used to resolve parent child dependency.
 *
 * Code below shows how to create a Path:
 * <pre class='brush: java'>
 * 		Path path = new Path (path);
 *
 * 		new Path("/test/file.txt");
 *		new Path("hdfs://localhost:9000/test/file.txt");
 * </pre>
 *
 * HDFS's directory structure is similar to TeamSite's one:
 * INTERWOVEN: /iwmnt/{serverOid}/{dataStore}/main/{area}/WORKAREA/{workArea}/{tipology}/{contentName}/{documentName}/..........
 * HADOOP:	 /r01/content/{serverOid}/{dataStore}/main/{area}/WORKAREA{workArea}/{tipology}/{contentName}/{documentName}/..........
 *
 * /r01/content/server/euskadiplus/r01_euskadi_cont/wr0ecg1/noticia/20160414_noticia/es_def/index.shtml (contents area)
 * /r01/staging/server/euskadiplus/r01_euskadi_cont/wr0ecg1/noticia/20160414_noticia/es_def/index.shtml (consolidate area)
 *
 *
 */
@Slf4j
public class HDFSFileStoreAPI
	 extends HDFSFileStoreAPIBase
  implements FileStoreAPI {

/////////////////////////////////////////////////////////////////////////////////////////
// 	FILESYSTEM STATIC INIT
/////////////////////////////////////////////////////////////////////////////////////////
	public HDFSFileStoreAPI(final Configuration conf) {
		this(conf,
			 null);	// no credentials refresh period
	}
	public HDFSFileStoreAPI(final Configuration conf,
							final TimeLapse credentialsRefreshPeriod) {
		super(new HDFSFileSystemProvider(conf,
										 credentialsRefreshPeriod));
		_check = new FileStoreChecksDelegate(this,
										  	 new HDFSFileStoreFilerAPI(this.getHDFSFileSystemProvider(),this));	// reuse the filesystem provider
	}
	HDFSFileStoreAPI(final HDFSFileSystemProvider fsProvider,
					 final HDFSFileStoreFilerAPI filerApi) {
		super(fsProvider);
		_check = new FileStoreChecksDelegate(this,
										  	 filerApi);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static r01f.types.Path _fileIdToPath(final FileID fileId) {
		if (fileId == null) throw new IllegalArgumentException("fileId MUST NOT be null!");
		if (!(fileId instanceof r01f.types.Path)) throw new IllegalArgumentException(Strings.customized("The {} instance MUST be a {} instance",
																										FileID.class,r01f.types.Path.class));
		return (r01f.types.Path)fileId;
	}
	private static Path _fileIdToHDFSPath(final FileID fileId) {
		return r01fPathToHDFSPath(_fileIdToPath(fileId));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public boolean existsFile(final FileID fileId) throws IOException {
		boolean outExists = false;

		boolean retry = true; // retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				// check
				_check.checkFileId(fileId);

				// exists?
				Path theHDFSFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());
				outExists = this.getHDFSFileSystem().exists(theHDFSFilePath)
					     && this.getHDFSFileSystem().isFile(theHDFSFilePath);
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return outExists;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY & RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public boolean copyFile(final FileID srcFileId,final FileID dstFileId,
							final boolean overwrite) throws IOException {
		boolean copyFileStateOK = false;

		boolean retry = true;	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				// check
				_check.checkFileId(srcFileId,dstFileId);
				_check.checkBeforeCopyFile(srcFileId,dstFileId,
										   overwrite);

				// copy
				copyFileStateOK = FileUtil.copy(this.getHDFSFileSystem(),_fileIdToHDFSPath(srcFileId),
												this.getHDFSFileSystem(),_fileIdToHDFSPath(dstFileId),
												false, 		// delete source
												overwrite,	// overwrite
												this.getHDFSConfiguration());
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return copyFileStateOK;
	}
	@Override @SuppressWarnings("resource")
	public boolean renameFile(final FileID srcFileId,final FileID dstFileId) throws IOException {
		boolean isRenamed = false;

		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				// check
				_check.checkFileId(srcFileId,dstFileId);
				_check.checkBeforeMoveFile(srcFileId,dstFileId,
										   false);		// DO NOT overwrite

				// rename
				isRenamed = this.getHDFSFileSystem()
								.rename(_fileIdToHDFSPath(srcFileId),
									    _fileIdToHDFSPath(dstFileId));
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return isRenamed;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WRITE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,
													  final boolean overwrite) throws IOException {
		return this.getFileOutputStreamForWriting(dstFileId,
												  0,	// start at the beginning of the file
												  overwrite);
	}
	@Override @SuppressWarnings("resource")
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,final long offset,
													  final boolean overwrite) throws IOException {
		FSDataOutputStream out = null;

		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				log.trace("get outputstream for writing file {} (overwrite={})",
						  dstFileId,overwrite);
				if (offset > 0) throw new UnsupportedOperationException("HDFS does NOT supports random writes (only sequential writing from the beginning of the file or appending are supported)");

				// check
				_check.checkFileId(dstFileId);
				_check.checkBeforeWriteToFile(dstFileId,
											  overwrite);

				// write
				out = _prepareFileOutputStream(dstFileId,
											   false,		// append
											   overwrite);
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return out;
	}
	@Override @SuppressWarnings("resource")
	public void writeToFile(final InputStream srcIS,
							final FileID dstFileId,
							final boolean overwrite) throws IOException {
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				Preconditions.checkArgument(srcIS != null,"The source input stream cannot be null");

				// prepare source & destination
				InputStream in = new BufferedInputStream(srcIS);
				OutputStream out = this.getFileOutputStreamForWriting(dstFileId,0,		// offset = 0 > start writing at the beginning of the file
														 			  overwrite);
				// IOUtils.copyBytes close input and output streams unless it was tell to
				IOUtils.copyBytes(in,out,
								  this.getHDFSConfiguration(),
								  true);	// close the streams after writing
				out.flush();
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
	}
	@Override
	public void writeChunkToFile(final byte[] data,
								 final FileID dstFileId,final long offset,
								 final boolean overwrite) throws IOException {
		throw new UnsupportedOperationException("HDFS does NOT supports random writes (only sequential writing from the beginning of the file or appending are supported)");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public OutputStream getFileOutputStreamForAppending(final FileID dstFileId) throws IOException {
		OutputStream dstFOS = null;

		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				log.trace("Append to file {}",dstFileId);
				// check
				_check.checkFileId(dstFileId);
				_check.checkBeforeAppendToFile(dstFileId);

				// write
				dstFOS = _prepareFileOutputStream(dstFileId,
												  true,			// append
												  false);		// overwrite
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return dstFOS;
	}
	@Override @SuppressWarnings("resource")
	public void appendToFile(final InputStream srcIS,
							 final FileID dstFileId) throws IOException {
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				Preconditions.checkArgument(srcIS != null,"The source input stream cannot be null");

				// prepare source & destination
				InputStream in = new BufferedInputStream(srcIS);
				OutputStream out = this.getFileOutputStreamForAppending(dstFileId);

				// write
				// IOUtils.copyBytes close input and output streams unless it was tell to
				IOUtils.copyBytes(in,out,
								  this.getHDFSConfiguration(),
								  true);	// close after write
				out.flush();
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
	}
	@Override @SuppressWarnings("resource")
	public void appendChunkToFile(final byte[] srcDataChunk,
								  final FileID dstFileId) throws IOException {
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				Preconditions.checkArgument(dstFileId != null,"The path cannot be null");
				log.debug("Append chunk to file");
				if (srcDataChunk == null || srcDataChunk.length == 0) {
					log.warn("The data to write in file is NULL!!!");
					return;
				}

				// Prepare source and destination
				InputStream srcIS = new BufferedInputStream(new ByteArrayInputStream(srcDataChunk));
				OutputStream out = this.getFileOutputStreamForAppending(dstFileId);

				// write
				// IOUtils.copyBytes close input and output streams unless it was tell to
				IOUtils.copyBytes(srcIS,out,
								  this.getHDFSConfiguration(),
								  true);	// close after write
				out.flush();
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
	}
	@SuppressWarnings("resource")
	private FSDataOutputStream _prepareFileOutputStream(final FileID dstFileId,
														final boolean appendToFile,
														final boolean overwrite) throws IOException {
		FSDataOutputStream out = null;
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				Path theFilePath = _fileIdToHDFSPath(dstFileId);

				log.trace("\tPrepare file {} to be written (append={}, overwrite={})",
						  dstFileId,appendToFile,overwrite);

				// check if the file exists
				boolean prevExists = this.getHDFSFileSystem()
										 .exists(theFilePath);

				if (prevExists) {
					log.debug("\tFile {} already exists",dstFileId);
					if (appendToFile) {
						log.trace("Appending to file {}...",dstFileId);
						out = this.getHDFSFileSystem()
								  .append(theFilePath);
					} else if (overwrite) {
						log.trace("Overwrite file {}...",dstFileId);
						out = this.getHDFSFileSystem()
								  .create(theFilePath);
					} else {
						throw new IOException(Strings.customized("Cannot write to file {}: it previously exists and append=false / overwrite=false",
																 dstFileId));
					}
				} else {
					log.trace("\tCreate new file {}...",dstFileId);
					out = this.getHDFSFileSystem()
							  .create(theFilePath);
				}
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return out;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  READ
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public InputStream readFromFile(final FileID fileId) throws IOException {
		return this.readFromFile(fileId,
								 0); 		// starting at the beginning of the file
	}
	@Override @SuppressWarnings("resource")
	public InputStream readFromFile(final FileID fileId,final long offset) throws IOException {
		FSDataInputStream outIS = null;

		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				log.trace("Reading file {}",fileId);

				// check
				_check.checkFileId(fileId);
				_check.checkBeforeReadingFromFile(fileId);

				// read
				Path theFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());
				outIS = this.getHDFSFileSystem()
							.open(theFilePath);
				if (offset > 0) {
					outIS.seek(offset);
				} else if (offset < 0) {
					throw new IllegalArgumentException("file offset MUST be > 0");
				}
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return outIS;
	}
	@Override @SuppressWarnings("resource")
	public byte[] readChunkFromFile(final FileID fileId,
			  		   				final long offset,final int len) throws IOException {
		byte[] btbuffer = null;

		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				log.trace("Chunked reading {} bytes starting at {} from file at {}",len,offset,fileId);

				// check
				_check.checkFileId(fileId);
				_check.checkBeforeReadingFromFile(fileId);

				// read
				FSDataInputStream in = null;
				try {
					Path theFilePath = _fileIdToHDFSPath(fileId);
					in = this.getHDFSFileSystem()
							 .open(theFilePath); //FSDataInputStream implements Seekable interface

					// Adjust num of bytes to read
					FileStatus[] fstatus = this.getHDFSFileSystem()
											   .listStatus(theFilePath);
					long size = fstatus[0].getLen();
					log.trace("\tfile size={},offset={},len={}",size,offset,len);

					if (offset >= size) return null; // End of file

					int theLen = len;
					if (theLen >= ((size-offset) + 1)) {
						theLen = (int)(size - offset);
					}
					btbuffer = new byte[theLen];
					/*in.readFully(btbuffer,
								 (int)offset,theLen);*/
					in.readFully(offset,
								 btbuffer);
				} finally {
					if (in != null) in.close();
				}
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return btbuffer;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public boolean deleteFile(final FileID fileId) throws IOException {
		boolean opState = false;

		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				log.trace("Deleting file {}",fileId);

				// check
				_check.checkFileId(fileId);
				_check.checkBeforeDeleteFile(fileId);

				// delete
				opState = this.getHDFSFileSystem()
							  .delete(_fileIdToHDFSPath(fileId),
								      false);		// recursive=false (it's NOT a folder)
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return opState;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("resource")
	public FileProperties getFileProperties(final FileID fileId) throws IOException {
		FileProperties outProps = null;

		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				Path theFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());

				if (!this.getHDFSFileSystem()
						 .exists(theFilePath)) throw new IOException(Strings.customized("The file {} does not exists!",fileId.asString()));

				// return the status as FileProperties
				FileStatus hdfsStatus = this.getHDFSFileSystem()
											.getFileStatus(theFilePath);
				outProps = HDFSFileProperties.from(hdfsStatus);
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
		return outProps;
	}
	@Override @SuppressWarnings("resource")
	public void setFileModifiedDate(final FileID fileId, final long modifiedTimeInMillis) throws IOException {
		boolean retry = true; 	// retry
		for (int retryCount = 0; retry && retryCount < AUTH_CREDENTIAL_REFRESH_RETRY_NUM; retryCount++) {
			try {
				// ------------------------------------------------------------
				Path theFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());

				if (!this.getHDFSFileSystem()
						 .exists(theFilePath)) throw new IOException(Strings.customized("The file {} does not exists!",fileId.asString()));

				this.getHDFSFileSystem()
					.setTimes(theFilePath,
							  modifiedTimeInMillis,
							  -1); // A value of -1 means that this call should not set access time.
				// ------------------------------------------------------------
				retry = false;
			} catch (IOException ioEx) {
				retry = _isCredentialExpiredError(ioEx);
				if (!retry) throw ioEx;
			}
		} // retry
	}
}

