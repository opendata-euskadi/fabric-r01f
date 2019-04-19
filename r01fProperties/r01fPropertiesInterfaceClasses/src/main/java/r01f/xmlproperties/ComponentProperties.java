package r01f.xmlproperties;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

import r01f.guids.CommonOIDs.Environment;
import r01f.objectstreamer.Marshaller;
import r01f.resources.ResourcesLoaderDef;
import r01f.types.Path;

public interface ComponentProperties {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
     * @return true if the properties file exists; false otherwise
     */
    public boolean existsComponentPropertiesFile();
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Tries to guess the environment using these priorities:
	 * 1.- Find a system env var called R01ENV or ENV
	 * 2.- Find a xml properties' root's node's attribute called env
	 * @return the environment
	 */
	public Environment getEnvironment();
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the xml node that matches the XPath expression and contains the properties
	 */
	public Node node(final Path propXPath);
	/**
	 * @return the list of xml nodes that match the XPath expression
	 */
	public NodeList nodeList(final Path propXPath);
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Checks if a property is present a the properties
     * @param propXPath the XPath expression that provides access to the xml node that contains the property 
     * @return <code>true</code> if the property is present, <code>false</code> otherwise.
     */
    public boolean existProperty(final Path propXPath);
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Transform the child nodes into a collection of objects
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
	 * @param transformFunction
	 * @return
	 */
	public <T> Collection<T> getObjectList(final Path propXPath,
										   final Function<Node,T> transformFunction);
	/**
	 * Transform the child nodes into a collection of objects
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
	 * @param transformFunction
	 * @param defaultVal
	 * @return
	 */
	public <T> Collection<T> getObjectList(final Path propXPath,
										   final Function<Node,T> transformFunction,
										   final Collection<T> defaultVal);
    /**
     * Returns a typed property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return the typed property or <code>null</code> if it's not present.
     */
    public <T> T get(final Path propXPath);
    /**
     * Returns a typed property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param defaultValue the property default value
     * @return the typed property or the provided default value if it does NOT exists.
     */
    public <T> T get(final Path propXPath,final T defaultValue);
    /**
     * Returns a typed property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return the typed property 
     */
    public <T> T get(final Path propXPath,final XMLPropertyDefaultValueByEnv<T> valByEnv);
    /**
     * Returns a string property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return an string with the property or <code>null</code> if the propertys does NOT exists.
     */
    public String getString(final Path propXPath);
    /**
     * Returns a string property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param defaultValue the property default value.
     * @return an string with the property of the default value if the property does NOT exist
     */
    public String getString(final Path propXPath,final String defaultValue);
    /**
     * Returns a string property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return an string with the property of the default value if the property does NOT exist
     */
    public String getString(final Path propXPath,final XMLPropertyDefaultValueByEnv<String> valByEnv);
    /**
     * Returns a number property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return A number with the property or <code>null</code> if the property is NOT present
     */
    public Number getNumber(final Path propXPath);
    /**
     * Returns a number property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param default value
     * @return A number with the property or <code>null</code> if the property is NOT present
     */
    public Number getNumber(final Path propXPath,final Number defaultValue);
    /**
     * Returns a number property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return A number with the property or <code>null</code> if the property is NOT present
     */
    public Number getNumber(final Path propXPath,final XMLPropertyDefaultValueByEnv<Number> valByEnv);
    /**
     * Returns an integer property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return An integer with the property or <code>null</code> if the property is NOT present
     */
    public int getInteger(final Path propXPath);
    /**
     * Returns an integer property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param default value
     * @return An integer with the property or <code>null</code> if the property is NOT present
     */
    public int getInteger(final Path propXPath,final int defaultValue);
    /**
     * Returns an integer property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return An integer with the property or <code>null</code> if the property is NOT present
     */
    public int getInteger(final Path propXPath,final XMLPropertyDefaultValueByEnv<Integer> valByEnv);
    /**
     * Returns a long property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return A long with the property or <code>null</code> if the property is NOT present
     */
    public long getLong(final Path propXPath);
    /**
     * Returns a long property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param default value
     * @return A long with the property or <code>null</code> if the property is NOT present
     */
    public long getLong(final Path propXPath,final long defaultValue);
    /**
     * Returns a long property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return A long with the property or <code>null</code> if the property is NOT present
     */
    public long getLong(final Path propXPath,final XMLPropertyDefaultValueByEnv<Long> valByEnv);
    /**
     * Returns a double property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return A double with the property or <code>null</code> if the property is NOT present
     */
    public double getDouble(final Path propXPath);
    /**
     * Returns a double property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param default value
     * @return A double with the property or <code>null</code> if the property is NOT present
     */
    public double getDouble(final Path propXPath,final double defaultValue);
    /**
     * Returns a double property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return A double with the property or <code>null</code> if the property is NOT present
     */
    public double getDouble(final Path propXPath,final XMLPropertyDefaultValueByEnv<Double> valByEnv);
    /**
     * Returns a float property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return A float with the property or <code>null</code> if the property is NOT present
     */
    public float getFloat(final Path propXPath);
    /**
     * Returns a float property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param default value
     * @return A float with the property or <code>null</code> if the property is NOT present
     */
    public float getFloat(final Path propXPath,final float defaultValue);
    /**
     * Returns a float property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return A float with the property or <code>null</code> if the property is NOT present
     */
    public float getFloat(final Path propXPath,final XMLPropertyDefaultValueByEnv<Float> valByEnv);
    /**
     * Returns a boolean property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @return A boolean with the property or <code>null</code> if the property is NOT present
     */
    public boolean getBoolean(final Path propXPath);
    /**
     * Returns a boolean property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param default value
     * @return A boolean with the property or <code>null</code> if the property is NOT present
     */
    public boolean getBoolean(final Path propXPath,final boolean defaultValue);
    /**
     * Returns a boolean property
     * @param propXPath the XPath expression that provides access to the xml node that contains the property
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return A boolean with the property or <code>null</code> if the property is NOT present
     */
    public boolean getBoolean(final Path propXPath,final XMLPropertyDefaultValueByEnv<Boolean> valByEnv);
    /**
     * Returns a {@link Properties} containing all the values wrapped by the xml node matching the xPath expression<br>
     * If the xml node is not present, it returns null
     * ie: if the xpath matches:
     * <pre class='brush:xml'>
     * 	<myProperties>
     * 		<itemName1>value1</itemName1>
     * 		<itemName2>value2</itemName2>
     * 			...
     * 		</myProperties>
     * }
     * </pre>
     * <br>this method returns a {@link Properties} object as: {[itemName1,value1],[itemName2,value2]...}.
     * @param propXPath 
     * @return 
     */
    public Properties getProperties(final Path propXPath);
    /**
     * Returns a {@link Properties} containing all the values wrapped by the xml node matching the xPath expression<br>
     * If the xml node is not present, it returns the default value
     * ie: if the xpath matches:
     * <pre class='brush:xml'>
     * 	<myProperties>
     * 		<itemName1>value1</itemName1>
     * 		<itemName2>value2</itemName2>
     * 			...
     * 		</myProperties>
     * }
     * </pre>
     * <br>this method returns a {@link Properties} object as: {[itemName1,value1],[itemName2,value2]...}.
     * @param propXPath
     * @param defaultValue 
     * @return 
     */
    public Properties getProperties(final Path propXPath,final Properties defaultValue);
    /**
     * Returns a {@link Properties} containing all the values wrapped by the xml node matching the xPath expression<br>
     * If the xml node is not present, it returns the default value
     * ie: if the xpath matches:
     * <pre class='brush:xml'>
     * 	<myProperties>
     * 		<itemName1>value1</itemName1>
     * 		<itemName2>value2</itemName2>
     * 			...
     * 		</myProperties>
     * }
     * </pre>
     * <br>this method returns a {@link Properties} object as: {[itemName1,value1],[itemName2,value2]...}.
     * @param propXPath
     * @param valByEnv the property value by environment to be used if the property value is not found (defined) 
     * @return 
     */
    public Properties getProperties(final Path propXPath,final XMLPropertyDefaultValueByEnv<Properties> valByEnv);
    /**
     * Returns a string {@link List} containing all the values wrapped by the xml node matching the xPath expression<br>
     * If the xml node is not present, it returns null
     * ie: if the xpath matches
     * {@code
     * <myList>
     * 	<item>value1</item>
     * 	<item>value2</item>
     * 		...
     * 	</myList>
     * }
     * <br>return a List{value1,value2...}.
     * @param propXPath 
     * @return 
     */
    public List<String> getListOfStrings(final Path propXPath);
    /**
     * Returns a string {@link List} containing all the values wrapped by the xml node matching the xPath expression<br>
     * If the xml node is not present, it returns the default value
     * ie: if the xpath matches
     * {@code
     * <myList>
     * 	<item>value1</item>
     * 	<item>value2</item>
     * 		...
     * 	</myList>
     * }
     * <br>return a List{value1,value2...}.
     * @param propXPath 
     * @param defaultValue
     * @return 
     */
    public List<String> getListOfStrings(final Path propXPath,final List<String> defaultValue);
    /**
     * Returns a string {@link List} containing all the values wrapped by the xml node matching the xPath expression<br>
     * If the xml node is not present, it returns the default value
     * ie: if the xpath matches
     * {@code
     * <myList>
     * 	<item>value1</item>
     * 	<item>value2</item>
     * 		...
     * 	</myList>
     * }
     * <br>return a List{value1,value2...}.
     * @param propXPath 
     * @param valByEnv the property value by environment to be used if the property value is not found (defined)
     * @return 
     */
    public List<String> getListOfStrings(final Path propXPath,final XMLPropertyDefaultValueByEnv<List<String>> valByEnv);
    /**
     * Returns a java object from the xml wrapped by the node matching the given xPath expression
     * if the xPath does NOT match any xml node, it returns null
     * XML to java object marshalling is done by R01 marshaller
     * @param propXPath
     * @param marshaller
     * @param type
     * @return 
     */
    public <T> T getObject(final Path propXPath,
    					   final Marshaller marshaller,
    					   final Class<T> type);
    /**
     * Returns a java object from the xml wrapped by the node matching the given xPath expression
     * if the xPath does NOT match any xml node, it returns null
     * XML to java object marshalling is done by R01 marshaller
     * @param propXPath
     * @param marshaller
     * @param typeToken
     * @return 
     */
    public <T> T getObject(final Path propXPath,
    					   final Marshaller marshaller,
    					   final TypeToken<T> typeToken);
	/**
	 * Returns the property as an object transforming the node to an object
	 * using a {@link Function}
	 * @param propXPath 
	 * @param transformFuncion
	 * @return
	 */
	public <T> T getObject(final Path propXPath,
						   final Function<Node,T> transformFuncion);
	/**
	 * Returns the xml {@link Document} that backs the component's properties
	 * @return
	 */
	public Document getXMLDocument();
    /**
     * Returns the property as a resources load definition{@link ResourcesLoaderDef}
     * (the XML MUST conform the xml structure mandated by {@link ResourcesLoaderDef}).
     * @return 
     * @see ResourcesLoaderDef
     */
    public ResourcesLoaderDef getResourcesLoaderDef(final Path propXPath);
}
