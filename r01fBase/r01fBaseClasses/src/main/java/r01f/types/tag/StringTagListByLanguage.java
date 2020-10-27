package r01f.types.tag;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="tagsByLanguage")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class StringTagListByLanguage 
	 extends ArrayList<StringTagListInLanguage> {

	private static final long serialVersionUID = -154523500991790856L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public StringTagListByLanguage() {
		// default no-args constructor
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD
/////////////////////////////////////////////////////////////////////////////////////////
	public StringTagListByLanguage addInLang(final Language lang,
											 final StringTagList tags) {
		if (CollectionUtils.isNullOrEmpty(tags)) return this;
		_addInLang(lang,
				   tags);
		return this;
	}
	public StringTagListByLanguage addInLang(final Language lang,
											 final String... tags) {
		if (CollectionUtils.isNullOrEmpty(tags)) return this;
		_addInLang(lang,
				   Lists.newArrayList(tags));
		return this;
	}
	public StringTagListByLanguage addInLang(final Language lang,
											 final Collection<String> tags) {
		if (CollectionUtils.isNullOrEmpty(tags)) return this;
		_addInLang(lang,
				   tags);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	public StringTagList getIn(final Language lang) {
		StringTagListInLanguage inLang = _getIn(lang);
		return inLang != null ? inLang.getTags()
							  : null;
	}
	private StringTagListInLanguage _getIn(final Language lang) {
		return this.stream()
				   .filter(inLang -> inLang.getLang().is(lang))
				   .findFirst().orElse(null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean removeIn(final Language lang) {
		StringTagListInLanguage inLang = _getIn(lang);
		return inLang != null ? this.remove(inLang) : false;
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private void _addInLang(final Language lang,
							final Collection<String> tags) {
		if (CollectionUtils.isNullOrEmpty(tags)) return;
		StringTagListInLanguage inLang = _getIn(lang);
		if (inLang != null) {
			inLang.addTags(tags);
		} else {
			inLang = new StringTagListInLanguage(lang,tags);
			this.add(inLang);
		}
	}
}
