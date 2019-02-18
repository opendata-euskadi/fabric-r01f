package r01f.filestore.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.file.FileID;
import r01f.io.ChunkedInputStream;
import r01f.io.ChunkedInputStreamChunksProducer;
import r01f.io.ChunkedOutputStream;
import r01f.io.ChunkedOutputStreamChunksConsumer;
import r01f.io.Streams;
import r01f.types.Path;

/**
 * {@link FileStoreAPI} wrapper
 * READING FILE CONTENTS:
 * ======================
 * <pre class="brush:java">
 *		String readedText = Files.wrap(api)
 *								 .forLoading(TEST_FILE_PATH)
 *								 .asString();
 * </pre>
 * If a chunked reading is required:
 * <pre class="brush:java">
 *		InputStream is = Files.wrap(api)
 *							  .forLoading(TEST_FILE_PATH)
 *							  .asChunkedInputStream(5);	// chunks of 5 bytes
 *		String readedText = Streams.inputStreamAsString(is);
 * </pre>
 * APPENDING / WRITING TO FILE
 * ===========================
 * <pre class="brush:java">
 *		Files.wrap(api)
 *			 .forAppendingTo(TEST_FILE_PATH)
 *			 .append(TEST_TEXT);
 * </pre>
 * If the source data to be appended is chunked:
 * <pre class="brush:java">
 *		Files.wrap(api)
 *			 .forAppendingTo(TEST_FILE_PATH)
 *			 .append(new ChunkedInputStreamChunksProducer() {
 *								@Override
 *								public byte[] get(final long offset) throws IOException {
 *									long available = TEST_TEXT.length() - offset;
 *									if (available <= 0) return null;
 *									return available > 5 ? TEST_TEXT.substring((int)offset,(int)(offset + 5)).getBytes()
 *														 : TEST_TEXT.substring((int)offset).getBytes();
 *								}
 *			 		 });
 * </pre>
 */
@RequiredArgsConstructor
public class FileStoreAPIWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	private final FileStoreAPI _api;
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileApiLoadWrapper forLoading(final Path filePath) {
		return this.forReading(filePath);
	}
	public FileApiLoadWrapper forReading(final Path filePath) {
		return new FileApiLoadWrapper(_api,
									  filePath);
	}
	public FileApiAppendWrapper forAppendingTo(final Path filePath) {
		 return new FileApiAppendWrapper(_api,
				 						 filePath);
	}
	public FileApiWriteWrapper forOverwriting(final Path filePath) {
		 return new FileApiWriteWrapper(_api,
				 						filePath,
				 						true);		// overwrite
	}
	public FileApiWriteWrapper forWriting(final Path filePath) {
		 return new FileApiWriteWrapper(_api,
				 						filePath,
				 						false);		// do NOT overwrite
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	LOAD WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////    
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class FileApiLoadWrapper {
    	private final FileStoreAPI _api;
    	private final FileID _fileId;
    	
    	public String asString() throws IOException {
    		InputStream is = this.asInputStream();
    		return Streams.inputStreamAsString(is);
    	}
    	public byte[] asByteArray() throws IOException {
    		InputStream is = this.asInputStream();
    		return Streams.inputStreamBytes(is);
    	}
    	public InputStream asInputStream() throws IOException {
    		return _api.readFromFile(_fileId,
    								 0);	// start reading at the beginning of the file
    	}
    	public ChunkedInputStreamChunksProducer getChunksProducer(final int chunkSize) {
    		// crate a chunks producer that simply reads a chunk of data
    		// using the underlying api
    		return new ChunkedInputStreamChunksProducer() {
							@Override
							public byte[] get(final long offset) throws IOException {
								return _api.readChunkFromFile(_fileId,
															  offset,
															  chunkSize);
							}
				    };
    	}
    	public InputStream asChunkedInputStream(final int chunkSize) {
    		ChunkedInputStreamChunksProducer chunksProducer = this.getChunksProducer(chunkSize);
    		ChunkedInputStream outIS = new ChunkedInputStream(chunksProducer);
    		return outIS;
    	}
    	public void consumedBy(final ChunkedOutputStreamChunksConsumer consumer) {
    		// nothing
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	APPEND WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////    
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class FileApiAppendWrapper {
    	private final FileStoreAPI _api;
    	private final FileID _fileId;
    	
    	public void append(final String data) throws IOException {
    		this.append(data.getBytes());
    	}
    	public void append(final byte[] data) throws IOException {
    		this.append(new ByteArrayInputStream(data));
    	}
    	public void append(final ChunkedInputStreamChunksProducer chunksProducer) throws IOException {
    		// the source is chunked so the appending is also chunked
    		// (better if the number of readed bytes equals the number of written bytes) 
    		ChunkedInputStream chunkedInputStream = new ChunkedInputStream(chunksProducer);
    		ChunkedOutputStreamChunksConsumer chunksConsumer = this.getChunksAppender();
    		ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(chunksConsumer);
    		Streams.copy(chunkedInputStream,
    					 chunkedOutputStream,
    					 true);
    	}
    	public void append(final InputStream srcIS) throws IOException {
    		_api.appendToFile(srcIS,
    						  _fileId);    		
    	}
    	public ChunkedOutputStreamChunksConsumer getChunksAppender() {
    		return new ChunkedOutputStreamChunksConsumer() {
							@Override
							public boolean put(final long offset,final byte[] bytes) throws IOException {
								_api.appendChunkToFile(bytes,
													   _fileId);
								return true;
							}
					};
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	WRITE WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////    
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class FileApiWriteWrapper {
    	private final FileStoreAPI _api;
    	private final FileID _fileId;
    	private final boolean _overwrite;
    	
    	public void write(final String data) throws IOException {
    		this.write(data.getBytes());
    	}
    	public void write(final byte[] data) throws IOException {
    		this.write(new ByteArrayInputStream(data));
    	}
    	public void write(final ChunkedInputStreamChunksProducer chunksProducer) throws IOException {
    		// the source is chunked so the appending is also chunked
    		// (better if the number of readed bytes equals the number of written bytes) 
    		ChunkedInputStream chunkedInputStream = new ChunkedInputStream(chunksProducer);
    		ChunkedOutputStreamChunksConsumer chunksConsumer = this.getChunksWriter();
    		ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(chunksConsumer);
    		Streams.copy(chunkedInputStream,
    					 chunkedOutputStream,
    					 true);
    	}
    	public void write(final InputStream srcIS) throws IOException {
    		_api.writeToFile(srcIS,
    						 _fileId,
    						 _overwrite);		   		
    	}
    	public ChunkedOutputStreamChunksConsumer getChunksWriter() {
    		return new ChunkedOutputStreamChunksConsumer() {
							@Override
							public boolean put(final long offset,final byte[] bytes) throws IOException {
								_api.writeChunkToFile(bytes,
													  _fileId,offset,
													  _overwrite);	
								return true;
							}
    		   	  };
    	}
    }
}
