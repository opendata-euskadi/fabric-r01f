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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileID;
import r01f.file.FileProperties;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreChecksDelegate;
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
 * /r01/content/ejld003/euskadiplus/r01_euskadi_cont/wr0ecg1/noticia/20160414_noticia/es_def/index.shtml (contents area)
 * /r01/staging/ejld003/euskadiplus/r01_euskadi_cont/wr0ecg1/noticia/20160414_noticia/es_def/index.shtml (consolidate area)
 *
 *
 */
@Slf4j
public class HDFSFileStoreAPI
	 extends HDFSFileStoreAPIBase
  implements FileStoreAPI {

/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int HADOOP_BLOCK_SIZE = 4 * 1024;	// 4k


/////////////////////////////////////////////////////////////////////////////////////////
// 	FILESYSTEM STATIC INIT
/////////////////////////////////////////////////////////////////////////////////////////
	public HDFSFileStoreAPI(final Configuration conf) throws IOException {
		super(conf);
		_check = new FileStoreChecksDelegate(this,
										  	 new HDFSFileStoreFilerAPI(_fs,this));	// reuse the filesystem
	}
	HDFSFileStoreAPI(final FileSystem fs,
					 final HDFSFileStoreFilerAPI filerApi) throws IOException {
		super(fs);
		_check = new FileStoreChecksDelegate(this,
										  	 filerApi);								// reuse the filesystem
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private r01f.types.Path _fileIdToPath(final FileID fileId) {
		if (fileId == null) throw new IllegalArgumentException("fileId MUST NOT be null!");
		if (!(fileId instanceof r01f.types.Path)) throw new IllegalArgumentException(Strings.customized("The {} instance MUST be a {} instance",
																										FileID.class,r01f.types.Path.class));
		return (r01f.types.Path)fileId;
	}
	private Path _fileIdToHDFSPath(final FileID fileId) {
		return r01fPathToHDFSPath(_fileIdToPath(fileId));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsFile(final FileID fileId) throws IOException {
		// check
		_check.checkFileId(fileId);

		// exists?
		Path theHDFSFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());
		return _fs.exists(theHDFSFilePath) && _fs.isFile(theHDFSFilePath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY & RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean copyFile(final FileID srcFileId,final FileID dstFileId,
							final boolean overwrite) throws IOException {
		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeCopyFile(srcFileId,dstFileId,
								   overwrite);

		// copy
		boolean copyFileStateOK = FileUtil.copy(_fs,_fileIdToHDFSPath(srcFileId),
												_fs,_fileIdToHDFSPath(dstFileId),
												false, 		// delete source
												overwrite,	// overwrite
												_conf);
		return copyFileStateOK;
	}
	@Override
	public boolean renameFile(final FileID srcFileId,final FileID dstFileId) throws IOException {
		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeMoveFile(srcFileId,dstFileId,
								   false);		// DO NOT overwrite

		// rename
		boolean isRenamed = _fs.rename(_fileIdToHDFSPath(srcFileId),
									   _fileIdToHDFSPath(dstFileId));
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
	@Override
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,final long offset,
													  final boolean overwrite) throws IOException {
		log.trace("get outputstream for writing file {} (overwrite={})",
				  dstFileId,overwrite);
		if (offset > 0) throw new UnsupportedOperationException("HDFS does NOT supports random writes (only sequential writing from the beginning of the file or appending are supported)");

		// check
		_check.checkFileId(dstFileId);
		_check.checkBeforeWriteToFile(dstFileId,
									  overwrite);

		// write
		FSDataOutputStream out = _prepareFileOutputStream(dstFileId,
														  false,		// append
														  overwrite);
		return out;
	}
	@Override
	public void writeToFile(final InputStream srcIS,
							final FileID dstFileId,
							final boolean overwrite) throws IOException {
		Preconditions.checkArgument(srcIS != null,"The source input stream cannot be null");

		// prepare source & destination
		InputStream in = new BufferedInputStream(srcIS);
		OutputStream out = this.getFileOutputStreamForWriting(dstFileId,0,		// offset = 0 > start writing at the beginning of the file
												 			  overwrite);
		// IOUtils.copyBytes close input and output streams unless it was tell to
		IOUtils.copyBytes(in,out,
						  _conf,
						  true);	// close the streams after writing
		out.flush();
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
	@Override
	public OutputStream getFileOutputStreamForAppending(final FileID dstFileId) throws IOException {
		log.trace("Append to file {}",dstFileId);
		// check
		_check.checkFileId(dstFileId);
		_check.checkBeforeAppendToFile(dstFileId);

		// write
		OutputStream dstFOS = _prepareFileOutputStream(dstFileId,
													   true,			// append
													   false);			// overwrite
		return dstFOS;
	}
	@Override
	public void appendToFile(final InputStream srcIS,
							 final FileID dstFileId) throws IOException {
		Preconditions.checkArgument(srcIS != null,"The source input stream cannot be null");

		// prepare source & destination
		InputStream in = new BufferedInputStream(srcIS);
		OutputStream out = this.getFileOutputStreamForAppending(dstFileId);

		// write
		// IOUtils.copyBytes close input and output streams unless it was tell to
		IOUtils.copyBytes(in,out,
						  _conf,
						  true);	// close after write
		out.flush();
	}
	@Override
	public void appendChunkToFile(final byte[] srcDataChunk,
								  final FileID dstFileId) throws IOException {
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
						  _conf,
						  true);	// close after write
		out.flush();
	}
	private FSDataOutputStream _prepareFileOutputStream(final FileID dstFileId,
														final boolean appendToFile,
														final boolean overwrite) throws IOException {
		FSDataOutputStream out = null;

		Path theFilePath = _fileIdToHDFSPath(dstFileId);

		log.trace("\tPrepare file {} to be written (append={}, overwrite={})",
				  dstFileId,appendToFile,overwrite);

		// check if the file exists
		boolean prevExists = _fs.exists(theFilePath);

		if (prevExists) {
			log.debug("\tFile {} already exists",dstFileId);
			if (appendToFile) {
				log.trace("Appending to file {}...",dstFileId);
				out = _fs.append(theFilePath);
			} else if (overwrite) {
				log.trace("Overwrite file {}...",dstFileId);
				out = _fs.create(theFilePath);
			} else {
				throw new IOException(Strings.customized("Cannot write to file {}: it previously exists and append=false / overwrite=false",
														 dstFileId));
			}
		} else {
			log.trace("\tCreate new file {}...",dstFileId);
			out = _fs.create(theFilePath);
		}
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
	@Override
	public InputStream readFromFile(final FileID fileId,final long offset) throws IOException {
		log.trace("Reading file {}",fileId);

		// check
		_check.checkFileId(fileId);
		_check.checkBeforeReadingFromFile(fileId);

		// read
		Path theFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());
		FSDataInputStream outIS = _fs.open(theFilePath);
		if (offset > 0) {
			outIS.seek(offset);
		} else if (offset < 0) {
			throw new IllegalArgumentException("file offset MUST be > 0");
		}
		return outIS;
	}
	@Override
	public byte[] readChunkFromFile(final FileID fileId,
			  		   				final long offset,final int len) throws IOException {
		log.trace("Chunked reading {} bytes starting at {} from file at {}",len,offset,fileId);

		// check
		_check.checkFileId(fileId);
		_check.checkBeforeReadingFromFile(fileId);

		// read
		FSDataInputStream in = null;
		byte[] btbuffer = null;

		try {
			Path theFilePath = _fileIdToHDFSPath(fileId);
			in = _fs.open(theFilePath); //FSDataInputStream implements Seekable interface

			// Adjust num of bytes to read
			FileStatus[] fstatus = _fs.listStatus(theFilePath);
			long size = fstatus[0].getLen();
			log.trace("\tfile size={},offset={},len={}",size,offset,len);

			if (offset >= size) return null; // End of file

			int theLen = len;
			if (theLen > ((size-offset) + 1)) {
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
		return btbuffer;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean deleteFile(final FileID fileId) throws IOException {
		log.trace("Deleting file {}",fileId);

		// check
		_check.checkFileId(fileId);
		_check.checkBeforeDeleteFile(fileId);

		// delete
		boolean opState = _fs.delete(_fileIdToHDFSPath(fileId),
									 false);		// recursive=false (it's NOT a folder)
		return opState;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FileProperties getFileProperties(final FileID fileId) throws IOException {
		Path theFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());

		if (!_fs.exists(theFilePath)) throw new IOException(Strings.customized("The file {} does not exists!",fileId.asString()));

		// return the status as FileProperties
		FileStatus hdfsStatus = _fs.getFileStatus(theFilePath);
		return HDFSFileProperties.from(hdfsStatus);
	}
	@Override
	public void setFileModifiedDate(final FileID fileId, final long modifiedTimeInMillis) throws IOException {
		Path theFilePath = new Path(_fileIdToPath(fileId).asAbsoluteString());

		if (!_fs.exists(theFilePath)) throw new IOException(Strings.customized("The file {} does not exists!",fileId.asString()));

		_fs.setTimes(theFilePath,
					 modifiedTimeInMillis,
					 -1); // A value of -1 means that this call should not set access time.
	}
}

