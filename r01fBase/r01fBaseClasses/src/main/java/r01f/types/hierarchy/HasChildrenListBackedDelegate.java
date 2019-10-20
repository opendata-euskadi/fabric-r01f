package r01f.types.hierarchy;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import r01f.util.types.collections.CollectionUtils;

/**
 * Simple {@link HasChildren} delegate
 * @param <T>
 */
public abstract class HasChildrenListBackedDelegate<T>
		   implements HasChildren<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract List<T> getChildList();
	public abstract void setChildList(final List<T> children);
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public T addChild(final T item) {
		if (this.getChildList() == null) this.setChildList(Lists.newArrayList());
		this.getChildList().add(item);
		return item;
	}
	@Override
	public void addChildren(final Collection<T> items) {
		if (this.getChildList() == null) this.setChildList(Lists.newArrayList());
		this.getChildList().addAll(items);
	}
	@Override
	public T insertChildAt(final T child,final int index) {
		if (this.getChildList() == null) this.setChildList(Lists.newArrayList());
		List<T> structureList = this.getChildList();
		structureList.add(index,child);
		return child;
	}
	@Override
	public void removeChild(final T child) {
		if (this.getChildList() == null) return;
		this.getChildList().remove(child);
	}
	@Override
	public void removeChildAt(final int index) {
		if (this.getChildList() == null) return;
		List<T> structureList = this.getChildList();
		structureList.remove(index);
	}
	@Override
	public void removeAllChilds() {
		if (this.getChildList() == null) return;
		this.getChildList().clear();
	}
	@Override
	public boolean hasChildren() {
		return CollectionUtils.hasData(this.getChildList());
	}
	@Override
	public T getChildAt(final int index) {
		if (this.getChildList() == null) return null;
		List<T> structureList = this.getChildList();
		return structureList.get(index);
	}
	@Override
	public int getChildCount() {
		return CollectionUtils.hasData(this.getChildList()) ? this.getChildList().size()
															: 0;
	}
	@Override
	public int getChildIndex(final T child) {
		if (this.getChildList() == null) return -1;
		List<T> structureList = this.getChildList();
		return structureList.indexOf(child);
	}
	@Override
	public Collection<T> getChildren() {
		return this.getChildList();
	}
}
