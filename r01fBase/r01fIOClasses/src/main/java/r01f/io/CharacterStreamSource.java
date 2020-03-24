package r01f.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

/**
 * A buffered character reader/source based upon a {@link CharBuffer}
 * Usage: 
 * <pre class='brush:java'>
 *		String src = "0123456789_0123456789_0123456789";
 *
 *		System.out.println(src);
 *		
 *		String readedStr = "";
 *		CharacterStreamSource source = new CharacterStreamSource(new ByteArrayInputStream(src.getBytes()),Charset.defaultCharset());
 *		while (source.hasData()) {
 *			char[] buf = new char[100];
 *			int readed = source.read(buf);
 *			String str = new String(Arrays.copyOf(buf,readed));
 *			readedStr += str;
 *
 *			System.out.print(str);
 *		}
 *		assert(readedStr.equals(src));
 * </pre>
 */
@Slf4j
public class CharacterStreamSource {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    public static final char NULL_CHAR = '\u0000';
	public static final char EOF = (char)-1;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int DEFAULT_BUFFER_SIZE = 1024;

    // Internal buffer used to hold input
    private AutoGrowCharBufferWrapper _buf;
/////////////////////////////////////////////////////////////////////////////////////////
//  SOURCE
/////////////////////////////////////////////////////////////////////////////////////////
    // The input source
    private Readable _source;

