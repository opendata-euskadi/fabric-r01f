package r01f.types.tag;

import java.io.Serializable;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasLang;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="tagsInLanguage")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class StringTagListInLanguage 
  implements Serializable,
  			 HasLang {

	private static final long serialVersionUID = -9142125164319177774L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _lang;
	
	@MarshallField(as="tags")
	@Getter @Setter private StringTagList _tags;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public StringTagListInLanguage() {
		// default no-args constructor
	}
	public StringTagListInLanguage(final Language lang,final StringTagList tags) {
		_lang = lang;
		_tags = tags;
	}
	public StringTagListInLanguage(final Language lang,final Collection<String> tags) {
		this(lang,
			 new StringTagList(tags));
	}
	public StringTagListInLanguage(final Language lang,final String... tags) {
		this(lang,
			 new StringTagList(tags));
	}
	public StringTagListInLanguage(final Language lang) {
		this(lang,
			 new StringTagList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public StringTagListInLanguage addTag(final String tag) {
		return this.addTags(tag);
	}
	public StringTagListInLanguage addTags(final Collection<String> tags) {
		if (_tags == null) _tags = new StringTagList();
		if (CollectionUtils.hasData(tags)) _tags.addAll(tags);
		return this;
	}
	public StringTagListInLanguage addTags(final String... tags) {
		if (_tags == null) _tags = new StringTagList();
		if (CollectionUtils.hasData(tags)) _tags.addAll(tags);
		return this;
	}
	public boolean remove(final String tag) {
		return _tags != null ? _tags.remove(tag)
							 : false;
	}
	public void clear() {
		if (_tags != null) _tags.clear();
	}
}
