package r01f.reflection.scanner;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.reflections.util.ClasspathHelper;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;


@Slf4j
abstract class ClasspathScannerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Collection<URL> _urlsForPackages(final Collection<JavaPackage> pckgNames,
											  		  final ClassLoader otherClassLoader) {
		if (CollectionUtils.isNullOrEmpty(pckgNames)) throw new IllegalArgumentException();
		List<URL> outUrls = Lists.newLinkedList();
		for (JavaPackage pckgName : pckgNames) {
			outUrls.addAll(_urlsForPackage(pckgName,
										   otherClassLoader));
		}
		return Sets.newLinkedHashSet(outUrls);
	}
	protected static Collection<URL> _urlsForPackage(final JavaPackage pckg,
                                                     final ClassLoader otherClassLoader) {
		ClassLoader[] classLoaders = _scanClassLoaders(otherClassLoader);
         
		// org.reflections.ClasspathHelper seems to return ONLY the jar or path containing the given package
		// ... so the package MUST be added back to the url to minimize scan time and unneeded class loading
        Collection<URL> outUrls = ClasspathHelper.forPackage(pckg.asString(),
                                                             classLoaders);
        if (CollectionUtils.hasData(outUrls)) {
        	outUrls = FluentIterable.from(outUrls)
                                    .transform(new Function<URL,URL>() {
														@Override
														public URL apply(final URL url) {
															try {
																URL fullUrl = new URL(url.toString() + _resourceName(pckg));
																log.trace("URL to be scanned: {}",fullUrl);
														        return fullUrl;
														    } catch (Throwable th) {
														    	th.printStackTrace(System.out);
														    }
														    return url;
														}
                                               })
                                    .toList();
         }
         return outUrls;
	}
	private static ClassLoader[] _scanClassLoaders(final ClassLoader otherClassLoader) {
		ClassLoader[] outClassLoaders =	otherClassLoader != null ? ClasspathHelper.classLoaders(ClasspathHelper.staticClassLoader(),
											 						 							ClasspathHelper.contextClassLoader(),
											 						 							otherClassLoader)
																 : ClasspathHelper.classLoaders(ClasspathHelper.staticClassLoader(),
											 						 							ClasspathHelper.contextClassLoader());
		return outClassLoaders;
	}	
    private static String _resourceName(final JavaPackage name) {
        if (name == null) return null;
        String resourceName = name.asString().replace(".","/")
        						  			 .replace("\\", "/");
        if (resourceName.startsWith("/")) resourceName = resourceName.substring(1);
        return resourceName;
    }
}
