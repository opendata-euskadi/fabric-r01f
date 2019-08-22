package r01f.objectstreamer;

import java.util.Set;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.google.common.collect.FluentIterable;

import r01f.guids.CommonOIDs.AppCode;
import r01f.objectstreamer.annotationintrospector.MarshallerXmlAnnotationIntrospector;
import r01f.types.JavaPackage;

/**
 * see https://spin.atomicobject.com/2016/07/01/custom-serializer-jackson/
 */
public class MarshallerModuleForXml
	 extends MarshallerModuleBase {

	private static final long serialVersionUID = 526390669822422298L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String NAME = "r01.marshallerXmlModule";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerModuleForXml(final Set<JavaPackage> javaPackages) {
		super(NAME,VersionUtil.versionFor(MarshallerModuleForXml.class),
			  javaPackages);
	}
	public static MarshallerModuleForXml forJavaPackages(final Set<JavaPackage> javaPackages) {
		return new MarshallerModuleForXml(javaPackages);
	}
	public static MarshallerModuleForXml forApps(final Set<AppCode> appCodes) {
		return new MarshallerModuleForXml(FluentIterable.from(appCodes)
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
        context.appendAnnotationIntrospector(new MarshallerXmlAnnotationIntrospector(_javaPackages,
        																			 context));
	}
}
