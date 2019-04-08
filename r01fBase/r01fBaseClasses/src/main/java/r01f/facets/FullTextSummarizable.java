package r01f.facets;

import r01f.types.summary.Summary;

/**
 * Usage:
 * <pre class='brush:java'>
 * 		FullTextSummarizable summarizable = new FullTextSummarizable() {
 * 												@Override
 * 												public Summary getFullTextSummary() {
 * 													// use SummaryBuilder to build the full text summary	
 * 												}
 * 											}
 * </pre>
 */
public interface FullTextSummarizable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Interface to be implemented by {@link IndexableModelObject}s that can be summarized to
	 * be full-text indexed
	 */
	public static interface HasFullTextSummaryFacet
			 		extends Facet {
		/**
		 * @return the full text summary
		 */
		public FullTextSummarizable asFullTextSummarizable();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the full text summary
	 */
	public Summary getFullTextSummary();
}
