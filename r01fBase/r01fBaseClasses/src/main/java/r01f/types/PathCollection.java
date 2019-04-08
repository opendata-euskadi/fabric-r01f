package r01f.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasID;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.UrlCollectionItemID;
import r01f.util.types.collections.CollectionUtils;

/**
 * A collection of paths
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="urlCollection")
public class PathCollection<P extends HasPath<?> & HasID<?> & HasTaggeableFacet<String>>
	 extends LinkedHashSet<P> {

	private static final long serialVersionUID = 8522332353669168499L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PathCollection() {
		// default no-args constructor
	}
	// required by the marshaller
	public PathCollection(final int length) {
		super(length);
	}
	public PathCollection(final P... items) {
		if (CollectionUtils.hasData(items)) this.addAll(Arrays.asList(items));
	}
	public PathCollection(final Collection<P> items) {
		if (CollectionUtils.hasData(items)) this.addAll(items);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public PathCollection<P> addPaths(final P... urls) {
		if (CollectionUtils.hasData(urls)) return this.addPaths(Arrays.asList(urls));
		return this;
	}
	public PathCollection<P> addPaths(final Collection<P> paths) {
		if (CollectionUtils.hasData(paths)) this.addAll(paths);
		return this;
	}
	public boolean removePath(final Path path) {
		if (this.size() == 0) return false;

		Collection<P> itemsToBeRemoved = Lists.newArrayList();
		// find the items to be removed
		for (P item : this) {
			if (path.equals(item.getPath())) itemsToBeRemoved.add(item);
		}
		// Effectively remove the items
		boolean removed = this.removeAll(itemsToBeRemoved);
		return removed;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET Paths by tag
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<P> getPathsWithId(final UrlCollectionItemID id) {
		Collection<P> outItems = FluentIterable.from(this)
								   .filter(new Predicate<P>() {
													@Override
													public boolean apply(final P item) {
														return item.getId() != null
															&& item.getId().is(id);
													}
								   		   })
								   	.toList();
		return outItems;
	}
	/**
	 * Returns only paths tagged by a certain tag
	 * @param tag
	 * @return
	 */
	public Collection<P> getPathsTaggedBy(final String tag) {
		Collection<P> outItems = FluentIterable.from(this)
								   .filter(new Predicate<P>() {
													@Override
													public boolean apply(final P item) {
														return item.asTaggeable()
																   .containsTag(tag);
													}
								   		   })
								   	.toList();
		return outItems;
	}
}