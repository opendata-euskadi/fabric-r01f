package r01f.html.parser.starttag;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import io.reactivex.Flowable;
import lombok.experimental.Accessors;
import r01f.html.parser.HtmlParserToken;
import r01f.html.parser.base.HtmlTokenizerFlowableBase;
import r01f.io.CharacterStreamSource;

/**
 * Creates an {@link Flowable} of {@link HtmlParserToken}s
 * Usage: 
 * <pre class='brush:java'>
 *		String src = HttpClient.forUrl("http://www.euskadi.eus")
 *							   .GET()
 *							   .loadAsString();
 *		CharacterReader charReader = new CharacterReader(new ByteArrayInputStream(src.getBytes()),
 *														 Charset.defaultCharset());		
 *		Flowable<HtmlStartTagParserToken> flowable = HtmlStartTagTokenizerFlowable.createFrom(charReader);
 *		flowable.blockingSubscribe(new ResourceSubscriber<HtmlStartTagParserToken>() {{
 *					               		    @Override
 *					               		    public void onNext(final HtmlStartTagParserToken t) {		
 *					               		    	String tokenText = t.asString().trim();
 *					               		    	System.out.println(t.getType() + " token \t\t:{" + tokenText + "}");
 *					               		    }
 *					               		    @Override
 *					               		    public void onError(final Throwable th) {
 *					               		    	th.printStackTrace();
 *					               		    }
 *										    @Override
 *										    public void onComplete() { 
 *										    }
 *					               });
 * </pre>
 */
@Accessors(prefix="_")
public class HtmlStartTagTokenizerFlowable 
	 extends HtmlTokenizerFlowableBase<HtmlStartTagParserToken,
	 								   HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private HtmlStartTagTokenizerFlowable(final CharacterStreamSource charReader) {
		super(new HtmlStartTagTokenizer(charReader));
	}
	public static Flowable<HtmlStartTagParserToken> createFrom(final Readable readable) {
		CharacterStreamSource charReader = new CharacterStreamSource(readable);
		return HtmlStartTagTokenizerFlowable.createFrom(charReader);
	}
	public static Flowable<HtmlStartTagParserToken> createFrom(final InputStream source,final String charsetName) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charsetName);
		return HtmlStartTagTokenizerFlowable.createFrom(charReader);
	}
	public static Flowable<HtmlStartTagParserToken> createFrom(final InputStream source,final Charset charset) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charset);
		return HtmlStartTagTokenizerFlowable.createFrom(charReader);
	}
	public static Flowable<HtmlStartTagParserToken> createFrom(final CharacterStreamSource charReader) {
		HtmlStartTagTokenizerFlowable outObs = new HtmlStartTagTokenizerFlowable(charReader);
		return outObs.createFlowable();
	}
	public static Flowable<HtmlStartTagParserToken> createFrom(final String str) {
		CharacterStreamSource charReader = new CharacterStreamSource(new ByteArrayInputStream(str.getBytes()),
		 														 			 Charset.defaultCharset());
		return HtmlStartTagTokenizerFlowable.createFrom(charReader);
	}
}
