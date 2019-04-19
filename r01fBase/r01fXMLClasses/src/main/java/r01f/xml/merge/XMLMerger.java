package r01f.xml.merge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Splitter;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

import r01f.types.Path;
import r01f.xml.XMLUtils;

/**
 * Merges two or more XML DOM trees.
 * Usage
 *		String recessive = "\n"
 *				+ "<config>\n"
 *				+ "    <service id='1'>\n"
 *				+ "        <parameter>parameter</parameter>\n"
 *				+ "    </service>\n" 
 *				+ "    <a>a</a>\n"
 *				+ "</config>";
 *		String dominant = "\n"
 *				+ "<config>\n"
 *				+ "    <service id='1'>\n"
 *				+ "        <parameter>parameter_dominant</parameter>\n"
 *				+ "    </service>\n"
 *				+ "    <b>b</b>\n"
 * 				+ "</config>";
 *		XMLMerger merger = new XMLMerger();
 *		
 *		Document recessiveDoc = XMLUtils.parse(new ByteArrayInputStream(recessive.getBytes("UTF-8")));
 *		Document dominiantDoc = XMLUtils.parse(new ByteArrayInputStream(dominant.getBytes("UTF-8")));
 *		merger.merge(recessiveDoc);
 *		merger.merge(dominiantDoc);
 *		Document result = merger.buildDocument();
 *
 *		log.info("{}",XMLUtils.asString(result));
 *		
 *
 * The merging algorithm is:<br/>
 * 		[1]: direct subelements of selected node are examined.
 * 		[2]: The elements from both trees with matching ids are paired.
 * 		[3]: Based on selected behavior the content of the paired elements is then merged.
 * 		[4]: The paired elements are recursively merged.
 * 			 Any not paired elements are appended.
 * 
 * Merging behavior can be controlled using two xml attrs:
 * 		- {@link XMLMergeSelf 		'combine.self'}
 * 		- {@link XMLMergeChildren 	'combine.children'}
 * 
 * The merging algorithm was inspired by similar functionality in Plexus Utils.
 * @see <a href="https://github.com/atteo/xml-combiner">xml combiner</a>
 * @see <a href="http://www.sonatype.com/people/2011/01/maven-how-to-merging-plugin-configuration-in-complex-projects/">merging in Maven</a>
 * @see <a href="http://plexus.codehaus.org/plexus-utils/apidocs/org/codehaus/plexus/util/xml/Xpp3DomUtils.html">Plexus utils implementation of merging</a>
 */
public class XMLMerger {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Allows to filter the result of the merging.
	 */
	public interface Filter {
		/**
		 * Post process the matching elements after merging.
		 * @param recessive recessive element, can be null, should not be modified
		 * @param dominant  dominant element, can be null, should not be modified
		 * @param result    result element, will not be null, it can be freely modified
		 */
		void postProcess(final Element recessive,final Element dominant,
						 final Element result);
	}
	private static final Filter NULL_FILTER = new Filter() {
													@Override
													public void postProcess(final Element recessive,final Element dominant,
																			final Element result) {
														// nothing
													}
	};
	interface XMLMergerChildContextsMapper {
		ListMultimap<XMLMergerKey,XMLMergerContext> mapChildContexts(final XMLMergerContext parent,
																	 final List<String> idAttributeNames);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final DocumentBuilder _documentBuilder;
	private final Document _document;
	private final List<String> _idAttributeNames;

	private Filter filter = NULL_FILTER;
	private final XMLMergerChildContextsMapper _childContextMapper = new XMLMergerChildContextMapperUsingIdAttrs();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates XML merger using default {@link DocumentBuilder}.
	 * @throws ParserConfigurationException when {@link DocumentBuilder} creation fails
	 */
	public XMLMerger() throws ParserConfigurationException {
		this(DocumentBuilderFactory.newInstance().newDocumentBuilder());
	}

	public XMLMerger(final DocumentBuilder documentBuilder) {
		this(documentBuilder, 
			 Lists.<String>newArrayList());
	}
	/**
	 * Creates XML merger using given attribute as an id.
	 */
	public XMLMerger(final String idAttributeName) throws ParserConfigurationException {
		this(Lists.newArrayList(idAttributeName));
	}

