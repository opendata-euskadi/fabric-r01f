package r01f.html.parser.starttag;

import java.io.InputStream;
import java.nio.charset.Charset;

import io.reactivex.Observable;
import lombok.experimental.Accessors;
import r01f.html.parser.HtmlParserToken;
import r01f.html.parser.base.HtmlTokenizerObservableBase;
import r01f.io.CharacterStreamSource;

/**
 * Creates an {@link Observable} of {@link HtmlParserToken}s
 * Usage: 
 * <pre class='brush:java'>
 *		String src = HttpClient.forUrl("http://www.euskadi.eus")
 *							   .GET()
 *							   .loadAsString();
 *		CharacterReader charReader = new CharacterReader(new ByteArrayInputStream(src.getBytes()),
 *														 Charset.defaultCharset());		
 *		Observable<HtmlStartTagParserToken> observable = HtmlStartTagTokenizerObservable.createFrom(charReader);
 *		Observable.blockingSubscribe(new Subscriber<HtmlStartTagParserToken>() {{
 *					               		      @Override
 *					               		      public void onCompleted() {
 *					               		      	System.out.println("Completed!");
 *					               		      }
 *					               		      @Override
 *					               		      public void onNext(final HtmlStartTagParserToken t) {		
 *					               		      	String tokenText = t.asString().trim();
 *					               		      	System.out.println(t.getType() + " token \t\t:{" + tokenText + "}");
 *					               		      }
 *					               		      @Override
 *					               		      public void onError(final Throwable th) {
 *					               		      	th.printStackTrace();
 *					               		      }
 *										      @Override
 *										      public void onComplete() { 
 *										      }
 *					               	});
 * </pre>
 */
@Accessors(prefix="_")
public class HtmlStartTagTokenizerObservable 
	 extends HtmlTokenizerObservableBase<HtmlStartTagParserToken,
	 								     HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private HtmlStartTagTokenizerObservable(final CharacterStreamSource charReader) {
		super(new HtmlStartTagTokenizer(charReader));
	}
	public static Observable<HtmlStartTagParserToken> createFrom(final Readable readable) {
		CharacterStreamSource charReader = new CharacterStreamSource(readable);
		return HtmlStartTagTokenizerObservable.createFrom(charReader);
	}
	public static Observable<HtmlStartTagParserToken> createFrom(final InputStream source,final String charsetName) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charsetName);
		return HtmlStartTagTokenizerObservable.createFrom(charReader);
	}
	public static Observable<HtmlStartTagParserToken> createFrom(final InputStream source,final Charset charset) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charset);
		return HtmlStartTagTokenizerObservable.createFrom(charReader);
	}
	public static Observable<HtmlStartTagParserToken> createFrom(final CharacterStreamSource charReader) {
		HtmlStartTagTokenizerObservable outObs = new HtmlStartTagTokenizerObservable(charReader);
		return outObs.createObservable();
	}
}
