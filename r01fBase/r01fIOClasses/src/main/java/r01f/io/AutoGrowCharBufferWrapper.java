package r01f.io;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;

/**
 * Wrapper of a {@link CharBuffer} that exposes a method that automatically 
 * makes space at the internal {@link CharBuffer} if required
 * To do so, sub types just have to call {@link #_makeSpaceIfRequired(int)} method
 * before storing something
 */
@Slf4j
public class AutoGrowCharBufferWrapper 
	 extends AutoGrowBufferWrapperBase<CharBuffer,
	 							       AutoGrowCharBufferWrapper> 
  implements Appendable,
			 CharSequence, 
			 Comparable<CharBuffer>,
			 Readable {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a buffer that at most can grow maxGrowthRate times the 
	 * initial size (1024)
	 * @param maxGrowthRate
	 */
	public AutoGrowCharBufferWrapper(final int maxGrowthRate) {
		super(maxGrowthRate);
	}
	/**
	 * Creates a buffer that at most can grow maxGrowthRate times the 
	 * initial size
	 * @param initialSize
	 * @param maxGrowthRate
	 */
	public AutoGrowCharBufferWrapper(final int initialSize,
									 final int maxGrowthRate) {
		super(initialSize,
			  maxGrowthRate);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected CharBuffer _allocateNew(final int size) {
		return CharBuffer.allocate(size);
	}
	@Override
	protected void _bulkPut(final CharBuffer srcBuffer,final CharBuffer wrappedDstBuffer) {
		wrappedDstBuffer.put(srcBuffer);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Tries to fill the buffer (writes data to the buffer) from the {@link Readable} input
     * May block
     * see http://howtodoinjava.com/2015/01/15/java-nio-2-0-working-with-buffers/    
     * see http://tutorials.jenkov.com/java-nio/buffers.html
     */
    public int fillFrom(final Readable source,
    					final int required) {

    	// [0]: Prepare to receive data (put the buffer into WRITE mode)        
        this.switchToWriteMode();

        // [1]: If the buffer is full, increase the space
        this.ensureSpaceFor(required);
        
        // [2]: Read from the source
        if (log.isTraceEnabled()) log.trace("filling the buffer of {} with {} > {}",
        									(_buf.limit() - _buf.position()),required,this.debugInfo() + " ");
        
        int numReaded = -1;
        try {
        	// underlying buffer
        	numReaded = source.read(_buf);		// it's sure there's room for the readed chars though the source can send less chars        	
        } catch (IOException ioEx) {
        	Throwables.throwUnchecked(ioEx);
        }

        // [4]: Restore current position and limit for reading (put the buffer into READ mode)
        this.switchToReadMode();

        if (log.isTraceEnabled()) log.trace("readed {} > {}",
        									numReaded,this.debugInfo());	// read mode
        return numReaded;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  READABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int read(final CharBuffer chars) throws IOException {
		if (!_readMode) throw new IllegalStateException("NOT in read mode");
		int outChar = _buf.read(chars);
		_readPosition = _buf.position();	// store the read position
		return outChar;
	}
	/**
	 * Moves the read position backwards 
	 * @param length
	 */
	public void unread(final int length) {
		if (!_readMode) throw new IllegalStateException("NOT in read mode");
		_readPosition = (_readPosition - length) > 0 ? (_readPosition - length) 
													 : 0;
		_buf.position(_readPosition);
	}
	/**
	 * Skips a number of characters
	 * @param length
	 */
	public void skip(final int length) throws IOException {
		if (!_readMode) throw new IllegalStateException("NOT in read mode");
		if (_readPosition + length >= _buf.limit()) {
			// read and discard 
			CharBuffer buf = CharBuffer.allocate(length);
			int totalReaded = 0;
			int numReaded = 0;
			do {
				numReaded = this.read(buf);
				if (numReaded > 0) totalReaded += numReaded;
			} while (numReaded > 0 && totalReaded < length);
		} else {
			_readPosition = _readPosition + length;
			_buf.position(_readPosition);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR SEQUENCE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public char charAt(final int pos) {
		return _buf.charAt(pos);
	}
	@Override
	public int length() {
		return _buf.length();
	}
	@Override
	public CharSequence subSequence(final int p1,final int p2) {
		return _buf.subSequence(p1,p2);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  APPENDABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Appendable append(final char c) throws IOException {
		String chars = new String(new char[] {c});
		this.fillFrom(new StringReader(chars),
					  1);
		return this;
	}
	@Override
	public Appendable append(final CharSequence chars) throws IOException {
		this.fillFrom(new StringReader(chars.toString()),
					  chars.length());
		return this;
	}
	@Override
	public Appendable append(final CharSequence chars,final int from,final int length) throws IOException {
		String str = new String(chars.subSequence(from,length)
									 .toString());
		this.fillFrom(new StringReader(str),
					  length);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Relative buffer get (returns the current buffer read position character)
	 * @return
	 */
	public char get() {
		char outChar = _buf.get();
		_readPosition = _buf.position();	// store the read position
		return outChar;
	}
	/**
	 * Absolute get method
	 * @param index
	 * @return
	 */
	public char	get(final int index) {
		return _buf.get(index);
	}
	/**
	 * Relative bulk get method (fills the given char array reading chars from the current buffer read position)
	 * @param dst
	 * @return this object that wraps the buffer
	 */
	public AutoGrowCharBufferWrapper get(final char[] dst) {
		_buf.get(dst);
		_readPosition = _buf.position();	// store the read position
		return this;
	}
	/**
	 * Relative bulk get method (fills the given array reading chars from the current buffer read position)
	 * @param dst
	 * @param from
	 * @param length
	 * @return
	 */
	public AutoGrowCharBufferWrapper get(final char[] dst,final int from,final int length) {
 		_buf.get(dst,from,length);
		_readPosition = _buf.position();	// store the read position
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PUT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Relative put (writes a character at the current buffer write position)
	 * @param c
	 * @return
	 */
	public AutoGrowCharBufferWrapper put(final char c) {
		this.ensureSpaceFor(1);
		_buf.put(c);
		return this;
	}
	/**
	 * Relative put (writes the given characters from the current buffer write position)
	 * @param src
	 * @return
	 */
	public AutoGrowCharBufferWrapper put(final char[] src) {
		this.ensureSpaceFor(src.length);
		_buf.put(src);
		return this;
	}
	/**
	 * Relative put (writes the given characters from the current buffer write position)
	 * @param src
	 * @return
	 */
	public AutoGrowCharBufferWrapper put(final CharBuffer src) {
		this.ensureSpaceFor(src.length());
		_buf.put(src);
		return this;
	}
	/**
	 * Relative bulk put (write the given string from the current buffer write position)
	 * @param src
	 * @return
	 */
	public AutoGrowCharBufferWrapper put(final String src) {
		this.ensureSpaceFor(src.length());
		_buf.put(src);
		return this;
	}
	/**
	 * Relative bulk put (write a slice of the given string from the current buffer write position)
	 * @param src
	 * @return
	 */
	public AutoGrowCharBufferWrapper put(final String src,final int start, final int end) {
		this.ensureSpaceFor(src.length());
		_buf.put(src,start,end);
		return this;
	}
	/**
	 * Absolute put (writes the given character at the given buffer position)
	 * @param index
	 * @param c
	 * @return
	 */
	public AutoGrowCharBufferWrapper put(final int index,final char c) {
		this.ensureSpaceFor(1);
		_buf.put(index,c);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new char buffer whose content is a shared subsequence of this buffer's content.
	 * @return
	 */
	public AutoGrowCharBufferWrapper slice() {
		AutoGrowCharBufferWrapper outBuffer = new AutoGrowCharBufferWrapper(_size,_maxGrowthRate);
		outBuffer.put(_buf.slice());
		return outBuffer;
	}
	/**
	 * Returns the char array that backs this buffer
	 * @return
	 */
	public char[] array() {
		return _buf.array();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int compareTo(final CharBuffer chars) {
		return _buf.compareTo(chars);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return _buf.toString();
	}
}
