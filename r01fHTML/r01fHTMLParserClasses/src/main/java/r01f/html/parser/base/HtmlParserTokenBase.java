package r01f.html.parser.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.types.CanBeRepresentedAsString;

@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class HtmlParserTokenBase<T extends Enum<T> & HtmlParserTokenTypeBase<?>>
     implements CanBeRepresentedAsString {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final T _type;
	@Getter private final String _text;
/////////////////////////////////////////////////////////////////////////////////////////
//  CanBeRepresentedAsString
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _text;
	}
}
