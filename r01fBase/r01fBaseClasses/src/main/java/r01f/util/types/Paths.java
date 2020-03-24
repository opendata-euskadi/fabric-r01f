package r01f.util.types;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Map;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathBase;
import r01f.types.PathBase.Paths2;
import r01f.types.PathFactory;
import r01f.types.url.UrlPath;

/**
 * path utils
 */
@GwtIncompatible
@Accessors(prefix="_")
@Slf4j
public abstract class Paths { 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	// a cache of the path types constructor
	private static Map<Class<? extends IsPath>,Constructor<? extends IsPath>> CONSTRUCTOR_REF_CACHE = Maps.newHashMap();
	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class PathFactoryUsingType<P extends IsPath>   
			  implements PathFactory<P> {
		private final Class<P> _pathType;
		
		@Override @SuppressWarnings("unchecked")
		public P createPathFrom(final Collection<String> elements) {
			P outPathInstance = null;
			try {
				Constructor<? extends IsPath> constructor = CONSTRUCTOR_REF_CACHE.get(_pathType);
				if (constructor == null) {
					// load the constructor
					constructor = (Constructor<? extends IsPath>)_getConstructor(_pathType,
		        												 				 new Class<?>[] {Collection.class},
		        												 				 true);
					if (constructor == null) throw new IllegalStateException(Throwables.message("Path type {} does NOT have a Collection<String> based constructor! It's mandatory!",
																								_pathType));
					CONSTRUCTOR_REF_CACHE.put(_pathType,constructor);
				}
				outPathInstance = (P)constructor.newInstance(new Object[] {elements});		// BEWARE!! the path elements are encapsulated in a ImmutableList
			} catch (Throwable th) {
				log.error("Could NOT create a {} instance: {}",
						  _pathType.getName(),th.getMessage(),th);
				// should never happen
			}
			return outPathInstance;
		}
	}
    private static Constructor<?> _getConstructor(final Class<?> type,
    											  final Class<?>[] constructorArgsTypes,
    											  final boolean force) {
        Constructor<?> constructor = null;
        try {
            constructor = type.getDeclaredConstructor(constructorArgsTypes != null ? constructorArgsTypes
                                                                                   : new Class<?>[] {}); 	// Constructor
            if (force) AccessController.doPrivileged(new SetAccessibleAction(constructor));       //  make the constructor accesible
        } catch (NoSuchMethodException nsmEx) {
        	/* the constructor does NOT exists */
        }
        return constructor;
    }
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class SetAccessibleAction 
	 		   implements PrivilegedAction<Void> {
		private final AccessibleObject _obj;

		@Override
		public Void run() {
			_obj.setAccessible(true);
			return null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static String toString(final IsPath path) {
		return Paths.asString(path);
	}
	public static String asString(final IsPath path) {
		return PathBase.asString(path);
	}
	public static String asRelativeString(final IsPath path) {
		return PathBase.asRelativeString(path);
	}
	public static String asAbsoluteString(final IsPath path) {
		return PathBase.asAbsoluteString(path);
	}
	public static String asAbsoluteStringFrom(final IsPath parentPath,
									  		  final IsPath path) {
		return PathBase.asAbsoluteStringFrom(parentPath,
											 path);
	}
	public static String asRelativeStringFrom(final IsPath parentPath,
									  		  final IsPath path) {
		return PathBase.asRelativeStringFrom(parentPath,
											 path);
	}
	public static String asRelativeStringOrNull(final IsPath path) {
		return PathBase.asRelativeStringOrNull(path);
	}
	public static String asAbsoluteStringOrNull(final IsPath path) {
		return PathBase.asAbsoluteStringOrNull(path);
	}
	public static String asRelativeStringOrNull(final String path) {
		return Paths.asRelativeStringOrNull(Path.from(path));
	}
	public static String asAbsoluteStringOrNull(final String path) {
		return Paths.asAbsoluteStringOrNull(Path.from(path));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MORE CONVENIENT JOIN & PREPEND ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	public static <P extends IsPath> Paths2<P> forPathType(final Class<P> pathType) {
		return PathBase.createPaths2(new PathFactoryUsingType<P>(pathType));
	}
	public static Paths2<Path> forPaths() {
		return PathBase.createPaths2(Path.PATH_FACTORY);
	}
	public static Paths2<UrlPath> forUrlPaths() {
		return PathBase.createPaths2(UrlPath.URL_PATH_FACTORY);
	}
}
