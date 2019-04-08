package r01f.model;

import java.io.Serializable;

import lombok.RequiredArgsConstructor;
import r01f.facets.HasOID;
import r01f.facets.LangDependentNamed;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.guids.OID;
import r01f.locale.LanguageTexts;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryHasLanguageDependentNameBacked;
import r01f.types.summary.SummaryHasLanguageIndependentNameBacked;
import r01f.types.summary.SummaryLanguageTextsBacked;
import r01f.types.summary.SummaryStringBacked;

/**
 * Models a resume about a model object that includes the oid and a summary
 * @param <O>
 */
@RequiredArgsConstructor
public class ModelObjectSummary
  implements Serializable {

	private static final long serialVersionUID = 4320627553035814416L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The model object oid
	 */
	private final OID _oid;	
	/**
	 * The model object summary 
	 */
	private final Summary _summary;
			
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link ModelObjectSummary} from a summary
	 * @param oid
	 * @param summary
	 * @return
	 */
	public static <O extends OID,S extends Summary> ModelObjectSummary create(final O oid,final S summary) {
		return new ModelObjectSummary(oid,summary);
	}
	/**
	 * Creates a {@link ModelObjectSummary} from a text
	 * @param oid
	 * @param text
	 * @return
	 */
	public static <O extends OID> ModelObjectSummary create(final O oid,final String text) {
		return new ModelObjectSummary(oid,
									  SummaryStringBacked.of(text));
	}
	/**
	 * Creates a {@link ModelObjectSummary} from a {@link LanguageTexts}
	 * @param oid
	 * @param langTexts
	 * @return
	 */
	public static <O extends OID> ModelObjectSummary create(final O oid,final LanguageTexts langTexts) {
		return new ModelObjectSummary(oid,
									  SummaryLanguageTextsBacked.of(langTexts));
	}
	/**
	 * Creates a {@link ModelObjectSummary} from a {@link LangDependentNamed} model object
	 * @param oid
	 * @param hasNames
	 * @return
	 */
	public static <O extends OID,M extends HasOID<O> & HasLangDependentNamedFacet> ModelObjectSummary createForLangDependantNamedModelObj(final M hasNames) {
		return new ModelObjectSummary(hasNames.getOid(),
									  SummaryHasLanguageDependentNameBacked.of(hasNames));
	}
	/**
	 * Creates a {@link ModelObjectSummary} from a {@link LangDependentNamed} model object
	 * @param oid
	 * @param hasNames
	 * @return
	 */
	public static <O extends OID,M extends HasOID<O> & HasLangInDependentNamedFacet> ModelObjectSummary createForLangIndependantNamedModelObj(final M hasNames) {
		return new ModelObjectSummary(hasNames.getOid(),
									  SummaryHasLanguageIndependentNameBacked.of(hasNames));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <O extends OID> O getOid() {
		return (O)_oid;
	}
	public Summary getSummary() {
		return _summary;
	}
}
