package r01f.html.parser.base;

import java.util.regex.Pattern;

import r01f.io.CharacterStreamSource;

public abstract class HtmlTokenizerBase<T extends Enum<T> & HtmlParserTokenTypeBase<?>,
										TK extends HtmlParserTokenBase<T>,
								   		S extends Enum<S> & HtmlParserTokenizerStateBase<T,TK,SELF_TYPE,?>,
								   		SELF_TYPE extends HtmlTokenizerBase<T,TK,S,SELF_TYPE>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final CharacterStreamSource _charReader;
	
	protected StringBuilder _currTokenText = new StringBuilder();
	
	protected S _prevState;
	
	protected S _currState;
	
	protected S _nextState;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlTokenizerBase(final CharacterStreamSource charReader,
							 final S currState) {
		_charReader = charReader;
		
		_prevState = null;
		_currState = currState;
		_nextState = null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public S getPreviousState() {
		return _prevState;
	}
	public S getCurrentState() {
		return _currState;
	}
	public S getNextState() {
		return _nextState;
	}
	public boolean hasToChangeState() {
		return _nextState != null
			&& _nextState != _currState;
	}
	public void changeState() {
		_currState = _nextState;
	}
	public void nextState(final S nextState) {
		_nextState = nextState;
	}
	public boolean isEof() {
		return _currState.getType().isEof();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  READ METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public boolean read() throws HtmlParseError {
		return _currState.read((SELF_TYPE)this,_charReader);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void addTextToCurrentToken(final char character) {
		_currTokenText.append(character);
	}
	public void addTextToCurrentToken(final String str) {
		_currTokenText.append(str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public StringBuilder getCurrentTokenText() {
		return _currTokenText;
	}
	public TK getCurrentToken() {
		if (_currTokenText == null) return null;		// no token to emit
		
		// Build token
		TK outToken = this.createToken(_currState.getType(),
								   	   _currTokenText.toString());
		// set current token from next token data
		_currTokenText = new StringBuilder();
		
		// store the prev state
		_prevState = _currState;
		
		return outToken;
	}
	public boolean currentTokenHasText() {
		return _currTokenText.length() > 0;
	}
	public char currentTokenTextLastChar() {
		if (!this.currentTokenHasText()) throw new IllegalStateException();
		return _currTokenText.charAt(_currTokenText.length());
	}
	public int currentTokenTextIndexOf(final String text) {
		return _currTokenText.indexOf(text);
	}
	public int currentTokenTextLastIndexOf(final String text) {
		return _currTokenText.lastIndexOf(text);
	}
	public int currentTokenTextLastIndexOf(final String text,final int begin) {
		return _currTokenText.lastIndexOf(text,begin);
	}
	public boolean currentTokenTextMatches(final Pattern p) {
		return p.matcher(_currTokenText)
				.matches();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public TK createToken(final T type,final String text) {
		return (TK)type.getFactory()
				   	   .from(text);
	}
}
