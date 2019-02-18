package r01f.aspects.core.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

class CollectionUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Checks if a type is an array
     * @param type the type
     * @return true if the type is an array, false if not
     */
    public static boolean isArray(final Class<?> type) {
        return type.isArray();
    }
	/**
	 * Checks if an array is composed of elements of a primitive type
	 * @param type the array type
	 * @return true if the elements type is a primitive type
	 */
	public static boolean isObjectsArray(final Class<?> type) {
		return type.isArray() && !type.getComponentType().isPrimitive();
	}
    /**
     * Checks if a type implements the {@link Map} interface
     * @param type the type
     * @return true if the interface is implemented
     */
    public static boolean isMap(final Class<?> type) {
    	return Map.class.isAssignableFrom(type);
    }
    /**
     * Checks if a type implements the {@link Collection} interface
     * @param type the type
     * @return true if the interface is implemented
     */
    public static boolean isCollection(final Class<?> type) {
    	return Collection.class.isAssignableFrom(type);
    } 
    /**
     * Checks if a type implements the {@link List} interface
     * @param type the type
     * @return true if the interface is implemented
     */
    public static boolean isList(final Class<?> type) {
    	return List.class.isAssignableFrom(type);
    }
    /**
     * Checks if a type implements the {@link Set} interface
     * @param type the type
     * @return true if the interface is implemented
     */
    public static boolean isSet(final Class<?> type) {
    	return Set.class.isAssignableFrom(type);
    } 
}
