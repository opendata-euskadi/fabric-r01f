package r01f.model.search.query;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.search.query.ContainsTextQueryClause.ContainedTextAt;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.types.TypeInstanceFromString;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible
@Slf4j
class QueryClauseSerializerUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> T instanceFromString(final String text,
										   final Class<T> dataType) {
		T outValue = TypeInstanceFromString.instanceFrom(text,
													  	 dataType,
														 // If it's NOt a "normal" type, try to use a string-based constructor... maybe a static valueOf(string) method should be also tried													  	 
													  	 new Function<String,T>() {
																@Override
																public T apply(final String input) {
																	T value = ReflectionUtils.<T>createInstanceOf(dataType,
																											      new Class<?>[] {String.class},
																											      new Object[] {input});
																	return value;
																}			
													  	 });
		return outValue;
	}
	public static <T> Set<T> spectrumFromString(final String text,
										     	final Class<T> dataType) {
		return (Set<T>)TypeInstanceFromString.collectionFrom(text,
													 		 dataType,
													 		 Set.class);
	}
	public static <T> T[] spectrumArrayFromString(final String text,
										     	  final Class<T> dataType) {
		return TypeInstanceFromString.arrayFrom(text,
											 	dataType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static enum STRING_ESCAPE {
		NONE,
		XML;
	}
	public static String serializeValue(final QueryClause clause,
										final STRING_ESCAPE stringEscape) {
		String outSerializedValue = null;
		
		// equals
		if (clause instanceof EqualsQueryClause) {
			EqualsQueryClause<?> eqQry = (EqualsQueryClause<?>)clause;
			outSerializedValue = _serialize(eqQry.getEqValue(),
											stringEscape);
			
		} 
		// contains text
		else if (clause instanceof ContainsTextQueryClause) {
			ContainsTextQueryClause containsTextQry = (ContainsTextQueryClause)clause;
			ContainedTextSpec containedTextSpec = new ContainedTextSpec(containsTextQry.getText(),
																		containsTextQry.getLang(),
																		containsTextQry.getPosition());
			outSerializedValue = _serialize(containedTextSpec.toSerializedFormat(),
											stringEscape);
		} 
		// contained in
		else if (clause instanceof ContainedInQueryClause) {
			ContainedInQueryClause<?> containedInQry = (ContainedInQueryClause<?>)clause;
			outSerializedValue = _serialize(containedInQry.getSpectrum(),STRING_ESCAPE.NONE);			
		} 
		// range
		else if (clause instanceof RangeQueryClause) {
			RangeQueryClause<?> rangeQry = (RangeQueryClause<?>)clause;
			outSerializedValue = _serialize(rangeQry.getRange(),STRING_ESCAPE.NONE);
		}
		// has data
		else if (clause instanceof HasDataQueryClause) {
			throw new IllegalArgumentException(Throwables.message("{} do not have values",HasDataQueryClause.class.getSimpleName()));
		}
		return outSerializedValue;
	}
	@SuppressWarnings({ "cast","unchecked" })
	private static <T> String _serialize(final T object,
										 final STRING_ESCAPE stringEscape) {
		String outSerializedValue = null;
		
		if (object.getClass().isArray()) {	// Array of objects (contained in)
			if (((Object[])object).getClass().getComponentType() == Date.class) {
				Collection<Date> col = (Collection<Date>)CollectionUtils.of((Date[])object).asCollection();
		    	StringBuilder sb = new StringBuilder();
		    	for (Iterator<Date> it = col.iterator(); it.hasNext(); ) {
		    		sb.append(Dates.asMillis(it.next()));
		    		if (it.hasNext()) sb.append(",");
		    	}
		    	outSerializedValue = sb.toString();
			} else {
				outSerializedValue = CollectionUtils.of((Object[])object).toStringCommaSeparated();
			}
		}
		else if (object instanceof Date) {
			outSerializedValue = Long.toString(Dates.asMillis((Date)object));
		} 
		else if (object instanceof Range) {
			outSerializedValue = object.toString();
		} 
		else if (object instanceof com.google.common.collect.Range) {
			@SuppressWarnings("rawtypes")
			Range<?> r01fRange = new Range((com.google.common.collect.Range<?>)object);
			outSerializedValue = r01fRange.toString();
			System.out.println("----->" + outSerializedValue);
		}
		else if (ReflectionUtils.isSubClassOf(object.getClass(),Number.class)) {
			outSerializedValue = object.toString();
		} 
		else if (ReflectionUtils.isSubClassOf(object.getClass(),OID.class)) {
			outSerializedValue = object.toString();
		} 
		else if (ReflectionUtils.isSubClassOf(object.getClass(),Enum.class)) {
			outSerializedValue = object.toString();
		} 
		else {
			String template = stringEscape == STRING_ESCAPE.XML ? "<![CDATA[{}]]>" : "{}";
			outSerializedValue = Strings.customized(template,
													object.toString());
		}
		return outSerializedValue;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONTAINS TEXT
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	static class ContainedTextSpec {
		private static final Pattern CONTAINED_TEXT_LANGDEP_SPEC_POSITION = Pattern.compile("([^:]+) IN (" + Language.pattern() + ") AT (" + ContainedTextAt.pattern() + ")");
		private static final Pattern CONTAINED_TEXT_LANGINDEP_SPEC_POSITION = Pattern.compile("([^:]+) AT (" + ContainedTextAt.pattern() + ")");
		
		@Getter @Setter(AccessLevel.PROTECTED) private String _text;
		@Getter @Setter(AccessLevel.PROTECTED) private Language _lang;
		@Getter @Setter(AccessLevel.PROTECTED) private ContainedTextAt _position;
		
		public static ContainedTextSpec fromSerializedFormat(final String textSpecStr) {
			ContainedTextSpec outSpec = new ContainedTextSpec();
			
			// Try to match language dependant text
			Matcher m = CONTAINED_TEXT_LANGDEP_SPEC_POSITION.matcher(textSpecStr);
			if (m.find()) {
				outSpec.setText(m.group(1));
				outSpec.setLang(Language.fromName(m.group(2)));
				outSpec.setPosition(ContainedTextAt.fromName(m.group(3)));
			} else {
				m = CONTAINED_TEXT_LANGINDEP_SPEC_POSITION.matcher(textSpecStr);
				if (m.find()) {
					outSpec.setText(m.group(1));
					outSpec.setPosition(ContainedTextAt.fromName(m.group(2)));					
				} else {
					log.warn("The contained text spec '{}' does NOT match either the language dependant pattern {} neither the language independant one {}",
							 textSpecStr,CONTAINED_TEXT_LANGDEP_SPEC_POSITION.pattern(),CONTAINED_TEXT_LANGINDEP_SPEC_POSITION.pattern());
					outSpec.setText(textSpecStr);
					outSpec.setLang(null);
					outSpec.setPosition(ContainedTextAt.FULL);
				}
			}
			return outSpec;
		}
		public String toSerializedFormat() {
			return _lang != null ? Strings.customized("{} IN {} AT {}",
									  				  _text,_lang,_position)
								 : Strings.customized("{} AT {}",
										 			  _text,_position);
		}
	}
}
