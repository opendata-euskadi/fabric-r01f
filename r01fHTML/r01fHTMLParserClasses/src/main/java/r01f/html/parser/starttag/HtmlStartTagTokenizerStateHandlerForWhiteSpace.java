package r01f.html.parser.starttag;


import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlStartTagTokenizerStateHandlerForWhiteSpace
extends HtmlTokenizerStateHandlerBase<HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlStartTagTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		char c = charReader.read();
		
		if (_isWhitespaceChar(c)) {
			// still whitespace
			tokenizer.addTextToCurrentToken(c);
		}
		else if (c == '=') {
			// whitespace between attr name & attr value
			charReader.unread(1);
			tokenizer.nextState(HtmlStartTagParserTokenizerState.EqualsSign);
			tokenFinished = true;
		}
		else if (_isAllowedChar(HtmlStartTagTokenizerStateHandlerForTagAttrName.ALLOWED_CHARS,c)
			  || (c == '<' && charReader.nextEquals("!--#"))) {
			// begins an attr name or value
        	charReader.unread(1);					// unread... it's another token	
        	
        	if (tokenizer.getPreviousState() == HtmlStartTagParserTokenizerState.TagName) {
        		// first attribute after the tag name
        		tokenizer.nextState(HtmlStartTagParserTokenizerState.AttributeName);
        	} else if (tokenizer.getPreviousState() == HtmlStartTagParserTokenizerState.EqualsSign) {
        		// the attribute value
        		tokenizer.nextState(HtmlStartTagParserTokenizerState.AttributeValue);
        	} else  {
        		// another attribute ie: attribute [src] after [defer] one in: <script defer src='a.js'/>
        		tokenizer.nextState(HtmlStartTagParserTokenizerState.AttributeName);
        	}
			tokenFinished = true;
		}
		else if (c == '\'' || c == '"') {
			// begins the attr value
			charReader.unread(1);
			tokenizer.nextState(HtmlStartTagParserTokenizerState.AttributeValue);
			tokenFinished = true;
		}
		else if (c == '>' || (c == '/' && charReader.nextEquals(">"))) {	
			// end tag: > or />
			tokenizer.nextState(HtmlStartTagParserTokenizerState.EOF);	// we've done!
			tokenFinished = true;
		}
        else if (c == CharacterStreamSource.NULL_CHAR) {                
            throw new HtmlParseError(charReader.currentPosition()-1,"Null char detected");
        } 
        else if (c == CharacterStreamSource.EOF) {
        	tokenizer.nextState(HtmlStartTagParserTokenizerState.EOF);	// we've done!
        	tokenFinished = true;
        }
        else {
        	throw new HtmlParseError(charReader.currentPosition()-1,"Malformed start tag white space: illegal character: " + c);
        }
        return tokenFinished;
	}
}
