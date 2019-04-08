package r01f.types;

import java.util.Collection;

/**
 * Creates a {@link IsPath} object from it's elements 
 */
public interface PathFactory<P extends IsPath> {
	public P createPathFrom(final Collection<String> elements);
}