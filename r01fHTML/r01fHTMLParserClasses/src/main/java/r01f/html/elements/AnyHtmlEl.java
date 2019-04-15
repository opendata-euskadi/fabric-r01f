package r01f.html.elements;

import java.util.Map;

import lombok.experimental.Accessors;

/**
 * Any html element
 */
@Accessors(prefix="_")
public class AnyHtmlEl 
     extends HtmlElementBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AnyHtmlEl(final String tagName,final Map<String,String> attrs) {
		super(tagName,
			  attrs);
	}
}