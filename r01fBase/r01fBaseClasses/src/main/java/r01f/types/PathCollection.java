package r01f.types;

import java.util.Collection;

import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="pathCollection")
public class PathCollection 
	 extends IsPathCollection<PathCollectionItem> {

	private static final long serialVersionUID = 8640918754419234685L;
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
	public PathCollection(final PathCollectionItem... items) {
		super(items);
	}
	public PathCollection(final Collection<PathCollectionItem> items) {
		super(items);
	}
}
