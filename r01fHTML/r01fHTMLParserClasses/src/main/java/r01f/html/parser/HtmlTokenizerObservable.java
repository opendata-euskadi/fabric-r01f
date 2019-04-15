package r01f.html.parser;

import java.io.InputStream;
import java.nio.charset.Charset;

import io.reactivex.Observable;
import lombok.experimental.Accessors;
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
 *		Observable<HtmlParserToken> observable = HtmlTokenizerObservable.createFrom(charReader);
 *		Observable.blockingSubscribe(new Subscriber<HtmlParserToken>() {{
 *					               		      @Override
 *					               		      public void onCompleted() {
 *					               		      	System.out.println("Completed!");
 *					               		      }
 *					               		      @Override
 *					               		      public void onNext(final HtmlParserToken t) {		
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
public class HtmlTokenizerObservable 
	 extends HtmlTokenizerObservableBase<HtmlParserToken,
	 								     HtmlTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private HtmlTokenizerObservable(final CharacterStreamSource charReader) {
		super(new HtmlTokenizer(charReader));
	}
	public static Observable<HtmlParserToken> createFrom(final Readable readable) {
		CharacterStreamSource charReader = new CharacterStreamSource(readable);
		return HtmlTokenizerObservable.createFrom(charReader);
	}
	public static Observable<HtmlParserToken> createFrom(final InputStream source,final String charsetName) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charsetName);
		return HtmlTokenizerObservable.createFrom(charReader);
	}
	public static Observable<HtmlParserToken> createFrom(final InputStream source,final Charset charset) {
		CharacterStreamSource charReader = new CharacterStreamSource(source,charset);
		return HtmlTokenizerObservable.createFrom(charReader);
	}
	public static Observable<HtmlParserToken> createFrom(final CharacterStreamSource charReader) {
		HtmlTokenizerObservable outObs = new HtmlTokenizerObservable(charReader);
		return outObs.createObservable();
	}
}
