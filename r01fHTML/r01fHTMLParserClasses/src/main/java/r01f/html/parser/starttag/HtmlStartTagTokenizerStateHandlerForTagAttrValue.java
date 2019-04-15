package r01f.html.parser.starttag;


import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlStartTagTokenizerStateHandlerForTagAttrValue
extends HtmlTokenizerStateHandlerBase<HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlStartTagTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		char c = charReader.read();
		
		if (_insideSSIDirective(tokenizer)) {
			// it's an ssi directive <!--#echo var='a'-->
			tokenizer.addTextToCurrentToken(c);
		}
		else if (_isQuote(c)) {
			// it's a quote
			tokenizer.addTextToCurrentToken(c);
			if (tokenizer.getCurrentTokenText().length() > 0) {
				// not the first quote: see if it's a closing quote
				char firstQuote = tokenizer.getCurrentTokenText().charAt(0);
				if (firstQuote == c) {
					int numQuotes = _numOf(firstQuote,tokenizer);
					if (numQuotes % 2 == 0) {
						// last quote: finish
						tokenizer.nextState(HtmlStartTagParserTokenizerState.WhiteSpace);
						tokenFinished = true;
					} 
				} 
			}
		}
		else if (_isWhitespaceChar(c)) {
			// it's a whitespace
			if (_isQuote(tokenizer.getCurrentTokenText().charAt(0))) {
				// a whitespace inside an unclosed attr
				tokenizer.addTextToCurrentToken(c);
			} else {
				// starts another attr
				charReader.unread(1);
				tokenizer.nextState(HtmlStartTagParserTokenizerState.WhiteSpace);
				tokenFinished = true;
			}
		}
		else if (c == '>' || (c == '/' && charReader.nextEquals(">"))) {		
			// end tag: > or />
			tokenizer.nextState(HtmlStartTagParserTokenizerState.EOF);	// we've done!
			tokenFinished = true;
		}
		else if (c != CharacterStreamSource.EOF) {	// do not move!
			// any char 
			tokenizer.addTextToCurrentToken(c);
		}
		else if (c == CharacterStreamSource.NULL_CHAR) {                
            throw new HtmlParseError(charReader.currentPosition()-1,"Null char detected");
        } 
        else if (c == CharacterStreamSource.EOF) {
        	tokenizer.nextState(HtmlStartTagParserTokenizerState.EOF);	// we've done!
        	tokenFinished = true;
        }
        else {
        	throw new HtmlParseError(charReader.currentPosition()-1,"Malformed attribute value: illegal character: " + c);
        }
        return tokenFinished;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
}
