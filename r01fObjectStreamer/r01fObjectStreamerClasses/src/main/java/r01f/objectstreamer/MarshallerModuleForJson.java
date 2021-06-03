package r01f.objectstreamer;

import java.util.Set;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.google.common.collect.FluentIterable;

import r01f.guids.CommonOIDs.AppCode;
import r01f.objectstreamer.annotationintrospector.MarshallerAnnotationIntrospector;
import r01f.types.JavaPackage;

/**
 * see https://spin.atomicobject.com/2016/07/01/custom-serializer-jackson/
 */
public class MarshallerModuleForJson
	 extends MarshallerModuleBase {

	private static final long serialVersionUID = 526390669822422298L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String NAME = "r01.marshallerJsonModule";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerModuleForJson(final Set<JavaPackage> javaPackages) {
		super(NAME,VersionUtil.versionFor(MarshallerModuleForJson.class),
			  javaPackages);
	}
	public static MarshallerModuleForJson forJavaPackages(final Set<JavaPackage> javaPackages) {
		return new MarshallerModuleForJson(javaPackages);
	}
	public static MarshallerModuleForJson forApps(final Set<AppCode> appCodes) {
		return new MarshallerModuleForJson(FluentIterable.from(appCodes)
											 			 .transform(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
											 			 .toSet());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setupModule(final Module.SetupContext context) {
		super.setupModule(context);

		// Append after other introspectors (instead of before) since
		// explicit annotations should have precedence
		context.appendAnnotationIntrospector(new MarshallerAnnotationIntrospector(_javaPackages,
																				  context));
	}
}
