package r01f.xml.merge;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import r01f.xml.XMLUtils;

@Slf4j
public class XMLMergerTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void miscTest() throws SAXException, 
								  IOException,
								  ParserConfigurationException,
								  TransformerConfigurationException,TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "    </service>\n" 
				+ "    <a>a</a>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter_dominant</parameter>\n"
				+ "    </service>\n"
				+ "    <b>b</b>\n"
				+ "</config>";
		XMLMerger merger = new XMLMerger();
		
		Document recessiveDoc = XMLUtils.parse(new ByteArrayInputStream(recessive.getBytes("UTF-8")));
		Document dominiantDoc = XMLUtils.parse(new ByteArrayInputStream(dominant.getBytes("UTF-8")));
		merger.merge(recessiveDoc);
		merger.merge(dominiantDoc);
		Document result = merger.buildDocument();
		
		log.info("{}",XMLUtils.asString(result));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void identity() throws SAXException, 
								  IOException,
								  ParserConfigurationException,
								  TransformerConfigurationException,TransformerException {
		String content = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "    </service>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(content,
											  _combineWithIdKey(content,content)),
									 true);
	}
	@Test
	public void mergeChildren() throws SAXException, IOException, ParserConfigurationException,
			TransformerConfigurationException, TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result,
											  _combineWithIdKey(recessive,dominant)),
									 true);
	}
	@Test
	public void appendChildren() throws SAXException, 
										IOException, 
										ParserConfigurationException,
			TransformerConfigurationException, TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1' combine.children='append'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithIdKey(recessive,dominant)),
									 true);
	}

	@Test
	public void commentPropagation() throws SAXException, 
											IOException, 
											ParserConfigurationException,
											TransformerConfigurationException,TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <!-- Service 1 -->\n"
				+ "    <service id='1'>\n"
				+ "        <!-- This comment will be removed -->\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <!-- Service 1 with different configuration -->\n"
				+ "    <service id='1'>\n"
				+ "        <!-- Changed value -->\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "    <!-- End of configuration file -->\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <!-- Service 1 with different configuration -->\n"
				+ "    <service id='1'>\n"
				+ "        <!-- Changed value -->\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "    <!-- End of configuration file -->\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result,
											  _combineWithIdKey(recessive,dominant)),
									 true);
	}
	@Test
	public void attributes() throws SAXException, 
									IOException,
									ParserConfigurationException,
									TransformerConfigurationException,TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1' parameter='parameter' parameter2='parameter2'/>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1' parameter='other value' parameter3='parameter3'/>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='1' parameter='other value' parameter2='parameter2' parameter3='parameter3'/>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithIdKey(recessive,dominant)),
									 true);
	}
	@Test
	public void remove() throws SAXException, 
								IOException,
								ParserConfigurationException,
								TransformerConfigurationException,TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "    <service id='2' combine.self='remove'/>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1' combine.self='remove'/>\n"
				+ "    <service id='2'/>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='2'/>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result,
											  _combineWithIdKey(recessive,dominant)),
									 true);
	}
	@Test
	public void override() throws SAXException, 
								  IOException,
								  ParserConfigurationException,
								  TransformerConfigurationException,TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1' combine.self='override'>\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>other value</parameter>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithIdKey(recessive,dominant)), 
									 true);
	}
	@Test
	public void multipleChildren() throws SAXException,
										  IOException, 
										  ParserConfigurationException,
										  TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter9>parameter2</parameter9>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "    </service>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='1'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter9>parameter2</parameter9>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithIdKey(recessive,dominant)), 
									 true);
	}
	@Test
	public void defaults() throws SAXException, 
								  IOException, 
								  ParserConfigurationException, 
								  TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1' combine.self='DEFAULTS'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter9>parameter2</parameter9>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "    <service id='2' combine.self='DEFAULTS'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='2'>\n"
				+ "    </service>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='2'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result,
											  _combineWithIdKey(recessive,dominant)),
									 true);
	}
	@Test
	public void overridable() throws SAXException, 
									 IOException, 
									 ParserConfigurationException, 
									 TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='id1' combine.self='overridable'>\n"
				+ "        <test/>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='id1'>\n"
				+ "        <test/>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant2 = "\n"
				+ "<config>\n"
				+ "    <service id='id1'/>\n"
				+ "</config>";
		String dominant3 = "\n"
				+ "<config>\n"
				+ "    <service id='id2'/>\n"
				+ "</config>";
		String result3 = "\n"
				+ "<config>\n"
				+ "    <service id='id1'>\n"
				+ "        <test/>\n"
				+ "    </service>\n"
				+ "    <service id='id2'/>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithIdKey(recessive,dominant)),
									 true);
		XMLAssert.assertXMLIdentical(new Diff(dominant2,
											  _combineWithIdKey(recessive,dominant2)),
									 true);
		XMLAssert.assertXMLIdentical(new Diff(result3,
											  _combineWithIdKey(recessive,dominant3)), 
									 true);
		XMLAssert.assertXMLIdentical(new Diff(result3, 
											  _combineWithIdKey(recessive,dominant,dominant3)),
									 true);
	}
	@Test
	public void overridableByTag() throws SAXException,
										  IOException, 
										  ParserConfigurationException,
										  TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='id1' combine.self='overridable_by_tag'>\n"
				+ "        <test/>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='id1'>\n"
				+ "        <test/>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant2 = "\n"
				+ "<config>\n"
				+ "    <service id='id1'/>\n"
				+ "</config>";
		String dominant3 = "\n"
				+ "<config>\n"
				+ "    <service id='id2'/>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithIdKey(recessive,dominant)),
									 true);
		XMLAssert.assertXMLIdentical(new Diff(dominant2,
											  _combineWithIdKey(recessive,dominant2)),
									 true);
		XMLAssert.assertXMLIdentical(new Diff(dominant3,
											  _combineWithIdKey(recessive,dominant3)),
									 true);
	}
	@Test
	public void subnodes() throws SAXException,
								  IOException, 
								  ParserConfigurationException,
								  TransformerConfigurationException, 
								  TransformerException {
		String recessive = "\n"
				+ "<outer>\n"
				+ "  <inner>\n"
				+ "    content\n"
				+ "  </inner>\n"
				+ "  <inner2>\n"
				+ "    content2\n"
				+ "  </inner2>\n"
				+ "</outer>";
		String dominant = "\n"
				+ "<outer>\n"
				+ "  <inner>\n"
				+ "    content3\n"
				+ "  </inner>\n"
				+ "</outer>";
		String result = "\n"
				+ "<outer>\n"
				+ "  <inner>\n"
				+ "    content3\n"
				+ "  </inner>\n"
				+ "  <inner2>\n"
				+ "    content2\n"
				+ "  </inner2>\n"
				+ "</outer>";
		XMLAssert.assertXMLIdentical(new Diff(result,
											  _combineWithIdKey(recessive,dominant)),
									 true);

		String dominant2 = "\n"
				+ "<outer combine.children='APPEND'>\n"
				+ "  <inner>\n"
				+ "    content3\n"
				+ "  </inner>\n"
				+ "</outer>";
		String result2 = "\n"
				+ "<outer>\n"
				+ "  <inner>\n"
				+ "    content\n"
				+ "  </inner>\n"
				+ "  <inner2>\n"
				+ "    content2\n"
				+ "  </inner2>\n"
				+ "  <inner>\n"
				+ "    content3\n"
				+ "  </inner>\n"
				+ "</outer>";
		XMLAssert.assertXMLIdentical(new Diff(result2, 
											  _combineWithIdKey(recessive, 
													    		dominant2)), 
									 true);

		String dominant3 = "\n"
				+ "<outer combine.self='override'>\n"
				+ "  <inner>\n"
				+ "    content3\n"
				+ "  </inner>\n"
				+ "</outer>";
		String result3 = "\n"
				+ "<outer>\n"
				+ "  <inner>\n"
				+ "    content3\n"
				+ "  </inner>\n"
				+ "</outer>";

		XMLAssert.assertXMLIdentical(new Diff(result3, 
											  _combineWithIdKey(recessive,dominant3)),
									 true);
	}
	@Test
	public void threeDocuments() throws SAXException, 
									    IOException, 
									    ParserConfigurationException,
									    TransformerConfigurationException,TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1' combine.self='DEFAULTS'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "    </service>\n"
				+ "    <service id='2' combine.self='DEFAULTS'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "    </service>\n"
				+ "    <service id='3'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "    </service>\n"
				+ "</config>";
		String middle = "\n"
				+ "<config>\n"
				+ "    <service id='1' combine.self='DEFAULTS'>\n"
				+ "        <parameter3>parameter3</parameter3>\n"
				+ "    </service>\n"
				+ "    <service id='2' combine.self='DEFAULTS'>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "    <service id='3' combine.self='DEFAULTS'>\n"
				+ "        <parameter2>parameter</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='2'>\n"
				+ "    </service>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='2'>\n"
				+ "        <parameter>parameter</parameter>\n"
				+ "        <parameter2>parameter2</parameter2>\n"
				+ "    </service>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithIdKey(recessive,middle,dominant)), 
									 true);
	}
	@Test
	public void shouldWorkWithCustomKeys() throws IOException, 
												  SAXException,
												  ParserConfigurationException,
												  TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service name='a'>\n"
				+ "        <parameter>old value2</parameter>\n"
				+ "    </service>\n"
				+ "    <service name='b'>\n"
				+ "        <parameter>old value</parameter>\n"
				+ "    </service>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service name='b'>\n"
				+ "        <parameter>new value</parameter>\n"
				+ "    </service>\n"
				+ "    <service name='a'>\n"
				+ "        <parameter>new value2</parameter>\n"
				+ "    </service>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service name='a'>\n"
				+ "        <parameter>new value2</parameter>\n"
				+ "    </service>\n"
				+ "    <service name='b'>\n"
				+ "        <parameter>new value</parameter>\n"
				+ "    </service>\n"
				+ "</config>";

		XMLAssert.assertXMLNotEqual(result,
								    _combineWithIdKey(recessive,dominant));
		XMLAssert.assertXMLNotEqual(result,
									_combineWithKey("n",
													recessive,dominant));
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithKey("name",
													  		  recessive,dominant)), 
									 true);
	}
	@Test
	public void shouldWorkWithCustomIdAttribute2() throws IOException, 
														  SAXException, 
														  ParserConfigurationException,
														  TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <nested>\n"
				+ "        <service name='a'>\n"
				+ "            <parameter>old value2</parameter>\n"
				+ "        </service>\n"
				+ "    </nested>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <nested>\n"
				+ "        <service name='a'>\n"
				+ "            <parameter>new value</parameter>\n"
				+ "        </service>\n"
				+ "    </nested>\n"
				+ "</config>";
		String result = dominant;

		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithKey("name",
													  		  recessive,dominant)),
									 true);
	}
	@Test
	public void shouldSupportManyCustomKeys() throws IOException, 
													 SAXException, 
													 ParserConfigurationException,
													 TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <nested>\n"
				+ "        <service name='a'>\n"
				+ "            <parameter>old value2</parameter>\n"
				+ "        </service>\n"
				+ "        <service name='b' id='2'>\n"
				+ "            <parameter>old value2</parameter>\n"
				+ "        </service>\n"
				+ "    </nested>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <nested>\n"
				+ "        <service name='a' id='2'>\n"
				+ "            <parameter>new value</parameter>\n"
				+ "        </service>\n"
				+ "        <service name='b' id='2'>\n"
				+ "            <parameter>new value</parameter>\n"
				+ "        </service>\n"
				+ "    </nested>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <nested>\n"
				+ "        <service name='a'>\n"
				+ "            <parameter>old value2</parameter>\n"
				+ "        </service>\n"
				+ "        <service name='b' id='2'>\n"
				+ "            <parameter>new value</parameter>\n"
				+ "        </service>\n"
				+ "        <service name='a' id='2'>\n"
				+ "            <parameter>new value</parameter>\n"
				+ "        </service>\n"
				+ "    </nested>\n"
				+ "</config>";

		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithKeys(Lists.newArrayList("name", "id"),
													  		   recessive,dominant)),
									 true);
	}
	@Test
	public void shouldAllowToSpecifyKeys() throws IOException, 
												  SAXException, 
												  ParserConfigurationException,
												  TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service id='1'/>\n"
				+ "    <service id='2'/>\n"
				+ "    <nested combine.keys='id'>\n"
				+ "        <service id='1'/>\n"
				+ "        <service id='2'/>\n"
				+ "        <nested>\n"
				+ "            <service id='1'/>\n"
				+ "            <service id='2'/>\n"
				+ "        </nested>\n"
				+ "    </nested>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service id='1'/>\n"
				+ "    <service id='2'/>\n"
				+ "    <nested>\n"
				+ "        <service id='1'/>\n"
				+ "        <service id='2'/>\n"
				+ "        <nested combine.keys='name'>\n"
				+ "            <service id='1'/>\n"
				+ "            <service id='2'/>\n"
				+ "        </nested>\n"
				+ "    </nested>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service id='1'/>\n"
				+ "    <service id='2'/>\n"
				+ "    <nested>\n"
				+ "        <service id='1'/>\n"
				+ "        <service id='2'/>\n"
				+ "        <nested>\n"
				+ "            <service id='1'/>\n"
				+ "            <service id='2'/>\n"
				+ "            <service id='1'/>\n"
				+ "            <service id='2'/>\n"
				+ "        </nested>\n"
				+ "    </nested>\n"
				+ "    <service id='1'/>\n"
				+ "    <service id='2'/>\n"
				+ "</config>";
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithKey("", 
													  		  recessive,dominant)),
									 true);
	}
	@Test
	public void shouldAllowToSpecifyArtificialKey() throws IOException, 
														   SAXException, 
														   ParserConfigurationException,
														   TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service combine.id='1' name='a'/>\n"
				+ "    <service combine.id='2' name='b'/>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service combine.id='1' name='c'/>\n"
				+ "    <service combine.id='3' name='d'/>\n"
				+ "</config>";
		String result = "\n"
				+ "<config>\n"
				+ "    <service name='c'/>\n"
				+ "    <service name='b'/>\n"
				+ "    <service name='d'/>\n"
				+ "</config>";

		//System.out.println(combineWithKey("", recessive, dominant));
		XMLAssert.assertXMLIdentical(new Diff(result, 
											  _combineWithKey("", 
													  		  recessive,dominant)), 
									 true);
	}
	@Test
	public void shouldSupportFilters() throws SAXException, 
											  IOException, 
											  ParserConfigurationException,
											  TransformerException {
		String recessive = "\n"
				+ "<config>\n"
				+ "    <service combine.id='1' value='1'/>\n"
				+ "    <service combine.id='2' value='2'/>\n"
				+ "</config>";
		String dominant = "\n"
				+ "<config>\n"
				+ "    <service combine.id='1' value='10'/>\n"
				+ "    <service combine.id='3' value='20'/>\n"
				+ "</config>";
		String result = "\n"
				+ "<config processed='true'>\n"
				+ "    <service value='11' processed='true'/>\n"
				+ "    <service value='2' processed='true'/>\n"
				+ "    <service value='20' processed='true'/>\n"
				+ "</config>";

		XMLMerger.Filter filter = new XMLMerger.Filter() {
											@Override
											public void postProcess(final Element recessiveEl,final Element dominantEl, 
																    final Element resultEl) {
												resultEl.setAttribute("processed", "true");
												if (recessiveEl == null || dominantEl == null) {
													return;
												}
												Attr recessiveNode = recessiveEl.getAttributeNode("value");
												Attr dominantNode = dominantEl.getAttributeNode("value");
												if (recessiveNode == null || dominantNode == null) {
													return;
												}
								
												int recessiveValue = Integer.parseInt(recessiveNode.getValue());
												int dominantValue = Integer.parseInt(dominantNode.getValue());
								
												resultEl.setAttribute("value", Integer.toString(recessiveValue + dominantValue));
											}
										};
		//System.out.println(combineWithKeysAndFilter(Lists.<String>newArrayList(), filter, recessive, dominant));
		XMLAssert.assertXMLIdentical(new Diff(result,	
											  _combineWithKeysAndFilter(Lists.<String>newArrayList(),
													  					filter,
													  					recessive,dominant)),
									 true);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String _combineWithIdKey(final String... inputs) throws IOException,
																	 	   ParserConfigurationException, 
																	 	   SAXException, 
																	 	   TransformerConfigurationException,TransformerException {
		return _combineWithKey("id",
							   inputs);
	}
	private static String _combineWithKey(final String keyAttributeName,
										  final String... inputs) throws IOException,
																		 ParserConfigurationException, 
																		 SAXException, 
																		 TransformerConfigurationException,TransformerException {
		return _combineWithKeys(Lists.newArrayList(keyAttributeName), 
								inputs);
	}
	private static String _combineWithKeys(final List<String> keyAttributeNames,
										   final String... inputs) throws IOException,
																		  ParserConfigurationException, 
																		  SAXException,
																		  TransformerConfigurationException,TransformerException {
		XMLMerger merger = new XMLMerger(keyAttributeNames);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		for (String input : inputs) {
			Document document = builder.parse(new ByteArrayInputStream(input.getBytes("UTF-8")));
			merger.merge(document);
		}
		Document result = merger.buildDocument();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(result), new StreamResult(writer));
		return writer.toString();
	}
	private static String _combineWithKeysAndFilter(final List<String> keyAttributeNames,
													final XMLMerger.Filter filter,
													final String... inputs) throws IOException,
																				   ParserConfigurationException, 
																				   SAXException,
																				   TransformerConfigurationException,TransformerException {
		XMLMerger merger = new XMLMerger(keyAttributeNames);
		merger.setFilter(filter);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		for (String input : inputs) {
			Document document = builder.parse(new ByteArrayInputStream(input.getBytes("UTF-8")));
			merger.merge(document);
		}
		Document result = merger.buildDocument();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(result), new StreamResult(writer));
		return writer.toString();
	}
}
