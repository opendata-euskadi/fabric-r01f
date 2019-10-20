package r01f.filestore.api.local;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileID;
import r01f.file.FileProperties;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.types.Path;

/**
 * Usage:
 * <pre class='brush:java'>
 * 		LocalFileStoreAPI api = new LocalFileStoreAPI();	// ouch!
 * </pre>
 */
@Slf4j
public class LocalFileStoreAPI 
	 extends LocalFileStoreAPIBase
  implements FileStoreAPI {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FILESYSTEM STATIC INIT
/////////////////////////////////////////////////////////////////////////////////////////
	public LocalFileStoreAPI() throws IOException {
		_check = new FileStoreChecksDelegate(this,
											 new LocalFileStoreFilerAPI(this));
	}
	LocalFileStoreAPI(final LocalFileStoreFilerAPI fileStoreFilerApi) throws IOException {
		_check = new FileStoreChecksDelegate(this,
											 fileStoreFilerApi);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean existsFile(final FileID fileId) throws IOException {
		// check
		_check.checkFileId(fileId);
		
		// check
		Path path = (Path)fileId;
		File file = new File(path.asAbsoluteString());
		return file.exists() && file.isFile();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean copyFile(final FileID srcFileId,
    						final FileID dstFileId,
    						final boolean overwrite) throws IOException {
		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeCopyFile(srcFileId,dstFileId,
								   overwrite);
		
		// copy
		File srcFile = new File(((Path)srcFileId).asAbsoluteString());
		File dstFile = new File(((Path)srcFileId).asAbsoluteString());
		Files.copy(srcFile,dstFile);
		return true;
	}
	@Override
	public boolean renameFile(final FileID srcFileId,
    						  final FileID dstFileId) throws IOException {
		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeMoveFile(srcFileId,dstFileId,
								   false);		// do NOT overwrite it the file exists!
		
		// copy
		File srcFile = new File(((Path)srcFileId).asAbsoluteString());
		File dstFile = new File(((Path)dstFileId).asAbsoluteString());
		Files.move(srcFile,dstFile);
		return true;
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
		// check 
		_check.checkFileId(dstFileId);
		_check.checkBeforeWriteToFile(dstFileId,
									  overwrite);
		// convert to path 
		Path path = (Path)dstFileId;
		File dstFile = new File(path.asAbsoluteString());
		
		// if the parent folder does not exists, create it
		Path parentFolderPath = Path.from(path.getPathElementsExceptLast());
		if (parentFolderPath.getPathElementCount() > 0) {
			File parentFolder = new File(parentFolderPath.asAbsoluteString());
			if (!parentFolder.exists()) Files.createParentDirs(dstFile);
		}
		
		// write
		OutputStream dstFOS = null;
		if (offset == 0) {
			// sequential access
			dstFOS = new FileOutputStream(dstFile,
									   	  false);		// DO NOT append
		} else if (offset > 0) {
			// random access (use NIO when java7 is available)
			final RandomAccessFile rFile = new RandomAccessFile(path.asAbsoluteString(),
														 		"rw");
			rFile.seek(offset);
			dstFOS = new OutputStream() {
							@Override
							public void write(final int b) throws IOException {
								rFile.write(b);
							}
							@Override
							public void write(final byte[] b,final int off,final int len) throws IOException {
								rFile.write(b,off,len);
							}
							@Override
							public void close() throws IOException {
								super.close();
								rFile.close();
							}
					 };
		} else {
			throw new IllegalArgumentException("file offset MUST be > 0");
		}
		return dstFOS;
	}	
	@Override
    public void writeToFile(final InputStream srcIS,
    						final FileID dstFileId,
    					    final boolean overwrite) throws IOException {	
		if (srcIS == null) throw new IllegalArgumentException("the source stream is null");
		OutputStream dstFOS = this.getFileOutputStreamForWriting(dstFileId,0,	// starting at the beginning of the file (offset=0)
																 overwrite);
		long copied = ByteStreams.copy(srcIS,dstFOS);
		dstFOS.close();
    }
	@Override
    public void writeChunkToFile(final byte[] data,
    							 final FileID dstFileId,final long offset,
    							 final boolean overwrite) throws IOException {
		if (data == null) throw new IllegalArgumentException("the source data is null");
		OutputStream dstFOS = this.getFileOutputStreamForWriting(dstFileId,offset,	// starting at the given offset
																 overwrite);				
		long copied = ByteStreams.copy(new ByteArrayInputStream(data),dstFOS);
		dstFOS.close();
    }
	@Override
	public OutputStream getFileOutputStreamForAppending(final FileID dstFileId) throws IOException {
		// check 
		_check.checkFileId(dstFileId);
		_check.checkBeforeAppendToFile(dstFileId);
		
		// write
		Path path = (Path)dstFileId;
		File dstFile = new File(path.asAbsoluteString());
		FileOutputStream dstFOS = new FileOutputStream(dstFile,
													   true);		// append
		return dstFOS;
	}
	@Override
    public void appendToFile(final InputStream srcIS,
    						 final FileID dstFileId) throws IOException {
		if (srcIS == null) throw new IllegalArgumentException("the source stream is null or the destination FileID is null or it's NOT a Path");
		OutputStream dstFOS = this.getFileOutputStreamForAppending(dstFileId);
		long copied = ByteStreams.copy(srcIS,dstFOS);
		dstFOS.close();
    }
    @Override
    public void appendChunkToFile(final byte[] srcDataChunk,
    							  final FileID dstFileId) throws IOException {
		this.appendToFile(new ByteArrayInputStream(srcDataChunk),
						  dstFileId);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  STREAM READ
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public InputStream readFromFile(final FileID fileId) throws IOException {
		return this.readFromFile(fileId,
								 0); 		// starting at the beginning of the file
	}
	@Override
    public InputStream readFromFile(final FileID fileId,final long offset) throws IOException {
		// check 
		_check.checkFileId(fileId);
		_check.checkBeforeReadingFromFile(fileId);
    	
		// read
		Path path = (Path)fileId;
		File srcFile = new File(path.asAbsoluteString());
		
		InputStream srcFIS = null;
		if (offset == 0) {
			// sequential access
			srcFIS = new FileInputStream(srcFile);
		} else if (offset > 0) {
			// random access (use NIO when java7 is available)
			final RandomAccessFile rFile = new RandomAccessFile(path.asAbsoluteString(),
														 		"rw");
			rFile.seek(offset);
			srcFIS = new InputStream() {
							@Override
							public int read() throws IOException {
								return rFile.read();
							}
							@Override
							public int read(final byte[] b,final int off,final int len) throws IOException {
								return rFile.read(b,off,len);
							}
							@Override
							public long skip(final long n) throws IOException {
								return rFile.skipBytes((int)n);
							}
							@Override
							public void close() throws IOException {
								rFile.close();
							}
					 };
		} else {
			throw new IllegalArgumentException("file offset MUST be > 0");
		}
		return srcFIS;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CHUNK READ
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public byte[] readChunkFromFile(final FileID fileId,
			  		   				final long offset,final int len) throws IOException {
		// check 
		_check.checkFileId(fileId);
		_check.checkBeforeReadingFromFile(fileId);
		
    	// Read
		Path path = (Path)fileId;
		File srcFile = new File(path.asAbsoluteString());
		
		FileInputStream srcFIS = null;
		try {
			srcFIS = new FileInputStream(srcFile);
			srcFIS.skip(offset);	// skip till the offset
			
			byte[] outReaded = new byte[len];
			int readed = srcFIS.read(outReaded);
			if (readed > 0) return readed < len ? Arrays.copyOf(outReaded,readed)
												: outReaded;
			return null;	// no data readed
		} finally {
			if (srcFIS != null)  srcFIS.close();
		}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean deleteFile(final FileID fileId) throws IOException {
    	// check
    	_check.checkFileId(fileId);
		_check.checkBeforeDeleteFile(fileId);
		
    	// delete
		Path path = (Path)fileId;
		File file = new File(path.asAbsoluteString());
		return file.delete();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public FileProperties getFileProperties(final FileID fileId) throws IOException {
		if (fileId == null || !(fileId instanceof Path)) throw new IllegalArgumentException("the FileID MUST NOT be null and it have to be a Path");
		
		Path path = (Path)fileId;
		File file = new File(path.asAbsoluteString());
		if (!file.exists()) throw new IllegalArgumentException("File / folder at " + path + " DOES NOT exists!");
		
		return LocalFileProperties.from(file);
    }
	@Override
	public void setFileModifiedDate(final FileID fileId, final long modifiedTimeInMillis) throws IOException {
		if (fileId == null || !(fileId instanceof Path)) throw new IllegalArgumentException("the FileID MUST NOT be null and it have to be a Path");
		
		Path path = (Path)fileId;
		File file = new File(path.asAbsoluteString());
		if (!file.exists()) throw new IllegalArgumentException("File / folder at " + path + " DOES NOT exists!");
		
		file.setLastModified(modifiedTimeInMillis);
	}
}

