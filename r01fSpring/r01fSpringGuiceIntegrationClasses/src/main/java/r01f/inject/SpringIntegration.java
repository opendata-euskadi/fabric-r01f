package r01f.inject;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Bridge between Guice and Spring:
 * 
 * The guice module:
 * <pre class='brush:java'>
 *	private static class GuiceBootStrapModule
 *		         extends AbstractModule {
 *		@Override
 *		protected void configure() {
 *			ApplicationContext springAppContext = new AnnotationConfigApplicationContext(SpringConfiguration.class);
 *			bind(BeanFactory.class).toInstance(springAppContext);
 *			
 *			bind(MessageService.class).toProvider(SpringIntegration.fromSpring(MessageService.class,"mockMessageService"));
 *		}
 *	}
 * </pre>
 * 
 * The Spring configuration
 * <pre class='brush:java'>
 *	@Configuration
 *	@NoArgsConstructor
 *	private static class SpringConfiguration {
 *		@Bean	// the method name (mockMessageService) becomes the bean "name"
 *		@SuppressWarnings("static-method")
 *		MessageService mockMessageService() {
 *							return new MessageService() {
 *									@Override
 *									public String getMessage() {
 *											return "Hello World!";
 *									}
 *							};
 *		}
 *	}
 * </pre>
 * 
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class SpringIntegration {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Binds all Spring beans from the given factory by name. 
	 * For a Spring bean named "foo", this method creates a binding to the bean's type and
	 * {@code @Named("foo")}.
	 *
	 * @see com.google.inject.name.Named
	 * @see com.google.inject.name.Names#named(String)
	 */
	public static void bindAll(final Binder binder, 
							   final ListableBeanFactory beanFactory) {
		for (String name : beanFactory.getBeanDefinitionNames()) {
			Class<?> type = beanFactory.getType(name);
			SpringIntegration.bindBean(binder,beanFactory,
				     				   type,name);
		}
	}
	public static <T> void bindBean(final Binder binder,
							 		final ListableBeanFactory beanFactory,
							 		final Class<T> type,final String name) {
		SpringProvider<T> provider = new SpringProvider<T>(type,name);
		try {
			provider._initialize(beanFactory);
		} catch (Exception e) {
			binder.addError(e);
			return;
		}
		binder.bind(type)
			  .annotatedWith(Names.named(name))
			  .toProvider(provider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a provider which looks up objects from Spring using the given
	 * name. 
	 * Expects a binding to
	 * {@link org.springframework.beans.factory.BeanFactory}. 
	 * 
	 * Example usage:
	 * <pre class='brush:java'>
	 * 		bind(DataSource.class).toProvider(fromSpring(DataSource.class, &quot;dataSource&quot;));
	 * </pre>
	 */
	public static <T> Provider<T> fromSpring(final Class<T> type,final String name) {
		// Gets injected!
		return new InjectableSpringProvider<T>(type,name);
	}
	static class InjectableSpringProvider<T> 
		 extends SpringProvider<T> {

		InjectableSpringProvider(final Class<T> type,final String name) {
			super(type,name);
		}

		@Inject
		@Override
		protected void _initialize(final BeanFactory beanFactory) {
			super._initialize(beanFactory);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static class SpringProvider<T>
	  implements Provider<T> {

		private BeanFactory _beanFactory;

		private final Class<T> _type;
		private final String _name;

		private boolean _singleton;	
		private volatile T _singletonInstance;
		
		public SpringProvider(final Class<T> type,final String name) {
			_type = Preconditions.checkNotNull(type);
			_name = Preconditions.checkNotNull(name);
		}
		protected void _initialize(final BeanFactory beanFactory) {
			_beanFactory = beanFactory;
			
			if (!_beanFactory.isTypeMatch(_name,_type)) throw new ClassCastException("Spring bean named '" + _name + "' does not implement " + _type.getName() + ".");
			_singleton = _beanFactory.isSingleton(_name);
		}		
		
		@Override
		public T get() {
			return _singleton ? _gestSingletonInstance() 
							  : _type.cast(_beanFactory.getBean(_name));
		}
		private T _gestSingletonInstance() {
			if (_singletonInstance == null) _singletonInstance = _type.cast(_beanFactory.getBean(_name));	// cache the singleton instance
			return _singletonInstance;
		}
	}

}
