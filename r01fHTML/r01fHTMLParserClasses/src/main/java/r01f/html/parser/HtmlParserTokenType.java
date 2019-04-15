package r01f.html.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.html.parser.base.HtmlParserTokenTypeBase;
import r01f.patterns.FactoryFrom;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum HtmlParserTokenType
 implements HtmlParserTokenTypeBase<HtmlParserToken> {
	DocType		(false),
	StartTag	(false),
	EndTag		(false),
	Comment		(false),
	Text		(false),
	EOF			(true);
	
	@Getter private final boolean _eof;
	@Getter private final FactoryFrom<String,HtmlParserToken> _factory = new FactoryFrom<String,HtmlParserToken>() {
																							@Override
																							public HtmlParserToken from(final String text) {
																								return new HtmlParserToken(HtmlParserTokenType.this,
																														   text);
																							}
																				 };
}
