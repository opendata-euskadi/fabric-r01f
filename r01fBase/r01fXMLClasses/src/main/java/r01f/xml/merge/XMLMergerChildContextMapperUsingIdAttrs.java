package r01f.xml.merge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import r01f.xml.merge.XMLMerger.XMLMergerChildContextsMapper;

     class XMLMergerChildContextMapperUsingIdAttrs 
implements XMLMergerChildContextsMapper {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////    	 
	@Override
	public ListMultimap<XMLMergerKey,XMLMergerContext> mapChildContexts(final XMLMergerContext parent,
																		final List<String> keyAttributeNames) {
		
		List<XMLMergerContext> contexts = parent.groupChildContexts();

		ListMultimap<XMLMergerKey,XMLMergerContext> map = LinkedListMultimap.create();
		for (XMLMergerContext context : contexts) {
			Element contextElement = context.getElement();

			if (contextElement != null) {
				Map<String,String> keys = new HashMap<String,String>();
				
				// find id attribute				
				for (String keyAttributeName : keyAttributeNames) {
					Attr keyNode = contextElement.getAttributeNode(keyAttributeName);
					if (keyNode != null) {
						keys.put(keyAttributeName, keyNode.getValue());
					}
				}
				// find 'combine.id' attribute value
				{
					Attr keyNode = contextElement.getAttributeNode(XMLMergerContext.ID_ATTRIBUTE_NAME);
					if (keyNode != null) {
						keys.put(XMLMergerContext.ID_ATTRIBUTE_NAME,keyNode.getValue());
					}
				}
				
				// Create the key with all id attributes
				XMLMergerKey key = new XMLMergerKey(contextElement.getTagName(),keys);
				map.put(key, context);
			} else {
				map.put(XMLMergerKey.BEFORE_END, context);
			}
		}
		return map;
	}
}
