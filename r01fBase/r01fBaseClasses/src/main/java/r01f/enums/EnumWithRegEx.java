package r01f.enums;

import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public interface EnumWithRegEx<T> 
         extends EnumExtended<T> {
	/**
	 * Returns the patterns
	 * @return
	 */
	public Pattern[] getPatterns(); 
	/**
	 * Checks if the enum element can be assigned from a label 
	 * @param desc 
	 * @return 
	 */
	public boolean canBeFrom(String label);
}
