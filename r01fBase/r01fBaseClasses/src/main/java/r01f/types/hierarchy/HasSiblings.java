package r01f.types.hierarchy;

import java.util.List;

public interface HasSiblings<T> {
	/**
     * Returns the object's siblings
     * @return
     */
    public List<T> getSiblings();
	/**
     * Returns the object's siblings that comes before this or null if this is the first sibling
     * @return
     */
    public List<T> getSiblingsBefore();
    /**
     * Returns the object's sibling that comes right before this or null if this is the first sibling
     * @return
     */
    public T getPrevSibling();
	/**
     * Returns the object's siblings that comes after this or null if this is the last sibling
     * @return
     */
    public List<T> getSiblingsAfter();
    /**
     * Returns the object's sibling that comes right after this or null if this is the last sibling
     * @return
     */
    public T getNextSibling();
}
