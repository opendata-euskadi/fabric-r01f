package r01f.io;

import java.io.IOException;

/**
 * {@link ChunkedOutputStream} chunks consumer.
 */
public interface ChunkedOutputStreamChunksConsumer {
	/**
	 * Consumes a chunk of bytes from a a chunked stream
	 * @param offset the offset where the next chunk is going to be started to be writen
	 * @param bytes the chunk data
	 * @exception IOException if an I/O error occurs.
	 */
	public boolean put(long offset,
					   byte[] bytes) throws IOException;
}
