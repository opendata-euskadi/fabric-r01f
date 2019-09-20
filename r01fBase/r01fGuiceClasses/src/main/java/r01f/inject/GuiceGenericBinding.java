package r01f.inject;

import com.google.inject.TypeLiteral;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Binding a type to a generic interface is like
 * <pre class='brush:java'>		
 * 		binder.bind(new TypeLiteral<MyGenericInterface<T>>() {  })
			  .to(MyTypeImplementingTheGenericInterface.class)
 * </pre>
 * This class just STORES the binding definition so that given a definition,
 * the binding could be done like:
 * <pre class='brush:java'>
 * 		// define the binding
 * 		GuiceGenericBinding<MyGenericInterface<T>> bindingDef = new GuiceGenericBinding<>(new TypeLiteral<MyGenericInterface<T>,
 * 																						  MyTypeImplementingTheGenericInterface.class);
 * 		// create the binding
 * 		binder.bind(bindigDef.getGenericType())
 * 			  .to(bindingDef.getImplementingType());
 * </pre> 
 * This is useful to pass a generic bindig def as a method param
 * @param <T>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class GuiceGenericBinding<T> {
	@Getter private final TypeLiteral<T> _genericType;
	@Getter private final Class<? extends T> _implementingType;
}