    // Boolean is true if source is done
    private boolean _sourceDrained = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public CharacterStreamSource(final Readable source) {
        _source = source;
        _buf = new AutoGrowCharBufferWrapper(DEFAULT_BUFFER_SIZE,	// 1k          >>  default buffer size
        									 4);					// 1k x 4 = 4k >>  max buffer size
        // fill the buffer
		_readInput(_buf.capacity());
    }
    public CharacterStreamSource(final InputStream source) {
        this(new InputStreamReader(source,Charset.defaultCharset()));
    }
    public CharacterStreamSource(final InputStream source,final Charset charset) {
        this(new InputStreamReader(source,charset));
    }
    public CharacterStreamSource(final InputStream source,final String charsetName) {
        this(new InputStreamReader(source,_toCharset(charsetName)));
    }
    private static Charset _toCharset(String csn) {
        Preconditions.checkNotNull(csn);
        try {
            return Charset.forName(csn);
        } catch (IllegalCharsetNameException e) {
            // IllegalArgumentException should be thrown
            throw new IllegalArgumentException(e);
        } catch (UnsupportedCharsetException e) {
        	throw new IllegalArgumentException(e);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns this object as a {@link Readable} object
     * @return
     */
    public Readable asReadable() {
		return new Readable() {
					@Override
					public int read(final CharBuffer buff) throws IOException {
				        int len = buff.remaining();
				        char[] cbuf = new char[len];
				        int n = CharacterStreamSource.this.read(cbuf);
				        if (n > 0) buff.put(cbuf, 0, n);
				        return n;
					}
		};
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FILLING THE BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
    private void _fillBuffer() {
    	if (_sourceDrained) {
//    		if (log.isTraceEnabled()) log.trace("Cannot fill the buffer: Source drained!");
    		return;
    	}
    	int[] availableWritePositions = _buf._availablePositionsToBeWritten();	
//    	if (_buf.remaining() > 0) {
//    		return;
//    	}
    	if (availableWritePositions[1] > 0) {
    		_readInput(availableWritePositions[1]);
    	}
    }
    /**
     * Tries to read more input (writes data to the buffer)
     * May block
     * see http://howtodoinjava.com/2015/01/15/java-nio-2-0-working-with-buffers/    
     * see http://tutorials.jenkov.com/java-nio/buffers.html
     */
    private int _readInput(final int required) {
    	if (_sourceDrained) throw new IllegalStateException("Source is drained!");
    	
    	// Read from source
        int numReaded = _buf.fillFrom(_source,
        						  	  required);
        // Close the source if no more data is available
        if (numReaded == -1) {
        	_sourceDrained = true;
        	if (_source instanceof Closeable) {
        		try {
        			((Closeable)_source).close();
        		} catch (IOException ioEx) {
        			log.error("Could NOT close the underlying stream {}",ioEx.getMessage(),ioEx);
        		}
        	}
        }
        return numReaded;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the current reading position
	 */
	public int currentPosition() {
		return _buf.readPosition();					
	}
	/**
	 * @return true if there's no more characters to be read
	 */
	public boolean isEmpty() {
		// ensure there's no data to be read by reading as much as possible from the source
		_fillBuffer();	
		// check that actually no data has been read
		return !_buf.hasRemaining();
	}
	/**
	 * @return true if there's more characters to be read
	 */
	public boolean hasData() {
		return !this.isEmpty();
	}
	/**
	 * @return the current character (the reading position is not moved forward)
	 */
	public char current() {	
		// Ensure the buffer has data
		_fillBuffer();

		// return EOF if no data is available after filling the buffer
		if (!_buf.hasRemaining()) return EOF; 
		
		// read but do not move the position forward
		return _buf.get(_buf.readPosition());		
	}
	/**
	 * @return the current character moving the reading position a position forward
	 */
	public char read() {
		// Ensure the buffer has data
		_fillBuffer();
		
		// return EOF if no data is available after filling the buffer
		if (!_buf.hasRemaining()) return EOF; 
		
		// read the buffer and move the position forward
		char outChar = _buf.get();
		return outChar;
	}
	/**
	 * Tries to fill the given buffer with read characters
	 * @param dstBuf the buffer to be filled
	 * @return the number of charactes put at the buffer
	 */
	public int read(final char[] dstBuf) {
		// Fill the buffer if not enough data is available
		if (_buf.remaining() > dstBuf.length) {
			if (log.isTraceEnabled()) log.trace("Read {} requested (the buffer has enough data {}) > {}",
												dstBuf.length,_buf.remaining(),_buf.debugInfo());
		} 
		else if (_buf.remaining() < dstBuf.length && !_sourceDrained) {
			if (log.isTraceEnabled()) log.trace("Read {} requested (the buffer has NOT enough data {}) > {}",
												dstBuf.length,_buf.remaining(),_buf.debugInfo());
			_readInput(dstBuf.length);
		}
		// Return the readed data
		int readedChars = dstBuf.length > _buf.remaining() ? _buf.remaining()
														   : dstBuf.length;
		if (readedChars <= 0) return -1;	// no readed chars
		_buf.get(dstBuf,0,readedChars);		// this updates the _readPosition
		return readedChars;
	}
	/**
	 * Moves the read position backwards 
	 * @param length
	 */
	public void unread(final int length) {
		_buf.unread(length);
	}
	/**
	 * Skips a number of characters
	 * @param length
	 */
	public void skip(final int length) throws IOException {
		_buf.skip(length);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	// A pattern cache to avoid pattern creation
	private LoadingCache<String,Pattern> _patternCache = CacheBuilder.newBuilder()
																 .maximumSize(100)
																 .initialCapacity(10)
																 .build(new CacheLoader<String,Pattern>() {
																				@Override
																				public Pattern load(final String key) throws Exception {
																					return Pattern.compile(key);
																				}
																 		});
	/**
	 * Checks if the next characters are equal to the given string
	 * @param str
	 * @return
	 */
	public boolean nextEquals(final String str) {
		return _nextEquals(str,
						   false);	// do not ignore case
	}
	public boolean nextEqualsIgnoreCase(final String str) {
		return _nextEquals(str,
						   true);	// ignore case
	}
	private boolean _nextEquals(final String str,final boolean ignoreCase) {
		// [1]-Read the same chars as the given string length BUT do not move forward the read position
		//	   (next reads will read again the same chars)
		String next = _readNextNoForward(str.length());
		
		// [2]-Do the checking
		return next != null ? (ignoreCase ? next.equalsIgnoreCase(str) : next.equals(str))
							: false;
	}
	public boolean nextMatchesPattern(final int length,
									  final String regex) {
		// [1]-Read the chars for the given length BUT do not move forward the read position
		//	   (next reads will read again the same chars)
		String next = _readNextNoForward(length); 
		
		// [2]-Try to match the pattern
		if (next != null) {
			Pattern p = null;
			try {
				p = _patternCache.get(regex);	// use a pattern cache instead of Pattern p = Pattern.compile(regex);
			} catch (ExecutionException exEx) {
				log.error("Could NOT get pattern {} from regexp pattern cache: {}",
						  regex,exEx.getMessage(),
						  exEx);
				p = Pattern.compile(regex);
			}
			Matcher m = p.matcher(next);
			return m.lookingAt();
		}
		return false;
	}
	/**
	 * Reads chars for the given length BUT does NOT move the buffer read position forward
	 * @param length
	 * @return
	 */
	private String _readNextNoForward(final int length) {
		int pos = _buf.readPosition();				// store the read position
		char[] nextChars = new char[length];
		int readed = this.read(nextChars);
		String next = readed > 0 ? new String(Arrays.copyOf(nextChars,readed)) 
								 : null;
		_buf.readPosition(pos);						// restore the read position
		return next;
	}
}
