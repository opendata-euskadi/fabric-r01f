package r01f.inject;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import lombok.experimental.Accessors;
import r01f.annotations.Immutable;

/**
 * Reference to a GUICE binding
 * This type is useful to store guice ids in config XMLs
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
 * 2.- In guice the interface and impl binding is tagged by for example 'myId'
 * </pre>
 * <pre class='brush:java'>
 *		binder.bind(MyInterface.class).annotatedWith(Names.named("myId"))
 *			  .to(MyImpl.class)
 *			  .in(Singleton.class);
 * </pre>
 * <pre>
 * 3.- Then an instance of MyInterface tagged by 'myId' can be obtained from a guice reference
 *     represented by this type
 * </pre>
 * <pre class='brush:java'>
 *		GuiceNamedBinding<MyInterface> id = GuiceNamedBinding.create("myId"); 
 *		MyInterface impl = id.getInstance(injector,
 *										  MyInterface.class);
 * </pre>
 * 
 * @param <T> the expected type. This is included only to have load time type checking
 */
@Immutable
@Accessors(prefix="_")
public class GuiceNamedBinding<T>
     extends GuiceNamedBindingBase {
	
	private static final long serialVersionUID = -8513841164425982182L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR  
/////////////////////////////////////////////////////////////////////////////////////////
	public GuiceNamedBinding(final String id) {
		super(id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> GuiceNamedBinding<T> create(final String id) {
		GuiceNamedBinding<T> outBinding = new GuiceNamedBinding<T>(id);
		return outBinding;
	}
	public static <T> GuiceNamedBinding<T> forId(final String id) {
		return GuiceNamedBinding.create(id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retrieves from guice an instance of the type identified by the id encapsulated in
	 * this object
	 * Ej: If id 'myId' is assigned to a MyInterface instance
	 * <pre class='brush:java'>
	 * 		binder.bind(MyInterface.class).annotatedWith(Names.named("myId"))
	 *		  	  .to(MyImpl.class)
	 *			  .in(Singleton.class);
	 * </pre>
	 * @param guiceInjector guice injector
	 * @param type the type to retrieve (ie MyInterface)
	 * @return the binded instance in guice
	 */
	public T getInstance(final Injector guiceInjector,
						 final Class<T> type) {
		return guiceInjector.getInstance(Key.get(type,							// instance of type T
												 Names.named(this.getId())));	// annotated by the identifier 
	}
	/**
	 * Retrieves from guice an instance of the type identified by the id encapsulated in
	 * this object
	 * This is used when the type is a generic type
	 * @param guiceInjector guice injector
	 * @param typeLiteral the generic type to retrieve (ej: new TypeLiteral<MyInterface<String>>() {};) 
	 * @return the binded instance in guice
	 */
	public T getInstance(final Injector guiceInjector,
						 final TypeLiteral<T> typeLiteral) {
		return guiceInjector.getInstance(Key.get(typeLiteral,					// instance of type T
												 Names.named(this.getId())));	// annotated by the identifier 
	}
}
