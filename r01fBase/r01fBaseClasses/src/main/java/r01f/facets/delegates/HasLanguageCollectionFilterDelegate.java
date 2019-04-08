package r01f.facets.delegates;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.RequiredArgsConstructor;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.util.types.collections.CollectionUtils;

@RequiredArgsConstructor
public class HasLanguageCollectionFilterDelegate<L extends HasLanguage> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Collection<L> _col;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Filters the collection and returns only the elements with the given language
	 * @param lang
	 * @return
	 */
	public Collection<L> filterIn(final Language lang) {
		if (CollectionUtils.isNullOrEmpty(_col)) return null;
		return FluentIterable.from(_col)
							 .filter(new Predicate<L>() {
											@Override
											public boolean apply(final L el) {
												return el.getLanguage() != null
													&& el.getLanguage().is(lang);
											}
							 		 })
							 .toList();
	}
	/**
	 * Returns the first element with the given language
	 * @param lang
	 * @return
	 */
	public L firstIn(final Language lang) {
		if (CollectionUtils.isNullOrEmpty(_col)) return null;
		return FluentIterable.from(_col)
							 .firstMatch(new Predicate<L>() {
												@Override
												public boolean apply(final L el) {
													return el.getLanguage() != null
														&& el.getLanguage().is(lang);
												}
							 		 })
							 .orNull();
	}
}
