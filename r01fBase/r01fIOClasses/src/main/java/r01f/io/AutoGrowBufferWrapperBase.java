package r01f.io;

import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.LongBuffer;

import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;

/**
 * Wrapper of a {@link CharBuffer} or {@link ByteBuffer}, {@link LongBuffer}, etc (any {@link Buffer} subtype) that exposes a method that automatically 
 * makes space at the internal {@link Buffer} if required
 * To do so, sub types just have to call {@link #_makeSpaceIfRequired(int)} method
 * before storing something
 */
@Slf4j
abstract class AutoGrowBufferWrapperBase<B extends Buffer,
										 SELF_TYPE extends AutoGrowBufferWrapperBase<B,SELF_TYPE>> 
	implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
    // Size of internal character buffer
    private static final int DEFAULT_BUFFER_SIZE = 1024; 
    /**
     * The initial buffer size
     */
    protected final int _size;
    /**
     * The number of times the buffer is allowed to grow
     */
    protected final int _maxGrowthRate;
    /**
	 * Internal wrapped buffer
     * see http://howtodoinjava.com/2015/01/15/java-nio-2-0-working-with-buffers/    
     * see http://tutorials.jenkov.com/java-nio/buffers.html#reading
     */
    protected B _buf;
    /**
     * True if the buffer is in read mode
     */
    protected boolean _readMode = true;
    /**
     * The position where the buffer is being readed
     */
    protected int _readPosition = 0;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    protected AutoGrowBufferWrapperBase(final int maxGrowthRate) {
    	this(DEFAULT_BUFFER_SIZE,
    		 maxGrowthRate);
    }
    protected AutoGrowBufferWrapperBase(final int initialSize,
    								    final int maxGrowthRate) {
    	_size = initialSize;
    	_maxGrowthRate = maxGrowthRate;
    	_buf = _allocateNew(_size);			// create a buffer
    	
    	// set the buffer in READ mode by default
    	_buf.position(0);
    	_buf.limit(0);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the underlying buffer
     * @return
     */
    public B getWrappedBuffer() {
    	return _buf;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a NEW {@link Buffer} usually delegating to the static factory
     * methods of the concrete {@link Buffer} instance:
     * <pre class='brush:java'>
     * 		@Override
     * 		protected BytBuffer _allocateNew(final int size) {
     * 			return ByteBuffer.allocate(size);
     * 		}	
     * </pre>
     * @param size
     * @return
     */
    protected abstract B _allocateNew(final int size);
    /**
     * Bulk puts the given buffer contents in the wrapped buffer
     * This method is needed since the {@link Buffer} contract does NOT provides
     * this kind of method
     * <pre class='brush:java'>
     * 		@Override
     * 		protected void _bulkPut(final ByteBuffer otherBuff,final B wrappedDstBuffer) {
     * 			wrappedDstBuf.put(otherBuf);
     * 		}	
     * </pre>
     * @param buffer
     */
    protected abstract void _bulkPut(final B srcBuffer,final B wrappedDstBuffer);
    
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Computes the buffer's positions available to be written
     * Note that the computation differs whether the buffer is in write or read mode
     * @return
     */
    protected int[]_availablePositionsToBeWritten() {   	
		// (see http://howtodoinjava.com/2015/01/15/java-nio-2-0-working-with-buffers/)
    	int[] writePositions = null;
    	boolean writeMode = !_readMode;
    	if (writeMode) {
			// WRITE mode
	    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
	    	//  A | B | C | D | E |   |   |   |   |   |
	    	//          ^           ^               ^^ 
	    	//          |           |        limit__||__capacity
			//			|           |__position
			//          |__readPosition
			// \------/            \------------------/
	    	int availableWritePositionsNoCompact = _buf.capacity() - _buf.position();
			int availableWritePositionsIfCompact = availableWritePositionsNoCompact + _readPosition;
	    	writePositions = new int[] {availableWritePositionsNoCompact,availableWritePositionsIfCompact};
    	} else {
	    	// (buffer is be in READ mode )
			// The buffer is full but not yet readed 
			// READ mode
	    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
	    	//  A | B | C | D | E |   |   |   |   |   |
	    	//          ^           ^               ^ 
	    	//          |__position |__limit        |__capacity
    		//          |__readPosition
    		// \------/            \------------------/
	    	int availableWritePositionsNoCompact = _buf.capacity() - _buf.limit();
			int availableWritePositionsIfCompact = availableWritePositionsNoCompact + _readPosition;
	    	writePositions = new int[] {availableWritePositionsNoCompact,availableWritePositionsIfCompact};
    	}
    	if (log.isTraceEnabled()) log.trace("{} > available positions to be written compacting/NOT compacting = {}/{}",
    										this.debugInfo(),writePositions[1],writePositions[0]);
    	return writePositions;
    }
    
    /**
     * Call this method BEFORE store anything inside the CharacterBuffer
     * because it ensures there's enough space in the {@link CharBuffer}
     * If the internal {@link CharBuffer} grows beyond 1024 * _maxGrowthRate
     * a {@link BufferOverflowException} is thrown
     * BEWARE! the buffer MUST be in write mode before calling this method
     * 		   and the buffer will REMAIN in write mode after calling this method
     * @param required
     */
    public void ensureSpaceFor(final int required) {
    	if (_readMode) throw new IllegalStateException();
    
    	// (see http://howtodoinjava.com/2015/01/15/java-nio-2-0-working-with-buffers/)
    	// Buffer in write mode
    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
    	//  A | B | C |   |   |   |   |   |   |   |
    	//          ^                          ^ ^
    	//          |__position        limit __| |__capacity
    	// Buffer in read mode (DO NOT CALL THIS METHOD IN READ MODE SINCE the available positions are not known for sure)
    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
    	//  A | B | C |   |   |   |   |   |   |   |
    	//  ^           ^                       ^
    	//  |__position |__limit                |__capacity
    	
    	int[] availableWritePositions = _availablePositionsToBeWritten();	// we're in WRITE mode
    	int availablePositionsNoCompact = availableWritePositions[0];
    	int availablePositionsIfCompact = availableWritePositions[1];
    	
    	if (availablePositionsNoCompact >= required) {
    		// NO need to make space for the required data
    		if (log.isTraceEnabled()) log.trace("no need to make space for {} > {}",
    										    required,this.debugInfo());
    	}
    	else if (availablePositionsIfCompact >= required) {
    		if (log.isTraceEnabled()) log.trace("make space compacting for {} > {}",
    											required,this.debugInfo());
    		
    		// If NOT all buffer data has been readed, maybe compacting the buffer will make space 
    		// READ mode BEFORE compacting
	    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
	    	//  A | B | C | D | E |   |   |   |   |   |
	    	//          ^           ^               ^ 
	    	//          |__position |__limit        |__capacity
    		//	        |__readPosition
    		// \------/\----------/\----------------/
    		//  readed   unreaded  avaliable for write
    		// Total available for being written: readed + available for write 
    		//
    		// BEWARE! we were in write mode BEFORE compacting
    		// Note that 0 and 1 positions are available to be written BUT in write mode we're NOT aware of this fact
    		// (that's why the _readPosition is stored
	    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
	    	//  x | x | C | D | E |   |   |   |   |   |
	    	//          ^           ^              ^ ^
	    	//          | position__|      limit __| |__capacity
    		//          |__readPosition
    		// \------/            \------------------/
	    		
	        // Gain space by compacting buffer
	        //		compact() copies all unread data to the beginning of the Buffer; 
	        //		then it 
	        //			1.- sets position to right after the last unread element.
	        //			2.- sets the limit property to capacity (write mode), just like clear() does
    		//				(clears puts the position at 0 and limit at capacity). 
	        //		now the buffer is ready for writing, but the unreaded data will NOT be overwritten
    		// AFTER compacting
    		// Read mode
	    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
	    	//  C | D | E |   |   |   |   |   |   |   |
	    	//  ^           ^                       ^
	    	//  |__position |__limit                |__capacity
	    	//  |__readPosition
    		
    		// Write mode
	    	//  0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
	    	//  C | D | E |   |   |   |   |   |   |   |
	    	//  ^           ^                       ^^
	    	//  |           |__position      limit__||__capacity
	    	//  |__readPosition
    		
    		// ... compact
    		B newBuf = _allocateNew(_buf.capacity());
    		
    		int p = _buf.position();
    		_buf.limit(_buf.position());
    		_buf.position(_readPosition);
    		_bulkPut(_buf,newBuf);
    		
    		// ... set the position & limit (WRITE mode)
    		newBuf.position(p - _readPosition);	
    		newBuf.limit(newBuf.capacity());
    		
    		// ... replace the buffer
    		_buf = newBuf;
    		
    		_readPosition = 0;
    		if (log.isTraceEnabled()) log.trace("buffer compacted with {} elements > {}",
    											_buf.remaining(),this.debugInfo());
    	}
    	else {
	        // Gain space by growing buffer (grow at least the initial size / 2)
    		int minGrowth = _size / 2;
    		int theRequired = minGrowth > required ? minGrowth : required;
	        int newSize = (_buf.position() - _readPosition) +	// the actual size 
	        			  (theRequired);						// the required size
			if (log.isTraceEnabled()) log.trace("need to grow to make space for {} and for the {} not readed data > {}",
												required,newSize,
												this.debugInfo());		
	        if (newSize > _maxGrowthRate * _size) {
	        	log.error("The {} max size is {}x{}={}; the requested size is {}",
						  this.getClass().getSimpleName(),
						  _maxGrowthRate,_size,(_maxGrowthRate * _size),
						  required);
	        	throw new BufferOverflowException();
	        }
	        
	        B newBuf = _allocateNew(newSize);
	        _buf.limit(_buf.position());
	        _buf.position(0);
	        _bulkPut(_buf,newBuf);				// put all the until-the-moment buffer contents into the new buffer
	        
	        // put the buffer in write position
	        newBuf.position(_buf.limit());		// start writing at the end of the data
	        newBuf.limit(newBuf.capacity());	// ... until the buffer is full
	        
	        // replace the buffer
	        _buf = newBuf;						// no need to put the buffer in write mode (it's already in write mode)
	        _readPosition = 0;					// the buffer is compacted: the read position is now 0
	        if (log.isTraceEnabled()) log.trace("new buffer of {} created > {}",
	        									newBuf.capacity(),this.debugInfo());
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns this buffer's capacity.
	 * @return
	 */
	public int capacity() {
		return _buf.capacity();
	}
	/**
	 * Tells whether there are any elements between the current position and the limit.
	 * @return
	 */
	public boolean hasRemaining() {
		return _buf.hasRemaining();
	}
	/**
	 * Returns this buffer's position.
	 * @return
	 */
	public int readPosition() {
		return _readPosition; 
	}
	public void readPosition(final int readPosition) {
		_readPosition = readPosition;
		_buf.position(readPosition);
	}
	/**
	 * Returns the number of elements between the current position and the limit.
	 * @return
	 */
	public int remaining() {
		return _buf.remaining();
	}
	/**
	 * Clears this buffer.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE clear() {
		_buf.clear();
		_readPosition = 0;
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isInReadMode() {
		return _readMode;
	}
	public boolean isInWriteMode() {
		return !_readMode;
	}
	/**
	 * Flips this buffer to read mode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE switchToReadMode() {
		if (_readMode) return (SELF_TYPE)this;			// it's already in read mode
		_buf.limit(_buf.position());
		_buf.position(_readPosition);
		_readMode = true;
		return (SELF_TYPE)this;
	}
	/**
	 * Flips this buffer to write mode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE switchToWriteMode() {
		if (!_readMode) return (SELF_TYPE)this;			// it's already in write mode
		_buf.position(_buf.limit());
		_buf.limit(_buf.capacity());
		_readMode = false;
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{}(pos={},lim={},readPos={})",
								  (_readMode ? "R":"W"),_buf.position(),_buf.limit(),_readPosition);
	}
	
}
