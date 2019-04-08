package r01f.exceptions;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Encloses a {@link Collections} of exceptions thrown as one
 */
@Accessors(prefix="_")
public class MultiException
	 extends RuntimeException {

	private static final long serialVersionUID = -2163521318635289782L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
  static final String MULTIPLE = " exceptions caught: ";
  static final String ONE = "Exception caught: ";
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * All the exception.
	 */
	@Getter private Set<Throwable> _causes;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Required for GWT RPC serialization.
	 */
	protected MultiException() {
		// Can't delegate to the other constructor or GWT RPC gets cranky
		super(MULTIPLE);
		_causes = Collections.<Throwable> emptySet();
	}
	public MultiException(final Set<Throwable> causes) {
		super(_makeMessage(causes),
			  _firstCause(causes));
		_causes = causes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _makeMessage(final Set<Throwable> causes) {
		int exCount = causes.size();
		if (exCount == 0) return null;

		StringBuilder b = new StringBuilder(exCount == 1 ? ONE : exCount + MULTIPLE);
		boolean first = true;
		for (Throwable t : causes) {
			if (first) {
				first = false;
			} else {
				b.append("; ");
			}
			b.append(t.getMessage());
		}
		return b.toString();
	}
	protected static Throwable _firstCause(final Set<Throwable> causes) {
		Iterator<Throwable> iterator = causes.iterator();
		if (!iterator.hasNext()) return null;
		return iterator.next();
	}
}
