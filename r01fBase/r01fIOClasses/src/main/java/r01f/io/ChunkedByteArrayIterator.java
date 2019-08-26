package r01f.io;

import java.io.IOException;
import java.util.Arrays;

/**
 * Abstracts client from iterating over a byte array to chuck it
 *
 * This utility type is usefull when a byte array must be chunked in order to be sent somewhere (ie upload a file)
 * ... this way, the client is abstracted from the complexity of chunking the byte array
 * Usage_
 * <pre class='brush:java'>
 *		R01MContentDataAPI contentDataAPI = ClientFactory.getContentDataAPI(securityContext,
 *																			contentOid);
 *		ChunkedStreamIterator bytesIt = new ChunkedByteArrayIterator(bytes);
 *		while (bytesIt.hasNext()) {
 *			int offset = bytesIt.getOffset();
 *			byte[] bytes = bytesIt.next();
 *			contentDataAPI.uploadAttachmentFileChunk(documentOid,
 *													 path,bytes,offset);
 *		}
 * </pre>
 * It also can be used as a reader:
 * <pre class='brush:java'>
 *		ContentDataAPI contentDataAPI = ClientFactory.getContentDataAPI(securityContext,
 *																	    contentOid);
 *		@Cleanup ChunkedByteArrayIterator chunkedReader = new ChunkedByteArrayIterator(bytes);
 *		byte[] currChunk = null;
 *		do {
 *			int offset = chunkedIS.offset();						// BEWARE!! read offset BEFOREHAND
 *			currChunk = chunkedIS.readChunk();						// read a chunk
 *			if (bytesReaded != null) contentDataAPI.uploadAttachmentFileChunk(documentOid,
 *																			  path,currChunk,offset);	// upload the chunk
 *		} while (bytesReaded != null);
 * </pre>
 */
public class ChunkedByteArrayIterator
  implements ChunkedStreamIterator {
///////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////
	private byte[] _bytes;
	private int _initOffset = 0;
	private int _endOffset = 0;
	private int _blockSize = 10*1024;	// chunk size
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	public ChunkedByteArrayIterator() {
		// default no-args constructor
	}
	public ChunkedByteArrayIterator(final byte[] bytes) {
		this.setBytes(bytes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET / SET
/////////////////////////////////////////////////////////////////////////////////////////
	public void setBytes(final byte[] bytes) {
		_bytes = bytes;
		_computeBlockSize();
	}
	@Override
	public int getOffset() {
		return _endOffset;
	}
	private void _computeBlockSize() {
		// Compute the block size
		//		- if the file size is GREATER than 1Mb: use chunks
		if (_bytes.length > 999 * 1024) {
			_blockSize = 100*1024;		// chunk size = 100 Kb
		} else {
			_blockSize = _bytes.length;	// chunk size = file size: just ONE chunk
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public byte[] readChunk() {
		byte[] outBytes = _read();
		return outBytes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Closeable
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void close() throws IOException {
		// no hace nada ya que no hay nada que cerrar
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Iterator
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public byte[] next() {
		byte[] outBytes = _read();
		return outBytes;
	}
	@Override
	public boolean hasNext() {
		return _endOffset < _bytes.length;
	}
	@Override
	public void remove() {
		/* nothing to do */
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE
/////////////////////////////////////////////////////////////////////////////////////////
	private byte[] _read() {
		_initOffset = _endOffset;	// move pointer to the beginning
		byte[] outBytes = null;
		if (this.hasNext()) {
			outBytes = Arrays.copyOfRange(_bytes,						// byte array from the where data is readed
										  _initOffset,					// read position begin
										  _initOffset + _blockSize); 	// read position end
			// Update
			_endOffset += _blockSize;
			// BEWARE! there could be less data than the chunk size
			if (_endOffset + _blockSize > _bytes.length) {
				_blockSize = _bytes.length - _endOffset;
			}
		}
		return outBytes;
	}
}
