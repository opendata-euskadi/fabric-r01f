package r01f.types;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import lombok.experimental.Accessors;
import r01f.types.hierarchy.IsHierarchical;

/**
 * A node in an hierarchical structure of T objects
 * @param <T>
 */
@Accessors(prefix="_")
public abstract class HierarchyNodeBase<T,SELF_TYPE extends HierarchyNodeBase<T,SELF_TYPE>>
	          extends HierarchyNodeNoParentRefBase<T,SELF_TYPE>
  		   implements IsHierarchical<SELF_TYPE>,		// has an hierarchical structure
  			 		  Serializable {
	private static final long serialVersionUID = 6955527630723451277L;
/////////////////////////////////////////////////////////////////////////////////////////
//  PARENT
/////////////////////////////////////////////////////////////////////////////////////////
	private SELF_TYPE _parent;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public HierarchyNodeBase() {
		// no args constructor
	}
	public HierarchyNodeBase(final T data) {
		super(data);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PARENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDirectAncestor(final SELF_TYPE father) {
		_parent = father;
	}
	@Override
	public SELF_TYPE getDirectAncestor() {
		return _parent;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SLIBINGS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public List<SELF_TYPE> getSiblings() {
		if (_parent == null) return null;
		Collection<SELF_TYPE> allParentChilds = _parent.getChildren();
		return r01f.util.types.collections.Lists.getSiblings((List<SELF_TYPE>)allParentChilds,
							     							 (SELF_TYPE)this);
	}
	@Override @SuppressWarnings("unchecked")
	public List<SELF_TYPE> getSiblingsBefore() {
		if (_parent == null) return null;
		Collection<SELF_TYPE> allParentChilds = _parent.getChildren();
		return r01f.util.types.collections.Lists.getSiblingsBefore((List<SELF_TYPE>)allParentChilds,
									   							   (SELF_TYPE)this);
	}
	@Override @SuppressWarnings("unchecked")
	public SELF_TYPE getPrevSibling() {
		if (_parent == null) return null;
		Collection<SELF_TYPE> allParentChilds = _parent.getChildren();
		return r01f.util.types.collections.Lists.getPrevSibling((List<SELF_TYPE>)allParentChilds,
																 (SELF_TYPE)this);
	}
	@Override @SuppressWarnings("unchecked")
	public List<SELF_TYPE> getSiblingsAfter() {
		if (_parent == null) return null;
		Collection<SELF_TYPE> allParentChilds = _parent.getChildren();
		return r01f.util.types.collections.Lists.getSiblingsAfter((List<SELF_TYPE>)allParentChilds,
									  							  (SELF_TYPE)this);
	}
	@Override @SuppressWarnings("unchecked")
	public SELF_TYPE getNextSibling() {
		if (_parent == null) return null;
		Collection<SELF_TYPE> allParentChilds = _parent.getChildren();
		return r01f.util.types.collections.Lists.getNextSibling((List<SELF_TYPE>)allParentChilds,
								 								(SELF_TYPE)this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public SELF_TYPE branchRoot() {
		SELF_TYPE currNode = (SELF_TYPE)this;
		SELF_TYPE currNodeParent = this.getDirectAncestor();
		while (currNodeParent != null) {
			currNode = currNodeParent;
			currNodeParent = currNode.getDirectAncestor();
		}
		return currNode;
	}
	@Override
	public boolean isDescendantOf(final SELF_TYPE ancestor) {
		boolean outIsDescendant = false;
		SELF_TYPE currNodeParent = this.getDirectAncestor();
		while (currNodeParent != null) {
			if (currNodeParent == ancestor) {
				outIsDescendant = true;
				break;
			}
			currNodeParent = currNodeParent.getDirectAncestor();
		}
		return outIsDescendant;
	}
}
