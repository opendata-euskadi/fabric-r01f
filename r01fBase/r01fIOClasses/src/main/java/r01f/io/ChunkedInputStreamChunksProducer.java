package r01f.io;

import java.io.IOException;

/**
 * {@link ChunkedInputStream} chunks provider.
 */
public interface ChunkedInputStreamChunksProducer {
	/**
	 * Returns the next chunk of data starting at the provided offset
	 * @param offset the offset where the next chunk is going to be started to read
	 * @return the chunk data
	 * @exception IOException if an I/O error occurs.
	 */
	public byte[] get(long offset) throws IOException;
}
