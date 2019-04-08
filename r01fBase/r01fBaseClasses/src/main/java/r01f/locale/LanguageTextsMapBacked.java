package r01f.locale;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;


/**
 * Collection of various language texts backed-up by a Map
 * <pre class='brush:java'>
 *	LanguageTexts text = LanguageTextsFactory.createMapBacked()
 *									         .addForLang(Language.BASQUE,"testu1")
 *									         .addForLang(Language.ENGLISH,"text1");
 *	String text_in_spanish = text.getFor(Language.SPANISH);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="default") 
@Accessors(prefix="_") 
public class LanguageTextsMapBacked 
     extends LanguageTextsBase<LanguageTextsMapBacked> 
  implements Map<Language,String> {
	
	private static final long serialVersionUID = -3302253934368756020L;
/////////////////////////////////////////////////////////////////////////////////////////
//	STATE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	@Getter(AccessLevel.PRIVATE) private Map<Language,String> _backEndTextsMap;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageTextsMapBacked() {
		super(LangTextNotFoundBehabior.THROW_EXCEPTION,
			  null);
		_backEndTextsMap = new LinkedHashMap<Language,String>();
	}
	public LanguageTextsMapBacked(final LangTextNotFoundBehabior langTextNotFoundBehabior) {
		super(langTextNotFoundBehabior,"--not defined--");
	}
	public LanguageTextsMapBacked(final LangTextNotFoundBehabior langTextNotFoundBehabior,final String defaultValue) {
		super(langTextNotFoundBehabior,defaultValue);
	}
	public LanguageTextsMapBacked(final int size) {
		this();
		_backEndTextsMap = new LinkedHashMap<Language,String>(size);
	}
	public LanguageTextsMapBacked(final int size,final LangTextNotFoundBehabior notFoundBehabiour) {
		this(size);
		_langTextNotFoundBehabior = notFoundBehabiour;
	}
	public LanguageTextsMapBacked(final Map<Language,String> texts) {
		this();
		if (CollectionUtils.hasData(texts)) {
			_backEndTextsMap = new LinkedHashMap<Language,String>(texts);
		}
	}
	public LanguageTextsMapBacked(final LanguageTexts other) {
		super(other.getLangTextNotFoundBehabior(),
			  other.getDefaultValue());
		Set<Language> definedLangs = other.getDefinedLanguages();
		if (CollectionUtils.hasData(definedLangs)) {
			_backEndTextsMap = new LinkedHashMap<Language,String>(definedLangs.size());
			for (Language lang : definedLangs) {
				_backEndTextsMap.put(lang,
									 other.getFor(lang));
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<Language> getDefinedLanguages() {
		return CollectionUtils.hasData(_backEndTextsMap) ? _backEndTextsMap.keySet() 
														 : Sets.<Language>newLinkedHashSet();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Map interface
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int size() {
		return _backEndTextsMap.size();
	}
	@Override
	public boolean isEmpty() {
		return _backEndTextsMap.isEmpty();
	}
	@Override
	public boolean containsKey(Object key) {
		return _backEndTextsMap.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return _backEndTextsMap.containsValue(value);
	}
	@Override
	public String get(Object key) {
		return _backEndTextsMap.get(key);
	}
	@Override
	public String put(Language key,String value) {
		_put(key,value);
		return _backEndTextsMap.put(key,value);
	}
	@Override
	public String remove(Object key) {
		return _backEndTextsMap.remove(key);
	}
	@Override
	public void putAll(Map<? extends Language,? extends String> m) {
		if (CollectionUtils.hasData(m)) {
			for (Map.Entry<? extends Language,? extends String> me : m.entrySet()) {
				_put(me.getKey(),me.getValue());
			}
		}
	}
	@Override
	public void clear() {
		if (_backEndTextsMap != null) _backEndTextsMap.clear();
	}
	@Override
	public Set<Language> keySet() {
		return _backEndTextsMap != null ? _backEndTextsMap.keySet() 
										: Sets.<Language>newHashSet();
	}
	@Override
	public Collection<String> values() {
		return _backEndTextsMap != null ? _backEndTextsMap.values()
										: Sets.<String>newHashSet();
	}
	@Override
	public Set<Map.Entry<Language,String>> entrySet() {
		return _backEndTextsMap != null ? _backEndTextsMap.entrySet()
										: Sets.<Map.Entry<Language,String>>newHashSet();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS IMPL
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _put(final Language lang,final String text) {
		if (_backEndTextsMap == null) _backEndTextsMap = Maps.newLinkedHashMap();
		_backEndTextsMap.put(lang,text);
	}
	@Override
	protected String _retrieve(final Language lang) {
		String outText = this.hasData() ? _backEndTextsMap.get(lang)
						  				: null;
		return outText;
	}
	@Override
	public Map<Language,String> asMap() {
		return _backEndTextsMap;
	}
	/**
	 * @return true if there is some data
	 */
	public boolean hasData() {
		return CollectionUtils.hasData(_backEndTextsMap);
	}
}
