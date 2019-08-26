package r01f.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Abstract client from the logic of iterating over a byte array to chunk it
 *
 * This utility type is usefull when a byte array must be chunked in order to be sent somewhere (ie upload a file)
 * ... this way, the client is abstracted from the complexity of chunking the byte array
 * Usage:
 * <pre class='brush:java'>
 *		ContentDataAPI contentDataAPI = ClientFactory.getContentDataAPI(securityContext,
 *																		contentOid);
 *		@Cleanup ChunkedStreamIterator chunkedIt = new ChunkedInputStreamIterator(is);
 *		while (bytesIt.hasNext()) {
 *			int offset = chunkedIt.offset();												// BEWARE!! read offset BEFOREHAND
 *			byte[] currChunk = chunkedIt.next();											// read a chunk
 *			contentDataAPI.uploadAttachmentFileChunk(documentOid,
 *													 path,
 *													 currChunk,offset);						// upload
 *		}
 * </pre>
 * It also can be used as a reader:
 * <pre class='brush:java'>
 *		ContentDataAPI contentDataAPI = ClientFactory.getContentDataAPI(securityContext,
 *																	    contentOid);
 *		@Cleanup ChunkedStreamIterator chunkedReader = new ChunkedInputStreamIterator(is);
 *		byte[] currChunk = null;
 *		do {
 *			int offset = chunkedReader.offset();					// BEWARE!! read offset BEFOREHAND
 *			currChunk = chunkedReader.readChunk();					// read a chunk
 *			if (bytesReaded != null) contentDataAPI.uploadAttachmentFileChunk(documentOid,
 *																			  path,
 *																			  currChunk,offset);	// upload
 *		} while (bytesReaded != null);
 * </pre>
 */
public class ChunkedInputStreamIterator
  implements ChunkedStreamIterator {
///////////////////////////////////////////////////////////////////////////////
// 	MIEMBROS
///////////////////////////////////////////////////////////////////////////////
	private InputStream _is;
	private int _offset = 0;
	private int _blockSize = -1;	// chunk size

///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTORES
///////////////////////////////////////////////////////////////////////////////
	public ChunkedInputStreamIterator(final byte[] bytes) {
		this.setBytes(bytes);
		this.setBlockSize(bytes.length);
	}
	public ChunkedInputStreamIterator(final byte[] bytes,final int blockSize) {
		this.setBytes(bytes);
		this.setBlockSize(blockSize);
	}
	public ChunkedInputStreamIterator(final InputStream is) {
		this.setInputStream(is);
		this.setBlockSize(100 * 1024);	// since the data ammount is NOT known beforehand create a buffer with the default size
	}
	public ChunkedInputStreamIterator(final InputStream is,final int blockSize) {
		this.setInputStream(is);
		this.setBlockSize(blockSize);
	}
///////////////////////////////////////////////////////////////////////////////
// 	GET & SET
///////////////////////////////////////////////////////////////////////////////
	public void setBytes(final byte[] bytes) {
		_is = new ByteArrayInputStream(bytes);
	}
	public void setInputStream(final InputStream is) {
		_is = is;
	}
	public void setBlockSize(final int size) {
		// Compute the block size
		//		- if the file size is GREATER than 1Mb: use chunks
		if (_blockSize <= 0) {
			if (size > 999 * 1024) {
				_blockSize = 100*1024;		// 100Kb chunk
			} else {
				_blockSize = size;			// chunk size = file size: just ONE chunk
			}
		} else {
			_blockSize = 100 * 1024;
		}
	}
	@Override
	public int getOffset() {
		return _offset;
	}
///////////////////////////////////////////////////////////////////////////////
// 	METHODS
///////////////////////////////////////////////////////////////////////////////
	public byte[] readChunk() throws IOException {
		return this.readChunk(_blockSize);
	}
	public byte[] readChunk(final int chunkSize) throws IOException {
		byte[] buff = new byte[chunkSize];
		int readed = _is.read(buff);
		byte[] outBytes = readed > 0 ? Arrays.copyOf(buff,readed) : null;
		_offset += readed;
		return outBytes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Closeable
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void close() throws IOException {
		_is.close();
	}
///////////////////////////////////////////////////////////////////////////////
// 	ITERATOR
///////////////////////////////////////////////////////////////////////////////
	@Override
	public byte[] next() {
		byte[] outBytes = null;
		try {
			outBytes = this.readChunk();
		} catch (IOException ioEx) {
			ioEx.printStackTrace(System.out);
		}
		return outBytes;
	}
	@Override
	public boolean hasNext() {
		try {
			return _is.available() > 0;
		} catch (IOException ioEx) {ioEx.printStackTrace(System.out);}
		return false;
	}
	@Override
	public void remove() {
		/* nothing to do */
	}
	public boolean isAvailable() {
		return this.hasNext();
	}
}
