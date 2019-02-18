package r01f.facets;

import com.google.common.annotations.GwtIncompatible;

import r01f.facets.util.Facetables;

/**
 * Interface for facetable objects
 * @see Facetables
 */
@GwtIncompatible
public interface Facetable {
	/**
	 * Casts a {@link Facetable} object to a facet
	 * (obviously the {@link Facetable} object MUST implements the facet
	 * @param facetType
	 * @return
	 */
	
	public <F extends Facet> F asFacet(final Class<F> facetType);
	/**
	 * Checks if a {@link Facetable} object has a facet
	 * @param facetType
	 * @return
	 */
	public <F extends Facet> boolean hasFacet(final Class<F> facetType);
}
