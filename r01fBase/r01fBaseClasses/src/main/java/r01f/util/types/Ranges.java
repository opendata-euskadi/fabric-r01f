package r01f.util.types;

import java.util.Date;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.Range;

/**
 * {@link Range} of some types used at REST resources where a type with a single String arg is needed
 * to build the @PathParam mapped resource function parameters
 */
@GwtIncompatible("Range NOT usable in GWT")
public class Ranges {
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class DateRange {
		@Getter private final Range<Date> _range;
		/**
		 * This factory method is MANDATORY since {@link Range} is mapped as a @PathParam
		 * at REST jesrouces.
		 * This types of params NEEDS either:
		 * 		<ul>
		 *			<li>Be a primitive type</li>
		 *			<li>Have a constructor that accepts a single String argument</li>
		 *			<li>Have a static method named valueOf or fromString that accepts a single String argument (see, for example, Integer.valueOf(String))</li>
		 *			<li>Have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.</li>
		 *			<li>Be List<T>, Set<T> or SortedSet<T>, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.</li>
		 *		</ul>
		 * @param rangeAsString
		 */
		public static DateRange valueOf(final String rangeAsString) {
			return new DateRange(rangeAsString);
		}
		public DateRange(final String rangeAsString) {
			_range = Range.parse(rangeAsString,Date.class);
		}
		public DateRange(final Range<Date> range) {
			_range = range;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LONG RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class LongRange {
		@Getter private final Range<Long> _range;
		/**
		 * This factory method is MANDATORY since {@link Range} is mapped as a @PathParam
		 * at REST jesrouces.
		 * This types of params NEEDS either:
		 * 		<ul>
		 *			<li>Be a primitive type</li>
		 *			<li>Have a constructor that accepts a single String argument</li>
		 *			<li>Have a static method named valueOf or fromString that accepts a single String argument (see, for example, Integer.valueOf(String))</li>
		 *			<li>Have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.</li>
		 *			<li>Be List<T>, Set<T> or SortedSet<T>, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.</li>
		 *		</ul>
		 * @param rangeAsString
		 */
		public static LongRange valueOf(final String rangeAsString) {
			return new LongRange(rangeAsString);
		}
		public LongRange(final String rangeAsString) {
			_range = Range.parse(rangeAsString,Long.class);
		}
		public LongRange(final Range<Long> range) {
			_range = range;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INT RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class IntRange {
		@Getter private final Range<Integer> _range;
		/**
		 * This factory method is MANDATORY since {@link Range} is mapped as a @PathParam
		 * at REST jesrouces.
		 * This types of params NEEDS either:
		 * 		<ul>
		 *			<li>Be a primitive type</li>
		 *			<li>Have a constructor that accepts a single String argument</li>
		 *			<li>Have a static method named valueOf or fromString that accepts a single String argument (see, for example, Integer.valueOf(String))</li>
		 *			<li>Have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.</li>
		 *			<li>Be List<T>, Set<T> or SortedSet<T>, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.</li>
		 *		</ul>
		 * @param rangeAsString
		 */
		public static IntRange valueOf(final String rangeAsString) {
			return new IntRange(rangeAsString);
		}
		public IntRange(final String rangeAsString) {
			_range = Range.parse(rangeAsString,Integer.class);
		}
		public IntRange(final Range<Integer> range) {
			_range = range;
		}
	}
}
