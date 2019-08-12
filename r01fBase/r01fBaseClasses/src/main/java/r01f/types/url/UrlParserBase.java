package r01f.types.url;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.patterns.Memoized;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
abstract class UrlParserBase
	implements UrlParser {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter protected final String _url;	
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	PUBLIC
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public UrlComponents getComponents() {
		return _urlComponents.get();
	}
	private final transient Memoized<UrlComponents> _urlComponents = 
									new Memoized<UrlComponents>() {
											@Override
											public UrlComponents supply() {
												if (_url == null) throw new IllegalStateException("The url is null!!");
												
												String theUrl = _normalize(_url);												
												
												UrlComponents outUrlComponents = _parse(theUrl); 
												return outUrlComponents;
											}		
									};
	abstract UrlComponents _parse(final String url);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _normalize(final String url) {
		return url.trim();
	}
}
