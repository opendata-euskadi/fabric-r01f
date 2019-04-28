package r01f.model.search.query;

import java.util.Date;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Range;
import r01f.util.types.Dates;


/**
 * Creates a {@link Range} query clause
 * If 
 * <ul>
 * 		<li>"[" or "]" represents a lower or upper bound where the bound itself is included in the range</li>
 * 		<li>"(" or ")" represents a lower or upper bound where the bound itself is EXCLUDED from the range</li>
 * <li>
 * <pre>
 * 		(a..b)		open(C, C)
 * 		[a..b]		closed(C, C)
 * 		[a..b)		closedOpen(C, C)
 * 		(a..b]		openClosed(C, C)
 * 		(a..+oo)	greaterThan(C)
 * 		[a..+oo)	atLeast(C)
 * 		(-oo..b)	lessThan(C)
 * 		(-oo..b]	atMost(C)
 * 		(-oo..+oo)	all() 
 * </pre>
 * 
 * Usage:
 * <pre class='brush:java'>
 * 		RangeQueryClause<Integer> range = RangeQueryClause.forField("myField")
 * 														  .open(2,3);
 * </pre>
 * 
 * @param <T>
 */

@Immutable	
@MarshallType(as="rangeClause")
@GwtIncompatible
@Accessors(prefix="_")
@NoArgsConstructor
public class RangeQueryClause<T extends Comparable<T>>
     extends QueryClauseBase {

	private static final long serialVersionUID = 7724662019199616500L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The range
	 */
	@MarshallField(as="range",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Range<T> _range;
	
	public com.google.common.collect.Range<T> getGuavaRange() {
		return _range.asGuavaRange();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	RangeQueryClause(final FieldID fieldId,
					 final Range<T> range) {
		super(fieldId);
		_range = range;
	}
	@SuppressWarnings("unused")
	private RangeQueryClause(final FieldID metaDataId,
							 final com.google.common.collect.Range<T> range) {
		super(metaDataId);
		_range = new Range<T>(range);
	}
	public static RangeQueryClauseBuilder forField(final FieldID fieldId) {
		return new RangeQueryClauseBuilder(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.RANGE;
	}	
	@Override @SuppressWarnings("unchecked")
	public <V> V getValue() {
		return (V)_range;
	}
	@Override @SuppressWarnings("unchecked")
	public <V> Class<V> getValueType() {
		return (Class<V>)this.getRangeType();
	}
	@SuppressWarnings("unchecked")
	public Class<T> getRangeType() {
		Class<T> boundType = (Class<T>)(_range.lowerEndpoint() != null ? _range.lowerEndpoint().getClass()
												 			 	   	   : _range.upperEndpoint().getClass());
		return boundType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof RangeQueryClause)) return false;
		
		if (!super.equals(obj)) return false;	// checks fieldId
		
		RangeQueryClause<?> otherRange = (RangeQueryClause<?>)obj;
		return _range != null ? otherRange.getRange() != null ? _range.equals(otherRange.getRange())
							    							  : false
							  : true;		// both null
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getFieldId(),
								this.getRange());	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class RangeQueryClauseBuilder {
		private final FieldID _fieldId;
		
		//  -------------------------- Dates ranges
		/**
		 * Creates a range query clause in which the lower bound is NOT included and has NO upper bound<br/>
		 * <b>(L..+oo)</b>
		 * @param lower
		 * @return
		 */
		public RangeQueryClause<Date> greaterThan(final Date lower) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.greaterThan(lower));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>[L..+oo)</b>
		 * @param lower 
		 * @return
		 */
		public RangeQueryClause<Date> atLeast(final Date lower) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.atLeast(lower));
		}
		/**
		 * Creates a range query clause in which the upper bound is NOT included and has NO lower bound<br/>
		 * <b>(-oo..U)</b>
		 * @param upper 
		 * @return
		 */
		public RangeQueryClause<Date> lessThan(final Date upper) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.lessThan(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>(-oo..U]</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Date> atMost(final Date upper) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.atMost(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included but NOT the upper one<br/>
		 * <b>[-L..U)</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Date> closedOpen(final Date lower,final Date upper) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.closedOpen(lower,upper));
		}
		/**
		 * Creates a range query clause in which the upper bound is included but NOT the lower one<br/>
		 * <b>(-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Date> openClosed(final Date lower,final Date upper) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.openClosed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are included<br/>
		 * <b>[-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Date> closed(final Date lower,final Date upper) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.closed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are NOT included<br/>
		 * <b>(-L..U)</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Date> open(final Date lower,final Date upper) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.open(lower,upper));
		}
		//  -------------------------- Integer ranges
		/**
		 * Creates a range query clause in which the lower bound is NOT included and has NO upper bound<br/>
		 * <b>(L..+oo)</b>
		 * @param lower
		 * @return
		 */
		public RangeQueryClause<Integer> greaterThan(final int lower) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.greaterThan(lower));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>[L..+oo)</b>
		 * @param lower 
		 * @return
		 */
		public RangeQueryClause<Integer> atLeast(final int lower) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.atLeast(lower));
		}
		/**
		 * Creates a range query clause in which the upper bound is NOT included and has NO lower bound<br/>
		 * <b>(-oo..U)</b>
		 * @param upper 
		 * @return
		 */
		public RangeQueryClause<Integer> lessThan(final int upper) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.lessThan(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>(-oo..U]</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Integer> atMost(final int upper) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.atMost(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included but NOT the upper one<br/>
		 * <b>[-L..U)</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Integer> closedOpen(final int lower,final int upper) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.closedOpen(lower,upper));
		}
		/**
		 * Creates a range query clause in which the upper bound is included but NOT the lower one<br/>
		 * <b>(-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Integer> openClosed(final int lower,final int upper) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.openClosed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are included<br/>
		 * <b>[-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Integer> closed(final int lower,final int upper) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.closed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are NOT included<br/>
		 * <b>(-L..U)</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Integer> open(final int lower,final int upper) {
			return new RangeQueryClause<Integer>(_fieldId,
												 Range.open(lower,upper));
		}
		//  -------------------------- Long ranges
		/**
		 * Creates a range query clause in which the lower bound is NOT included and has NO upper bound<br/>
		 * <b>(L..+oo)</b>
		 * @param lower
		 * @return
		 */
		public RangeQueryClause<Long> greaterThan(final long lower) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.greaterThan(lower));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>[L..+oo)</b>
		 * @param lower 
		 * @return
		 */
		public RangeQueryClause<Long> atLeast(final long lower) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.atLeast(lower));
		}
		/**
		 * Creates a range query clause in which the upper bound is NOT included and has NO lower bound<br/>
		 * <b>(-oo..U)</b>
		 * @param upper 
		 * @return
		 */
		public RangeQueryClause<Long> lessThan(final long upper) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.lessThan(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>(-oo..U]</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Long> atMost(final long upper) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.atMost(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included but NOT the upper one<br/>
		 * <b>[-L..U)</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Long> closedOpen(final long lower,final long upper) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.closedOpen(lower,upper));
		}
		/**
		 * Creates a range query clause in which the upper bound is included but NOT the lower one<br/>
		 * <b>(-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Long> openClosed(final long lower,final long upper) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.openClosed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are included<br/>
		 * <b>[-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Long> closed(final long lower,final long upper) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.closed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are NOT included<br/>
		 * <b>(-L..U)</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Long> open(final long lower,final long upper) {
			return new RangeQueryClause<Long>(_fieldId,
											  Range.open(lower,upper));
		}
		//  -------------------------- Double ranges
		/**
		 * Creates a range query clause in which the lower bound is NOT included and has NO upper bound<br/>
		 * <b>(L..+oo)</b>
		 * @param lower
		 * @return
		 */
		public RangeQueryClause<Double> greaterThan(final double lower) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.greaterThan(lower));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>[L..+oo)</b>
		 * @param lower 
		 * @return
		 */
		public RangeQueryClause<Double> atLeast(final double lower) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.atLeast(lower));
		}
		/**
		 * Creates a range query clause in which the upper bound is NOT included and has NO lower bound<br/>
		 * <b>(-oo..U)</b>
		 * @param upper 
		 * @return
		 */
		public RangeQueryClause<Double> lessThan(final double upper) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.lessThan(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>(-oo..U]</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Double> atMost(final double upper) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.atMost(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included but NOT the upper one<br/>
		 * <b>[-L..U)</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Double> closedOpen(final double lower,final double upper) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.closedOpen(lower,upper));
		}
		/**
		 * Creates a range query clause in which the upper bound is included but NOT the lower one<br/>
		 * <b>(-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Double> openClosed(final double lower,final double upper) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.openClosed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are included<br/>
		 * <b>[-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Double> closed(final double lower,final double upper) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.closed(lower,upper));
			
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are NOT included<br/>
		 * <b>(-L..U)</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Double> open(final double lower,final double upper) {
			return new RangeQueryClause<Double>(_fieldId,
												Range.open(lower,upper));
			
		}
		//  -------------------------- Float ranges
		/**
		 * Creates a range query clause in which the lower bound is NOT included and has NO upper bound<br/>
		 * <b>(L..+oo)</b>
		 * @param lower
		 * @return
		 */
		public RangeQueryClause<Float> greaterThan(final float lower) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.greaterThan(lower));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>[L..+oo)</b>
		 * @param lower 
		 * @return
		 */
		public RangeQueryClause<Float> atLeast(final float lower) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.atLeast(lower));
		}
		/**
		 * Creates a range query clause in which the upper bound is NOT included and has NO lower bound<br/>
		 * <b>(-oo..U)</b>
		 * @param upper 
		 * @return
		 */
		public RangeQueryClause<Float> lessThan(final float upper) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.lessThan(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included and has NO upper bound<br/>
		 * <b>(-oo..U]</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Float> atMost(final float upper) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.atMost(upper));
		}
		/**
		 * Creates a range query clause in which the lower bound is included but NOT the upper one<br/>
		 * <b>[-L..U)</b>
		 * @param lower the lower bound
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Float> closedOpen(final float lower,final float upper) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.closedOpen(lower,upper));
		}
		/**
		 * Creates a range query clause in which the upper bound is included but NOT the lower one<br/>
		 * <b>(-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Float> openClosed(final float lower,final float upper) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.openClosed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are included<br/>
		 * <b>[-L..U]</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Float> closed(final float lower,final float upper) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.closed(lower,upper));
		}
		/**
		 * Creates a range query clause in which both the upper and lower bound are NOT included<br/>
		 * <b>(-L..U)</b>
		 * @param lower
		 * @param upper
		 * @return
		 */
		public RangeQueryClause<Float> open(final float lower,final float upper) {
			return new RangeQueryClause<Float>(_fieldId,
											   Range.open(lower,upper));
		}

		//  -------------------------- Range
		/**
		 * Creates a range 
		 * @param range
		 * @return
		 */
		public <T extends Comparable<T>> RangeQueryClause<T> of(final Range<T> range) {
			return new RangeQueryClause<T>(_fieldId,
										   range);
		}
		/**
		 * Creates a range defined by {@link Long} milis bounds
		 * @param range
		 * @return
		 */
		public RangeQueryClause<Date> ofMilis(final Range<Long> range) {
			return new RangeQueryClause<Date>(_fieldId,
											  Range.range(Dates.fromMillis(range.lowerEndpoint()),range.lowerBoundType(), 
													  	  Dates.fromMillis(range.upperEndpoint()),range.upperBoundType()));
		}
	}
}
