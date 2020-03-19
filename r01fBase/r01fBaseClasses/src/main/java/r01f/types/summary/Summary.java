package r01f.types.summary;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.types.CanBeRepresentedAsString;


/**
 * Interface for summarizable objects, that is, objects for which a text summary can be obtained and used
 * to be indexed and full-text searched against
 * The {@link Summary} can be:
 * <ul>
 * 		<li> {@link LangDependentSummary} for language-dependent summaries </li>
 * 		<li> {@link LangIndependentSummary} for language-INdependant summaries </li>
 * </ul>
 */
@MarshallPolymorphicTypeInfo
public interface Summary
	     extends CanBeRepresentedAsString {
	/**
	 * @return true if this a full text summary intended to be indexed
	 */
	public boolean isFullTextSummary();
	/**
	 * @return true if the summary depends on the language
	 */
	public boolean isLangDependent();
	/**
	 * @return true if the summary do not depend on the language
	 */
	public boolean isLangIndependent();
	/**
	 * @return the {@link Summary} as a {@link LangDependentSummary}
	 */
	public LangDependentSummary asLangDependent();
	/**
	 * @return the {@link Summary} as a {@link LangIndependentSummary}
	 */
	public LangIndependentSummary asLangIndependent();
}
