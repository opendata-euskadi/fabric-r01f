package r01f.html.elements;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.google.common.collect.Maps;

import io.reactivex.Flowable;
import r01f.html.parser.starttag.HtmlStartTagParserToken;
import r01f.html.parser.starttag.HtmlStartTagParserTokenType;
import r01f.html.parser.starttag.HtmlStartTagTokenizerFlowable;
import r01f.types.Pair;
import r01f.util.types.collections.CollectionUtils;

public class HtmlElements {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static AnyHtmlEl parseStartTag(final String startTag) {
		AnyHtmlEl outHtmlEl = null;
		
		Flowable<HtmlStartTagParserToken> fw = HtmlStartTagTokenizerFlowable.createFrom(startTag);
		HtmlStartTagParserTokenSubscriber subscriber = new HtmlStartTagParserTokenSubscriber();
		fw.blockingSubscribe(subscriber);
		if (subscriber.valid) {
			Map<String,String> attrs = null;
			if (CollectionUtils.hasData(subscriber.attrsList)) {
				attrs = Maps.newLinkedHashMapWithExpectedSize(subscriber.attrsList.size());
				for (final Pair<String,String> p : subscriber.attrsList) {
					attrs.put(p.getA(),p.getB());
				}
			} else {
				attrs = Maps.newHashMap();
			}
			outHtmlEl = new AnyHtmlEl(subscriber.tagName,attrs);
		}
		return outHtmlEl;
	}
	private static class HtmlStartTagParserTokenSubscriber
			  implements Subscriber<HtmlStartTagParserToken> {
		boolean valid = false;		// is the tag valid?
		String tagName = null;		// the tag name
		Deque<Pair<String,String>> attrsList = new LinkedList<Pair<String,String>>();
		
		@Override
		public void onSubscribe(final Subscription s) {
			// ignore
		}
		@Override
		public void onNext(final HtmlStartTagParserToken token) {
			if (token.getType() == HtmlStartTagParserTokenType.TagName) {
				this.tagName = token.getText();
			}
			if (token.getType() == HtmlStartTagParserTokenType.AttributeName) {
				String attrName = token.getText();
				this.attrsList.push(new Pair<String,String>(attrName,null));
			} else if (token.getType() == HtmlStartTagParserTokenType.AttributeValue) {
				String attrValue = token.getText();
				if (attrValue.length() > 2
				 && (attrValue.startsWith("'") || attrValue.startsWith("\""))) {
					attrValue = attrValue.substring(1,attrValue.length()-1);		// ignore quotes
				}
				Pair<String,String> prev = this.attrsList.pop();
				this.attrsList.push(new Pair<String,String>(prev.getA(),attrValue));
			}
		}
		@Override
		public void onError(final Throwable th) {
			this.valid = false;
		}
		@Override
		public void onComplete() {
			this.valid = true;	// it's valid!
		}
	}
	public static Map<String,String> parseAttributes(final String startTag) {
		AnyHtmlEl el = HtmlElements.parseStartTag(startTag);
		if (el == null) throw new IllegalArgumentException(startTag + " is NOT a valid html start tag!!");
		return el.getAttrs();
	}
	public static String attributesMapToString(final Map<String,String> attrs) {
		if (CollectionUtils.isNullOrEmpty(attrs)) return "";
		StringBuilder sb = new StringBuilder(attrs.size() * 20);
		for (final Iterator<Map.Entry<String,String>> it = attrs.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String,String> me = it.next();
			if (me.getValue() != null) {
				sb.append(me.getKey())
				  .append("=")
				  .append("'")
				  .append(me.getValue().replaceAll("'","\""))
				  .append("'");
			} else {
				sb.append(me.getKey());
			}
			if (it.hasNext()) sb.append(" ");
		}
		return sb.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Pattern START_TAG_PATTERN = Pattern.compile("^" +
																	"<" + 					// start
																		"(\\S+)" + 			// tag name = text (not whitespace)
																		// 0..n attributes
																		"(?:\\s+" + 		// any space
																		_buildTagAttrsMatcher(2) +
																		")*" +		
																	"\\s*\\/?>" + 			// end (maybe />)
																	"$");
//	public static final Pattern START_TAG_PATTERN = Pattern.compile("^" + 
//																	"<" +
//																		"(\\S+)" +				// tag name = not whitespace chars 
//																			"(?:\\s+\\S+)*" +	// multiple (space followed by any non-space)
//																			"\\s*" +			// spaces (maybe)
//																		">" + 
//																	"$");
	public static final Pattern END_TAG_PATTERN = Pattern.compile("^" +
																  "</" + 					// start 
																		"(\\S+)" + 			// tag name = not whitespace chars
																		"[^>]*" + 			// usually is NOT present = any NOT > char
																   ">" +					// end
																   "$");
	private static String _buildTagAttrsMatcher(final int pos) {
		String pattern = "(\\S+)" +										// attr name		group(pos)			
							   "\\s*=?\\s*" +							// = (maybe)
							   "(['\"]?)" + 							// " or '			group(pos+1)
								   "(" +								// 					group(pos+2)				
								   	 	"[^\"'<>]+" + 					// ie: lang="es"
								   	 	"|" +
								   	 	"(?:[^<>]*<!--#[^>]+-->)*" +	// ie: lang="<!--#echo var='XXX'-->" or class="AAA<!--#echo var='A' -->_BBB<!--#echo var='b' -->"
								   ")" +
							   "\\" + (pos + 1);						// " or '			back reference to group(pos+1)="
		return pattern;
	}
}
