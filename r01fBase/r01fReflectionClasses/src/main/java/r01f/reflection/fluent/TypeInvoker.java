package r01f.reflection.fluent;

import r01f.reflection.ReflectionException;

/**
 * Class Loading using a specific <code>{@link ClassLoader}</code>.
 */

public final class TypeInvoker {
	private final String _className;
	private final ClassLoader _classLoader;	
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Constructor
	 * @param className
	 * @param classLoader
	 */
	private TypeInvoker(final String className,final ClassLoader classLoader) {
		_className = className;
		_classLoader = classLoader;
	}
///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////	
	static TypeInvoker newLoader(final String className,final ClassLoader classLoader) {
		if (classLoader == null) throw new NullPointerException("The given class loader should not be null");
		return new TypeInvoker(className,classLoader);
	}
	/**
	 * Loads the class with the name specified in this type, using this class' <code>ClassLoader</code>.
	 * Example:
	 * <pre>
	 * Class<?> type = Reflection.type("org.republic.Jedi").withClassLoader(myClassLoader).load();
	 * </pre>
	 * @return the loaded class.
	 * @throws ReflectionError wrapping any error that occurred during class loading.
	 */
	public Class<?> load() {
		try {
			return _loadType();
		} catch (Exception e) {
			throw ReflectionException.of(e);
		}
	}
	/**
	 * Loads the class with the name specified in this type, as the given type, using this class' <code>ClassLoader</code>.
	 * The following example shows how to use this method. 
	 * Let's assume that we have the class <code>Jedi</code> that extends the class <code>Person</code>:
	 * <pre>
	 * Class<Person> type = Reflection.type("org.republic.Jedi").withClassLoader(myClassLoader).loadAs(Person.class);
	 * </pre>
	 * 
	 * @param type the given type.
	 * @param <T> the generic type of the type.
	 * @return the loaded class.
	 * @throws NullPointerException if the given type is <code>null</code>.
	 * @throws ReflectionError wrapping any error that occurred during class loading.
	 */
	public <T> Class<? extends T> loadAs(final Class<T> type) {
		if (type == null) {
			throw new NullPointerException("The given type should not be null");
		}
		try {
			return _loadType().asSubclass(type);
		} catch (Exception e) {
			throw ReflectionException.of(e);
		}
	}
///////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////		
	private Class<?> _loadType() throws ClassNotFoundException {
		return _classLoader.loadClass(_className);
	}
}
