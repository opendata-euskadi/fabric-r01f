package r01f.reflection.scanner;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;


@Slf4j
public class JBoss6VFS
	 extends VFS {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * A class that mimics a tiny subset of the JBoss VirtualFile class.
     */
    static class VirtualFile {
        static Class<?> VirtualFile;
        static Method getPathNameRelativeToMethod;
        static Method getChildrenRecursivelyMethod;

        Object virtualFile;

        VirtualFile(Object virtualFile) {
            this.virtualFile = virtualFile;
        }

        String getPathNameRelativeTo(final VirtualFile parent) {
            try {
                return invoke(getPathNameRelativeToMethod,
                			  virtualFile,
                			  parent.virtualFile);
            } catch (IOException e) {
                // This exception is not thrown by the called method
                log.error("This should not be possible. VirtualFile.getPathNameRelativeTo() threw IOException.");
                return null;
            }
        }
        List<VirtualFile> getChildren() throws IOException {
            List<?> objects = invoke(getChildrenRecursivelyMethod, virtualFile);
            List<VirtualFile> children = new ArrayList<VirtualFile>(objects.size());
            for (Object object : objects) {
                children.add(new VirtualFile(object));
            }
            return children;
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * A class that mimics a tiny subset of the JBoss VFS class. 
     */
    static class VFS {
        static Class<?> VFS;
        static Method getChild;

        static VirtualFile getChild(URL url) throws IOException {
            Object o = invoke(getChild, VFS, url);
            return o == null ? null : new VirtualFile(o);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Invoke a method on an object and return whatever it returns.
     * 
     * @param method The method to invoke.
     * @param object The instance or class (for static methods) on which to invoke the method.
     * @param parameters The parameters to pass to the method.
     * @return Whatever the method returns.
     * @throws IOException If I/O errors occur
     * @throws StripesRuntimeException If anything else goes wrong
     */
    @SuppressWarnings("unchecked")
    protected static <T> T invoke(final Method method,final Object object,final Object... parameters) throws IOException {
        try {
            return (T) method.invoke(object, parameters);
        } catch (IllegalArgumentException e) {
            throw Throwables.throwUnchecked(e);
        } catch (IllegalAccessException e) {
            throw Throwables.throwUnchecked(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof IOException) {
            	throw (IOException)e.getTargetException();
            } 
            Throwables.throwUnchecked(e);
        }
        return null;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * Flag that indicates if this VFS is valid for the current environment. 
     */
    private static Boolean _valid;

    static {
        _initialize();
    }
    /** 
     * Find all the classes and methods that are required to access the JBoss 6 VFS. 
     */
    protected static synchronized void _initialize() {
        if (_valid == null) {
            // Assume valid. It will get flipped later if something goes wrong.
            _valid = true;

            // Look up and verify required classes
            VFS.VFS = _checkNotNull(_getClass("org.jboss.vfs.VFS"));
            VirtualFile.VirtualFile = _checkNotNull(_getClass("org.jboss.vfs.VirtualFile"));

            // Look up and verify required methods
            VFS.getChild = _checkNotNull(_getMethod(VFS.VFS, "getChild", URL.class));
            VirtualFile.getChildrenRecursivelyMethod = _checkNotNull(_getMethod(VirtualFile.VirtualFile,
                    "getChildrenRecursively"));
            VirtualFile.getPathNameRelativeToMethod = _checkNotNull(_getMethod(VirtualFile.VirtualFile,
                    "getPathNameRelativeTo", VirtualFile.VirtualFile));

            // Verify that the API has not changed
            _checkReturnType(VFS.getChild, VirtualFile.VirtualFile);
            _checkReturnType(VirtualFile.getChildrenRecursivelyMethod, List.class);
            _checkReturnType(VirtualFile.getPathNameRelativeToMethod, String.class);
        }
    }
    /** 
     * Get a class by name. If the class is not found then return null. 
     */
    protected static Class<?> _getClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            log.debug("Class not found: {}",className);
            return null;
        }
    }
    /**
     * Get a method by name and parameter types. If the method is not found then return null.
     * 
     * @param clazz The class to which the method belongs.
     * @param methodName The name of the method.
     * @param parameterTypes The types of the parameters accepted by the method.
     */
    @SuppressWarnings("null")
	protected static Method _getMethod(final Class<?> clazz,final String methodName,final Class<?>... parameterTypes) {
        try {
            if (clazz == null) return null;
            return clazz.getMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            log.debug("Security exception looking for method {}.{} > {}",clazz.getName(),methodName,e);
            return null;
        } catch (NoSuchMethodException e) {
            log.debug("Method not found {}.{} > {}", clazz.getName(),methodName,e);
            return null;
        }
    }
    /**
     * Verifies that the provided object reference is null. If it is null, then this VFS is marked
     * as invalid for the current environment.
     * 
     * @param object The object reference to check for null.
     */
    protected static <T> T _checkNotNull(final T object) {
        if (object == null) _setInvalid();
        return object;
    }
    /** Mark this {@link VFS} as invalid for the current environment. */
    protected static void _setInvalid() {
        if (JBoss6VFS._valid != null && JBoss6VFS._valid) {
            log.debug("JBoss 6 VFS API is not available in this environment.");
            JBoss6VFS._valid = false;
        }
    }    
    /**
     * Verifies that the return type of a method is what it is expected to be. If it is not, then
     * this VFS is marked as invalid for the current environment.
     * 
     * @param method The method whose return type is to be checked.
     * @param expected A type to which the method's return type must be assignable.
     * @see Class#isAssignableFrom(Class)
     */
    protected static void _checkReturnType(final Method method,final Class<?> expected) {
        if (method != null && !expected.isAssignableFrom(method.getReturnType())) {
            log.warn("Method {}.{}(..) should return {} but returns {} instead",
            		 method.getClass().getName(),method.getName(),
            		 expected.getName(),method.getReturnType().getName());
            _setInvalid();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean isValid() {
        return _valid;
    }
    @Override
    public List<String> _list(final URL url,final String path) throws IOException {
        VirtualFile directory;
        directory = VFS.getChild(url);
        if (directory == null) return Collections.emptyList();

        String thePath = path;
        if (!path.endsWith("/")) thePath += "/";

        List<VirtualFile> children = directory.getChildren();
        List<String> names = new ArrayList<String>(children.size());
        for (VirtualFile vf : children) {
            String relative = vf.getPathNameRelativeTo(directory);
            names.add(thePath + relative);
        }
        return names;
    }
}
