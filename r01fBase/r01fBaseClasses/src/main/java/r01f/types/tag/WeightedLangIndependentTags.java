package r01f.types.tag;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Sets;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="tags")
@ConvertToDirtyStateTrackable
public class WeightedLangIndependentTags 
	 extends TagList<WeightedLangIndependentTag> {

	private static final long serialVersionUID = 6246353907986624402L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public WeightedLangIndependentTags() {
		// no-args constructor
	}
	/**
	 * @param tags
	 */
	public WeightedLangIndependentTags(final Collection<WeightedLangIndependentTag> tags) {
		this.addAll(Sets.newHashSet(tags));
	}
	/**
	 * @param tags
	 */
	public WeightedLangIndependentTags(final WeightedLangIndependentTag... tags) {
		this.addAll(Arrays.asList(tags));
	}
	/**
	 * @param size
	 */
	public WeightedLangIndependentTags(final int size) {
		// marshaller needs this method
	}
}
