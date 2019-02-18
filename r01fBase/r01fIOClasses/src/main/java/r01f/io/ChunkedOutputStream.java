package r01f.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link OutputStream} that operates in a chunked way
 * 
 * This class maintains an internal byte[] buffer that is flushed to the chunks consumer
 * (an instance of ChunkedOutputStreamChunksConsumer) when the number of bytes written
 * into the buffer exceeds the consumer accepted chunk size
 * 
 * Sample code:
 * <pre class='brush:java'>
 *		ChunkedOutputStreamChunksConsumer consumer = new ChunkedOutputStreamChunksConsumer() {
 *															@Override
 *															public boolean put(long offset, byte[] bytes) throws IOException {
 *																System.out.print(new String(bytes));
 *																return true;
 *															}
 *													 };
 *		ChunkedOutputStream chunkedOS = new ChunkedOutputStream(10,5,consumer);
 *		chunkedOS.write("El veloz murciélago hindú comía feliz cardillo y kiwis".getBytes());
 *		chunkedOS.flush();
 * </pre>
 * 
 */
public class ChunkedOutputStream 
     extends OutputStream {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int DEF_BUFFER_SIZE = 1024 * 2;	// 2k
	private static final int DEF_CHUNK_SIZE = 1024;			// 1K
/////////////////////////////////////////////////////////////////////////////////////////
//  STATE
/////////////////////////////////////////////////////////////////////////////////////////
	/** 
	 * the chunks consumer 
	 */
	private ChunkedOutputStreamChunksConsumer _consumer;
	
	/** 
	 * Absolute position in the chunks consummer where data is to be written
	 */
	private long _absoluteOutputStreamWritePosition;

	/**
	 * Ring buffer to which the {@link OutputStream} methods are delegated
	 */
	private ChunkedRingOutputStreamBase _buffer = null;
		
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor using the chunks consumer.
	 * @param consumer The chunks consumer
	 */
	public ChunkedOutputStream(final int chunkSize,
							   final ChunkedOutputStreamChunksConsumer consumer) {
		this(chunkSize * 2,chunkSize,
			 consumer);
	}
	/**
	 * Constructor using the chunks consumer.
	 * @param consumer The chunks consumer
	 */
	public ChunkedOutputStream(final int internalBufferSize,final int chunkSize,
							   final ChunkedOutputStreamChunksConsumer consumer) {
		this(internalBufferSize,chunkSize,
			 consumer,
			 0);		// write start position
	}
	/**
	 * Constructor using the chunks consumer.
	 * @param consumer The chunks consumer
	 */
	public ChunkedOutputStream(final ChunkedOutputStreamChunksConsumer consumer) {
		this(DEF_BUFFER_SIZE,DEF_CHUNK_SIZE,
			 consumer,
			 0);		// write start position
	}
	/**
	 * Constructor using the chunks consumer and the starting offset
	 * @param consumer The chunks consumer
	 * @param writeStartPosition the offset where the data is going to start to be written
	 */
	public ChunkedOutputStream(final int internalBufferSize,final int chunkSize,
							   final ChunkedOutputStreamChunksConsumer consumer,
							   final long writeStartPosition) {
		if (internalBufferSize < chunkSize) throw new IllegalArgumentException("The internal buffer size must be greater than the chunk size");
		int theBufferSize = internalBufferSize > 0 ? internalBufferSize
												   : DEF_BUFFER_SIZE;
		int theChunkSize = chunkSize > 0 ? chunkSize 
									     : DEF_CHUNK_SIZE;
		_consumer = consumer;
		_absoluteOutputStreamWritePosition = writeStartPosition;
		_buffer = new ChunkedRingOutputStreamBase(theBufferSize,
												  theChunkSize) {
								@Override
								protected void writeChunk(byte[] chunkBytes) throws IOException {
									_consumer.put(_absoluteOutputStreamWritePosition,
												  chunkBytes);
									// start writing position 
									_absoluteOutputStreamWritePosition = _absoluteOutputStreamWritePosition + chunkBytes.length;
								}
					  };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void write(int b) throws IOException {
		_buffer.write(b);
	}
	@Override
	public void write(byte[] b) throws IOException {
		_buffer.write(b);
	}
	@Override
	public void write(byte[] b,int off,int len) throws IOException {
		_buffer.write(b,off,len);
	}
	
	@Override
	public void flush() throws IOException {
		_buffer.flush();
	}
	
	@Override
	public void close() throws IOException {
		_buffer.flush();
		_buffer.close();
	}
}
