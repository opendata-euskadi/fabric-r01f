package r01f.facets.delegates;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Sets;

import r01f.facets.Tagged;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.types.tag.TagList;

/**
 * Delegate for {@link Tagged} behavior
 * @param <M>
 */
public class TaggeableDelegate<T extends Comparable<T>,M extends HasTaggeableFacet<T>>
	 extends FacetDelegateBase<M>
  implements Tagged<T> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TaggeableDelegate(final M hasTaggeableFacet) {
		super(hasTaggeableFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Taggeable interface
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public TagList<T> getTags() {
		return _modelObject.getTags();
	}
	@Override
	public boolean containsTag(final T tag) {
		return this.getTags() != null ? this.getTags().contains(tag)
									  : false;
	}
	@Override
	public boolean containsAllTags(final T... tags) {
		return this.containsAllTags(Arrays.asList(tags));
	}
	@Override
	public boolean containsAllTags(final Collection<T> tags) {
		return this.getTags() != null ? this.getTags().containsAll(tags)
									  : false;
	}
	@Override
	public boolean addTag(final T tag) {
		_ensureTagList(_modelObject);
		return this.getTags().add(tag);
	}
	@Override
	public boolean addTags(final Collection<T> tags) {
		if (tags == null || tags.size() == 0) return false;
		_ensureTagList(_modelObject);
		return this.getTags().addAll(tags);
	}
	@Override
	public boolean addTags(final T... tags) {
		if (tags == null || tags.length == 0) return false;
		return this.addTags(Sets.newHashSet(tags));
	}
	@Override
	public boolean removeTag(final T tag) {
		return this.getTags() != null ? this.getTags().remove(tag)
									  : false;
	}
	@Override
	public void clearTags() {
		if (this.getTags() != null) this.getTags().clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asStringSeparatedWith(final char sep) {
		return this.getTags() != null ? this.getTags().asStringSeparatedWith(sep)
									  : "";
	}
	@Override
	public String asStringQuotedAndSeparatedWith(final char startQuote,final char endQuote,
												 final char sep) {
		return this.getTags() != null ? this.getTags().asStringQuotedAndSeparatedWith(startQuote,endQuote,
																					  sep)
									  : "";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static <T extends Comparable<T>> void _ensureTagList(final HasTaggeableFacet<T> modelObject) {
		if (modelObject.getTags() == null) modelObject.setTags(new TagList<T>());
	}

}
