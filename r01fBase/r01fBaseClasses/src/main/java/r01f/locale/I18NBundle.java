package r01f.locale;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import lombok.RequiredArgsConstructor;
import r01f.guids.OID;

/**
 * Default {@link I18NBundleAccess} impl wrapping a {@link ResourceBundle}
 */
@RequiredArgsConstructor
public class I18NBundle 	
  implements I18NBundleAccess {

	private static final long serialVersionUID = -8359446748100035701L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final ResourceBundle _bundle;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean hasKey(final OID key) {
		return this.hasKey(key.asString());
	}
	@Override
	public boolean hasKey(final String key) {
		return _bundle.containsKey(key);
	}
	@Override
	public List<String> keys() {
		List<String> outKeys = new LinkedList<String>();
		Enumeration<String> en = _bundle.getKeys();
		while (en.hasMoreElements()) outKeys.add(en.nextElement());
		return outKeys;
	}
	@Override
	public String getMessage(final OID key,final Object... params) {
		return this.getMessage(key.asString(),params);
	}
	@Override
	public String getMessage(final String key,final Object... params) {
		if (key == null) throw new IllegalArgumentException("Cannot load bundle key: Missing key!");

		String outValue = _bundle.getString(key);
		return outValue == null || params == null || params.length == 0 ? outValue
																		: MessageFormat.format(outValue,params);
	}
	@Override
	public Map<String,String> getMessagesWithKeysStartingWith(final String keyPrefix) {
		if (keyPrefix == null) throw new IllegalArgumentException("Cannot load bundle key: Missing key!");
		Map<String,String> outMessages = new HashMap<String,String>();
		
		// Load the resourceBundle and iterate for every key
		Enumeration<String> keys = _bundle.getKeys();
		if (keys != null && keys.hasMoreElements()) {
			do {
				String key = keys.nextElement();
				String msg = key.startsWith(keyPrefix) ? _bundle.getString(key)
													   : null;
				if (msg != null) outMessages.put(key,msg);
			} while (keys.hasMoreElements());
		}
		return outMessages;
	}
	@Override
	public Map<String,String> getMessagesMap() {
		Map<String,String> outMessages = new HashMap<String,String>();

		// Load the resourceBundle and iterate
		Enumeration<String> keys = _bundle.getKeys();
		if (keys != null && keys.hasMoreElements()) {
			do {
				String key = keys.nextElement();
				String msg = _bundle.getString(key);
				if (msg != null) outMessages.put(key,msg);
			} while (keys.hasMoreElements());
		}
		return outMessages;
	}
}
