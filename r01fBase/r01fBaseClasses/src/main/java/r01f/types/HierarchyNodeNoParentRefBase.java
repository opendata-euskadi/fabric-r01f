package r01f.types;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.types.hierarchy.HasChildren;
import r01f.util.types.collections.CollectionUtils;

/**
 * A node in an hierarchical structure of T objects
 * @param <T>
 */
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public abstract class HierarchyNodeNoParentRefBase<T,SELF_TYPE extends HierarchyNodeNoParentRefBase<T,SELF_TYPE>>
  		   implements HasChildren<SELF_TYPE>,		// has an hierarchical structure
  			 		  Serializable {

	private static final long serialVersionUID = 6955527630723451277L;
/////////////////////////////////////////////////////////////////////////////////////////
//  DATA ASSOCIATED WITH THE NODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter protected T _data;

/////////////////////////////////////////////////////////////////////////////////////////
//  HIERARCHY
/////////////////////////////////////////////////////////////////////////////////////////
	protected List<SELF_TYPE> _children;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public HierarchyNodeNoParentRefBase() {
		// no args constructor
	}
	public HierarchyNodeNoParentRefBase(final T data) {
		this();
		_data = data;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SELF_TYPE addChild(final SELF_TYPE child) {
		int position = CollectionUtils.hasData(_children) ? _children.size() : 0;
		return this.insertChildAt(child,position);
	}
	@Override
	public void addChildren(final Collection<SELF_TYPE> children) {
		if (CollectionUtils.hasData(children)) {
			for (SELF_TYPE item : children) this.addChild(item);
		}
	}
	@Override
	public SELF_TYPE insertChildAt(final SELF_TYPE child,final int index) {
		if (_children == null) _children = Lists.newArrayList();
		_children.add(index,child);
		return child;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void removeChild(final SELF_TYPE child) {
		if (_children != null) _children.remove(child);
	}
	@Override
	public void removeChildAt(final int index) {
		if (_children != null) _children.remove(index);
	}
	@Override
	public void removeAllChilds() {
		if (_children != null) _children.clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasChildren() {
		return CollectionUtils.hasData(_children);
	}
	@Override
	public SELF_TYPE getChildAt(final int index) {
		return _children != null ? _children.get(index)
								 : null;
	}
	@Override
	public int getChildCount() {
		return _children != null ? _children.size()
								 : 0;
	}
	@Override
	public int getChildIndex(final SELF_TYPE child) {
		return _children != null ? _children.indexOf(child)
								 : -1;
	}
	@Override
	public Collection<SELF_TYPE> getChildren() {
		return _children;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean containsChildWithData(final T data) {
		return this.getDescendantWithData(data,
								   		  this.getChildren(),
								   		  false) != null;		// not recursive
	}
	public boolean containsDescendantWithData(final T data) {
		return this.getDescendantWithData(data,
								   		  this.getChildren(),
								   		  true) != null;		// recursive
	}
	public SELF_TYPE getChildWithData(final T data) {
		return this.getDescendantWithData(data,
								   		  this.getChildren(),
								   		  false);				// not recursive
	}
	public SELF_TYPE getDescendantWithData(final T data,
									    		  final Collection<SELF_TYPE> nodes,
									    		  final boolean recurse) {
		SELF_TYPE outNode = null;
		if (CollectionUtils.hasData(nodes)) {
			for (SELF_TYPE node : nodes) {
				if (node.getData().equals(data)) {
					outNode = node;
					break;
				}
				if (recurse) {
					outNode = this.getDescendantWithData(data,
										   	      		 node.getChildren(),
										   	      		 true);
					if (outNode != null) break;
				}
			}
		}
		return outNode;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public interface HierarchyNodeDataDebug<T> {
		public CharSequence debugInfoOf(final T data);
	}
	@SuppressWarnings("unchecked")
	public CharSequence debug(final HierarchyNodeDataDebug<T> nodeDataDebug) {
		StringBuilder dbg = new StringBuilder();
		_recurseDebug(dbg,
					  Lists.newArrayList((SELF_TYPE)this),
					  nodeDataDebug,
					  0);
		return dbg;
	}
	private void _recurseDebug(final StringBuilder dbg,
							   final Collection<SELF_TYPE> nodes,
							   final HierarchyNodeDataDebug<T> nodeDataDebug,
							   final int indent) {
		if (CollectionUtils.isNullOrEmpty(nodes)) return;
		for (SELF_TYPE node : nodes) {
			// indent
			for (int i=0; i < indent; i++) dbg.append("\t");
			// node data
			dbg.append("- ").append(nodeDataDebug.debugInfoOf(node.getData())).append("\n");
			// recurse
			_recurseDebug(dbg,
						  node.getChildren(),
						  nodeDataDebug,
						  indent + 1);
		}
	}
}
