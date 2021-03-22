package r01f.objectstreamer.util;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.outline.TypeOutline;
import r01f.reflection.outline.TypeOutline.OutlineTreeNode;
import r01f.reflection.scanner.SubTypeOfScanner;
import r01f.types.JavaPackage;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class TypeScan {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public static class TypeAnnotation<A extends Annotation> {
		@Getter private final Class<?> _type;
		@Getter private final A _annotation;
	}
	/**
	 * Finds a given annotation in the java type's hierarchy
	 * @param annType
	 * @param javaType
	 * @return the annotation alongside the type of the hierarchy where the annotation is defined
	 */
	public static <A extends Annotation> TypeAnnotation<A> findTypeAnnotaion(final Class<A> annType,
													  	 				 	 final JavaType javaType) {
		TypeOutline typeOutline = TypeOutline.from(javaType.getRawClass());
		return _recurseFindTypeAnnotationAtSuperTypeOrInterface(annType,
																typeOutline.getBottomType());
	}
	/**
	 * Finds a given annotation in the java type's hierarchy
	 * @param annType
	 * @param javaType
	 * @return the annotation alongside the type of the hierarchy where the annotation is defined
	 */
	public static <A extends Annotation> TypeAnnotation<A> findTypeAnnotaion(final Class<A> annType,
													  	 				 	 final Class<?> type) {
		TypeOutline typeOutline = TypeOutline.from(type);
		return _recurseFindTypeAnnotationAtSuperTypeOrInterface(annType,
																typeOutline.getBottomType());
	}
	private static <A extends Annotation> TypeAnnotation<A> _recurseFindTypeAnnotationAtSuperTypeOrInterface(final Class<A> annType,
																		 				 					 final OutlineTreeNode node) {
		TypeAnnotation<A> outTypeAnnotation = null;

		A annotation = node.getObjectType().getAnnotation(annType);
		if (annotation == null) {
			for (OutlineTreeNode parent : node.getParents()) {
				outTypeAnnotation = _recurseFindTypeAnnotationAtSuperTypeOrInterface(annType,
																					 parent);
				if (outTypeAnnotation != null) break;
			}
		} else {
			outTypeAnnotation = new TypeAnnotation<A>(node.getObjectType(),annotation);
		}
		return outTypeAnnotation;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Does the type has a given annotation in it's hierarchy?
	 * @param annType
	 * @param javaType
	 * @return
	 */
	public static boolean hasSuperTypeAnnotatedWith(final Class<? extends Annotation> annType,
													final JavaType javaType) {
		TypeOutline typeOutline = TypeOutline.from(javaType.getRawClass());
		return _recurseFindSuperTypeOrInterfaceAnnotatedWith(annType,
															 typeOutline.getBottomType());
	}
	private static boolean _recurseFindSuperTypeOrInterfaceAnnotatedWith(final Class<? extends Annotation> annType,
																		 final OutlineTreeNode node) {
		boolean outAnnotated = node.getObjectType().isAnnotationPresent(annType);
		if (!outAnnotated) {
			for (OutlineTreeNode parent : node.getParents()) {
				outAnnotated = _recurseFindSuperTypeOrInterfaceAnnotatedWith(annType,
																			 parent);
				if (outAnnotated) break;
			}
		}
		return outAnnotated;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds subtypes of a certain base (abstract / interface class)
	 * @param baseClass
	 * @return
	 */
	public static <T> Set<Class<? extends T>> findSubTypesOfInApps(final Class<T> baseClass,
															 	   final Collection<AppCode> appCodes) {
		return TypeScan.findSubTypesOfInJavaPackages(baseClass,
									   				 FluentIterable.from(appCodes)
														 .transform(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
														 .toSet());
	}
	/**
	 * Finds subtypes of a certain base (abstract / interface class)
	 * BEWARE! this type is synchronized because SubTypeOfScanner.findSubTypesAt(...) is NOT thread safe
	 * 		   (this method is just called ONCE from MarshallerAnnotationIntrospector when the Marshalling is bootstrapped)
	 * @param baseClass
	 * @return
	 */
	public synchronized static <T> Set<Class<? extends T>> findSubTypesOfInJavaPackages(final Class<T> baseClass,
															 			   			    final Collection<JavaPackage> javaPackages) {
		// find the sub-types
		Set<Class<? extends T>> outSubTypes = SubTypeOfScanner.findSubTypesAt(baseClass,
																			  javaPackages);
		// filter instanciable types
		return FluentIterable.from(outSubTypes)
							 .filter(new Predicate<Object>() {
										@Override
										public boolean apply(final Object obj) {
											return ReflectionUtils.isInstanciable((Class<?>)obj);
										}
								  })
							 .toSet();
	}
}
