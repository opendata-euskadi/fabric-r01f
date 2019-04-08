package r01f.reflection.scanner;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.extern.slf4j.Slf4j;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;


@Slf4j
public class AnnotatedWithScanner 
	 extends ClasspathScannerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds types annotated at given packages
	 * @param annotationType
	 * @param pckgNames
	 * @return
	 */
	public static <A extends Annotation> Set<Class<?>> findTypesAnnotatedWitAt(final Class<A> annotationType,
													  		 		  		   final Collection<JavaPackage> pckgNames) {
		return AnnotatedWithScanner.findTypesAnnotatedWitAtPackages(annotationType,
											   	   					pckgNames,
											   	   					null,	// the default scanner filter will apply
											   	   					AnnotatedWithScanner.class.getClassLoader());
	}
	/**
	 * Finds types annotated at given packages
	 * @param annotationType
	 * @param pckgNames
	 * @param otherClassLoader
	 * @return
	 */
	public static <A extends Annotation> Set<Class<?>> findTypesAnnotatedWitAt(final Class<A> annotationType,
													  		 				   final Collection<JavaPackage> pckgNames,
													  		 				   final ClassLoader otherClassLoader) {
		return AnnotatedWithScanner.findTypesAnnotatedWitAtPackages(annotationType,
											   	   					pckgNames,
											   	   					null,	// the default scanner filter will apply
											   	   					otherClassLoader);
	}
	/**
	 * Finds types annotated at given packages
	 * @param annotationType
	 * @param pckgs
	 * @param scannerFilter
	 * @param otherClassLoader
	 * @return
	 */
	public static <A extends Annotation> Set<Class<?>> findTypesAnnotatedWitAtPackages(final Class<A> annotationType,
													  		 				   		   final Collection<JavaPackage> pckgs,
													  		 				   		   final Predicate<JavaPackage> scannerFilter,
													  		 				   		   final ClassLoader otherClassLoader) {
		log.info("...finding types annotated with {} at packages {} (BEWARE that every type between the type to be found and the supertype MUST be accesible in the package names list)",
				 annotationType,pckgs);
		Collection<URL> pckgUrls = _urlsForPackages(pckgs,
											  		otherClassLoader);
		
		Set<Class<?>> putAnnotatedTypes = null;
		if (CollectionUtils.isNullOrEmpty(pckgUrls)) {
			log.error("Could NOT get any URL for packages {} from any classloader!!!",pckgs);
			// The org.reflections' ClasspathHelper.forPackage method, at the end does: 
	        // 		for (ClassLoader classLader : ClasspathHelper.classLoaders()) {
			//			Enumeration<URL> urls = classLoader.getResources(<change package dots for />);
			//			if (urls != null) convert the enumeration into a Collection<URL> and return
			// 		}
			// BUT for an unknown reason, when the package is INSIDE a JAR at APP-INF/lib,
			// classLoader.getResources(pckgName) returns null
			// WHAT TO DO???
			// see:
			//		http://www.javaworld.com/article/2077352/java-se/smartly-load-your-properties.html
			//		http://stackoverflow.com/questions/676250/different-ways-of-loading-a-file-as-an-inputstream
			// 		https://github.com/Atmosphere/atmosphere/issues/1229
			//		http://middlewaresnippets.blogspot.com.es/2011/05/class-loading-and-application-packaging.html
			//
		}
		else {
			putAnnotatedTypes = AnnotatedWithScanner.findTypesAnnotatedWitAtURLs(annotationType,
																	   			 pckgUrls,
																	   			 scannerFilter,
																	   			 otherClassLoader);
		}
		
		// When deploying at a WLS, the classes are inside a jar file whose URL is zip:/dominio_wls/servers/server1/tmp/_WL_user/myEAR/n5ymxm/APP-INF/lib/{appCode}Classes.jar!/{appCode}/client/internal  
		// (see org.reflections.vfs.Vfs.DefaultUrlTypes) 
		// ... so the scanned URLs loses the package part {appCode}Classes.jar!/{appCode}/client/internal and ALL jar classes are scanned
		if (CollectionUtils.hasData(putAnnotatedTypes)) {
			putAnnotatedTypes = FluentIterable.from(putAnnotatedTypes)
										.filter(new Predicate<Class<?>>() {
														@Override
														public boolean apply(final Class<?> annotatedWith) {
															boolean inPackage = false;
															for (JavaPackage pckg : pckgs) {
																if (annotatedWith.getPackage().getName().startsWith(pckg.asString())) {
																	inPackage = true;
																	break;
																}
															}
															return inPackage;
														}
												})
										.toSet();
		}
		return putAnnotatedTypes;
	}
	/**
	 * Finds types annotated at given packages
	 * @param annotationType
	 * @param classPathUrls
	 * @pram scannerFilter
	 * @param otherClassLoader
	 * @return
	 */
	public static <A extends Annotation> Set<Class<?>> findTypesAnnotatedWitAtURLs(final Class<A> annotationType,
													  		 			  	   	   final Collection<URL> classPathUrls,
													  		 			  	   	   final Predicate<JavaPackage> scannerFilter,
													  		 			  	   	   final ClassLoader otherClassLoader) {
		log.info("...finding types annotated with {} at classpath urls {} (BEWARE that every type between the type to be found and the supertype MUST be accesible in the classpath url list)",
				 annotationType,classPathUrls);
		
		// ensure the scanner filter
		Predicate<String> theScannerFilter = scannerFilter != null ? ScannerFilter.createScannerFilter(scannerFilter)
																   : ScannerFilter.DEFAULT_TYPE_FILTER;
		
		// The usual case (at least in tomcat) is that the package resources URLs can be found 
		// and org.Reflections can be used
		Reflections typeScanner = new Reflections(new ConfigurationBuilder()		// Reflections library NEEDS to have both the interface containing package and the implementation containing package
														.setUrls(classPathUrls)	// see https://code.google.com/p/reflections/issues/detail?id=53
														.filterInputsBy(theScannerFilter)
														.setScanners(// new SubTypesScanner(true), 		// true = exclude object class
																	 new TypeAnnotationsScanner()));	
		Set<Class<?>> outAnnotatedWith = typeScanner.getTypesAnnotatedWith(annotationType,
																		   true);			// honor inherited???
		
		return outAnnotatedWith;
	}	
}
