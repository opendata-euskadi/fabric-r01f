package r01f.types.tag;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="tags")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class StringTagList 
	 extends TagList<String> {

	private static final long serialVersionUID = 4219370572494826625L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public StringTagList() {
		// no-args constructor
	}
	/**
	 * @param tags
	 */
	public StringTagList(final Collection<String> tags) {
		super(tags);
	}
	/**
	 * @param tags
	 */
	public StringTagList(final String... tags) {
		super(tags);
	}
	/**
	 * @param size
	 */
	public StringTagList(final int size) {
		// marshaller needs this method
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void addAll(final String... tags) {
		if (CollectionUtils.hasData(tags)) this.addAll(Lists.newArrayList(tags));
	}
}
