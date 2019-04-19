package r01f.xml.merge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * DOM {@link Element} with any other non-element nodes which precede it.
 */
@Accessors(prefix="_")
class XMLMergerContext {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final String KEYS_ATTRIBUTE_NAME = "combine.keys";
	public static final String ID_ATTRIBUTE_NAME = "combine.id";
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private Element _element;
	@Getter private final List<Node> _neighbours = new ArrayList<Node>();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLMergerContext() {
		// default no-args constructor
	}
	public static XMLMergerContext fromElement(final Element element) {
		XMLMergerContext context = new XMLMergerContext();
		context.setElement(element);
		return context;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void addNeighbour(final Node node) {
		_neighbours.add(node);
	}
	public void addAsChildTo(final Node node) {
		for (Node neighbour : _neighbours) {
			node.appendChild(neighbour);
		}
		node.appendChild(_element);
	}
	public void addAsChildTo(final Node node,final Document document) {
		for (Node neighbour : _neighbours) {
			node.appendChild(document.importNode(neighbour, true));
		}
		if (_element != null) {
			node.appendChild(document.importNode(_element, true));
		}
	}
	public List<XMLMergerContext> groupChildContexts() {
		if (_element == null) {
			return Collections.emptyList();
		}
		NodeList nodes = _element.getChildNodes();
		List<XMLMergerContext> contexts = new ArrayList<XMLMergerContext>(nodes.getLength());

		XMLMergerContext context = new XMLMergerContext();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				context.setElement((Element) node);
				contexts.add(context);
				context = new XMLMergerContext();
			} else {
				context.addNeighbour(node);
			}
		}
		// add last with empty element
		contexts.add(context);
		return contexts;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return "[" + _neighbours + ", " + _element + "]";
	}
}
