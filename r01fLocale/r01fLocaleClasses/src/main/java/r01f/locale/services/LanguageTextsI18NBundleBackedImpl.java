package r01f.locale.services;

import java.util.Map;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.bundles.ResourceBundleControl;
import r01f.exceptions.Throwables;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsBase;
import r01f.locale.LanguageTextsI18NBundleBacked;
import r01f.locale.services.I18NServiceBuilder.I18NServiceBuilderBundleChainStep;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.xmlproperties.XMLPropertyLocation;

/**
 * Collection of various language texts backed-up by a ResourceBoundle
 * <pre class='brush:java'>
 * 	XMLProperties xmlProperties = new XMLProperties();	// IMPORTANT!! XMLProperties is usually a singleton managed by guice or an static var
 *	LanguageTexts text = LanguageTextsFactory.createBundleBacked()
 *											 .forBundle("myBundle")
 *											 .loadedAsDefinedAt(xmlProperties,
 *															    AppCode.forId("r01fb"),AppComponent.forId("test"),Path.of("/resourcesLoader[@id='myResourcesLoader']"))
 *											 .forKey("myMessageKey");
 *	String text_in_spanish = text.getFor(Language.SPANISH);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="bundle")
@GwtIncompatible
@Slf4j
@Accessors(prefix="_")
public class LanguageTextsI18NBundleBackedImpl 
     extends LanguageTextsBase<LanguageTextsI18NBundleBackedImpl>
  implements LanguageTextsI18NBundleBacked {

	private static final long serialVersionUID = -5152862690732303091L;
/////////////////////////////////////////////////////////////////////////////////////////
//  NON-PERSISTENT FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private transient I18NService _service;
	
	@MarshallIgnoredField
	private transient I18NServiceBuilderBundleChainStep _i18nServiceFactory;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="resourcesLoaderDefLocationInProperties")
	@Getter @Setter private XMLPropertyLocation _resourcesLoaderDefLocationInProperties;
	
	@MarshallField(as="bundleChain",
				  whenXml=@MarshallFieldAsXml(collectionElementName="link"))
	@Getter @Setter private String[] _bundleChain;
	
	@MarshallField(as="messageKey",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _messageKey;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	LanguageTextsI18NBundleBackedImpl() {
		super(LangTextNotFoundBehabior.THROW_EXCEPTION,
			  null);
	}
	LanguageTextsI18NBundleBackedImpl(final ResourceBundleControl resourceBundleControl) {
		this();
		_i18nServiceFactory = I18NServiceBuilder.createUsing(resourceBundleControl);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public LanguageTexts forKey(final String messageKey) {
		_messageKey = messageKey;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS IMPL
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _put(final Language lang,final String text) {
		throw new UnsupportedOperationException(Throwables.message("{} does not support addition of language strings",LanguageTextsI18NBundleBackedImpl.class.getName()));
	}	
	@Override
	protected String _retrieve(final Language lang) {
		if (_messageKey == null) throw new IllegalStateException("The bundle message key has not been setted! Call forKey(messageKey) before!");
		if (_service == null) {
			String[] theBundleChain = _bundleChain == null || _bundleChain.length == 0 ? new String[] {"default"}
																			      	   : _bundleChain;
			_service = _i18nServiceFactory.forBundleChain(theBundleChain)
										  .usingDefaultClassLoader();
		}
		String outMessage = _service.forLanguage(lang)
									.message(_messageKey);
		return outMessage;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDEN METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<Language> getDefinedLanguages() {
		log.warn("is not possible to say beforehand the defined languages when {} is backed by an I18N resource bundle",
				 LanguageTexts.class.getName());
		return null;
	}
	@Override
	public Map<Language,String> asMap() {
		log.warn("is not possible to say beforehand the defined languages when {} is backed by an I18N resource bundle",
				 LanguageTexts.class.getName());
		return null;
	}
}
