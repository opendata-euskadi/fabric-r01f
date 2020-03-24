package r01f.html.parser.base;

import java.io.IOException;

import r01f.io.CharacterStreamSource;

public abstract class HtmlTokenizerStateHandlerBase<TK extends HtmlTokenizerBase<?,?,?,?>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Reads a character and returns true if the token is finished
	 * (see {@link HtmlTokenizerFlowableBase})
	 * @param tokenizer
	 * @param charReader
	 * @return
	 * @throws HtmlParseError
	 */
	public abstract boolean read(final TK tokenizer,final CharacterStreamSource charReader) throws HtmlParseError;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected static void _skip(final CharacterStreamSource charReader,
						 		final int numChars) throws HtmlParseError {
		try {
			charReader.skip(numChars);
		} catch (IOException ioEx) {
			throw new HtmlParseError(charReader.currentPosition(),ioEx.getMessage(),ioEx);
		}		
	}
	protected static boolean _isWhitespaceChar(final char c) {
		return c == ' ' 
			|| c == '\t' 
			|| c == '\r' 
			|| c == '\n';
	}
	protected static boolean _isQuote(final char c) {
		return c == '\'' || c == '"';
	}
	protected static boolean _isAllowedChar(final String allowedChars,
											final char c) {
		boolean outAllowed = false;
		for (int i=0; i < allowedChars.length(); i++) {
			if (allowedChars.charAt(i) == c) {
				outAllowed = true;
				break;
			}
		}
		return outAllowed;
	}
	protected boolean _insideSSIDirective(final TK tokenizer) {
		boolean inside = false;
		int ssiDirEnd = tokenizer.getCurrentTokenText().lastIndexOf("-->");
		int ssiDirStart = tokenizer.getCurrentTokenText().lastIndexOf("<!--#");
		if ((ssiDirStart >= 0 && ssiDirEnd < 0)		// unfinished ssi dir
		 || (ssiDirStart >= 0 && ssiDirEnd > 0 && ssiDirEnd < ssiDirStart)) {
			inside = true;
		}
		return inside;
	}
	protected int _numOf(final char c,final TK tokenizer) {
		int out = 0;
		for (int p = 0; p < tokenizer.getCurrentTokenText().length(); p++) {
			if (tokenizer.getCurrentTokenText().charAt(p) == c) out = out + 1;
		}
		return out;
	}
	protected int _numOf(final String text,final TK tokenizer) {
		int out = 0;
		int i = tokenizer.getCurrentTokenText().length();
		do {
			i = tokenizer.currentTokenTextLastIndexOf(text,i);
			if (i >= 0) {
				i = i - text.length();
				out = out + 1;
			}
		} while (i >= 0);
		return out;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
}
