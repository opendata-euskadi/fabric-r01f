package r01f.html.parser.starttag;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.html.parser.base.HtmlParserTokenTypeBase;
import r01f.patterns.FactoryFrom;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum HtmlStartTagParserTokenType 
 implements HtmlParserTokenTypeBase<HtmlStartTagParserToken> {
	TagName			(false),	// tag name
	WhiteSpace		(false),	// whitespace between tag name and attributes
	AttributeName	(false),	// attribute name
	EqualsSign		(false),	// = 
	AttributeValue	(false),	// attribute value
	EOF				(true);		// EOF
	
	@Getter private final boolean _eof;
	@Getter private final FactoryFrom<String,HtmlStartTagParserToken> _factory = new FactoryFrom<String,HtmlStartTagParserToken>() {
																							@Override
																							public HtmlStartTagParserToken from(final String text) {
																								return new HtmlStartTagParserToken(HtmlStartTagParserTokenType.this,
																																   text);
																							}
																				 };
}
