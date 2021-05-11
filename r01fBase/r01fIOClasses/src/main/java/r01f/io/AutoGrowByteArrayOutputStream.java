package r01f.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A {@link ByteArrayOutputStream} implementation that auto-grows when needed
 */
public class AutoGrowByteArrayOutputStream
	 extends OutputStream {

	private static final int DEFAULT_BLOCK_SIZE = 256;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	// The buffers used to store the content bytes
	private final LinkedList<byte[]> _buffers = new LinkedList<>();

	// The maximum buffer size
	private final int _maxBufferSize;

	// The size, in bytes, to use when allocating the first byte[]
	private final int _initialBlockSize;

	// The size, in bytes, to use when allocating the next byte[]
	private int _nextBlockSize = 0;

	// The TOTAL number of bytes in previous buffers.
	// (The number of bytes in the current buffer is in '_currentBufferIndex' -next property-)
	private int _alreadyBufferedBytesCount = 0;

	// The index in the byte[] found at buffers.getLast() to be written next
	private int _currentBufferIndex = 0;

	// Is the stream closed?
	private boolean _closed = false;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public AutoGrowByteArrayOutputStream(final int maxBufferSize) {
		this(DEFAULT_BLOCK_SIZE,
			 maxBufferSize);
	}
	public AutoGrowByteArrayOutputStream(final int initialBlockSize,
										 final int maxBufferSize) {
		if (initialBlockSize <= 0 || maxBufferSize <= 0) throw new IllegalArgumentException("Initial block size and max buffer size must be greater than 0");

		_maxBufferSize = maxBufferSize;
		_initialBlockSize = initialBlockSize;
		_nextBlockSize = initialBlockSize;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void write(final int datum) throws IOException {
		if (_closed) throw new IOException("Stream closed");

		// have to create a new buffer?
		if (_buffers.peekLast() == null || _buffers.getLast().length == _currentBufferIndex) {
			_createNewBuffer(1);	// 1 = min capacity
		}
		// store the byte
		_buffers.getLast()[_currentBufferIndex++] = (byte)datum;
	}
	@Override
	public void write(final byte[] data,final int offset,final int length) throws IOException {
		if (offset < 0 || offset + length > data.length || length < 0) throw new IndexOutOfBoundsException();
		if (_closed) throw new IOException("Stream closed");

		// check if there's an available buffer; if not create a NEW one
		if (_buffers.peekLast() == null || _buffers.getLast().length == _currentBufferIndex) {
			_createNewBuffer(length);	// length = min capacity
		}

		// if the current buffer DOES NOT have enough space for the
		// bytes to be written, create as much buffers as necessary
		if (_currentBufferIndex + length > _buffers.getLast().length) {
			int bytesLeft = length;

			int pos = offset;
			do {
				if (_currentBufferIndex == _buffers.getLast().length) {
					_createNewBuffer(bytesLeft);	// length = min capacity
				}
				int copyLength = _buffers.getLast().length - _currentBufferIndex;
				if (bytesLeft < copyLength) {
					copyLength = bytesLeft;
				}
				System.arraycopy(data,pos,					// source
								 _buffers.getLast(),_currentBufferIndex, // dest
								 copyLength);				// length
				pos += copyLength;
				_currentBufferIndex += copyLength;
				bytesLeft -= copyLength;
			}
			while (bytesLeft > 0);
		}
		// there's enough space for the bytes to be written
		else {
			// copy in the sub-array
			System.arraycopy(data,offset,				// source
							 _buffers.getLast(),_currentBufferIndex,	// dest
							 length);					// length
			_currentBufferIndex += length;
		}
	}
	@Override
	public void close() {
		_closed = true;
	}
	/**
	 * Convert the buffer's contents into a string decoding bytes using the default character set.
	 * The length of the new String depends on of the character set, and hence may not be equal to the
	 * buffer's size
	 * @return a String decoded from the buffer's contents
	 */
	@Override
	public String toString() {
		return new String(this.toByteArrayUnsafe());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the number of bytes stored in this {@link OutputStream}
	 */
	public int size() {
		return _alreadyBufferedBytesCount + _currentBufferIndex;
	}
	/**
	 * Convert the stream's data to a byte array and return the byte array.
	 * Also replaces the internal structures with the byte array to conserve memory:
	 * if the byte array is being made anyways, mind as well as use it. This approach
	 * also means that if this method is called twice without any writes in between,
	 * the second call is a no-op.
	 *
	 * This method is "unsafe" as it returns the internal buffer.
	 * Callers should not modify the returned buffer.
	 * @return the current contents of this output stream, as a byte array.
	 * @see #size()
	 * @see #toByteArray()
	 */
	public byte[] toByteArrayUnsafe() {
		int totalSize = size();
		if (totalSize == 0) return new byte[0];

		this.resize(totalSize);
		return _buffers.getFirst();
	}
	/**
	 * Creates a newly allocated byte array.
	 * Its size is the current size of this output stream and the valid contents of the buffer
	 * have been copied into it.
	 * @return the current contents of this output stream, as a byte array.
	 * @see #size()
	 * @see #toByteArrayUnsafe()
	 */
	public byte[] toByteArray() {
		byte[] bytesUnsafe = this.toByteArrayUnsafe();
		return bytesUnsafe.clone();
	}
	/**
	 * Reset the contents of this <code>FastByteArrayOutputStream</code>.
	 * <p>All currently accumulated output in the output stream is discarded.
	 * The output stream can be used again.
	 */
	public void reset() {
		_buffers.clear();
		_nextBlockSize = _initialBlockSize;
		_closed = false;
		_currentBufferIndex = 0;
		_alreadyBufferedBytesCount = 0;
	}
	/**
	 * Get an {@link InputStream} to retrieve the data in this {@link OutputStream}
	 * Note that if any methods are called on the {@link OutputStream}
	 * (including, but not limited to, any of the write methods, {@link #reset()},
	 * {@link #toByteArray()}, and {@link #toByteArrayUnsafe()}) then the
	 * {@link java.io.InputStream}'s behavior is undefined.
	 * @return {@link InputStream} of the contents of this OutputStream
	 */
	public InputStream getInputStream() {
		return new AutoGrowByteArrayInputStream(_buffers,
												_currentBufferIndex);
	}
	/**
	 * Write the buffers content to the given OutputStream.
	 * @param out the OutputStream to write to
	 */
	public void writeTo(final OutputStream out) throws IOException {
		// put all buffers content to the given output stream
		Iterator<byte[]> it = _buffers.iterator();
		while (it.hasNext()) {
			byte[] bytes = it.next();
			boolean notCurrentBuffer = it.hasNext();

			if (notCurrentBuffer) {
				// not the current buffer (the buffer MUST be full)
				out.write(bytes,0,			// source
						  bytes.length);	// length
			}
			else {
				// the current buffer (the buffer might not be full)
				out.write(bytes,0,			// source
						  _currentBufferIndex);			// length
			}
		}
	}
	/**
	 * Resize the internal buffer size to a specified capacity.
	 * @param targetCapacity the desired size of the buffer
	 * @throws IllegalArgumentException if the given capacity is smaller than the actual size of the content stored in the buffer already
	 */
	public void resize(final int targetCapacity) {
		if (targetCapacity < this.size()) throw new IllegalArgumentException("New capacity must not be smaller than current size");

		if (_buffers.peekFirst() == null) {
			_nextBlockSize = targetCapacity - size();
		} else if (this.size() == targetCapacity
			    && _buffers.getFirst().length == targetCapacity) {
			// do nothing - already at the targetCapacity
		} else {
			int totalSize = size();
			byte[] data = new byte[targetCapacity];
			int pos = 0;
			Iterator<byte[]> it = _buffers.iterator();
			while (it.hasNext()) {
				byte[] bytes = it.next();
				if (it.hasNext()) {
					System.arraycopy(bytes,0,		// source
									 data,pos,		// dest
									 bytes.length);	// length
					pos += bytes.length;
				}
				else {
					System.arraycopy(bytes,0,	// source
									 data,pos,	// dest
									 _currentBufferIndex);	// length
				}
			}
			_buffers.clear();
			_buffers.add(data);
			_currentBufferIndex = totalSize;
			_alreadyBufferedBytesCount = 0;
		}
	}
	/**
	 * Create a new buffer and store it in the LinkedList
	 * <p>Adds a new buffer that can store at least {@code minCapacity} bytes.
	 */
	private void _createNewBuffer(final int minCapacity) {
		if (_buffers.peekLast() != null) {
			_alreadyBufferedBytesCount += _currentBufferIndex;
			if (_alreadyBufferedBytesCount > _maxBufferSize) throw new IllegalStateException("The buffer has grown over the max limit (" + _maxBufferSize + ")");
			_currentBufferIndex = 0;
		}
		if (_nextBlockSize < minCapacity) {
			_nextBlockSize = _nextPowerOf2(minCapacity);
		}
		_buffers.add(new byte[_nextBlockSize]);
		_nextBlockSize *= 2;  // block size doubles each time
	}
	/**
	 * Get the next power of 2 of a number (ex, the next power of 2 of 119 is 128).
	 */
	private static int _nextPowerOf2(final int val) {
		int theVal = val - 1;
		theVal = (theVal >> 1) | theVal;
		theVal = (theVal >> 2) | theVal;
		theVal = (theVal >> 4) | theVal;
		theVal = (theVal >> 8) | theVal;
		theVal = (theVal >> 16) | theVal;
		theVal++;
		return theVal;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * An implementation of {@link java.io.InputStream} that reads from a given
	 * {@link LinkedList} of buffers (from the {@link AutoGrowByteArrayOutputStream}
	 */
	private static final class AutoGrowByteArrayInputStream
					   extends InputStream {

		private final LinkedList<byte[]> _buffers;
		private final int _currBufferIndex;
		private final int _available;

		private byte[] _currentBuffer;				// the current buffer being readed
		private int _currentBufferLength = 0;		// the length of the current buffer being readed
		private int _nextIndexInCurrentBuffer = 0;	// the position being readed in the current buffer
		private int _totalBytesRead = 0;			// the number of bytes readed

		public AutoGrowByteArrayInputStream(final LinkedList<byte[]> buffers,
											final int currBufferIndex) {
			_buffers = buffers;
			_currBufferIndex = currBufferIndex;

			// compute the available bytes
			int available = 0;
			for (Iterator<byte[]> buffIt = _buffers.iterator(); buffIt.hasNext(); ) {
				if (buffIt.hasNext()) {
					byte[] buff = buffIt.next();
					available = available + buff.length;
				} else {
					available = available + currBufferIndex;
				}
			}
			_available = available;

			// current buffer = first buffer
			// current buffer length = first buffer length
			Iterator<byte[]> buffersIt = _buffers.iterator();
			if (buffersIt.hasNext()) {
				// current buffer = first buffer
				_currentBuffer = buffersIt.next();

				// current buffer length = first buffer length (maybe the buffer is the unique one)
				if (_currentBuffer == _buffers.getLast()) {
					_currentBufferLength = currBufferIndex;
				}
				else {
					_currentBufferLength = _currentBuffer != null ? _currentBuffer.length
																  : 0;
				}
			}
		}
		@Override
		public int read() {
			if (_currentBuffer == null) return -1;	// This stream doesn't have any data in it...
			if (_nextIndexInCurrentBuffer < _currentBufferLength) {
				_totalBytesRead++;
				return _currentBuffer[_nextIndexInCurrentBuffer++] & 0xFF;
			}
			_swithToNextBufferIfNeeded();

			return this.read();
		}
		@Override
		public int read(final byte[] b) {
			return this.read(b,0,		// source
							 b.length);	// length
		}
		@Override
		public int read(final byte[] b,final int off,	// source
						final int len) {				// length
			if (off < 0 || len < 0 || len > b.length - off) throw new IndexOutOfBoundsException();
			if (len == 0) return 0;

			if (_currentBuffer == null) return -1; 	// This stream doesn't have any data in it...

			if (_nextIndexInCurrentBuffer < _currentBufferLength) {
				int bytesToCopy = Math.min(len, _currentBufferLength - _nextIndexInCurrentBuffer);
				System.arraycopy(_currentBuffer,_nextIndexInCurrentBuffer,	// source
								 b,off,										// dest
								 bytesToCopy);								// length
				_totalBytesRead += bytesToCopy;
				_nextIndexInCurrentBuffer += bytesToCopy;
				int remaining = read(b,off + bytesToCopy,	// source
									 len - bytesToCopy);	// length
				return bytesToCopy + Math.max(remaining,0);
			}
			_swithToNextBufferIfNeeded();

			return this.read(b,off,	// source
							 len);	// length
		}
		@Override
		public long skip(final long n) throws IOException {
			if (n > Integer.MAX_VALUE) throw new IllegalArgumentException("n exceeds maximum (" + Integer.MAX_VALUE + "): " + n);
			if (n == 0) return 0;
			if (n < 0) throw new IllegalArgumentException("n must be 0 or greater: " + n);

			if (_currentBuffer == null) return 0; // This stream doesn't have any data in it...

			int len = (int)n;
			if (_nextIndexInCurrentBuffer < _currentBufferLength) {
				int bytesToSkip = Math.min(len,_currentBufferLength - _nextIndexInCurrentBuffer);
				_totalBytesRead += bytesToSkip;
				_nextIndexInCurrentBuffer += bytesToSkip;
				return (bytesToSkip + this.skip(len - bytesToSkip));
			}
			_swithToNextBufferIfNeeded();

			return this.skip(len);
		}
		@Override
		public int available() {
			return _available - _totalBytesRead;
		}
		private void _swithToNextBufferIfNeeded() {
			Iterator<byte[]> buffersIt = _buffers.iterator();

			// set the current buffer > the next buffer
			if (buffersIt.hasNext()) {
				_currentBuffer = buffersIt.next();
			}
			else {
				_currentBuffer = null;
			}

			// set the current buffer length
			if (_currentBuffer == _buffers.getLast()) {
				_currentBufferLength = _currBufferIndex;
			}
			else {
				_currentBufferLength = _currentBuffer != null ? _currentBuffer.length
															  : 0;
			}
			// set the reading position
			_nextIndexInCurrentBuffer = 0;
		}
	}
}
