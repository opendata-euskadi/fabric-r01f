package r01f.html.parser;

import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;

@NoArgsConstructor
abstract class HtmlTokenizerStateHandlerForTagBase
       extends HtmlTokenizerStateHandlerBase<HtmlTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	protected static final String ALLOWED_CHARS = "\t\r\n /.,;:#@$%&?¿!¡*() {}[]Ç=_+-\"'áéíóúÁÉÍÓÚüÜàèìòùabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ0123456789";

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected static boolean _isAllowedChar(final char c) {
		return c != '\\';
		//return _isAllowedChar(ALLOWED_CHARS,c);
	}
}
