package r01f.reflection.scanner;

import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import r01f.types.JavaPackage;

/**
 * A filter used by org.reflections when scanning types
 */

public class ScannerFilter {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static Predicate<String> createScannerFilter(final Predicate<JavaPackage> filter) {
		return Predicates.and(DEFAULT_TYPE_FILTER,
							  new Predicate<String>() {
										@Override
										public boolean apply(final String pckg) {
											return filter.apply(new JavaPackage(pckg));
										}
							  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
//	public static final Predicate<JavaPackage> DEFAULT_TYPE_FILTER = new Predicate<JavaPackage>() {
//																				@Override
//																				public boolean apply(final JavaPackage pckg) {
//																					return DEF_TYPE_FILTER.apply(pckg.asString());
//																				}
//																	  };
	public static final Predicate<String> DEFAULT_TYPE_FILTER = new FilterBuilder()
													                    .excludePackage("java.")
													                    .excludePackage("javax.")
													                    .excludePackage("com.")
													                    .excludePackage("org.")
													                    .excludePackage("weblogic.")
													                    .excludePackage("apache.")
													                    .add(new Predicate<String>() {
																					@Override
																					public boolean apply(final String input) {
																						// process only class files
																						return input.contains(".class");
																					}
													                    	 }); 

}
