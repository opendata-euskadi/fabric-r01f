package r01f.reflection;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.reflect.TypeToken;

class CollectionUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Converts a Collection to an array
     * @param col the {@link Collection}
     * @return the array
     */
	@SuppressWarnings({"unchecked","serial"})
    public static <T> T[] toArray(final Collection<T> col) {
    	TypeToken<T> tr = new TypeToken<T>() {/* nothing */};
		T[] outArray = col != null && col.size() > 0 ? col.toArray((T[])Array.newInstance(tr.getRawType(),col.size()))
    												 : null;
    	return outArray;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Returns the collection type as it's inteface, ie, if the type is extending a Map, it returns {@link Map}
     * or if the type is extending a Collection, it returns {@link Collection}
     * @param obj the object that's supossed to be a collection
     * @return List.class if it's a list, Map.class if it's a map, Set.class if it's a Set or null otherwise
     */
    public static Class<?> getCollectionType(final Class<?> type) {
        Class<?> colType = null;
        if (CollectionUtils.isMap(type)) {
            colType = Map.class;
        } else if (CollectionUtils.isSet(type)) {
        	colType = Set.class;
        } else if (CollectionUtils.isList(type)) {
        	colType = List.class;
        } else if (CollectionUtils.isCollection(type)) {
            colType = List.class;
        }
        return colType;
    }
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
    	return ReflectionUtils.isImplementing(type,Map.class);
    }
    /**
     * Checks if a type implements the {@link Collection} interface
     * @param type the type
     * @return true if the interface is implemented
     */
    public static boolean isCollection(final Class<?> type) {
    	return ReflectionUtils.isImplementing(type,Collection.class);
    } 
    /**
     * Checks if a type implements the {@link List} interface
     * @param type the type
     * @return true if the interface is implemented
     */
    public static boolean isList(final Class<?> type) {
    	return ReflectionUtils.isImplementing(type,List.class);
    }
    /**
     * Checks if a type implements the {@link Set} interface
     * @param type the type
     * @return true if the interface is implemented
     */
    public static boolean isSet(final Class<?> type) {
    	return ReflectionUtils.isImplementing(type,Set.class);
    } 
}
