package r01f.html.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import io.reactivex.Flowable;
import lombok.experimental.Accessors;
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
 *		Flowable<HtmlParserToken> flowable = HtmlTokenizerFlowable.createFrom(charReader);
 *		flowable.blockingSubscribe(new ResourceSubscriber<HtmlParserToken>() {{
 *					               		    @Override
 *					               		    public void onNext(final HtmlParserToken t) {		
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
public class HtmlTokenizerFlowable 
	 extends HtmlTokenizerFlowableBase<HtmlParserToken,
	 								   HtmlTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private HtmlTokenizerFlowable(final CharacterStreamSource charReader) {
		super(new HtmlTokenizer(charReader));
	}
	public static Flowable<HtmlParserToken> createFrom(final Readable readable) {
		CharacterStreamSource charReader = new CharacterStreamSource(readable);
		return HtmlTokenizerFlowable.createFrom(charReader);
	}
	public static Flowable<HtmlParserToken> createFrom(final InputStream source,final String charsetName) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charsetName);
		return HtmlTokenizerFlowable.createFrom(charReader);
	}
	public static Flowable<HtmlParserToken> createFrom(final InputStream source,final Charset charset) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charset);
		return HtmlTokenizerFlowable.createFrom(charReader);
	}
	public static Flowable<HtmlParserToken> createFrom(final CharacterStreamSource charReader) {
		HtmlTokenizerFlowable outObs = new HtmlTokenizerFlowable(charReader);
		return outObs.createFlowable();
	}
	public static Flowable<HtmlParserToken> createFrom(final String str) {
		CharacterStreamSource charReader = new CharacterStreamSource(new ByteArrayInputStream(str.getBytes()),
		 														 			 Charset.defaultCharset());
		return HtmlTokenizerFlowable.createFrom(charReader);
	}
}
