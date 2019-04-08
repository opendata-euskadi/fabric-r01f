package r01f.inject;

import java.util.Map;

import com.google.inject.EvenMoreTypes;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import lombok.experimental.Accessors;
import r01f.annotations.Immutable;

/**
 * Reference to a GUICE binding that returns a {@link Map} with also guiced injected instances
 * @see {@link com.google.inject.multibindings.MapBinder}
 * 
 * This type is useful to store in a config XML a guice identifier to a {@link com.google.inject.multibindings.MapBinder} 
 * that contains some object instances also managed by guice
 * 
 * Ej: an attribute as >	bindingId='myId'
 * 
 * The normal use is as follows:
 * <pre>
 * 1.- From an interface and it's implementation:
 * </pre>
 * <pre class='brush:java'>
 *	public static interface MyInterface { 
 *		public void doSomething();
 *	}
 *	public static class MyImpl 
 *			 implements MyInterface {
 *		@Override
 *		public void doSomething() {
 *			System.out.println("doing something...");
 *		}
 *	}
 * </pre>
 * <pre>
 * 2.- A map of MyInterface instances using {@link com.google.inject.multibindings.MapBinder} is defined 
 * 	   and this {@link Map} is linked to an id
 * </pre>
 * <pre class='brush:java'>
 * 		MapBinder<String,MyInterface> mapBinder = MapBinder.newMapBinder(binder,
 *																    	 String.class,MyInterface.class,
 *																		Names.named("myId"));
 *		mapBinder.addBinding("myId_1").toInstance(new MyImpl());
 *		mapBinder.addBinding("myId_2").to(MyImpl.class).in(Singleton.class);
 * </pre>
 * <pre>
 * 3.- An instance of the Map<String,MyInterface> tagged with 'myId' can be obtained using
 * 	   a guice reference represented by this object
 * </pre>
 * <pre class='brush:java'>
 *		GuiceNamedMapBinding<String,MyInterface> id = GuiceNamedMapBinding.create("myId"); 
 *		Map<String,MyInterface> impls = id.getInstance(MyInterface.class);
 * </pre>
 * 
 * @param <K> the map key type
 * @param <V> the map value type
 */
@Immutable
@Accessors(prefix="_")
public class GuiceNamedMapBinding<K,V>
     extends GuiceNamedBindingBase {
	
	private static final long serialVersionUID = 501461995176971095L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR  
/////////////////////////////////////////////////////////////////////////////////////////
	public GuiceNamedMapBinding(final String id) {
		super(id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> GuiceNamedMapBinding<K,V> create(final String id) {
		GuiceNamedMapBinding<K,V> outBinding = new GuiceNamedMapBinding<K,V>(id);
		return outBinding;
	}
	public static <K,V> GuiceNamedMapBinding<K,V> forId(final String id) {
		return GuiceNamedMapBinding.create(id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retrieves from guice an instance of a {@link Map} type identified by the id encapsulated in this object
	 * Ej: id 'myId' is assigned to a {@link Map} instance built with {@link com.google.inject.multibindings.MapBinder}
	 * <pre class='brush:java'>
	 * 		MapBinder<String,MyInterface> mapBinder = MapBinder.newMapBinder(binder,
	 *																    	 String.class,MyInterface.class,
	 *																		 Names.named("myId"));
	 *		mapBinder.addBinding("myId_1").toInstance(new MyImpl());
	 *		mapBinder.addBinding("myId_2").to(MyImpl.class).in(Singleton.class);
	 * </pre>
	 * @param guiceInjector
	 * @param keyType map's keys type
	 * @param valueType map's values type
	 * @return the Map sinstance
	 */
	public Map<K,V> getInstance(final Injector guiceInjector,
						 		final Class<K> keyType,final Class<V> valueType) {
		return guiceInjector.getInstance(Key.get(EvenMoreTypes.mapOf(keyType,valueType),	// Map
												 Names.named(this.getId())));				// annotated with the value stored in id
	}
	/**
	 * Retrieves from guice an instance of a {@link Map} type identified by the id encapsulated in this object
	 * Ej: id 'myId' is assigned to a {@link Map} instance built with {@link com.google.inject.multibindings.MapBinder}
	 * This method is used when either the key or value type are generics
	 * @param guiceInjector
	 * @param keyType map's keys type
	 * @param valueType map's values type
	 * @return the Map sinstance
	 */
	public Map<K,V> getInstance(final Injector guiceInjector,
						 		final TypeLiteral<K> keyType,final TypeLiteral<V> valueType) {
		return guiceInjector.getInstance(Key.get(EvenMoreTypes.mapOf(keyType,valueType),	// Map
												 Names.named(this.getId())));				// annotated with the value stored in id
	}

}
