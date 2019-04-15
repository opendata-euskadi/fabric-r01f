package r01f.html.parser.base;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class HtmlParseError 
	 extends Throwable {

	private static final long serialVersionUID = -7261886885152832502L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private int _readerPosition;
    @Getter private String _message;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public HtmlParseError(final int pos,final String errorMsg) {
        _readerPosition = pos;
        _message = errorMsg;
    }

    public HtmlParseError(final int pos,final String errorFormat,
    				  final Object... args) {
        _message = String.format(errorFormat, args);
        _readerPosition = pos;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return _readerPosition + ": " + _message;
    }
}
