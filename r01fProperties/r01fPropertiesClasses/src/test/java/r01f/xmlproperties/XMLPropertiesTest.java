package r01f.xmlproperties;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class XMLPropertiesTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void test() {
//		System.setProperty("R01ENV","loc");
//		try {
//			XMLPropertiesForApp testProps = XMLPropertiesBuilder.createForApp(AppCode.forId("r01f"))
//															    .notUsingCache();
//			XMLPropertiesForAppComponent testCompProps = testProps.forComponent("test");
//			
//			// Test some values
//			String attrValue = testCompProps.propertyAt("/properties/aProp/@attr")
//										   .asString();
//			Assert.assertEquals("attrValue2",attrValue);
//	
//			
//			String propValue = testCompProps.propertyAt("/properties/aProp")
//										    .asString();
//			Assert.assertEquals("propValue",propValue != null ? propValue.trim() : null);
//		} finally {
//			System.setProperty("R01ENV","");
//		}
	}
	@Test
	public void mergeTest() throws SAXException, 
								   IOException,
								   ParserConfigurationException,
								   TransformerConfigurationException,TransformerException {
//		String recessive = StringPersistenceUtils.load(Path.from("D:/develop/projects/zuzenean/tools/aa14/aa14cCORE/aa14cConfig/src/main/config/aa14b/aa14b.appointments.dbpersistence.properties.xml"));
//		String dominant = StringPersistenceUtils.load(Path.from("D:/develop/projects/zuzenean/tools/aa14Config/aa14ConfigByEnv/aa14cConfigByEnv/src/main/config/loc_win_d/aa14b/aa14b.appointments.dbpersistence.properties.xml"));
//		System.out.println(recessive);
//		System.out.println(dominant);
		
    	
//    	// [2] Load the XML file using the configured resourcesLoader and parse it
//		Document defXmlDoc = null;
//		try {
//			defXmlDoc = XMLUtils.parse(new ByteArrayInputStream(recessive.getBytes()));
//		} catch (SAXException saxEx) {
//			saxEx.printStackTrace();
//		}
//		// [3] Try to find an env-dependent XML Properties file
//		Document envXmlDoc = null;
//		try {
//			envXmlDoc = XMLUtils.parse(new ByteArrayInputStream(dominant.getBytes()));
//		} catch (SAXException saxEx) {
//			saxEx.printStackTrace();
//		}
//		// [4] Merge all files if necessary
//		Document outXml = null;
//		try {
//			XMLMerger merger = new XMLMerger();
//			merger.merge(defXmlDoc);		// recessive
//			merger.merge(envXmlDoc);	// dominant
//			outXml = merger.buildDocument();
//		} catch (ParserConfigurationException cfgEx) {
//			cfgEx.printStackTrace();
//		}
//		System.out.println(XMLUtils.asString(outXml));
	}

}
	