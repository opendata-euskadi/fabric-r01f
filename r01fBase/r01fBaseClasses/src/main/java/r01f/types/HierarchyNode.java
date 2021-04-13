package r01f.types;

import com.google.common.base.Function;

import lombok.experimental.Accessors;
import r01f.patterns.FactoryFrom;
import r01f.types.hierarchy.IsHierarchical;

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
	public static <T> HierarchyNode<T> clone(final HierarchyNode<T> other,
										 	 final FactoryFrom<T,T> dataCloner) {
		return _recurseClone(other,
							 dataCloner);
	}
	private static <T> HierarchyNode<T> _recurseClone(final HierarchyNode<T> other,
							   			   			  final FactoryFrom<T,T> dataCloner) {
		HierarchyNode<T> cloned = new HierarchyNode<T>(dataCloner.from(other.getData()));
		if (other.hasChildren()) {
			for (HierarchyNode<T> child : other.getChildren()) {
				HierarchyNode<T> childCloned = _recurseClone(child,
															 dataCloner);
				cloned.addChild(childCloned);
			}
		}
		return cloned;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	public <U> HierarchyNode<U> recurseTransformValueUsing(final Function<T,U> fun) {
		return _recurseTransformUsing(fun,
									  new FactoryFrom<U,HierarchyNode<U>>() {
											@Override
											public HierarchyNode<U> from(final U obj) {
												return new HierarchyNode<U>(obj);
											}
									  });
	}
	public <H extends IsHierarchical<H>> H recurseTransformUsing(final Function<HierarchyNode<T>,H> fun) {
		return _recurseTransformUsing(fun);
	}
}
