package r01f.objectstreamer;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.internal.R01F;
import r01f.patterns.IsBuilder;
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
		return new MarshallerBuilder() { /* nothing */ }
						.new MarshallerBuilderModulesStep(CollectionUtils.hasData(appCodes) ? Sets.<AppCode>newLinkedHashSet(Lists.newArrayList(appCodes))
																							: Sets.<AppCode>newLinkedHashSet());
	}
	public static MarshallerBuilderModulesStep findTypesToMarshallAt(final Collection<AppCode> appCodes) {
		return new MarshallerBuilder() { /* nothing */ }
						.new MarshallerBuilderModulesStep(Sets.newLinkedHashSet(appCodes));
	}
	public static MarshallerImpl build() {
		return new MarshallerImpl(Sets.<AppCode>newLinkedHashSet(),
								  Sets.<MarshallerModule>newLinkedHashSet(),
								  DEFAULT_MARSHALLER_CHARSET);		
	}
	public static MarshallerImpl build(final Charset defaultCharset) {
		return new MarshallerImpl(Sets.<AppCode>newLinkedHashSet(),
								  Sets.<MarshallerModule>newLinkedHashSet(),
								  defaultCharset);		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MarshallerBuilderModulesStep {
		private final Set<AppCode> _appCodes;
		
		public MarshallerBuilderBuildStep registerModules(final MarshallerModule... marshallerModules) {
			return new MarshallerBuilderBuildStep(_appCodes,
												  CollectionUtils.hasData(marshallerModules) ? Sets.newLinkedHashSet(Lists.newArrayList(marshallerModules))
														  									 : Sets.<MarshallerModule>newLinkedHashSet());
		}
		public MarshallerBuilderBuildStep registerModules(final Set<? extends MarshallerModule> marshallerModules) {
			return new MarshallerBuilderBuildStep(_appCodes,
												  marshallerModules);
		}
		public MarshallerImpl build() {
			return new MarshallerImpl(_appCodes,
									  Sets.<MarshallerModule>newLinkedHashSet(),		// no custom jackson modules
									  DEFAULT_MARSHALLER_CHARSET);
		}
		public MarshallerImpl build(final Charset defaultCharset) {
			return new MarshallerImpl(_appCodes,
									  Sets.<MarshallerModule>newLinkedHashSet(),		// no custom jackson modules
									  defaultCharset);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MarshallerBuilderBuildStep {
		private final Set<AppCode> _appCodes;
		private final Set<? extends MarshallerModule> _customModules;
					
		public MarshallerImpl build() {
			return new MarshallerImpl(Sets.newLinkedHashSet(_appCodes),
									  _customModules,
									  DEFAULT_MARSHALLER_CHARSET);		
		}
		public MarshallerImpl build(final Charset defaultCharset) {
			return new MarshallerImpl(Sets.newLinkedHashSet(_appCodes),
									  _customModules,
									  defaultCharset);		
		}
	}
}
