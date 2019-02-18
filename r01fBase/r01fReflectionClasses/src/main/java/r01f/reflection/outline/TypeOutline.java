package r01f.reflection.outline;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.util.types.collections.Lists;


@Accessors(prefix="_")
public class TypeOutline
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter protected OutlineTreeNode _bottomType;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructs a TypeOutline that represents all classes and interfaces that
     * are generalizations of the provided class.
     * This ends up with a tree structure of the inheritance hierarchy for that provided class all the
     * way up to java.lang.Object.
     * @param type The class to build the tree for.
     */
    public TypeOutline(final Class<?> type) {
        _bottomType = _recurseBuildNode(type,
        								Maps.<Class<?>,OutlineTreeNode>newHashMap());
    }
    public static TypeOutline from(final Class<?> type) {
    	return new TypeOutline(type);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructs an ordered list starting at the highest (most general) class
     * in the tree and moving down the tree, ensuring no generalization comes
     * after one of its specializations.
     * @return a list ordered as above
     */
    public Collection<Class<?>> getNodesFromGeneralToSpezialized() {
        Collection<Class<?>> typesList = this.getNodesFromSpezializedToGeneralization();
        
        // Reverse the list so that the top class in the hierarchy comes first
        return Lists.reverse((List<Class<?>>)typesList);
    }
    /**
     * Constructs an ordered list starting at the most specialized class
     * in the tree and moving up the tree to the most general types,
     * ensuring no especialization comes before one of its generalizations.
     * @return a list ordered as above
     */
    public Collection<Class<?>> getNodesFromSpezializedToGeneralization() {
        Collection<OutlineTreeNode> treeNodes = Lists.newArrayList();
        treeNodes.add(_bottomType);
        _recurseBuildList(_bottomType,
        		   treeNodes);

        // order the tree nodes
        Collection<OutlineTreeNode> orderedTreeNodes = Ordering.from(new Comparator<OutlineTreeNode>() {
																				@Override
																				public int compare(final OutlineTreeNode n1,final OutlineTreeNode n2) {
																					return n1.compareTo(n2);
																				}
												        			  })
        														.sortedCopy(treeNodes);
        // Refactor list into a list of classes from a list of OutlineTreeNode
        List<Class<?>> typesList = FluentIterable
        								.from(orderedTreeNodes)
    									 // transform to a collection of types
			        					 .transform(new Function<OutlineTreeNode,Class<?>>() {
															@Override
															public Class<?> apply(final OutlineTreeNode node) {
																return node.getObjectType();
															}
			        					 			})
			        					 .toList();
        return typesList;
    }
    /**
     * Build breadth first in order to maintain sudo ordering as per
     * class declarations (i.e. if A implements B, C... B is closer in the
     * chain to A than C is, because B comes first in the implements clause.
     *
     * Note that the list coming out here is preordered, but not natural
     * ordered. (i.e. some classes are out of order in relation to classes
     * they have direct relationships with. This is later fixed by a sort
     * on the list by natural ordering. Collecitons.sort, does preserve
     * the preordering for nodes that have no relationship.
     *
     * @param node the node to be browsed.
     * @param output this list is altered to add the contents as they are
     *   			 browsed in breadth-first order. Start with a list containing only
     *   			 the bottom node.
     */
    private void _recurseBuildList(final OutlineTreeNode node,
    							   final Collection<OutlineTreeNode> output) {

        for (OutlineTreeNode parent : node.getParents()) {
            if (!output.contains(parent)) output.add(parent);
        }
        for (OutlineTreeNode parent : node.getParents()) {
            _recurseBuildList(parent,
            		   		  output);
        }
    }
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		_recurseDebug(_bottomType,sb,0);
		return sb;
	}
	private void _recurseDebug(final OutlineTreeNode node,
							   final StringBuilder sb,
							   final int indent) {
		for (int i=0; i < indent; i++) sb.append("\t");
		sb.append(node.getObjectType());
		sb.append("\n");
        for (OutlineTreeNode parent : node.getParents()) {
            _recurseDebug(parent,
            		   	  sb,
            		   	  indent+1);
        }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OUTLINE NODE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Inner class representing each node in the tree. Holds references to the
     * nodes children, parent and provides the Comparable interface for sorting
     * by inheritance hierarchy.
     */
    @Accessors(prefix="_")
    public static class OutlineTreeNode
    		 implements Comparable<OutlineTreeNode> {

        // The class of this node
        @Getter protected final Class<?> _objectType;

        // The map of children classes to their class names
        @Getter protected final Collection<OutlineTreeNode> _children;

        // A reference to the parent node of this node
        @Getter protected final Collection<OutlineTreeNode> _parents;

        public OutlineTreeNode(final Class<?> objectType) {
            _objectType = objectType;
            _children = Lists.newArrayList();
            _parents = Lists.newArrayList();
        }
        public void addParent(final OutlineTreeNode node) {
        	_parents.add(node);
            node.addChild(this);
        }
        public void addChild(final OutlineTreeNode node) {
            _children.add(node);
        }
        @Override
        public boolean equals(final Object obj) {
        	if (obj == null) return false;
        	if (obj == this) return true;
        	if (!(obj instanceof OutlineTreeNode)) return false;

        	OutlineTreeNode otherNode = (OutlineTreeNode)obj;
            return _objectType != null ? otherNode.getObjectType() != null ? _objectType.equals(otherNode.getObjectType())
            															   : false
            						   : true;	// both null
        }
        @Override
        public int hashCode() {
            return _objectType != null ? _objectType.hashCode()
            						   : super.hashCode();
        }
        /**
         * Compares one class to another class by their inheritance tree.
         * @return an integer representing the comparison results as follows:<br>
         *    2  if this is a subclass of past in object<br>
         *    -2 if this is a superclass of past in object<br>
         *    0 if they are not related (and in relation to sorting, equal)<br>
         *    0  if they are the same<br>
         */
        @Override
        public int compareTo(final OutlineTreeNode other) {
            Class<?> otherType = other.getObjectType();
            if (otherType.equals(_objectType)) {
                return 0;
            } else if (_objectType.isAssignableFrom(otherType)) {
                return 2;
            } else if (otherType.isAssignableFrom(_objectType)) {
                return -2;
            } else {
                return 0;
            }
        }
    } // End of OutlineTreeNode
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Recursive method that builds the type hierarchy
     * @param objectType
     * @param allNodes
     * @return
     */
    private static OutlineTreeNode _recurseBuildNode(final Class<?> objectType,
    										  		 final Map<Class<?>,OutlineTreeNode> allNodes) {
        OutlineTreeNode _node;
        if (allNodes.containsKey(objectType)) {
            _node = allNodes.get(objectType);
        } else {
            _node = new OutlineTreeNode(objectType);
            allNodes.put(objectType,_node);
        }

        // Add the implemented interfaces...
        for (Class<?> superInterface : objectType.getInterfaces()) {
            OutlineTreeNode parent = _recurseBuildNode(superInterface,
            										   Maps.<Class<?>,OutlineTreeNode>newHashMap());		// recurse!
            _node.addParent(parent);
        }

        // Add the superclass after the interfaces...
        Class<?> superClass = objectType.getSuperclass();
        if (superClass != null) {
            OutlineTreeNode parent = _recurseBuildNode(superClass,
            								    	   Maps.<Class<?>,OutlineTreeNode>newHashMap());		// recurse!
            _node.addParent(parent);
        }
        return _node;
    }
}
