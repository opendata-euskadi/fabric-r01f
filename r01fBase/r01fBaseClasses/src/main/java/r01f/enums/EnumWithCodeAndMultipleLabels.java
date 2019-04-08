package r01f.enums;



/**
 * Interfaz que deben implementar los enum en base a codigo y N descripciones
 * @see EnumWithCodeAndMultipleLabelsWrapper
 * @param <T> el Enum concreto
 */
public interface EnumWithCodeAndMultipleLabels<C,T> 
         extends EnumWithCodeAndLabel<C,T> {
	/**
	 * Devuelve las descripciones del elemento del enum
	 * @return
	 */
	public String[] getLabels(); 
}
