package r01f.facets;

import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.types.summary.Summary;

/**
 * Usage:
 * <pre class='brush:java'>
 * 		Summarizable outSummarizable = new ImmutableSummarizable(this.getClass()) {
 * 												@Override
 * 												public Summary getSummary() {
 *													// use SummaryBuilder to build a summary
 *												}
 * 									   }
 * </pre>
 */
public interface Summarizable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Interface to be implemented by {@link IndexableModelObject}s that can be summarized to
	 * be full-text indexed
	 */
	public static interface HasSummaryFacet
			 		extends Facet {
		/**
		 * @return the full text summary
		 */
		public Summarizable asSummarizable();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the summary
	 * @return
	 */
	public Summary getSummary();
	/**
	 * Sets the summary
	 * @param summary
	 */
	public void setSummary(final Summary summary);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@RequiredArgsConstructor
	public static abstract class ImmutableSummarizable
				      implements Summarizable {
		@Override
		public void setSummary(Summary summary) {
			throw new UnsupportedOperationException("Cannot set summary!");
		}

	}
}
