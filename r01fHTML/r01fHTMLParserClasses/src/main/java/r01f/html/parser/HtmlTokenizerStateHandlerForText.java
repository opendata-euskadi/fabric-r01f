package r01f.html.parser;

import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlTokenizerStateHandlerForText
extends HtmlTokenizerStateHandlerBase<HtmlTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		
		char c = charReader.read();
        if (c == '<') {
        	if (charReader.nextMatchesPattern(1,"[a-zA-z_]")) {
        		charReader.unread(1);								// unread... it's a start tag token
        		tokenizer.nextState(HtmlParserTokenizerState.StartTag);
        		tokenFinished = true;
        	}
        	else if (charReader.nextMatchesPattern(2,"/[a-zA-Z_]")) {
        		charReader.unread(1);								// unread... it's an end tag token
        		tokenizer.nextState(HtmlParserTokenizerState.EndTag);
        		tokenFinished = true;
        	}
        	else if (charReader.nextEquals("!--")) {
        		charReader.unread(1);								// unread... it's a comment token
        		tokenizer.nextState(HtmlParserTokenizerState.Comment);
        		tokenFinished = true;
        	}
        	else if (charReader.nextEqualsIgnoreCase("!DOCTYPE")) {
        		charReader.unread(1);								// unread... it's a doctype token
        		tokenizer.nextState(HtmlParserTokenizerState.DocType);
        		tokenFinished = true;
        	} 
        	else {
        		tokenizer.addTextToCurrentToken(c);
        	}
        }
        else if (c == CharacterStreamSource.NULL_CHAR) {                
            throw new HtmlParseError(charReader.currentPosition()-1,"Null char detected");
        } 
        else if (c == CharacterStreamSource.EOF) {
        	tokenizer.nextState(HtmlParserTokenizerState.EOF);	// we've done!
        	tokenFinished = true;
        }
        else {
        	tokenizer.addTextToCurrentToken(c);	
        }
        return tokenFinished;	// it's all done?
	}
}
