package r01f.locale;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import r01f.guids.OID;


/**
 * An interface for I18NBundleAccess
 */
public interface I18NBundleAccess 
		 extends Serializable {
	/**
	 * Checks if there exists a message in this bundle
	 * @param key 
	 * @return 
	 */
	public boolean hasKey(final OID key);
	/**
	 * Checks if there exists a message in this bundle
	 * @param key
	 * @return
	 */
	public boolean hasKey(final String key);
	/**
	 * Returns all the keys for all message bundles
	 * @return 
	 */
	public List<String> keys();
	/**
	 * Returns a message from it's key
	 * @param key 
	 * @param params params to customize the message using {@link MessageFormat}
	 * @return 
	 * @throws ResourceBundleMissingKeyException if key is not found
	 */
	public String getMessage(final OID key,final Object... params);
	/**
	 * Returns a message from it's key
	 * @param key 
	 * @param params params to customize the message using {@link MessageFormat}
	 * @return 
	 * @throws ResourceBundleMissingKeyException if key is not found
	 */
	public String getMessage(final String key,final Object... params);
	/**
	 * Returns all messages whose keys starts with a given prefix
	 * ie: If the bundle contains the following keys:
	 * 			my.one = One
	 * 			my.two = Two
	 * 			yours.one = Your One
	 * 	   and a key is requested using:
	 * 	   <code>
	 * 			i18n.messagesWithKeysStartingWith("my")
	 *	   </code>
	 *	   messages will be returned as a {@link Map}
	 * 			my.one = One
	 * 			my.two = Two
	 * @param keyPrefix
	 * @return
	 */
	public Map<String,String> getMessagesWithKeysStartingWith(final String keyPrefix);
	/**
	 * Returns a Map with all bundles messages
	 * @return
	 */
	public Map<String,String> getMessagesMap();
}
