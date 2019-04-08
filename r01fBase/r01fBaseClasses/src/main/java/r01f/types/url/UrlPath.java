package r01f.types.url;

import java.net.URL;
import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathBase;
import r01f.types.PathFactory;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Represents a {@link Path} in a {@link URL}
 * ie: http://site:port/urlPath
 */
@Immutable
@MarshallType(as="urlPath")
public class UrlPath
	 extends Path
  implements IsUrlPath {
	private static final long serialVersionUID = -4132364966392988245L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * True is the trailing slash must be preserved
	 * (sometimes the trailing slash is important and MUST be preserved)
	 */
	private final boolean _preserveTrailingSlash;
	/**
	 * true if the path ends with a trailing slash (ie: /foo/bar/)
	 * -this is usually important in url paths-
	 */
	private final boolean _trailingSlash;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath() {
		this(false);		// do NOT preserve the trailing slash
	}
	public UrlPath(final boolean preserveTrailingSlash) {
		super(Lists.newArrayList());
		_trailingSlash = false;
		_preserveTrailingSlash = preserveTrailingSlash;
	}
	public UrlPath(final Collection<String> pathEls) {
		this(false,		// do not preserve trailing slash
			 pathEls);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final Collection<String> pathEls) {
		super(pathEls);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = CollectionUtils.hasData(pathEls)
								? Iterables.getLast(pathEls).endsWith("/")
								: false;
	}
	public  UrlPath(final String... elements) {
		this(false,		// do not preserve trailing slash
			 elements);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final String... elements) {
		super(elements);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = CollectionUtils.hasData(elements)
								? elements[elements.length-1].toString().endsWith("/")
								: false;
	}
	public UrlPath(final Object... objs) {
		this(false,		// do not preserve trailing slash
			  objs);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final Object... objs) {
		super(objs);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = CollectionUtils.hasData(objs)
								? objs[objs.length-1].toString().endsWith("/")
								: false;
	}
	public UrlPath(final Object obj) {
		this(false,		// do not preserve trailing slash
			 obj);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final Object obj) {
		super(obj);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = obj != null ? obj.toString().endsWith("/") : false;
	}
	public <P extends IsPath> UrlPath(final P otherPath) {
		this(false,			// do not preserve trailing slash
			 otherPath);
	}
	public <P extends IsPath> UrlPath(final boolean preserveTrailingSlash,
									  final P otherPath) {
		super(otherPath);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = otherPath != null ? otherPath.asString().endsWith("/")
										   : false;
	}
	public static final PathFactory<UrlPath> URL_PATH_FACTORY = _createUrlPathFactory(false);
	public static final PathFactory<UrlPath> URL_PATH_FACTORY_PRESERVE_TRAILING_SLASH = _createUrlPathFactory(true);
	private static final PathFactory<UrlPath> _createUrlPathFactory(final boolean preserveTrailingSlash) {
		return new PathFactory<UrlPath>() {
					@Override
					public UrlPath createPathFrom(final Collection<String> elements) {
						return new UrlPath(preserveTrailingSlash,
										   elements);
					}
				};
	}
	private final Memoized<PathFactory<UrlPath>> _memoizedPathFactory = new Memoized<PathFactory<UrlPath>>() {
																				@Override
																				protected PathFactory<UrlPath> supply() {
																					return _createUrlPathFactory(_preserveTrailingSlash);
																				}
																		};
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)_memoizedPathFactory.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORIES
/////////////////////////////////////////////////////////////////////////////////////////
	public static UrlPathBuilderPreserveTrailingSlash preservingTrailingSlash() {
		return new UrlPathBuilderPreserveTrailingSlash(true);
	}
	public static UrlPathBuilderPreserveTrailingSlash notPreservingTrailingSlash() {
		return new UrlPathBuilderPreserveTrailingSlash(false);
	}
	@RequiredArgsConstructor
	public static class UrlPathBuilderPreserveTrailingSlash {
		private final boolean _preserveTrailingSlash;

		public UrlPath from(final String... elements) {
			if (CollectionUtils.isNullOrEmpty(elements)) return null;
			return new UrlPath(_preserveTrailingSlash,
							   elements);
		}
		public <P extends IsPath> UrlPath from(final P other) {
			if (other == null) return null;
			UrlPath outPath = new UrlPath(_preserveTrailingSlash,
										  other);
			return outPath;
		}
		public UrlPath from(final Object... obj) {
			if (obj == null) return null;
			return new UrlPath(_preserveTrailingSlash,
							   obj);
		}
		@GwtIncompatible
		public UrlPath from(final URL url) {
			if (url == null) return null;
			return new UrlPath(_preserveTrailingSlash,
							   url.toString());
		}
	}
	/**
	 * Factory from {@link String}
	 * @param path
	 * @return
	 */
	public static UrlPath valueOf(final String path) {
		return new UrlPath(path);
	}
	/**
	 * Factory from path components
	 * @param elements
	 * @return the {@link Path} object
	 */
	public static UrlPath from(final String... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return null;
		return new UrlPath(elements);
	}
	/**
	 * Factory from other {@link Path} object
	 * @param other
	 * @return the new {@link Path} object
	 */
	public static <P extends IsPath> UrlPath from(final P other) {
		if (other == null) return null;
		UrlPath outPath = new UrlPath(other);
		return outPath;
	}
	/**
	 * Factory from an {@link Object} (the path is composed translating the {@link Object} to {@link String})
	 * @param obj
	 * @return the {@link Path} object
	 */
	public static UrlPath from(final Object... obj) {
		if (obj == null) return null;
		return new UrlPath(obj);
	}
	/**
	 * Factory from a {@link URL} object
	 * @param url
	 * @return
	 */
	@GwtIncompatible
	public static UrlPath from(final URL url) {
		if (url == null) return null;
		return new UrlPath(url.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		if (!_preserveTrailingSlash) return super.asString();
		return _trailingSlash ? super.asString() + "/"
							  : super.asString();
	}
	@Override
	public String asRelativeString() {
		if (!_preserveTrailingSlash) return super.asRelativeString();
		return _trailingSlash ? super.asRelativeString() + "/"
							  : super.asRelativeString();
	}
	@Override
	public String asAbsoluteString() {
		if (!_preserveTrailingSlash) return super.asAbsoluteString();
		return _trailingSlash ? super.asAbsoluteString() + "/"
							  : super.asAbsoluteString();
	}
	@Override
	public <P extends IsPath> String asAbsoluteStringFrom(final P parentPath) {
		if (!_preserveTrailingSlash) return super.asAbsoluteStringFrom(parentPath);
		return _trailingSlash ? super.<P>asAbsoluteStringFrom(parentPath) + "/"
							  : super.<P>asAbsoluteStringFrom(parentPath);
	}
	@Override
	public <P extends IsPath> String asRelativeStringFrom(final P parentPath) {
		if (!_preserveTrailingSlash) return super.asRelativeStringFrom(parentPath);
		return _trailingSlash ? super.<P>asRelativeStringFrom(parentPath) + "/"
							  : super.<P>asRelativeStringFrom(parentPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asAbsoluteStringIncludingQueryStringEncoded(final UrlQueryString queryString) {
		return this.asAbsoluteStringIncludingQueryString(queryString,
														 true);
	}
	@Override
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString) {
		return this.asAbsoluteStringIncludingQueryString(queryString,
														 false);
	}
	@Override
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues) {
		return queryString != null ? Strings.customized("{}?{}",
												   	    this.asAbsoluteString(),queryString.asString(encodeParamValues))
								   : this.asAbsoluteString();
	}
	@Override
	public String asRelativeStringIncludingQueryStringEncoded(final UrlQueryString queryString) {
		return this.asRelativeStringIncludingQueryString(queryString,
														 true);
	}
	@Override
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString) {
		return this.asRelativeStringIncludingQueryString(queryString,
														 false);
	}
	@Override
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues) {
		return queryString != null ? Strings.customized("{}?{}",
												   		this.asRelativeString(),queryString.asString(encodeParamValues))
								   : this.asRelativeString();
	}
