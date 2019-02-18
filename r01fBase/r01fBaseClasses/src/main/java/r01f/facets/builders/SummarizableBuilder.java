package r01f.facets.builders;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.facets.FullTextSummarizable;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.Summarizable.ImmutableSummarizable;
import r01f.patterns.IsBuilder;
import r01f.types.summary.Summary;

/**
 * Used to create {@link Summarizable} objects
 * <pre class='brush:java'>
 * 		Summarizable summarizable = SummarizableBuklder.summarizableFrom(summary);
 *		Summarizable fullTextSummarizable = SummarizableBuilder.fullTextSummarizableFrom(summarizable);
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SummarizableBuilder 
	       implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////		
	public static Summarizable summarizableFrom(final Summary summary) {
		return new ImmutableSummarizable() {
						@Override
						public Summary getSummary() {
							return summary;
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static FullTextSummarizable fullTextSummarizableFrom(final Summary summary) {
		return new FullTextSummarizable() {
						@Override
						public Summary getFullTextSummary() {
							return summary;
						}
			   };
	}
	public static FullTextSummarizable fullTextSummarizableFrom(final Summarizable summarizable) {
		return SummarizableBuilder.fullTextSummarizableFrom(summarizable.getSummary());
	}
	public static FullTextSummarizable fullTextSummarizableFrom(final HasSummaryFacet hasSummaryFacet) {
		return SummarizableBuilder.fullTextSummarizableFrom(hasSummaryFacet.asSummarizable());
	}
	
}
