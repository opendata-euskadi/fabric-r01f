package r01f.html.parser;

import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlTokenizerStateHandlerForComment
extends HtmlTokenizerStateHandlerBase<HtmlTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		
		char c = charReader.read();
		if (tokenizer.getCurrentTokenText().length() == 0 && c == '<') {
			// start comment
			tokenizer.addTextToCurrentToken(c);			
		} 
		else if (c == '-' && charReader.nextEquals("->")) {
        	tokenizer.addTextToCurrentToken(c);		
    		tokenizer.addTextToCurrentToken("->");
    		_skip(charReader,"->".length());
    	
     		if (_numOfCommentStartTags(tokenizer) == _numOfCommentEndTags(tokenizer)) {
				// end comment
	    		tokenizer.nextState(HtmlParserTokenizerState.Text);        		
	    		tokenFinished = true;
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
        	// inside comment... just add
        	tokenizer.addTextToCurrentToken(c);	
        }
        return tokenFinished;
	}
	private int _numOfCommentStartTags(final HtmlTokenizer tokenizer) {
		int i = _numOf("<!--",tokenizer);
		int j = _numOf("<!-->",tokenizer);		// BEWARE: 
												//		   <!--[if gte IE 9]><!-->   <--- there exist TWO <!-- in the same comment   
												//				something
												// 	       <!--<![endif]-->
		return i-j;
	}
	private int _numOfCommentEndTags(final HtmlTokenizer tokenizer) {
		return _numOf("-->",tokenizer);			 
	}
}
