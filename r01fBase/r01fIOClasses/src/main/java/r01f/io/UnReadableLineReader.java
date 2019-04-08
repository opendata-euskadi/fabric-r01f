package r01f.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;

/**
 * A line reader that stores a readed lines history that can be readed again
 * <pre class='brush:java'>
 * 		UnReadableLineReader lr = new UnReadableLineReader(new StringReader("1\n2\n3\n4\n5\n6\n7\n8\n9\n10"),
 * 														   2);		// readed lines history size
 * 		String line = lr.readLine();					// reads a line
 * 		lr.unreadLine(1);								// unreads a line
 * 		Assert.assertTrue(lr.readLine().equals(line));	// read the same line again
 * </pre>
 */
@Slf4j
public class UnReadableLineReader {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private final static int DEFAULT_HISTORY_LENGTH = 1;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The number of previously readed lines at history stack
	 */
	private final int _historyLength;
	/**
	 * Previously readed lines
	 */
	private final Deque<String> _lineHistory;
	/**
	 * Wrapped buffered reader 
	 */
	private final BufferedReader _lineReader;
	/**
	 * Positions to be readed from the history
	 */
	private final Deque<String> _historyReadPendingLines;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UnReadableLineReader(final Reader r) {
		this(r,DEFAULT_HISTORY_LENGTH);
	}
	public UnReadableLineReader(final Reader r,final int historyLength) {
		if (historyLength <= 0) log.error("History length MUST be greater than cero; defaulting to 1");
		_lineReader = new BufferedReader(r);
		_historyLength = historyLength > 0 ? historyLength : DEFAULT_HISTORY_LENGTH;
		_lineHistory = new ArrayDeque<String>(_historyLength);
		_historyReadPendingLines = new ArrayDeque<String>(_historyLength);
	}
	public UnReadableLineReader(final Readable r) {
		this(r,DEFAULT_HISTORY_LENGTH);
	}
	public UnReadableLineReader(final Readable r,final int historyLength) {
		this(new Reader() {
					@Override
					public int read(final char[] chars,final int from,final int length) throws IOException {
						CharBuffer cb = CharBuffer.wrap(chars,from,length);
						return r.read(cb);
					}
					@Override
					public void close() throws IOException {
						// nothing
					}
			 },
			 historyLength);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public String readLine() throws IOException {
		String readedLine = null;
		// [1]: Read the line
		if (!_historyReadPendingLines.isEmpty()) {
			// use the history
			readedLine = _historyReadPendingLines.pop();	// remove the first line to be readed
		} else {
			// use the source
			readedLine = _lineReader.readLine();
		}
		
		// [2]: Put the readed line at the top of the history stack
		// 		removing the remaining one from the bottom of the stack
		if (readedLine != null) {
			if (_lineHistory.size() < _historyLength) {
				_lineHistory.addFirst(readedLine);
			} else {
				_lineHistory.removeLast();
				_lineHistory.add(readedLine);
			}
		}
		return readedLine;
	}
	public void unreadLines(final int numLines) {
		if (numLines <= 0) throw new IllegalArgumentException("numLines <= 0");
		if (numLines > _lineHistory.size()) throw new IllegalStateException(Throwables.message("Cannot unread {} lines; the history only contains {} lines",
																							   numLines,_lineHistory.size()));
		// Move history lines to the history read pending positions (inserting at the bottom of the stack)
		for (int i=0; i < numLines; i++) {
			_historyReadPendingLines.addFirst(_lineHistory.pop());
		}
	}
}
