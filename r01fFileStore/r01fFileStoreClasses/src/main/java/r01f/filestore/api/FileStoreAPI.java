package r01f.filestore.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import r01f.file.FileID;
import r01f.file.FileProperties;

/**
 * Samples
 * [1] - Load a file
 * <pre class='brush:java'>
 * 		FileStoreAPI api = new LocalFileStoreAPI();
 *		
 *		// Read the test file contents
 *		String readedText = Files.wrap(api)
 *								 .forLoading(TEST_FILE_PATH)
 *								 .asString();
 * </pre>
 * [2] - Load chunked
 * <pre class='brush:java'>
 * 		FileStoreAPI api = new LocalFileStoreAPI();
 *		
 *		// Read the test file contents
 *		InputStream is = Files.wrap(api)
 *							  .forLoading(TEST_FILE_PATH)
 *							  .asChunkedInputStream(5);	// chunks of 5 bytes
 *		String readedText = Streams.inputStreamAsString(is);
 * </pre>
 * [3] - Append
 * <pre class='brush:java'>
 *		FileStoreAPI api = new LocalFileStoreAPI();
 *		
 *		// Append to the file
 *		Files.wrap(api)
 *			 .forAppendingTo(TEST_FILE_PATH)
 *			 .append(TEST_TEXT);
 * </pre>
 * [4] - Append chunked
 * <pre class='brush:java'>
 *		FileStoreAPI api = new LocalFileStoreAPI();
 *		
 *		// Append to the file
 *		Files.wrap(api)
 *			 .forAppendingTo(TEST_FILE_PATH)
 *			 .append(new ChunkedInputStreamChunksProducer() {
 *								@Override
 *								public byte[] get(final long offset) throws IOException {
 *									long available = TEST_TEXT.length() - offset;
 *									if (available <= 0) return null;
 *									String outStr = available >= 5 ? TEST_TEXT.substring((int)offset,(int)(offset + 5))
 *														  		   : TEST_TEXT.substring((int)offset);
 *									return outStr.getBytes();
 *								}
 *			 		 });
 * </pre>
 * [5] - Write
 * <pre class='brush:java'>
 *		FileStoreAPI api = new LocalFileStoreAPI();
 *		
 *		// Overwrite the file
 *		Files.wrap(api)
 *			 .forOverwriting(TEST_FILE_PATH)
 *			 .write(TEST_TEXT);
 * </pre>
 * [6] - Write chunked
 * <pre class='brush:java'>
 *		FileStoreAPI api = new LocalFileStoreAPI();
 *		
 *		// Append to the file
 *		Files.wrap(api)
 *			 .forOverwriting(TEST_FILE_PATH)
 *			 .write(new ChunkedInputStreamChunksProducer() {
 *								@Override
 *								public byte[] get(final long offset) throws IOException {
 *									long available = TEST_TEXT.length() - offset;
 *									if (available <= 0) return null;
 *									String outStr = available >= 5 ? TEST_TEXT.substring((int)offset,(int)(offset + 5))
 *														  		   : TEST_TEXT.substring((int)offset);
 *									return outStr.getBytes();
 *								}
 *			 		 });
 * </pre>
 */
public interface FileStoreAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTENCE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if a file exists
	 * @param a fileId
	 * @return
	 * @throws IOException
	 */
	public boolean existsFile(final FileID fileId) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copies a file
	 * @param srcFileId
	 * @param dstFileId
	 * @param overwrite
	 * @return
	 * @throws IOException
	 */
	public boolean copyFile(final FileID srcFileId,
							final FileID dstFileId,
							final boolean overwrite) throws IOException;
	/**
	 * Renames a file (the same as move)
	 * @param srcFileId 
	 * @param dstFileId
	 * @return
	 * @throws IOException
	 */
	public boolean renameFile(final FileID srcFileId,
							  final FileID dstFileId) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  WRITE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns an {@link OutputStream} to a file to be written
	 * @param dstFileId
	 * @param overwrite
	 * @return
	 * @throws IOException
	 */
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,
													  final boolean overwrite) throws IOException;
	/**
	 * Returns an {@link OutputStream} to a file to be written
	 * @param dstFileId
	 * @param offset the position inside the file where to start writing from
	 * @param overwrite
	 * @return
	 * @throws IOException
	 */
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,final long offset,
													  final boolean overwrite) throws IOException;
	/**
	 * Writes a stream of data to a file
	 * @param srcIS
	 * @param dstFileId
	 * @param overwrite true if the file must be overwritten if it previously exists
	 * @throws IOException
	 */
	public void writeToFile(final InputStream srcIS,
							final FileID dstFileId,						
							final boolean overwrite) throws IOException;
	/**
	 * Appends a chunk of data to a file
	 * @param data
	 * @param offset
	 * @param dstFileId
	 * @param offset the point from which the data is going to start being written (only if random access is supported)
	 * @param overwrite true if the file must be overwritten if it previously exists
	 * @throws IOException
	 */
	public void writeChunkToFile(final byte[] data,
								 final FileID dstFileId,final long offset,
								 final boolean overwrite) throws IOException;
	/**
	 * Returns an {@link OutputStream} to a file to be appended
	 * @param dstFileId
	 * @return
	 * @throws IOException
	 */
	public OutputStream getFileOutputStreamForAppending(final FileID dstFileId) throws IOException;
	/**
	 * Appends a stream of data to a file
	 * @param srcIS
	 * @param dstFileId
	 * @throws IOException
	 */
	public void appendToFile(final InputStream srcIS,
							 final FileID dstFileId) throws IOException;
	/**
	 * Appends a chunk of data to a file
	 * @param data
	 * @param dstFileId
	 * @throws IOException
	 */
	public void appendChunkToFile(final byte[] data,
								  final FileID dstFileId) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  READ
/////////////////////////////////////////////////////////////////////////////////////////   
	/**
	 * Reads a file
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	public InputStream readFromFile(final FileID fileId) throws IOException;
	/**
	 * Reads a file
	 * @param fileId
	 * @param offset the point from which the data is going to start being readed (only if random access is supported)
	 * @return
	 * @throws IOException
	 */
	public InputStream readFromFile(final FileID fileId,final long offset) throws IOException;
	/**
	 * Reads a chunk of data to a file
	 * @param fileId
	 * @param offset
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public byte[] readChunkFromFile(final FileID fileId,
			  		   				final long offset,final int len) throws IOException;

/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Deletes a file 
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	public boolean deleteFile(final FileID fileId) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns a file properties
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	public FileProperties getFileProperties(final FileID fileId) throws IOException;
	/**
	 * Change file modified date.
	 * @param fileId
	 * @param modifiedTimeInMillis
	 * @throws IOException
	 */
	public void setFileModifiedDate(final FileID fileId, 
									final long modifiedTimeInMillis) throws IOException;
}
