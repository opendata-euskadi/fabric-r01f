package r01f.xml.merge;

import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Element name and the value of it's 'id' attribute if exists.
 */
@Accessors(prefix="_")
class XMLMergerKey {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final XMLMergerKey BEFORE_END = new XMLMergerKey("",null);
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final String _name;
	@Getter private final Map<String,String> _keys;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLMergerKey(final String name,final Map<String,String> keys) {
		_name = name;
		_keys = keys;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Map<String,String> getId() {
		return _keys;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		int hash = 1;
		if (_name != null) {
			hash += _name.hashCode();
		}
		if (_keys != null) {
			hash = hash * 37 + _keys.hashCode();
		}
		return hash;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final XMLMergerKey other = (XMLMergerKey)obj;
		if ((_name == null) ? (other.getName() != null) : !_name.equals(other.getName())) {
			return false;
		}
		if ((_keys == null) ? (other.getId() != null) : !_keys.equals(other.getId())) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		if (_keys != null) {
			return _name + "#" + _keys;
		} else {
			return _name;
		}
	}

}
