package r01f.objectstreamer;

import java.util.Set;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import r01f.guids.CommonOIDs.AppCode;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;

public class MarshallerMapperForJson
	 extends ObjectMapper {

	private static final long serialVersionUID = -4318987020423327233L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static MarshallerMapperForJson forApps(final AppCode... appCodes) {
		return MarshallerMapperForJson.forApps(appCodes != null ? Sets.<AppCode>newLinkedHashSet(Lists.newArrayList(appCodes))
								  							    : Sets.<AppCode>newLinkedHashSet());
	}
	public static MarshallerMapperForJson forApps(final Set<AppCode> appCodes) {
		return new MarshallerMapperForJson(FluentIterable.from(appCodes)
												.transform(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
												.toSet());
	}
	public static MarshallerMapperForJson forJavaPackages(final Set<JavaPackage> javaPackages) {
		return new MarshallerMapperForJson(javaPackages);
	}
	public static MarshallerMapperForJson forJavaPackages(final JavaPackage... javaPackages) {
		return new MarshallerMapperForJson(javaPackages);
	}
	public MarshallerMapperForJson(final Set<JavaPackage> javaPackages) {
		this(javaPackages,
			 null);	// no custom modules
	}
	public MarshallerMapperForJson(final JavaPackage... javaPackages) {
		this(javaPackages != null ? Sets.<JavaPackage>newLinkedHashSet(Lists.newArrayList(javaPackages))
			 					  : Sets.<JavaPackage>newLinkedHashSet(),
			  null);	// no custom modules
	}
	public MarshallerMapperForJson(final Set<JavaPackage> javaPackages,
								   final Set<? extends MarshallerModule> jacksonModules) {
		// [1] - register the r01f module
		MarshallerModuleForJson mod = new MarshallerModuleForJson(javaPackages);		// BEWARE!!! JSON Module!
		this.registerModule(mod);

		// [2] - register given modules
		if (CollectionUtils.hasData(jacksonModules)) {
			for (MarshallerModule jsonMod : jacksonModules) {
				if (!(jsonMod instanceof Module)) throw new IllegalArgumentException(String.format("% MUST be a subtype of %s to be a jackson module",
																								   jsonMod.getClass(),Module.class));
				this.registerModule((Module)jsonMod);
			}
		}

		// [3] - Global default config
		MarshallerObjectMappers.setDefaultConfig(this);
	}
}
