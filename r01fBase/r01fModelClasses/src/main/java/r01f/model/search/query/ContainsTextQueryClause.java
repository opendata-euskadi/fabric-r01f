package r01f.model.search.query;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.locale.Language;
import r01f.model.metadata.FieldID;
import r01f.model.search.query.QueryClauseSerializerUtils.ContainedTextSpec;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

/**
 * Creates a text query clause wrapper
 * <pre class='brush:java'>
 * 		// A text begining with...
 * 		ContainsTextQueryClause textQry1 = ContainsTextQueryClause.forMetaData("myField")
 * 																  .in(Language.ENGLISH)
 * 														  		  .beginginWith("starting text");
 * 		// A full text search
 * 		ContainsTextQueryClause textQry2 = ContainsTextQueryClause.forMetaData("myField")
 * 																  .in(LANGUAGE.FRENCH)
 * 														  		  .fullText("starting text");
 * </pre>
 */
@MarshallType(as="containsTextClause")
@GwtIncompatible
@Accessors(prefix="_")
@NoArgsConstructor
public class ContainsTextQueryClause
     extends QueryClauseBase {

	private static final long serialVersionUID = 310700424674783299L;

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="text",escape=true,
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _text;
	
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _lang;
	
	@MarshallField(as="position",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ContainedTextAt _position;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public ContainsTextQueryClause(final FieldID fieldId,
								   final ContainedTextAt at, 
								   final String text,
								   final Language lang) {
		super(fieldId);
		_position = at;
		_text = text;
		_lang = lang;
	}
	ContainsTextQueryClause(final FieldID fieldId,
							final ContainedTextSpec containedTextSpec) {
		super(fieldId);
		_position = containedTextSpec.getPosition();
		_text = containedTextSpec.getText();
		_lang = containedTextSpec.getLang();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.CONTAINS_TEXT;
	}	
	@Override @SuppressWarnings("unchecked")
	public <V> V getValue() {
		return (V)_text;
	}
	@Override @SuppressWarnings("unchecked")
	public <V> Class<V> getValueType() {
		return (Class<V>)String.class;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isBegining() {
		return _position == ContainedTextAt.BEGINING;
	}
	public boolean isEnding() {
		return _position == ContainedTextAt.ENDING;
	}
	public boolean isContaining() {
		return _position == ContainedTextAt.CONTENT;
	}
	public boolean isFullText() {
		return _position == ContainedTextAt.FULL;
	}
	public boolean isLanguageIndependent() {
		return _lang == null;
	}
	public boolean isLanguageDependent() {
		return _lang != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static enum ContainedTextAt 
		    implements EnumExtended<ContainedTextAt> {
		BEGINING,
		CONTENT,
		ENDING,
		FULL;
		
		private static EnumExtendedWrapper<ContainedTextAt> _enums = EnumExtendedWrapper.wrapEnumExtended(ContainedTextAt.class);
		
		public static String pattern() {
			return CollectionUtils.of(ContainedTextAt.values()).toStringSeparatedWith('|');
		}
		public static ContainedTextAt fromName(final String name) {
			return _enums.fromName(name);
		}
		@Override
		public boolean isIn(final ContainedTextAt... els) {
			return _enums.isIn(this,els);
		}
		@Override
		public boolean is(final ContainedTextAt el) {
			return _enums.is(this,el);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ContainsTextQueryClause)) return false;
		
		if (!super.equals(obj)) return false;	// checks field id
		
		ContainsTextQueryClause otherContains = (ContainsTextQueryClause)obj;
		boolean textEqs = _text != null ? otherContains.getText() != null ? _text.equals(otherContains.getText())
																		  : false	
									    : true;	// both null
		boolean langEqs = _lang != null ? otherContains.getLang() != null ? _lang.equals(otherContains.getLang())
																		  : false
										: true;	// both null
		boolean positionEqs = _position != null ? otherContains.getPosition() != null ? _position.equals(otherContains.getPosition())
																					  : false
												: true;	// both null
		return textEqs && langEqs && positionEqs;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getFieldId(),
								this.getText(),
								this.getLang(),
								this.getPosition());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	private ContainsTextQueryClause(final FieldID fieldId,
							    	final String text,
							    	final Language lang,
							    	final ContainedTextAt at) {
		super(fieldId);
		_text = text;
		_lang = lang;
		_position = at;
	}
	public static ContainsTextQueryClauseStep1Builder forField(final FieldID fieldId) {
		return new ContainsTextQueryClauseStep1Builder(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainsTextQueryClauseStep1Builder {
		private final FieldID _fieldId;

		ContainsTextQueryClause forSpec(final ContainedTextSpec spec) {
			return new ContainsTextQueryClause(_fieldId,
										       spec.getPosition(),
										       spec.getText(),
										       spec.getLang());
		}
		public ContainsTextQueryClauseTextStepBuilder at(final ContainedTextAt position) {
			return new ContainsTextQueryClauseTextStepBuilder(_fieldId,
														      position);
		}
		public ContainsTextQueryClauseStep2Builder fullText(final String text) {
			return new ContainsTextQueryClauseStep2Builder(_fieldId,
											   			   text,
											   			   ContainedTextAt.FULL);
		}
		public ContainsTextQueryClauseStep2Builder beginingWith(final String text) {
			return new ContainsTextQueryClauseStep2Builder(_fieldId,
											   			   text,
											   			   ContainedTextAt.BEGINING);
		}
		public ContainsTextQueryClauseStep2Builder containing(final String text) {
			return new ContainsTextQueryClauseStep2Builder(_fieldId,
											   			   text,
											   			   ContainedTextAt.CONTENT);
		}
		public ContainsTextQueryClauseStep2Builder endingWith(final String text) {
			return new ContainsTextQueryClauseStep2Builder(_fieldId,
											   			   text,
											   			   ContainedTextAt.ENDING);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainsTextQueryClauseTextStepBuilder {
		private final FieldID _fieldId;
		private final ContainedTextAt _position;
		
		public ContainsTextQueryClauseStep2Builder text(final String text) {
			return new ContainsTextQueryClauseStep2Builder(_fieldId,
											   			   text,
											   			   _position);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ContainsTextQueryClauseStep2Builder {
		private final FieldID _fieldId;
		private final String _text;
		private final ContainedTextAt _position;
		
		public ContainsTextQueryClause in(final Language lang) {
			return new ContainsTextQueryClause(_fieldId,
											   _text,
											   lang,
											   _position);
		}
		public ContainsTextQueryClause languageIndependent() {
			return this.in(null);	// no language	
		}

	}
}
