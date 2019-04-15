package r01f.html.parser.base;

import io.reactivex.Emitter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class HtmlTokenizerEmitter<TK extends HtmlParserTokenBase<?>,
								    TN extends HtmlTokenizerBase<?,TK,?,?>,
									E extends Emitter<TK>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final TN _tokenizer;	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract boolean emitterAvailable(final E emitter);
	
	protected void subscribe(final E emitter) throws Exception {
		if (!this.emitterAvailable(emitter)) return;
	
		// start with a TextToken
		while(!_tokenizer.isEof() 					// not the EOF token
		   && this.emitterAvailable(emitter)) {		// observer still there
			
			boolean tokenComplete = false;
			
			// read a complete token
			while (!tokenComplete
				&& this.emitterAvailable(emitter)) {
				
				// force the token to read data
				try {
					tokenComplete = _tokenizer.read();
				} catch (HtmlParseError parseErr) {
					emitter.onError(parseErr);
				}
				
				// ... if the tokenizer has a complete token, emit it
				// note that if there are two consecutive mergeable tokens (ie html text tokens)
				// they should be emmited as a single one
				if (tokenComplete) {
					TK currToken = _tokenizer.getCurrentToken();
					
					if (currToken != null && currToken.getText().length() > 0) emitter.onNext(currToken);	
						
				} // while !token complete
				
				// tokenizer state may change from one state to another on result of
				// reading the stream (ie: if the current state is a Text and a 
				// 						   '<' char is read, transition to a StartTag state)
				if (_tokenizer.hasToChangeState()) _tokenizer.changeState();
			}
		} // while
		// that's all!!
		
		emitter.onComplete();
	}
}
