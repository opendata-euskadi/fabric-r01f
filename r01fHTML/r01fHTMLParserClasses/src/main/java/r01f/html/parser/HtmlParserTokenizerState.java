package r01f.html.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlParserTokenizerStateBase;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;
import r01f.util.types.collections.CollectionUtils;

/**
 * Models the tokenizer state as characters are being read 
 * Every state has a {@link HtmlTokenizerStateHandlerBase} type that reads from the character stream and
 * guess what's the next state to move to
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
      enum HtmlParserTokenizerState 
implements HtmlParserTokenizerStateBase<HtmlParserTokenType,HtmlParserToken,
										HtmlTokenizer,
										HtmlParserTokenizerState> {
	Text		(HtmlParserTokenType.Text,		 new HtmlTokenizerStateHandlerForText()),
	DocType		(HtmlParserTokenType.DocType,	 new HtmlTokenizerStateHandlerForDocType()),
	StartTag	(HtmlParserTokenType.StartTag,	 new HtmlTokenizerStateHandlerForTagStart()),
	EndTag		(HtmlParserTokenType.EndTag,	 new HtmlTokenizerStateHandlerForTagEnd()),
	Comment		(HtmlParserTokenType.Comment,	 new HtmlTokenizerStateHandlerForComment()),
	EOF			(HtmlParserTokenType.EOF,		 new HtmlTokenizerStateHandlerForEOF());
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final HtmlParserTokenType _type;
			private final HtmlTokenizerStateHandlerBase<HtmlTokenizer> _tokenHandler;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		return _tokenHandler.read(tokenizer,charReader);
	}
	@Override
	public boolean isIn(final HtmlParserTokenizerState... other) {
		if (CollectionUtils.isNullOrEmpty(other)) return false;
		boolean out = false;
		for (final HtmlParserTokenizerState o : other) {
			if (o == this) {
				out = true;
				break;
			}
		}
		return out;
	}
}
