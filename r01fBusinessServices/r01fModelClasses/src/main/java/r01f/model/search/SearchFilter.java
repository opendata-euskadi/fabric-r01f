package r01f.model.search;

import r01f.locale.Language;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;

public interface SearchFilter 
	     extends SearchModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the filter as query clauses
	 */
	public BooleanQueryClause getBooleanQuery();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Encodes the search criteria in a String to be added in an url like:
     * 		filter={filterCriteria}
     * where filterCriteria is the encoded search criteria returned by this method which 
     * is composed of clauses like metaDataId.(MUST|MUST_NOT|SHOULD...).operator(value) {dataType}
     * ie:
     * 		r01u.content.typo.type.MUST.beWithin(ayuda_subvencion) {r01m.model.oids.R01MTypoOIDs$R01MTypoTypeID}
     * 		r01.language.MUST.beEqualTo(SPANISH) {r01f.locale.Language};procedureCollection.MUST.beEqualTo(0) {java.lang.String}
     * 		r01u.content.catalog.structureLabels.SHOULD.beWithin(r01epd0122e4ed314423e0db04c97a47b5baa317f,r01epd0122e4edf39923e0db0b11fff216b637726) {r01m.model.oids.R01MStructuresOIDs$R01MStructureLabelOID}
     * 		r01u.content.typo.family.MUST.beWithin(procedimientos_administrativos) {r01m.model.oids.R01MTypoOIDs$R01MTypoFamilyID}
     * 		procedureStatus.MUST.beEqualTo(16) {java.lang.String}
     * 		r01u.content.typo.cluster.MUST.beWithin(myCluster) {r01m.model.oids.R01MTypoOIDs$R01MTypoClusterID}	
     * @return
     */
	public SearchFilterAsCriteriaString toCriteriaString();
/////////////////////////////////////////////////////////////////////////////////////////
//  UILanguage
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the language of the user interface 
	 * @return
	 */
	public Language getUILanguage();
	/**
	 * Sets the language of the user interface
	 * @param uiLang
	 */
	public void setUILanguage(final Language uiLang);
/////////////////////////////////////////////////////////////////////////////////////////
//  TEXT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if a text filter clause was set
	 */
	public boolean hasTextFilter();
	/**
	 * Gets the full text search clause
	 * @return
	 */
	public ContainsTextQueryClause getTextFilter();
	
}
