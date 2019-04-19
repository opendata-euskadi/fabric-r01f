package r01f.xml.merge;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Controls the behavior of merging child elements.
 */
enum XMLMergeChildren {
	/**
	 * Merge sub-elements from both elements.
	 * This is the default.
	 * Those subelements which can be uniquely paired between two documents using the key (tag+selected attributes)
	 * will be merged, those that cannot be paired will be appended.<br/>
	 * Example:<br/>
	 * First:                                         Second:                                         
	 *      <config>                                         <config>                                 
	 *         <service id="1">                                 <service id="1"/>                     
	 *             <parameter>parameter</parameter>                 <parameter>other value</parameter>
	 *         </service>                                       </service>                            
	 *      </config>                                        </config>                                
	 * 
	 * Result:
	 *       <config>
	 *          <service id="1"/>
	 *              <parameter>other value</parameter>
	 *          </service>
	 *       </config>
	 */
	MERGE,

	/**
	 * Always append child elements from both recessive and dominant elements.
	 * Example:<br/>
	 * First:                                              Second:                                        
	 *      <config>                                              <config>                                
	 *          <service id="1" combine.children="append">            <service id="1">                    
	 *              <parameter>parameter</parameter>                      <parameter>parameter</parameter>
	 *          </service>                                            </service>                          
	 *      </config>                                             </config>                               
	 * Result:
	 *       <config>
	 *           <service id="1" combine.children="append">
	 *               <parameter>parameter</parameter>
	 *               <parameter>parameter</parameter>
	 *           </service>
	 *       </config>
	 */
	APPEND;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String ATTRIBUTE_NAME = "combine.children";
	
	public static XMLMergeChildren of(final Element element) {
		XMLMergeChildren combine = null;
		if (element == null) {
			return null;
		}
		Attr combineAttribute = element.getAttributeNode(XMLMergeChildren.ATTRIBUTE_NAME);
		if (combineAttribute != null) {
			try {
				combine = XMLMergeChildren.valueOf(combineAttribute.getValue().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException("The attribute 'combine' of element '" + element.getTagName() + "' " +
										        "has invalid value '" + combineAttribute.getValue(), e);
			}
		}
		return combine;
	}
	public static XMLMergeChildren of(final Element recessive,final Element dominant) {
		XMLMergeChildren combineChildren = XMLMergeChildren.of(dominant);
		if (combineChildren == null) {
			combineChildren = XMLMergeChildren.of(recessive);
			if (combineChildren == null) {
				combineChildren = XMLMergeChildren.MERGE;
			}
		}
		return combineChildren;
	}
}
