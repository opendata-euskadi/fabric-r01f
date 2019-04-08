package r01f.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream that operates in a chunked way, requests chunks of data to a underlying chunks producer
 * The usage is very simple... it only relays in a chunks producer impl (a type that implements {@link ChunkedInputStreamChunksProducer})
 * <pre class='brush:java'>
 * 		@Cleanup ChunkedInputStream is = new ChunkedInputStream(new TestChunkedInputStreamproducer());
 *		System.out.println(Strings.of(is)
 *							   	  .asString());
 * </pre>
 * The {@link ChunkedInputStreamChunksProducer} could be as simple as:
 * <pre class='brush:java'>
 * 	private static final String TEST_FILE = "El murcielago hindú comía feliz cardillo y kiwis";
 *	private class TestChunkedInputStreamproducer
 *	   implements ChunkedInputStreamChunksProducer {
 *		
 *		private boolean _eof = false;
 *		
 *		@Override
 *		public byte[] get(final long offset) throws IOException {
 *			if (_eof) return null;
 *
 *			int pos = (int)offset;
 *			int chunkLength = Math.min(CHUNK_SIZE,
 *								  	   TEST_FILE.length() - pos);
 *			byte[] outBytes = TEST_FILE.substring(pos,pos + chunkLength)
 *									   .getBytes();
 *			if ((offset + chunkLength) >= TEST_FILE.length()) _eof = true;
 *			return outBytes;
 *		}
 *	}
 * </pre> 
 */
public class ChunkedInputStream 
     extends InputStream {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int BUFFER_SIZE = 2 * 1024;
	private static final boolean CONSUME_ALL_PROVIDED_DATA_BEFORE_CLOSING = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
	/** The chunks producer */
	private final ChunkedInputStreamChunksProducer _producer;
	
	/** Absolute position in the produce where data is readed */
	private long _absoluteInputStreamReadPosition;
	
	/** Last-readed chunk buffer */
	InputStream _chunkBuffer;

	/** Last-readed chunk size */
	private int _chunkBufferSize;

	/** The current position within the current chunk */
	private int _chunkBufferReadPosition;
	
	/** True if the end of a chunk has been reached */
	private boolean _chunkEOF = true;
	
	/** True if we've reached the end of stream */
	private boolean _eof = false;

	/** True if this stream is closed */
	private boolean _closed = false;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor using the chunks producer.
	 * @param producer The chunks producer
	 */
	public ChunkedInputStream(final ChunkedInputStreamChunksProducer producer) {
		this(producer,
			 null);			// first chunk
	}
	/**
	 * Constructor using the chunks producer and the starting offset
	 * @param producer The chunks producer
	 * @param readStartPosition the offest where the first chunk is going to be readed 
	 */
	public ChunkedInputStream(final ChunkedInputStreamChunksProducer producer,
							  final long readStartPosition) {
		this(producer,
			 readStartPosition,			
			 null);			// first chunk
	}
	/**
	 * Constructor using the chunks producer and the first chunk data
	 * @param producer The chunks producer
	 */
	public ChunkedInputStream(final ChunkedInputStreamChunksProducer producer,
							  final byte[] firstChunk) {
		this(producer,
			 0,
			 firstChunk);
	}
	/**
	 * Constructor using the chunks producer, the offset and the first chunk data
	 * @param producer The chunks producer
	 * @param readStartPosition the offset where the first chunk provided is going to be readed
	 * @param firstChunk the first chunk of data
	 */
	public ChunkedInputStream(final ChunkedInputStreamChunksProducer producer,
							  final long readStartPosition,
							  final byte[] firstChunk) {
		super();
		if (producer == null) throw new IllegalArgumentException("Chunks producer may not be null");
		_producer = producer;
		if (firstChunk != null && firstChunk.length > 0) {
			_chunkBufferReadPosition = 0;
			_chunkBufferSize = firstChunk.length;
			_chunkBuffer = new ByteArrayInputStream(firstChunk);
			_chunkEOF = false;
			_absoluteInputStreamReadPosition = readStartPosition + _chunkBufferSize;
		} else {
			_chunkBufferReadPosition = 0;
			_chunkEOF = true;
			_absoluteInputStreamReadPosition = readStartPosition;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  InputStream INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int available() throws IOException {
		int outAvailableData = 0;
		if (!_eof && _chunkEOF) _nextChunk();		// fill the underlying buffer
		if (!_eof) {
			outAvailableData = Math.min(_chunkBufferSize,								// all the buffer data (nothing has been readed)
										_chunkBufferSize - _chunkBufferReadPosition);	// the remaining bytes to be readed in the chunk buffer
		}
		return outAvailableData;
	}
	@Override
	public int read() throws IOException {
		if (_closed) throw new IOException("Attempted read from closed stream.");
		if (_eof) return -1;
			
		if (_chunkEOF) _nextChunk();			// if underlying chunk buffer has been consumed... fill the buffer
		
		int readedByte = _chunkBuffer.read();	// read a byte from the underlying chunk buffer
		if (readedByte != -1) {
			_chunkBufferReadPosition++;		
			if (_chunkBufferReadPosition >= _chunkBufferSize) _chunkEOF = true;
		}
		return readedByte;
	}

	@Override
	public int read(final byte[] b,final int off,final int len) throws IOException {
		if (_closed) throw new IOException("Attempted read from closed stream.");
		if (_eof) return -1;
		
		if (_chunkEOF) _nextChunk();	// if underlying chunk buffer has been consumed... fill the buffer
		if (_eof) return -1;			// ... oh! 
		
		int bytesToRead = Math.min(len,							// requested bytes num 
					   			   _chunkBufferSize - _chunkBufferReadPosition);		// remaining bytes in underlying chunk buffer
		int bytesReaded = _chunkBuffer.read(b,					// byte array where readed bytes will be placed
								  			off,				// the offeset into the previous byte array where the readed bytes will be started to be placed
								  			bytesToRead);		// the number of bytes to be readed
		if (bytesReaded != -1) {
			_chunkBufferReadPosition += bytesReaded;
			if (_chunkBufferReadPosition >= _chunkBufferSize) _chunkEOF = true;
		} else {
			_eof = true;
			throw new IOException("Truncated chunk " + "( expected size: " + _chunkBufferSize + "; actual size: " + _chunkBufferReadPosition + ")");
		}
		return bytesReaded;
	}
	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b,0,b.length);
	}
	@Override @SuppressWarnings("unused")
	public void close() throws IOException {
		if (!_closed) {
			try {
				if (!_eof && CONSUME_ALL_PROVIDED_DATA_BEFORE_CLOSING) {
					// read and discard the remainder of the message
					// leaving the underlying socket at a position to start 
					// reading the next response without scanning.
					byte buffer[] = new byte[BUFFER_SIZE];
					while (this.read(buffer) >= 0) { /* nothing to do with data */ }
				}
			} finally {
				_eof = true;
				_closed = true;
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Read the next chunk.
	 * @throws IOException in case of an I/O error
	 */
	private void _nextChunk() throws IOException {
		// another chunk is starting...
		byte[] nextChunkBytes = _producer.get(_absoluteInputStreamReadPosition);	// the producer dictates the chunk size
		if (nextChunkBytes != null && nextChunkBytes.length > 0) {
			_chunkBuffer = new ByteArrayInputStream(nextChunkBytes);
			_chunkBufferSize = nextChunkBytes.length;
			_chunkBufferReadPosition = 0;				
			_chunkEOF = false;	
			_absoluteInputStreamReadPosition += _chunkBufferSize;
		} else {
			_eof = true;
		}
	}
}
