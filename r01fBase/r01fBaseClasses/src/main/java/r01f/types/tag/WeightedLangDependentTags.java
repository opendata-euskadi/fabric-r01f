package r01f.types.tag;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Sets;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="tags")
@ConvertToDirtyStateTrackable
public class WeightedLangDependentTags 
	 extends TagList<WeightedLangDependentTag> {

	private static final long serialVersionUID = 6246353907986624402L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public WeightedLangDependentTags() {
		// no-args constructor
	}
	/**
	 * @param tags
	 */
	public WeightedLangDependentTags(final Collection<WeightedLangDependentTag> tags) {
		this.addAll(tags != null ? Sets.newHashSet(tags) : Sets.newHashSet());
	}
	/**
	 * @param tags
	 */
	public WeightedLangDependentTags(final WeightedLangDependentTag... tags) {
		this.addAll(Arrays.asList(tags));
	}
	/**
	 * @param size
	 */
	public WeightedLangDependentTags(final int size) {
		// marshaller needs this method
	}
}
