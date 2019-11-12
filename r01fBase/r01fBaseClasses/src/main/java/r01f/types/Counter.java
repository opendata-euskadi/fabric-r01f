package r01f.types;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class Counter 
  implements Serializable {

	private static final long serialVersionUID = -7146191851861875882L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final int _minVal;
	@Getter private final int _maxVal;
	@Getter private int _currVal;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public Counter() {
		this(Range.closed(Integer.MIN_VALUE,Integer.MAX_VALUE));
	}
	public Counter(final Range<Integer> range) {
		this(range,
			 0);
	}
	public Counter(final Range<Integer> range,
				   final int startVal) {
		_minVal = range.getLowerBound();
		_maxVal = range.getUpperBound();
		_currVal = startVal;
	}
	public static Counter startingAt(final int val) {
		return new Counter(Range.closed(Integer.MIN_VALUE,Integer.MAX_VALUE),
						   val);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public int increment() {
		return this.increment(1);
	}
	public int increment(final int num) {
		if ((_currVal + num) > _maxVal) throw new IllegalStateException("If increment " + num + " then counter max value would be reached " + _maxVal);
		_currVal = _currVal + num;
		return _currVal;
	}
	public boolean canIncrement(final int num) {
		return (_currVal + num) < _maxVal;
	}
	public int decrement() {
		return this.decrement(1);
	}
	public int decrement(final int num) {
		if ((_currVal - num) < _minVal) throw new IllegalStateException("If decrement " + num + " then counter would be < 0");
		_currVal = _currVal - num;
		return _currVal;
	}
	public boolean canDecrement(final int num) {
		return (_currVal - num) > _minVal;
	}
}
