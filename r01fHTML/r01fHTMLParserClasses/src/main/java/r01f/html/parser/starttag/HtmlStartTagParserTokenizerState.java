package r01f.html.parser.starttag;

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
 * Every state has a {@link HtmlStartTagTokenizerStateHandler} type that reads from the character stream and
 * guess what's the next state to move to
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
      enum HtmlStartTagParserTokenizerState 
implements HtmlParserTokenizerStateBase<HtmlStartTagParserTokenType,HtmlStartTagParserToken,
										HtmlStartTagTokenizer,
										HtmlStartTagParserTokenizerState> {
	TagName			(HtmlStartTagParserTokenType.TagName,		 new HtmlStartTagTokenizerStateHandlerForTagName()),
	WhiteSpace		(HtmlStartTagParserTokenType.WhiteSpace,	 new HtmlStartTagTokenizerStateHandlerForWhiteSpace()),	 
	AttributeName	(HtmlStartTagParserTokenType.AttributeName,	 new HtmlStartTagTokenizerStateHandlerForTagAttrName()),
	EqualsSign		(HtmlStartTagParserTokenType.EqualsSign,	 new HtmlStartTagTokenizerStateHandlerForAttrNameAndValueSeparator()), 				
	AttributeValue	(HtmlStartTagParserTokenType.AttributeValue, new HtmlStartTagTokenizerStateHandlerForTagAttrValue()),
	EOF				(HtmlStartTagParserTokenType.EOF, 			 new HtmlStartTagTokenizerStateHandlerForEOF());
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final HtmlStartTagParserTokenType _type;
			private final HtmlTokenizerStateHandlerBase<HtmlStartTagTokenizer> _tokenHandler;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlStartTagTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		return _tokenHandler.read(tokenizer,charReader);
	}
	@Override
	public boolean isIn(final HtmlStartTagParserTokenizerState... other) {
		if (CollectionUtils.isNullOrEmpty(other)) return false;
		boolean out = false;
		for (final HtmlStartTagParserTokenizerState o : other) {
			if (o == this) {
				out = true;
				break;
			}
		}
		return out;
	}
}