///////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public UrlPath joinedWith(final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return this;
		UrlPath outUrlPath = (UrlPath)PathBase.join(this.getPathFactory(),
								  	  				this,_sanitize(elements));
		return outUrlPath;
	}
	@Override @SuppressWarnings("unchecked")
	public UrlPath prependedWith(final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return this;
		UrlPath outUrlPath = (UrlPath)PathBase.prepend(this.getPathFactory(),
								   	  	 			   this,_sanitize(elements));
		return outUrlPath;
	}
	private Collection<Object> _sanitize(final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) throw new IllegalArgumentException();
		return FluentIterable.from(elements)
					.filter(new Predicate<Object>() {
									@Override
									public boolean apply(final Object el) {
										return el != null;
									}
					})
					.transform(new Function<Object,Object>() {
										@Override
										public Object apply(final Object el) {
											if (el instanceof String) {
												String elStr = el.toString();
												// remove query string or anchor if present
												int pQ = elStr.indexOf('?');
												if (pQ >= 0) elStr = elStr.substring(0,pQ);
												int pA = elStr.indexOf('#');
												if (pA >= 0) elStr = elStr.substring(0,pA);
												return elStr;
											}
											return el;
										}
							   })
					.filter(new Predicate<Object>() {
									@Override
									public boolean apply(final Object el) {
										return el instanceof String
													? ((String)el).length() > 0
												    : true;
									}
							})
					.toList();

	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the remaining path fragment begining where the given
	 * starting path ends
	 * ie: if path=/a/b/c/d
	 *     and startingPath = /a/b
	 *     ... this function will return /c/d
	 * @param startingPath
	 * @return
	 */
	public UrlPath remainingPathFrom(final UrlPath startingPath) {
		Collection<String> remainingPathEls = this.getPathElementsAfter(startingPath);
		UrlPath outUrlPath = remainingPathEls != null ? new UrlPath(_preserveTrailingSlash,
																    remainingPathEls)
													  : null;
		return outUrlPath;
	}
	/**
	 * Returns the url path AFTER the given prefix
	 * ie: if path=/foo/bar/baz.hml and prefix=/foo
	 * then, the returned path=/bar/baz.html
	 * If the path does NOT starts with the given prefix, it throws an IllegalStateException
	 * @param prefix
	 * @return
	 */
	public UrlPath urlPathAfter(final UrlPath prefix) {
		return this.remainingPathFrom(prefix);
	}
}
