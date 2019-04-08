package r01f.facets.util;

import com.google.common.annotations.GwtIncompatible;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.facets.Facet;
import r01f.facets.Facetable;

/**
 * Utility methods for {@link Facetable} objects
 */
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class Facetables {
	/**
	 * Casts a {@link Facetable} object to a facet
	 * (obviously the {@link Facetable} object MUST implements the facet
	 * @param f
	 * @param facetType
	 * @return
	 */
	public static <F extends Facet> F asFacet(final Facetable f,
											  final Class<F> facetType) {
		return facetType.cast(f);	// ReflectionUtils.cast(facetType,f);
	}
	/**
	 * Checks if a {@link Facetable} object has a facet
	 * @param facetable
	 * @param facetType
	 * @return
	 */
	public static <F extends Facet> boolean hasFacet(final Facetable facetable,
												     final Class<F> facetType) {
		return Facetables.hasFacet(facetable.getClass(),
								   facetType);
	}
	/**
	 * Checks if a {@link Facetable} object has a facet
	 * @param facetableType
	 * @param facetType
	 * @return
	 */
	public static <F extends Facet> boolean hasFacet(final Class<? extends Facetable> facetableType,
													 final Class<F> facetType) {
		return facetType.isAssignableFrom(facetableType); 	// ReflectionUtils.isImplementing(facetableType,
											  				//								  facetType);
	}
}
