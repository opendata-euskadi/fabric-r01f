package r01f.types;

import java.io.Serializable;

/**
 * Interface for types that can be represented as a {@link String}
 */
public interface CanBeRepresentedAsString
		 extends Serializable {
	public String asString();
}
