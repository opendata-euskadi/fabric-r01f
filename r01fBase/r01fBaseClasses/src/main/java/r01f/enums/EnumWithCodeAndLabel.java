package r01f.enums;




/**
 * Enum with a code and label operations 
 * @see EnumWithCodeAndLabelWrapper
 * @param <T> 
 */
public interface EnumWithCodeAndLabel<C,T> 
         extends EnumWithCode<C,T> {
	
	public String getLabel();
	
	public boolean canBeFrom(String label);
}
