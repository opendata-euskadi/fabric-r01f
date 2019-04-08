package r01f.types.summary;

import java.io.Reader;

import com.google.common.annotations.GwtIncompatible;


/**
 * Interface for summarizable objects, that is, objects for which a text summary can be obtained and used
 * to be indexed and full-text searched against
 */
public interface LangIndependentSummary 
	     extends Summary {
	/**
	 * Sets the summary
	 * @param summary
	 */
	public void setSummary(String summary);
	/**
	 * Gets the summary as a {@link Reader}
	 * @return the summary as a {@link Reader}
	 */
	@GwtIncompatible("GWT does not supports IO")
	public Reader asReader();
	/**
	 * @return true if the summary has no data
	 */
	public boolean isEmpty();
	/**
	 * @return true if the summary has data
	 */
	public boolean hasData();
}
