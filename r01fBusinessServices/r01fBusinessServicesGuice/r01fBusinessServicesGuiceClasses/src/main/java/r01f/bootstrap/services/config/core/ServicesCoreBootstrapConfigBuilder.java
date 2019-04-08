package r01f.bootstrap.services.config.core;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.bootstrap.services.core.BeanImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.RESTImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.patterns.IsBuilder;
import r01f.services.core.CoreService;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.util.types.collections.CollectionUtils;


/**
 * Builder for ServicesConfig
 * Usage: 
 * <pre class='brush:java'>
 * </pre>
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServicesCoreBootstrapConfigBuilder 
	       implements IsBuilder {
//////////////////////////////////////////////////////////////////////////////
//  CORE BEAN
//////////////////////////////////////////////////////////////////////////////
	public static ServicesConfigBuilderCOREBootstapTypeStep forCoreAppAndModule(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return new ServicesCoreBootstrapConfigBuilder() { /* nothing */ }
				.new ServicesConfigBuilderCOREBootstapTypeStep(coreAppCode,coreMod);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ServicesConfigBuilderCOREBootstapTypeStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		
		public ServicesConfigBuilderBeanCOREBootstapGuiceModuleStep beanImplemented() {
			return new ServicesConfigBuilderBeanCOREBootstapGuiceModuleStep(_coreAppCode,_coreModule);
		}
		public ServicesConfigBuilderRESTCOREBootstapGuiceModuleStep restImplemented() {
			return new ServicesConfigBuilderRESTCOREBootstapGuiceModuleStep(_coreAppCode,_coreModule);
		}
		public ServicesConfigBuilderServletCOREBootstapGuiceModuleStep servletImplemented() {
			return new ServicesConfigBuilderServletCOREBootstapGuiceModuleStep(_coreAppCode,_coreModule);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE BEAN
/////////////////////////////////////////////////////////////////////////////////////////
	public final class ServicesConfigBuilderBeanCOREBootstapGuiceModuleStep 
		 extends ServicesConfigBuilderCOREBootstapGuiceModuleStepBase<BeanImplementedServicesCoreBootstrapGuiceModuleBase,
		 															  ServicesConfigBuilderBeanCOREBootstapServicesImplStep> {
		public ServicesConfigBuilderBeanCOREBootstapGuiceModuleStep(final CoreAppCode coreAppCode,final CoreModule coreModule) {
			super(coreAppCode,coreModule);
		}
		@Override
		ServicesConfigBuilderBeanCOREBootstapServicesImplStep _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   											  final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			return new ServicesConfigBuilderBeanCOREBootstapServicesImplStep(_coreAppCode,_coreModule,
																			 coreBootstrapGuiceModuleType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ServicesConfigBuilderBeanCOREBootstapServicesImplStep 
	  implements ServicesCoreConfigBuilderStep {
		
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		
		public ServicesConfigBuilderBeanCOREBootstapSubModuleStep findServicesExtending(final Class<? extends CoreService> servicesImplIfaceType) {
			return new ServicesConfigBuilderBeanCOREBootstapSubModuleStep(_coreAppCode,_coreModule,
																		  _coreBootstrapGuiceModuleType,
																		   servicesImplIfaceType);
		}
	}
	public final class ServicesConfigBuilderBeanCOREBootstapSubModuleStep 
 		 extends ServicesConfigBuilderCOREBootstapSubModuleStepBase<BeanImplementedServicesCoreBootstrapGuiceModuleBase,
 		 															ServicesConfigBuilderBeanCOREBootstapModuleBuildStep> {
		
		protected final Class<? extends CoreService> _coreServicesBaseType;
		
		public ServicesConfigBuilderBeanCOREBootstapSubModuleStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   								  final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType,
																  final Class<? extends CoreService> coreServicesBaseType) {
			super(coreAppCode,coreModule,
				  coreBootstrapGuiceModuleType);
			_coreServicesBaseType = coreServicesBaseType;
		}
		@Override
		ServicesConfigBuilderBeanCOREBootstapModuleBuildStep _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
																			 final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType,
																			 final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs) {
			return new ServicesConfigBuilderBeanCOREBootstapModuleBuildStep(coreAppCode,coreModule,
																			coreBootstrapGuiceModuleType,
																			subModulesCfgs,
																			_coreServicesBaseType);
		}
	}
	public final class ServicesConfigBuilderBeanCOREBootstapModuleBuildStep
		 extends ServicesConfigBuilderCOREBootstapModuleBuildStepBase<BeanImplementedServicesCoreBootstrapGuiceModuleBase,
		 															  ServicesCoreBootstrapConfigWhenBeanExposed> {
		protected final Class<? extends CoreService> _coreServicesBaseType;
		
		public ServicesConfigBuilderBeanCOREBootstapModuleBuildStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   								    final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType,
																    final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
																    final Class<? extends CoreService> coreServicesBaseType) {
			super(coreAppCode,coreModule,
				  coreBootstrapGuiceModuleType,
				  subModulesCfgs);
			_coreServicesBaseType = coreServicesBaseType;
		}
		@Override
		public ServicesCoreBootstrapConfigWhenBeanExposed build() {
			return new ServicesCoreGuiceBootstrapConfigWhenBeanExposed(_coreAppCode,_coreModule,
																	   _coreBootstrapGuiceModuleType,
															   	  	   _subModulesCfgs,
															   	  	   _coreServicesBaseType);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE REST
/////////////////////////////////////////////////////////////////////////////////////////
	public final class ServicesConfigBuilderRESTCOREBootstapGuiceModuleStep 
	     extends ServicesConfigBuilderCOREBootstapGuiceModuleStepBase<RESTImplementedServicesCoreBootstrapGuiceModuleBase,
	     															  ServicesConfigBuilderRESTCOREBootstapSubModuleStep> {
		public ServicesConfigBuilderRESTCOREBootstapGuiceModuleStep(final CoreAppCode coreAppCode,final CoreModule coreModule) {
			super(coreAppCode,coreModule);
		}
		@Override
		ServicesConfigBuilderRESTCOREBootstapSubModuleStep _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   										   final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			return new ServicesConfigBuilderRESTCOREBootstapSubModuleStep(_coreAppCode,_coreModule,
																	       coreBootstrapGuiceModuleType);
		}
	}
	public final class ServicesConfigBuilderRESTCOREBootstapSubModuleStep 
 		 extends ServicesConfigBuilderCOREBootstapSubModuleStepBase<RESTImplementedServicesCoreBootstrapGuiceModuleBase,
 		 															ServicesConfigBuilderRESTCOREBootstapModuleBuildStep> {
		
		public ServicesConfigBuilderRESTCOREBootstapSubModuleStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   								  final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			super(coreAppCode,coreModule,
				  coreBootstrapGuiceModuleType);
		}
		@Override
		ServicesConfigBuilderRESTCOREBootstapModuleBuildStep _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
																			 final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType,
																			 final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs) {
			return new ServicesConfigBuilderRESTCOREBootstapModuleBuildStep(coreAppCode,coreModule,
																			coreBootstrapGuiceModuleType,
																			subModulesCfgs);
		}
	}
	public final class ServicesConfigBuilderRESTCOREBootstapModuleBuildStep
		 extends ServicesConfigBuilderCOREBootstapModuleBuildStepBase<RESTImplementedServicesCoreBootstrapGuiceModuleBase,
		 															  ServicesCoreBootstrapConfigWhenRESTExposed> {
		public ServicesConfigBuilderRESTCOREBootstapModuleBuildStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   								    final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType,
																    final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs) {
			super(coreAppCode,coreModule,
				  coreBootstrapGuiceModuleType,
				  subModulesCfgs);
		}
		@Override
		public ServicesCoreBootstrapConfigWhenRESTExposed build() {
			return new ServicesCoreGuiceBootstrapConfigWhenRESTExposed(_coreAppCode,_coreModule,
																  	   _coreBootstrapGuiceModuleType,
																  	   _subModulesCfgs);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE SERVLET
/////////////////////////////////////////////////////////////////////////////////////////
	public final class ServicesConfigBuilderServletCOREBootstapGuiceModuleStep 
	     extends ServicesConfigBuilderCOREBootstapGuiceModuleStepBase<ServletImplementedServicesCoreBootstrapGuiceModuleBase,
	     															  ServicesConfigBuilderServletCOREBootstapSubModuleStep> {
		public ServicesConfigBuilderServletCOREBootstapGuiceModuleStep(final CoreAppCode coreAppCode,final CoreModule coreModule) {
			super(coreAppCode,coreModule);
		}
		@Override
		ServicesConfigBuilderServletCOREBootstapSubModuleStep _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   										      final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			return new ServicesConfigBuilderServletCOREBootstapSubModuleStep(_coreAppCode,_coreModule,
																	       coreBootstrapGuiceModuleType);
		}
	}
	public final class ServicesConfigBuilderServletCOREBootstapSubModuleStep 
 		 extends ServicesConfigBuilderCOREBootstapSubModuleStepBase<ServletImplementedServicesCoreBootstrapGuiceModuleBase,
 		 															ServicesConfigBuilderServletCOREBootstapModuleBuildStep> {
		
		public ServicesConfigBuilderServletCOREBootstapSubModuleStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   								    final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			super(coreAppCode,coreModule,
				  coreBootstrapGuiceModuleType);
		}
		@Override
		ServicesConfigBuilderServletCOREBootstapModuleBuildStep _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
																			    final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType,
																			    final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs) {
			return new ServicesConfigBuilderServletCOREBootstapModuleBuildStep(coreAppCode,coreModule,
																			   coreBootstrapGuiceModuleType,
																			   subModulesCfgs);
		}
	}
	public final class ServicesConfigBuilderServletCOREBootstapModuleBuildStep
		 extends ServicesConfigBuilderCOREBootstapModuleBuildStepBase<ServletImplementedServicesCoreBootstrapGuiceModuleBase,
		 															  ServicesCoreBootstrapConfigWhenServletExposed> {
		public ServicesConfigBuilderServletCOREBootstapModuleBuildStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   								       final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType,
																       final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs) {
			super(coreAppCode,coreModule,
				  coreBootstrapGuiceModuleType,
				  subModulesCfgs);
		}
		@Override
		public ServicesCoreBootstrapConfigWhenServletExposed build() {
			return new ServicesCoreGuiceBootstrapConfigWhenServletExposed(_coreAppCode,_coreModule,
																  	      _coreBootstrapGuiceModuleType,
																  	      _subModulesCfgs);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BASE
/////////////////////////////////////////////////////////////////////////////////////////
	private interface ServicesCoreConfigBuilderStep {
		// just a marker interface
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	abstract class ServicesConfigBuilderCOREBootstapGuiceModuleStepBase<B extends ServicesCoreBootstrapGuiceModule,
																		S extends ServicesCoreConfigBuilderStep> 
		implements ServicesCoreConfigBuilderStep {
		
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		
		public S bootstrappedBy(final Class<? extends B> coreBootstrapGuiceModuleType) {
			return _createNextStep(_coreAppCode,_coreModule,
								   coreBootstrapGuiceModuleType);
		}
		abstract S _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   final Class<? extends B> coreBootstrapGuiceModuleType);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	abstract class ServicesConfigBuilderCOREBootstapSubModuleStepBase<B extends ServicesCoreBootstrapGuiceModule,
																	  S extends ServicesCoreConfigBuilderStep> 
		implements ServicesCoreConfigBuilderStep {
		
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends B> _coreBootstrapGuiceModuleType;
		
		public S withSubModulesConfigs(final ServicesCoreSubModuleBootstrapConfig<?>... subModulesCfgs) {
			return _createNextStep(_coreAppCode,_coreModule,
								   _coreBootstrapGuiceModuleType,
								   CollectionUtils.hasData(subModulesCfgs) ? Lists.newArrayList(subModulesCfgs) : null);
		}
		@SuppressWarnings("unchecked")
		public <C extends ServicesCoreBootstrapConfig> C build() {
			S nextStep = this.withoutSubModules();
			ServicesConfigBuilderCOREBootstapModuleBuildStepBase<?,C> nextStepTyped = (ServicesConfigBuilderCOREBootstapModuleBuildStepBase<?,C>)nextStep;
			return nextStepTyped.build();
		}
		public S withoutSubModules() {
			return _createNextStep(_coreAppCode,_coreModule,
								   _coreBootstrapGuiceModuleType,
								   null);
		}
		abstract S _createNextStep(final CoreAppCode coreAppCode,final CoreModule coreModule,
								   final Class<? extends B> coreBootstrapGuiceModuleType,
								   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	abstract class ServicesConfigBuilderCOREBootstapModuleBuildStepBase<B extends ServicesCoreBootstrapGuiceModule,
																		C extends ServicesCoreBootstrapConfig> 
		implements ServicesCoreConfigBuilderStep {
		
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends B> _coreBootstrapGuiceModuleType;
		protected final Collection<ServicesCoreSubModuleBootstrapConfig<?>> _subModulesCfgs;
		
		public abstract C build();
	}
}