	public XMLMerger(final List<String> idAttributeNames) throws ParserConfigurationException {
		this(DocumentBuilderFactory.newInstance().newDocumentBuilder(),
			 idAttributeNames);
	}
	/**
	 * Creates XML merger using given document builder and an id attribute name.
	 */
	public XMLMerger(final DocumentBuilder documentBuilder,
					 final String keyAttributeNames) {
		this(documentBuilder,
			 Lists.newArrayList(keyAttributeNames));
	}
	public XMLMerger(final DocumentBuilder documentBuilder,
					 final List<String> idAttributeNames) {
		_documentBuilder = documentBuilder;
		_document = documentBuilder.newDocument();
		_idAttributeNames = idAttributeNames;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the filter.
	 */
	public void setFilter(final Filter filter) {
		if (filter == null) {
			this.filter = NULL_FILTER;
			return;
		}
		this.filter = filter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Merge given file.
	 * @param filePath file to merge
	 */
	public void merge(final Path filePath) throws SAXException, 
												  IOException {
		File file = new File(filePath.asAbsoluteString());
		this.merge(_documentBuilder.parse(file));
	}
	/**
	 * Merge given input stream.
	 * @param stream input stream to combine
	 */
	public void merge(final InputStream stream) throws SAXException,
													   IOException {
		this.merge(_documentBuilder.parse(stream));
	}
	/**
	 * Merge given document.
	 * @param document document to combine
	 */
	public void merge(final Document document) {
		this.merge(document.getDocumentElement());
	}
	/**
	 * Combine given element.
	 * 
	 * @param element element to combine
	 */
	public void merge(final Element element) {
		Element parent = _document.getDocumentElement();
		if (parent != null) {
			// if there exists a previous merged element, just remove it from the 
			// result document
			_document.removeChild(parent);
		}
		// merge (parent can be empty if this is the first call to merge)
		XMLMergerContext result = _merge(XMLMergerContext.fromElement(parent),		// recessive
										 XMLMergerContext.fromElement(element));	// dominant
		result.addAsChildTo(_document);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Return the result of the merging process.
	 */
	public Document buildDocument() {
		_filterOutDefaults(XMLMergerContext.fromElement(_document.getDocumentElement()));
		_filterOutCombines(_document.getDocumentElement());
		return _document;
	}
	/**
	 * Stores the result of the merging process.
	 */
	public void buildDocument(final OutputStream out) throws TransformerException {
		// [1] - Build the merge-resultant document
		Document resultDoc = buildDocument();

		// [2] - transform
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Source input = new DOMSource(resultDoc);
		Result output = new StreamResult(out);

		transformer.transform(input,output);
	}
	/**
	 * Stores the result of the merging process.
	 */
	public void buildDocument(final Path path) throws TransformerException, 
													  FileNotFoundException {
		File file = new File(path.asAbsoluteString());
		this.buildDocument(new FileOutputStream(file));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 		[1]: direct subelements of selected node are examined.
	 * 		[2]: The elements from both trees with matching ids are paired.
	 * 		[3]: Based on selected behavior the content of the paired elements is then merged.
	 * 		[4]: The paired elements are recursively merged.
	 * 			 Any not paired elements are appended.
	 */
	private XMLMergerContext _merge(final XMLMergerContext recessive,final XMLMergerContext dominant) {
		// find 'combine.self' attribute of dominant & recessive elements
		XMLMergeSelf dominantCombineSelf = XMLMergeSelf.of(dominant.getElement());
		XMLMergeSelf recessiveCombineSelf = XMLMergeSelf.of(recessive.getElement());

		if (dominantCombineSelf == XMLMergeSelf.REMOVE) {
			return null;
		} 
		else if (dominantCombineSelf == XMLMergeSelf.OVERRIDE 
			    || recessiveCombineSelf == XMLMergeSelf.OVERRIDABLE) {
			XMLMergerContext result = _copyRecursively(dominant);
			result.getElement().removeAttribute(XMLMergeSelf.ATTRIBUTE_NAME);
			return result;
		}
		
		XMLMergeChildren combineChildren = XMLMergeChildren.of(recessive.getElement(),dominant.getElement());
		if (combineChildren == XMLMergeChildren.APPEND) {
			if (recessive.getElement() != null) {
				XMLUtils.removeWhitespace(recessive.getElement());
				_appendRecursively(dominant, recessive);
				return recessive;
			} else {
				return _copyRecursively(dominant);
			}
		}

		Element resultElement = _document.createElement(dominant.getElement().getTagName());
		XMLUtils.copyAttributes(_document,
								recessive.getElement(), resultElement);
		XMLUtils.copyAttributes(_document,
								dominant.getElement(), resultElement);

		// when dominant combineSelf is null or DEFAULTS use combineSelf from recessive
		XMLMergeSelf combineSelf = dominantCombineSelf;
		if ((combineSelf == null 
		  && recessiveCombineSelf != XMLMergeSelf.DEFAULTS)) {
			// || (combineSelf == XMLMergeSelf.DEFAULTS && recessive.getElement() != null)) {
			combineSelf = recessiveCombineSelf;
		}
		if (combineSelf != null) {
			resultElement.setAttribute(XMLMergeSelf.ATTRIBUTE_NAME,combineSelf.name().toLowerCase());
		} else {
			resultElement.removeAttribute(XMLMergeSelf.ATTRIBUTE_NAME);
		}

		List<String> keys = _idAttributeNames;
		if (recessive.getElement() != null) {
			Attr keysNode = recessive.getElement().getAttributeNode(XMLMergerContext.KEYS_ATTRIBUTE_NAME);
			if (keysNode != null) {
				keys = Splitter.on(",").splitToList(keysNode.getValue());
			}
		}
		if (dominant.getElement() != null) {
			Attr keysNode = dominant.getElement().getAttributeNode(XMLMergerContext.KEYS_ATTRIBUTE_NAME);
			if (keysNode != null) {
				keys = Splitter.on(",").splitToList(keysNode.getValue());
			}
		}

		ListMultimap<XMLMergerKey,XMLMergerContext> recessiveContexts = _childContextMapper.mapChildContexts(recessive, keys);
		ListMultimap<XMLMergerKey,XMLMergerContext> dominantContexts = _childContextMapper.mapChildContexts(dominant, keys);

		Set<String> tagNamesInDominant = _getTagNames(dominantContexts);

		// Execute only if there is at least one sub-element in recessive
		if (!recessiveContexts.isEmpty()) {
			for (Entry<XMLMergerKey,XMLMergerContext> entry : recessiveContexts.entries()) {
				XMLMergerKey key = entry.getKey();
				XMLMergerContext recessiveContext = entry.getValue();

				if (key == XMLMergerKey.BEFORE_END) {
					continue;
				}

				if (XMLMergeSelf.of(recessiveContext.getElement()) == XMLMergeSelf.OVERRIDABLE_BY_TAG) {
					if (!tagNamesInDominant.contains(key.getName())) {
						recessiveContext.addAsChildTo(resultElement);
						filter.postProcess(recessiveContext.getElement(), null, recessiveContext.getElement());
					}
					continue;
				}

				if (dominantContexts.get(key).size() == 1 && recessiveContexts.get(key).size() == 1) {
					XMLMergerContext dominantContext = dominantContexts.get(key).iterator().next();

					XMLMergerContext combined = _merge(recessiveContext,dominantContext);
					if (combined != null) {
						combined.addAsChildTo(resultElement);
					}
				} else {
					recessiveContext.addAsChildTo(resultElement);
					if (recessiveContext.getElement() != null) {
						filter.postProcess(recessiveContext.getElement(), null, recessiveContext.getElement());
					}
				}
			}
		}

		for (Entry<XMLMergerKey,XMLMergerContext> entry : dominantContexts.entries()) {
			XMLMergerKey key = entry.getKey();
			XMLMergerContext dominantContext = entry.getValue();

			if (key == XMLMergerKey.BEFORE_END) {
				dominantContext.addAsChildTo(resultElement, _document);
				if (dominantContext.getElement() != null) {
					filter.postProcess(null, dominantContext.getElement(), dominantContext.getElement());
				}
				// break? this should be the last anyway...
				continue;
			}
			List<XMLMergerContext> associatedRecessives = recessiveContexts.get(key);
			if (dominantContexts.get(key).size() == 1 
			 && associatedRecessives.size() == 1
			 && XMLMergeSelf.of(associatedRecessives.get(0).getElement()) != XMLMergeSelf.OVERRIDABLE_BY_TAG) {
				// already added
			} else {
				XMLMergerContext combined = _merge(XMLMergerContext.fromElement(null),dominantContext);
				if (combined != null) {
					combined.addAsChildTo(resultElement);
				}
			}
		}

		XMLMergerContext result = new XMLMergerContext();
		result.setElement(resultElement);
		_appendNeighbours(dominant, result);

		filter.postProcess(recessive.getElement(),dominant.getElement(),
						   result.getElement());

		return result;
	}
	/**
	 * Copy element recursively.
	 * @param context context to copy, it is assumed it is from unrelated document
	 * @return copied element in current document
	 */
	private XMLMergerContext _copyRecursively(final XMLMergerContext context) {
		XMLMergerContext copy = new XMLMergerContext();

		_appendNeighbours(context,copy);

		Element element = (Element) _document.importNode(context.getElement(), false);
		copy.setElement(element);

		_appendRecursively(context, copy);

		return copy;
	}
	/**
	 * Append neighbors from source to destination
	 * @param source      source element, it is assumed it is from unrelated
	 *                    document
	 * @param destination destination element
	 */
	private void _appendNeighbours(final XMLMergerContext source,final XMLMergerContext destination) {
		for (Node neighbour : source.getNeighbours()) {
			destination.addNeighbour(_document.importNode(neighbour, true));
		}
	}
	/**
	 * Appends all attributes and subelements from source element do destination
	 * element.
	 * @param source      source element, it is assumed it is from unrelated
	 *                    document
	 * @param destination destination element
	 */
	private void _appendRecursively(final XMLMergerContext source,final XMLMergerContext destination) {
		XMLUtils.copyAttributes(_document,
								source.getElement(),destination.getElement());

		List<XMLMergerContext> contexts = source.groupChildContexts();

		for (XMLMergerContext context : contexts) {
			if (context.getElement() == null) {
				context.addAsChildTo(destination.getElement(), _document);
				continue;
			}
			XMLMergerContext combined = _merge(XMLMergerContext.fromElement(null),context);
			if (combined != null) {
				combined.addAsChildTo(destination.getElement());
			}
		}
	}
	private static void _filterOutDefaults(final XMLMergerContext context) {
		Element element = context.getElement();
		List<XMLMergerContext> childContexts = context.groupChildContexts();

		for (XMLMergerContext childContext : childContexts) {
			if (childContext.getElement() == null) {
				continue;
			}
			XMLMergeSelf combineSelf = XMLMergeSelf.of(childContext.getElement());
			if (combineSelf == XMLMergeSelf.DEFAULTS) {
				for (Node neighbour : childContext.getNeighbours()) {
					element.removeChild(neighbour);
				}
				element.removeChild(childContext.getElement());
			} else {
				_filterOutDefaults(childContext);
			}
		}
	}
	private static void _filterOutCombines(final Element element) {
		element.removeAttribute(XMLMergeSelf.ATTRIBUTE_NAME);
		element.removeAttribute(XMLMergeChildren.ATTRIBUTE_NAME);
		element.removeAttribute(XMLMergerContext.KEYS_ATTRIBUTE_NAME);
		element.removeAttribute(XMLMergerContext.ID_ATTRIBUTE_NAME);

		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item instanceof Element) {
				_filterOutCombines((Element) item);
			}
		}
	}
	private static Set<String> _getTagNames(final ListMultimap<XMLMergerKey,XMLMergerContext> dominantContexts) {
		Set<String> names = new HashSet<String>();
		for (XMLMergerKey key : dominantContexts.keys()) {
			names.add(key.getName());
		}
		return names;
	}
}
