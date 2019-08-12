package r01f.html.elements;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Iterables;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.patterns.Memoized;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Html META tag can adopt several forms:
 *  	a) <meta name="keywords" content="HTML, CSS, XML, XHTML, JavaScript">
 *		b) <meta http-equiv="refresh" content="30">
 *		c) <meta charset="UTF-8">
 * {@link MetaHtmlEl} type models a META tag and has three concrete subtypes:
 * 		- {@link NamedMetaHtmlEl} that models a <meta name="foo" content="bar">
 * 		- {@link HttpEquivMetaHtmlEl} that models a <meta http-equiv="foo" content="bar">
 * 		- {@link CharsetMetaHtmlEl} that models a <meta charset="foo">
 * In order to construct a META from it's textual representation, just:
 * <pre class='brush:java'>
 * 		MetaHtmlEl meta = MetaHtmlEl.from("<meta http-equiv='refresh' content='30'>");
 * </pre>
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HtmlMetas {
/////////////////////////////////////////////////////////////////////////////////////////
//  META
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static abstract class MetaHtmlEl 
	    	             extends HtmlElementBase {
		private static final long serialVersionUID = -4116162026386653681L;
		
		private Memoized<Map.Entry<String,String>> _metaCont = new Memoized<Map.Entry<String,String>>() {
																		@Override
																		public Entry<String,String> supply() {
																			return CollectionUtils.hasData(_attrs) ? Iterables.getFirst(_attrs.entrySet(),
																																		null)
																												   : null;
																		}
															   };
		public MetaHtmlEl(final String key,final String content) {
			super("meta");
			this.addAttribute(key.toLowerCase(),content); // Normalize the keys to lowercase characters to avoid duplicated meta tags, 
														  // for example, <meta http-equiv="content-type" and <meta http-equiv="Content-Type" must be the same meta tag.
		}
		public String getKey() {
			Map.Entry<String,String> me = _metaCont.get();
			return me != null ? me.getKey() : null;
		}
		public String getContent() {
			Map.Entry<String,String> me = _metaCont.get();
			return me != null ? me.getValue() : null;
		}
		@Override
		public String asString() {
			if (CollectionUtils.isNullOrEmpty(this.getAttrs())) return "<meta />";
			
			if (this instanceof CharsetMetaHtmlEl) return Strings.customized("<meta charset='{}' />",
										  							       this.getAttributeValue("charset"));
			return Strings.customized("<meta {}='{}' content='{}' />",	// <meta name="x" content="y" /> or <meta http-equiv="x" content="y" />
									  (this instanceof HttpEquivMetaHtmlEl ? "http-equiv" : "name"),this.getKey(),
									  this.getContent());
		}
		/**
		 * Gets a meta tag from the parser token
		 * @param tokenText
		 * @return
		 */
		public static MetaHtmlEl from(final String tokenText) {
			MetaHtmlEl outMeta = null;
			
			Map<String,String> attrs = HtmlElements.parseAttributes(tokenText);
			if (CollectionUtils.hasData(attrs)) {				
				if (attrs.get("content") != null) {
					// can be: 	a) <meta name="keywords" content="HTML, CSS, XML, XHTML, JavaScript">
					//			b) <meta http-equiv="refresh" content="30">
					String content = Strings.removeNewlinesOrCarriageRetuns(attrs.get("content"));
					if (attrs.get("name") != null) {
						// a) <meta name="keywords" content="HTML, CSS, XML, XHTML, JavaScript">
						outMeta = new NamedMetaHtmlEl(attrs.get("name"),			// it's a named META
											          content);	
					} else if (attrs.get("http-equiv") != null) {
						// b) <meta http-equiv="refresh" content="30">
						outMeta = new HttpEquivMetaHtmlEl(attrs.get("http-equiv"),	// it's an http-equiv META
												          content);	
					}
				} else if (attrs.get("charset") != null) {
					// it's <meta charset="UTF-8">
					outMeta = new CharsetMetaHtmlEl(attrs.get("charset"));			// it's a charset META
				} else {
					log.warn("Illegal META detected: {}",tokenText);
				}
			} else {
				log.warn("Illegal META detected: {}",tokenText);
			}
			return outMeta;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	public static class NamedMetaHtmlEl 
	            extends MetaHtmlEl {
		private static final long serialVersionUID = -2337440173438210287L;

		public NamedMetaHtmlEl(final String name,final String content) {
			super(name,content);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	public static class HttpEquivMetaHtmlEl 
	            extends MetaHtmlEl {
		private static final long serialVersionUID = -5902383810156923996L;

		public HttpEquivMetaHtmlEl(final String name,final String content) {
			super(name,content);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static class CharsetMetaHtmlEl 
	            extends MetaHtmlEl {
		private static final long serialVersionUID = -2499272951024608888L;

		public CharsetMetaHtmlEl(final String content) {
			super("charset",content);
		}
	}
	
}
