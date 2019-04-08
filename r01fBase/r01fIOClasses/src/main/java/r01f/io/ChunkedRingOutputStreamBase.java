package r01f.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Ring {@link OutputStream} implementation that allows it's chunked consumption when the ring fills up.
 * It offers a normal {@link OutputStream} interface that allows any stream source to write bytes; internally it maintains a 
 * ring buffer that once filled up triggers a consumer method handing it the written bytes.
 * 
 * The normal usage is to extend the type providing an implementation of the writeChunk method:
 * <pre class='brush:java'>
 * 		OutputStream os = new ChunkedRingOutputStreamBase(10,i) {
 *								@Override
 *								protected void writeChunk(byte[] chunkBytes) {
 *									System.out.print( (new String(chunkBytes)) );	// simply log
 *								}
 *						  };
 *		os.write(testStr.getBytes());
 * </pre> 
 */
abstract class ChunkedRingOutputStreamBase
	   extends OutputStream {
/////////////////////////////////////////////////////////////////////////////////////////
//  STATE
/////////////////////////////////////////////////////////////////////////////////////////
	private byte[] _buffer;
	private int _writePos;
	private int _readPos;
	private int _chunkSize;
	private boolean _isFlushed = false;
	private boolean _isClosed = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ChunkedRingOutputStreamBase(final int bufferSize,
									   final int chunkSize) {
		_buffer = new byte[bufferSize];
		_chunkSize = chunkSize;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method is called N times when the ring buffer is filled up and new space for
	 * data is needed
	 * @param chunkBytes chunk bytes content
	 */
	protected abstract void writeChunk(final byte[] chunkBytes) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  OutputStream interface
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void write(int b) throws IOException {
		if (_isClosed) throw new IOException("Stream closed");
		
		_buffer[_writePos] = (byte)b;
		_writePos++;
		// A consumption of the buffer must be done if one of theese 
		// conditions is meet
		if (_writePos == _buffer.length && _readPos == 0) {
			// - case 1:		|****************_|
			//					 ^               ^
			//				     R               W
			int consumedBytes = _consume(true);		// the circular buffer is full: consume chunks 
			if (consumedBytes == 0) throw new IOException("The circular buffer is full and NO bytes were consumed");
		} else if (_writePos == _readPos) {
			// - case 2:		|********_*******_|
			//						     ^^
			//						     WR
			int consumedBytes = _consume(true);		// the circular buffer is full: consume chunks 
			if (consumedBytes == 0) throw new IOException("The circular buffer is full and NO bytes were consumed");
		}
		// Adjust the write position if the end of the ring buffer is reached
		if (_writePos == _buffer.length) _writePos = 0;
		_isFlushed = false;
	}
	@Override
	public void write(byte[] b) throws IOException {
		for (int i=0; i < b.length; i++) {
			this.write(b[i]);
		}
	}
	@Override
	public void write(byte[] b,int off,int len) throws IOException {
		byte[] bytesToWrite = Arrays.copyOfRange(b,off,off+len);
		this.write(bytesToWrite);
	}
	@Override
	public void flush() throws IOException {
		if (!_isFlushed) {
			_consume(false);	// consume all... even if there is not enought bytes to fill a chunk
			_isFlushed = true;
		}
	}
	@Override
	public void close() throws IOException {
		this.flush();
		_isClosed = true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSUMPTION OF BYTES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * consume every chunk byte between _readPos and _writePos
	 * @param chunks true if there are enought bytes to consume one or more chunks
	 * @return the number of bytes consumed
	 */
	private int _consume(boolean chunks) throws IOException {
//			boolean debug = false;
		
		int outNumberOfBytesConsumed = 0;
		
		int realNumberOfBytesInBuffer = this.size();
		if (realNumberOfBytesInBuffer == 0) return 0;
		
		// [1] - number of bytes to be consumed (if chunks = true, the bytes are only consumed if there are enought to fill a chunk)
		int numberOfBytesToBeConsumed = chunks && (realNumberOfBytesInBuffer >= _chunkSize) ? (realNumberOfBytesInBuffer / _chunkSize) * _chunkSize
																			  				: realNumberOfBytesInBuffer;
		outNumberOfBytesConsumed = numberOfBytesToBeConsumed;
		
		// [2] - create a byte[] with the bytes to be consumed
		// two cases must be taken into account (* represents the bytes to be readed)
		byte[] bytesToBeConsumed = new byte[numberOfBytesToBeConsumed];
		if (_writePos > _readPos) {
//				if (debug) System.out.println("\n====>" + _writePos + "/" + _readPos + " : " + numberOfBytesToBeConsumed + " of " + _buffer.length);
			// - case 1:		|----*********----|
			//						 ^        ^
			//						 R        W
			// bytes between _readPos and _writePos
			int j = 0;
			for (int i = _readPos; i <=_writePos && numberOfBytesToBeConsumed > 0; i++) {
				bytesToBeConsumed[j] = _buffer[i];
				j++;
				_readPos++;
				numberOfBytesToBeConsumed--;	
			}
//				if (debug) System.out.println("====>" + _writePos + "/" + _readPos + " : " + numberOfBytesToBeConsumed + " of " + _buffer.length);
			if (_readPos == _buffer.length) _readPos = 0;	// goto the beginning of the buffer
		} else if (_writePos <= _readPos) {
//				if (debug) System.out.println("\n---->" + _writePos + "/" + _readPos + " : " + numberOfBytesToBeConsumed + " of " + _buffer.length);
			// - case 2:		|****_--------****|
			//						 ^        ^
			//						 W        R
			// bytes from _readPos to the end of the buffer
			int j = 0;
			for (int i = _readPos; i < _buffer.length && numberOfBytesToBeConsumed > 0; i++) {
				bytesToBeConsumed[j] = _buffer[i];
				j++;
				_readPos++;
				numberOfBytesToBeConsumed--;
			}
//				if (debug) System.out.println("---->" + _writePos + "/" + _readPos + " : " + numberOfBytesToBeConsumed + " of " + _buffer.length);
			if (_readPos == _buffer.length) _readPos = 0;	// goto the beginning of the buffer
			// bytes from the beginning of the buffer to _writePos
			for (int i = 0; i <= _writePos && numberOfBytesToBeConsumed > 0; i++) {
				bytesToBeConsumed[j] = _buffer[i];
				j++;
				_readPos++;
				numberOfBytesToBeConsumed--;
			}
//				if (debug) System.out.println("---->" + _writePos + "/" + _readPos + " : " + numberOfBytesToBeConsumed + " of " + _buffer.length);
			if (_readPos == _buffer.length) _readPos = 0;	// goto the beginning of the buffer
		} 
		// [3] - consume the byte[] 
		this.writeChunk(bytesToBeConsumed);
		
		return outNumberOfBytesConsumed;
	}
	private int size() {
		int outSize = 0;
		// two cases must be taken into account (* represents the bytes to be readed)
		if (_writePos > _readPos) {
			// - case 1:		|----*********----|
			//						 ^        ^
			//						 R        W
			outSize = _writePos - _readPos;
		} else if (_writePos < _readPos) {
			// - case 2:		|****---------****|
			//						 ^        ^
			//						 W        R
			outSize = (_buffer.length - _readPos) + _writePos;
		} else if (_writePos == _readPos) {
			outSize = _buffer.length;
		}
		return outSize;
	}
}