package r01f.html.elements;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public abstract class HtmlElementBase 
           implements CanBeRepresentedAsString {
	private static final long serialVersionUID = -6645578980932528503L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final String _name;
	@Getter protected final Map<String,String> _attrs;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected HtmlElementBase(final String name) {
		_name = name;
		_attrs = Maps.newHashMap();
	}
	protected HtmlElementBase(final String name,
							  final Map<String,String> attrs) {
		_name = name;
		_attrs = attrs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ATTRIBUTES
/////////////////////////////////////////////////////////////////////////////////////////
	public String getAttributeValue(final String name) {
		return CollectionUtils.hasData(_attrs) ? _attrs.get(name) : null;
	}
	public boolean hasAttribute(final String name) {
		return CollectionUtils.hasData(_attrs) ? _attrs.containsKey(name) : false;
	}
	public void addAttribute(final String key,final String content) {
		_attrs.put(key,content);
	}
	public <E extends HtmlElementBase> void addAttributesFrom(final E otherEl) {
		if (otherEl == null || CollectionUtils.isNullOrEmpty(otherEl.getAttrs())) return;
		for (Map.Entry<String,String> otherBodyTagAttr : otherEl.getAttrs().entrySet()) {
			String thisVal = this.getAttributeValue(otherBodyTagAttr.getKey());
			if (thisVal != null) {
				this.addAttribute(otherBodyTagAttr.getKey(),
						   		  thisVal + " " + otherBodyTagAttr.getValue());
			} else {
				this.addAttribute(otherBodyTagAttr.getKey(),
						   		  otherBodyTagAttr.getValue());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return this.startTag(true);
	}
	public String startTag(final boolean hasBody) {
		StringBuilder outBodyTagStr = new StringBuilder();
		outBodyTagStr.append("<").append(_name);
		if (CollectionUtils.hasData(_attrs)) {
			for (Map.Entry<String,String> attr : _attrs.entrySet()) {
				if (attr.getValue() != null) {
					outBodyTagStr.append(" ")
								 .append(attr.getKey())
								 .append("='")
								 .append(attr.getValue())
								 .append("'");
				} else {
					outBodyTagStr.append(" ")
								 .append(attr.getKey());
				}
			}
		}
		if (hasBody) {
			outBodyTagStr.append(">\n");
		} else {
			outBodyTagStr.append("/>\n");
		}
		
		return outBodyTagStr.toString();
	}
	public String endTag() {
		return Strings.customized("</{}>",
								  _name);
	}
}