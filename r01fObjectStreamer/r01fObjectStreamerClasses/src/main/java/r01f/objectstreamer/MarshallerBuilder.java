package r01f.objectstreamer;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.internal.R01F;
import r01f.patterns.IsBuilder;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * Builds a {@link Marshaller}
 * <pre class="brush:java">
 * 		Marshaller marshaller = MarshallerBuilder.findTypesToMarshallAt(AppCode.forId("xxx"))
 * 												 .build();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MarshallerBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	// BEWARE: JSON specification states, that only valid encodings are UTF-8, UTF-16 and UTF-32.
	//		   No other encodings (like Latin-1) can be used
	private static final Charset DEFAULT_MARSHALLER_CHARSET = Charset.forName(R01F.ENCODING_UTF_8);	// Charset.defaultCharset();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static MarshallerBuilderModulesStep findTypesToMarshallAt(final AppCode... appCodes) {
		return MarshallerBuilder.findTypesToMarshallAtJavaPackages(appCodes != null ? FluentIterable.from(appCodes)
																							.transform(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
																							.toSet()
																				    : Sets.<JavaPackage>newLinkedHashSet());
	}
	public static MarshallerBuilderModulesStep findTypesToMarshallAt(final Collection<AppCode> appCodes) {
		return MarshallerBuilder.findTypesToMarshallAtJavaPackages(appCodes != null ? FluentIterable.from(appCodes)
																							.transform(JavaPackage.APP_CODE_TO_JAVA_PACKAGE)
																							.toSet()
																				    : Sets.<JavaPackage>newLinkedHashSet());
	}
	public static MarshallerBuilderModulesStep findTypesToMarshallAtJavaPackages(final JavaPackage... javaPackages) {
		return new MarshallerBuilder() { /* nothing */ }
					.new MarshallerBuilderModulesStep(CollectionUtils.hasData(javaPackages) ? Sets.<JavaPackage>newLinkedHashSet(Lists.newArrayList(javaPackages))
																						    : Sets.<JavaPackage>newLinkedHashSet());
	}
	public static MarshallerBuilderModulesStep findTypesToMarshallAtJavaPackages(final Collection<JavaPackage> javaPackages) {
		return new MarshallerBuilder() { /* nothing */ }
					.new MarshallerBuilderModulesStep(Sets.newLinkedHashSet(javaPackages));
	}
	public static MarshallerImpl build() {
		return new MarshallerImpl(Sets.<JavaPackage>newLinkedHashSet(),
								  Sets.<MarshallerModule>newLinkedHashSet(),
								  Sets.<SimpleModule>newLinkedHashSet(),
								  DEFAULT_MARSHALLER_CHARSET);
	}
	public static MarshallerImpl build(final Charset defaultCharset) {
		return new MarshallerImpl(Sets.<JavaPackage>newLinkedHashSet(),
								  Sets.<MarshallerModule>newLinkedHashSet(),
								  Sets.<SimpleModule>newLinkedHashSet(),
								  defaultCharset);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MarshallerBuilderModulesStep {
		private final Set<JavaPackage> _javaPackages;

		public MarshallerBuilderBuildStep registerModules(final MarshallerModule... marshallerModules) {
			return new MarshallerBuilderBuildStep(_javaPackages,
												  CollectionUtils.hasData(marshallerModules) ? Sets.newLinkedHashSet(Lists.newArrayList(marshallerModules))
														  									 : Sets.<MarshallerModule>newLinkedHashSet(),
												  null);
		}
		public MarshallerBuilderBuildStep registerModules(final Set<? extends MarshallerModule> marshallerModules) {
			return new MarshallerBuilderBuildStep(_javaPackages,
												  marshallerModules,
												  null);
		}
		public MarshallerBuilderBuildStep registerModules(final Set<? extends MarshallerModule> marshallerModules,
														  final Set<? extends SimpleModule> simpleModules) {
			return new MarshallerBuilderBuildStep(_javaPackages,
												  marshallerModules,
												  simpleModules);
		}
		public MarshallerImpl build() {
			return new MarshallerImpl(_javaPackages,
									  Sets.<MarshallerModule>newLinkedHashSet(),		// no custom jackson modules
									  null,
									  DEFAULT_MARSHALLER_CHARSET);
		}
		public MarshallerImpl build(final Charset defaultCharset) {
			return new MarshallerImpl(_javaPackages,
									  Sets.<MarshallerModule>newLinkedHashSet(),		// no custom jackson modules
									  null,
									  defaultCharset);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MarshallerBuilderBuildStep {
		private final Set<JavaPackage> _javaPackages;
		private final Set<? extends MarshallerModule> _customModules;
		private final Set<? extends SimpleModule> _simpleModules;

		public MarshallerImpl build() {
			return new MarshallerImpl(Sets.newLinkedHashSet(_javaPackages),
									  _customModules,
									  _simpleModules,
									  DEFAULT_MARSHALLER_CHARSET);
		}
		public MarshallerImpl build(final Charset defaultCharset) {
			return new MarshallerImpl(Sets.newLinkedHashSet(_javaPackages),
									  _customModules,
									  _simpleModules,
									  defaultCharset);
		}
	}
}
