package r01f.types.url;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasID;
import r01f.facets.HasLanguage;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.web.WebLinkCollection;
import r01f.util.types.collections.CollectionUtils;

/**
 * A collection of urls
 * @See {@link WebLinkCollection}
 */
@ConvertToDirtyStateTrackable
@Slf4j
@MarshallType(as="urlCollection")
public class UrlCollection<U extends HasUrl & HasID<?> & HasLanguage & HasTaggeableFacet<String>>
	 extends LinkedHashSet<U> {

	private static final long serialVersionUID = 8522332353669168499L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlCollection() {
		// default no-args constructor
	}
	// required by the marshaller
	public UrlCollection(final int length) {
		super(length);
	}
	@SuppressWarnings("unchecked")
	public UrlCollection(final U... items) {
		if (CollectionUtils.hasData(items)) this.addAll(Arrays.asList(items));
	}
	public UrlCollection(final Collection<U> items) {
		if (CollectionUtils.hasData(items)) this.addAll(items);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public UrlCollection<U> addUrls(final U... urls) {
		if (CollectionUtils.hasData(urls)) return this.addUrls(Arrays.asList(urls));
		return this;
	}
	public UrlCollection<U> addUrls(final Collection<U> urls) {
		if (CollectionUtils.hasData(urls)) this.addAll(urls);
		return this;
	}
	public boolean removeUrl(final Url url) {
		if (this.size() == 0) return false;

		Collection<U> itemsToBeRemoved = Lists.newArrayList();
		// find the items to be removed
		for (U item : this) {
			if (url.equals(item.getUrl())) itemsToBeRemoved.add(item);
		}
		// Effectively remove the items
		boolean removed = this.removeAll(itemsToBeRemoved);
		return removed;
	}
	/**
	 * Removes all urls with the given language and returns the replaced urls
	 * @param urls the new urls
	 * @return the replaced urls
	 */
	public Collection<U> replaceUrlsInLang(final Language lang,
										   final Collection<U> urls) {
		// get the EXISTING urls in the given lang
		Collection<U> outReplacedUrls = this.getUrlsIn(lang);
		// remove them all
		for (U replacedUrl : outReplacedUrls) {
			this.remove(replacedUrl);
		}
		// now ADD the new urls
		for (U url : urls) {
			if (url.getLanguage() == null || url.getLanguage().isNOT(lang)) throw new IllegalArgumentException(url.getUrl() + " is supposed to be in " + lang + " BUT it's " + url.getLanguage());
			this.add(url);
		}
		// return the replaced urls
		return outReplacedUrls;

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET Urls by tag
/////////////////////////////////////////////////////////////////////////////////////////
	public U getUrlWithId(final UrlCollectionItemID id,
						  final Language lang) {
		Collection<U> outItems = FluentIterable.from(this)
								   .filter(new Predicate<U>() {
													@Override
													public boolean apply(final U item) {
														return item.getLanguage() != null
															&& item.getLanguage().is(lang)
															&& item.getId() != null
															&& item.getId().is(id);
													}
								   		   })
								   	.toList();
		if (outItems != null && outItems.size() > 1) log.warn("The url collection is supposed to have a SINGE item with id={} & lang={}, BUT it contains {} with that id: returning ONLY one",
															  id,lang,
															  outItems.size());
		return outItems != null && outItems.size() >= 1 ? Iterables.getFirst(outItems,
																			 null)
														: null;
	}
	public Collection<U> getUrlsWithId(final UrlCollectionItemID id) {
		Collection<U> outItems = FluentIterable.from(this)
								   .filter(new Predicate<U>() {
													@Override
													public boolean apply(final U item) {
														return item.getId() != null
															&& item.getId().is(id);
													}
								   		   })
								   	.toList();
		return outItems;
	}
	/**
	 * Returns only urls in a certain language
	 * @param tag
	 * @return
	 */
	public Collection<U> getUrlsIn(final Language lang) {
		Collection<U> outItems = FluentIterable.from(this)
									   .filter(new Predicate<U>() {
														@Override
														public boolean apply(final U item) {
															return item.getLanguage() != null
																&& item.getLanguage() == lang;
														}
									   		   })
									   	.toList();
		return outItems;
	}
	/**
	 * Returns only urls tagged by a certain tag
	 * @param tag
	 * @return
	 */
	public Collection<U> getUrlsTaggedBy(final String tag) {
		Collection<U> outItems = FluentIterable.from(this)
								   .filter(new Predicate<U>() {
													@Override
													public boolean apply(final U item) {
														return item.asTaggeable()
																   .containsTag(tag);
													}
								   		   })
								   	.toList();
		return outItems;
	}
	/**
	 * Returns only urls tagged by a certain tag
	 * @param tag
	 * @return
	 */
	public Collection<U> getUrlsTaggedBy(final Language lang,
										 final String tag) {
		Collection<U> outItems = FluentIterable.from(this)
										   .filter(new Predicate<U>() {
															@Override
															public boolean apply(final U item) {
																return item.getLanguage() != null
																	&& item.getLanguage() == lang
																	&& item.asTaggeable().containsTag(tag);
															}
										   		   })
										   	.toList();
		return outItems;
	}
}