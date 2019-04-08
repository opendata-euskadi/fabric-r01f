package r01f.types.summary;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.summary.SummaryBases.LangIndependentSummaryBase;
import r01f.util.types.Strings;


/**
 * A simple {@link String} based {@link LangIndependentSummary}
 */
@MarshallType(as="langIndependentSummary")
@Accessors(prefix="_")
public class SummaryStringBacked
     extends LangIndependentSummaryBase {

	private static final long serialVersionUID = 6179099335861429978L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="summaryText",
				   escape=true,
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _summaryText;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public SummaryStringBacked() {
		super(false);		// not to be used as a full text summary by default
	}
	public SummaryStringBacked(final String str) {
		super(false);		// not to be used as a full text summary by default
		_summaryText = str;
	}
	public SummaryStringBacked(final String str,final Object... vars) {
		this(Strings.customized(str,vars));
	}
	public SummaryStringBacked(final boolean fullText,
							   final String str) {
		super(fullText);
		_summaryText = str;
	}
	public static SummaryStringBacked create() {
		return new SummaryStringBacked();
	}
	public static SummaryStringBacked of(final CanBeRepresentedAsString str) {
		return SummaryStringBacked.of(str.asString());
	}
	public static SummaryStringBacked of(final String str) {
		return new SummaryStringBacked(false,	// not to be used as a full text summary
									   str);
	}
	public static SummaryStringBacked of(final String str,final Object... params) {
		return new SummaryStringBacked(false,	// not to be used as a full text summary
									   Strings.customized(str,params));
	}
	public static SummaryStringBacked fullTextOf(final CanBeRepresentedAsString str) {
		return SummaryStringBacked.fullTextOf(str.asString());
	}
	public static SummaryStringBacked fullTextOf(final String str) {
		return new SummaryStringBacked(true,	// to be used as a full text summary
									   str);
	}
	public static SummaryStringBacked fullTextOf(final String str,final Object... params) {
		return new SummaryStringBacked(true,	// to be used as a full text summary
									   Strings.customized(str,params));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return _summaryText;
	}
	@Override
	public String asString() {
		return _summaryText;
	}
	@Override
	public void setSummary(final String summary) {
		_summaryText = summary;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( !(obj instanceof SummaryStringBacked) ) return false;
		SummaryStringBacked otherSumm = (SummaryStringBacked)obj;
		return _summaryText != null ? _summaryText.equals(otherSumm.getSummaryText())
									: otherSumm.getSummaryText() != null ? false
																		 : true;	
	}
	@Override
	public int hashCode() {
		return _summaryText != null ? Objects.hashCode(_summaryText)
									: super.hashCode();
	}
}
