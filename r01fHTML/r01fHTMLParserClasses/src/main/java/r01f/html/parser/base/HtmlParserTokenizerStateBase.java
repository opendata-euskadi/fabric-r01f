package r01f.html.parser.base;

import r01f.io.CharacterStreamSource;

public interface HtmlParserTokenizerStateBase<T extends Enum<T> & HtmlParserTokenTypeBase<?>,
											  TK extends HtmlParserTokenBase<T>,
										      TN extends HtmlTokenizerBase<T,TK,SELF_TYPE,?>,
										      SELF_TYPE extends Enum<SELF_TYPE> & HtmlParserTokenizerStateBase<T,TK,TN,SELF_TYPE>> {
	public T getType();
	/**
	 * Reads a token character; returns true if the token is finished
	 * (see {@link HtmlTokenizerFlowableBase})
	 * @param tokenizer
	 * @param charReader
	 * @return
	 * @throws HtmlParseError
	 */
	public boolean read(final TN tokenizer,final CharacterStreamSource charReader) throws HtmlParseError;
	/**
	 * Checks if this state is in the given spectrum
	 * @param other
	 * @return
	 */
	public boolean isIn(final SELF_TYPE... other);
}
