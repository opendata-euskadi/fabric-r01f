package r01f.reflection.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides a very simple API for accessing resources within an application server.
 * @see stripes framework: https://github.com/StripesFramework/stripes/blob/master/stripes/src/main/java/net/sourceforge/stripes/vfs/VFS.java 
 */
@Slf4j
public abstract class VFS {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * The built-in implementations. 
     */
    public static final Class<?>[] IMPLEMENTATIONS = { JBoss6VFS.class, DefaultVFS.class };
    /**
     * The list to which implementations are added by {@link #addVFSImplType(Class)}. 
     */
    public static final List<Class<? extends VFS>> USER_IMPLEMENTATIONS = new ArrayList<Class<? extends VFS>>();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Adds the specified class to the list of {@link VFS} implementations. Classes added in this
     * manner are tried in the order they are added and before any of the built-in implementations.
     * 
     * @param vfsImplementingType The {@link VFS} implementation class to add.
     */
    public static void addVFSImplType(final Class<? extends VFS> vfsImplementingType) {
        if (vfsImplementingType != null) USER_IMPLEMENTATIONS.add(vfsImplementingType);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    private static enum VFSSingletonHolder {
    	SINGLETON;
    	
    	@Getter private final VFS _instance;
    	
    	@SuppressWarnings("unchecked")
    	VFSSingletonHolder() {
	        // Try the user implementations first, then the built-ins
	        List<Class<? extends VFS>> impls = new ArrayList<Class<? extends VFS>>();
	        impls.addAll(USER_IMPLEMENTATIONS);
	        impls.addAll(Arrays.asList((Class<? extends VFS>[]) IMPLEMENTATIONS));
	
	        // Try each implementation class until a valid one is found
	        VFS vfs = null;
	        for (int i = 0; vfs == null || !vfs.isValid(); i++) {
	            Class<? extends VFS> impl = impls.get(i);
	            try {
	                vfs = impl.newInstance();
	                if (vfs == null || !vfs.isValid()) log.debug("VFS implementation {} is not valid in this environment.",impl.getName());
	            } catch (InstantiationException e) {
	                log.error("Failed to instantiate {}",impl,e);
	            } catch (IllegalAccessException e) {
	                log.error("Failed to instantiate {}",impl,e);
	            }
	        }
	        log.info("Using VFS adapter {}",vfs.getClass().getName());
	        _instance = vfs;
    	}
    }
    /**
     * Get the singleton {@link VFS} instance. If no {@link VFS} implementation can be found for the
     * current environment, then this method returns null.
     */
    public static VFS getInstance() {
    	return VFSSingletonHolder.SINGLETON.getInstance();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Recursively list the full resource path of all the resources that are children of all the
     * resources found at the specified path.
     * 
     * @param path The path of the resource(s) to list.
     * @return A list containing the names of the child resources.
     * @throws IOException If I/O errors occur
     */
    public Collection<String> list(final String path) throws IOException {
        List<String> names = new ArrayList<String>();
        for (URL url : _getResources(path)) {
            names.addAll(_list(url,
            			   	   path));
        }
        return names;
    }
    /**
     * Recursively list the full resource path of all the resources that are children of the
     * resource identified by a URL.
     * 
     * @param url The URL that identifies the resource to list.
     * @param forPath The path to the resource that is identified by the URL. Generally, this is the
     *            value passed to {@link #_getResources(String)} to get the resource URL.
     * @return A list containing the names of the child resources.
     * @throws IOException If I/O errors occur
     */
    protected abstract Collection<String> _list(final URL url,
    										    final String forPath) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Get a list of {@link URL}s from the context classloader for all the resources found at the
     * specified path.
     * 
     * @param path The resource path.
     * @return A list of {@link URL}s, as returned by {@link ClassLoader#getResources(String)}.
     * @throws IOException If I/O errors occur
     */
    protected static List<URL> _getResources(final String path) throws IOException {
        return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * Return true if the {@link VFS} implementation is valid for the current environment. 
     */
    public abstract boolean isValid();
}
