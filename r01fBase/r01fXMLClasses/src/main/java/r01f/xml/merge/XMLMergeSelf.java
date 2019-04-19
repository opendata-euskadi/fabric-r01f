package r01f.xml.merge;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

enum XMLMergeSelf {
	/**
	 * Merge elements.
	 * Attributes from dominant element override those from recessive element.
	 * Child elements are by default paired using their keys (tag name and selected attributes) and combined
	 * recursively. The exact behavior depends on {@link CombineChildren 'combine.children'} attribute value.
	 * 
	 * Example:<br/>
	 * First:                                              	 Second:    
	 *          <config>                                   	          <config>
	 *             <service id="1">                        	             <service id="1">
	 *                 <parameter>parameter</parameter>    	                 <parameter2>parameter</parameter2>
	 *             </service>                              	             </service>
	 *          </config>                                  	          </config>
	 * Result:
	 *          <config>
	 *             <service id="1">
	 *                 <parameter>parameter</parameter>
	 *                 <parameter2>parameter</parameter2>
	 *             </service>
	 *          </config>
	 */
	MERGE,

	/**
	 * Remove entire element with attributes and children.
	 * Example:<br/>
	 * First:                                               Second:                                                                                           
	 *         <config>                                             <config>                                   
	 *            <service id="1">                                     <service id="1" combine.self="REMOVE"/> 
	 *                <parameter>parameter</parameter>              </config>                                  
	 *            </service>                                                                             
	 *         </config>
	 * Result:
	 *  	<config>
	 *  	</config>
	 */
	REMOVE,

	/**
	 * Behaves exactly like {@link #MERGE} if paired element exists in any subsequent dominant document.
	 * If paired element is not found in any dominant document behaves the same as {@link #REMOVE}
	 * This behavior is specifically designed to allow specifying default values which are used only
	 * when given element exists in any subsequent document.
	 * Example:<br/>
	 * First:                                                 Second:                       
	 *        <config>                                               <config>               
	 *           <service id="1" combine.self="DEFAULTS">                <service id="1">   
	 *               <parameter>parameter</parameter>                    </service>         
	 *           </service>                                           </config>             
	 *           <service id="2" combine.self="DEFAULTS"/>                            
	 *        </config>
	 * Result:
	 *        <config>
	 *            <service id="1">
	 *                <parameter>parameter</parameter>
	 *            </service>
	 *         </config>
	 */
	DEFAULTS,

	/**
	 * Override element.
	 * Completely ignores content from recessive document by overwriting it
	 * with element from dominant document.
	 * Example:
	 * First:                                                Second
	 *        <config>                                             <config>                                      
	 *           <service id="1">                                      <service id="1" combine.self="override">  
	 *               <parameter>parameter</parameter>                      <parameter2>parameter2</parameter2>   
	 *           </service>                                            </service>                                
	 *        </config>                                            </config>                                    
	 * Result:
	 *        <config>
	 *            <service id="1">
	 *                <parameter2>parameter2</parameter2>
	 *            </service>
	 *        </config>
	 */
	OVERRIDE,

	/**
	 * Override element.
	 * Completely ignores content from recessive document by overwriting it
	 * with element from dominant document.
	 * The difference with {@link #OVERRIDE} is that OVERRIDABLE is specified on the tag in recessive document.
	 * Example:<br/>
	 * First:                                                    Second:
	 *        <config>                                                 <config>  
	 *           <service id="id1" combine.self="OVERRIDABLE">         </config> 
	 *               <test/>
	 *           </service>
	 *        </config>
	 * Result:
	 *        <config>
	 *            <service id="id1" combine.self="OVERRIDABLE">
	 *                <test/>
	 *            </service>
	 *        </config>
	 *  
	 * Example2:<br/>
	 * First:                                                     Second:                      
	 *         <config>                                                 <config>               
	 *            <service id="id1" combine.self="OVERRIDABLE">   	     	<service id="id1"/> 
	 *                <test/>                                           </config>              
	 *            </service>
	 *         </config>
	 * Result:
	 *         <config>
	 * 	       	<service id="id1"/>
	 *         </config>
	 */
	OVERRIDABLE,

	/**
	 * Override element.
	 * Completely ignores content from recessive document by overwriting it  with element from dominant document.
	 * The difference with {@link #OVERRIDABLE} is that with OVERRIDABLE_BY_TAG recessive element is ignored even when the id is different.
	 * Example:<br/>
	 * First:                                                 		Second:                        
	 *      <config>                                                	   <config>                 
	 *         <service id="id1" combine.self="OVERRIDABLE_BY_TAG">  	     	<service id="id2"/>   
	 *             <test/>                                          	   </config>                
	 *         </service>
	 *      </config>
	 * Result:
	 *       <config>
	 * 	     	<service id="id2"/>
	 *       </config>
	 */
	OVERRIDABLE_BY_TAG;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String ATTRIBUTE_NAME = "combine.self";
	
	public static XMLMergeSelf of(final Element element) {
		XMLMergeSelf combine = null;
		if (element == null) return null;
		Attr combineAttribute = element.getAttributeNode(XMLMergeSelf.ATTRIBUTE_NAME);
		if (combineAttribute != null) {
			try {
				combine = XMLMergeSelf.valueOf(combineAttribute.getValue().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException("The attribute 'combine' of element '" + element.getTagName() + "' " +
										        "has invalid value '" + combineAttribute.getValue(),e);
			}
		}
		return combine;
	}
}
