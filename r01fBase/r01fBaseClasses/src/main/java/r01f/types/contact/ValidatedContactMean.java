package r01f.types.contact;

import java.util.Collection;
import java.util.Iterator;

import r01f.util.types.collections.CollectionUtils;

public interface ValidatedContactMean
		 extends ContactMean {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Validates the id
	 */
	public boolean isValid();
	
/////////////////////////////////////////////////////////////////////////////////////////
//	STATIC UTIL METHOD
/////////////////////////////////////////////////////////////////////////////////////////	
	public static String colToString(final Collection<? extends ValidatedContactMean> col) {
		if (CollectionUtils.isNullOrEmpty(col)) return null;
		StringBuilder str = new StringBuilder();
		for (Iterator<? extends ValidatedContactMean> idIt = col.iterator(); idIt.hasNext(); ) {
			ValidatedContactMean id = idIt.next();
			if (id == null) continue;
			str.append(id.asString());
			if (idIt.hasNext()) str.append("; ");
		}
		return str.toString();
	}
}
