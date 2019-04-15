package r01f.html.parser.base;

import r01f.patterns.FactoryFrom;

public interface HtmlParserTokenTypeBase<TK extends HtmlParserTokenBase<?>> {
	public boolean isEof();
	public FactoryFrom<String,TK> getFactory();
}
