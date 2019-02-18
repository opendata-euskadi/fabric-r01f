package r01f.types;

import lombok.experimental.Accessors;

/**
 * A node in an hierarchical structure of T objects
 * @param <T>
 */
@Accessors(prefix="_")
public class HierarchyNode<T>
     extends HierarchyNodeBase<T,HierarchyNode<T>> {


	private static final long serialVersionUID = 7151854368273327003L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public HierarchyNode() {
		// no args constructor
	}
	public HierarchyNode(final T data) {
		super(data);
	}
	public static <T> HierarchyNode<T> create(final T data) {
		return new HierarchyNode<T>(data);
	}
}
