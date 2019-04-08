package r01f.xmlproperties;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.inject.Provider;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.util.types.Strings;

/**
 * @XmlPropertiesComponent annotation implementation needed to use the binder to set a binding 
 * with a {@link XMLPropertiesForAppComponent} {@link Provider}
 * 
 * Usually the {@link XMLPropertiesForAppComponent} is binded using a {@link Provides} annotated method:
 * <pre class='brush:java'>
 *		@Provides @XMLPropertiesComponent("myComponent")
 *		XMLPropertiesForAppComponent provideXMLPropertiesForAppComponent(final XMLProperties props) {
 *			XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AppCode.forId("xx")),
 *																								 AppComponent.forId("myComponent"));
 *			return outPropsForComponent;
 *		}
 * </pre>
 * ... but what if the provider cannot be statically set?? 
 * ... a "dynamic" binding must be used, something like:
 * <pre class='brush:java'>
 *		binder.bind(XMLPropertiesForAppComponent.class)
 *			  .annotatedWith(XMLPropertiesComponent.class)	<-- BUT how a component is set????
 *			  .toProvider(XMLPropertiesForServicesProvider.class)
 *			  .in(Singleton.class);
 * </pre>
 * the solution is:
 * <pre class='brush:java'>
 *		binder.bind(XMLPropertiesForAppComponent.class)
 *			  .annotatedWith(new XMLPropertiesComponentImpl("myComponent"))
 *			  .toProvider(XMLPropertiesForMyComponentProvider.class)
 *			  .in(Singleton.class);
 * </pre>
 * ... although a "longer" version can be used:
 * <pre class='brush:java'>
 *	binder.bind(XMLPropertiesForAppComponent.class)
 *		  .annotatedWith(new XMLPropertiesComponent() {		// see [Binding annotations with attributes] at https://github.com/google/guice/wiki/BindingAnnotations
 *									@Override
 *									public Class<? extends Annotation> annotationType() {
 *										return XMLPropertiesComponent.class;
 *									}
 *									@Override
 *									public String value() {
 *										return "myComponent";
 *									}
 *		  				 })
 *		  .toProvider(new Provider<XMLPropertiesForAppComponent>() {
 *							@Override
 *							public XMLPropertiesForAppComponent get() {
 *								XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AppCode.forId("xx")),
 *																								 					 AppComponent.forId("myComponent"));
 *								return outPropsForComponent;
 *							}
 *		  			  });
 * </pre>
 * 
 * @see http://stackoverflow.com/questions/28549549/guice-inject-based-on-annotation-value
 */
@Accessors(prefix="_")
public class XMLPropertiesComponentImpl 
  implements XMLPropertiesComponent,
		     Serializable {
    	 
      private static final long serialVersionUID = 0;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
   @Getter private final String _value;

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
  	public XMLPropertiesComponentImpl(final String value) {
  		if (Strings.isNullOrEmpty(value)) throw new IllegalArgumentException("XMLPropertiesComponent annotation MUST have a value (ej: @XMLPropertiesComponent(\"default\")");
  		_value = value;
  	}
/////////////////////////////////////////////////////////////////////////////////////////
//  XMLPropertiesComponent
/////////////////////////////////////////////////////////////////////////////////////////  	
	@Override
	public String value() {
		return _value;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
  	@Override
  	public int hashCode() {
  		// This is specified in java.lang.Annotation.
  		return (127 * "value".hashCode()) ^ _value.hashCode();
  	}
  	@Override
  	public boolean equals(Object o) {
  		if (!(o instanceof XMLPropertiesComponent)) {
  			return false;
  		}
  		XMLPropertiesComponent other = (XMLPropertiesComponent)o;
  		return _value.equals(other.value());
  	}
  	@Override
  	public String toString() {
  		return "@" + XMLPropertiesComponent.class.getName() + "(value=" + _value + ")";
  	}
  	@Override
  	public Class<? extends Annotation> annotationType() {
  		return XMLPropertiesComponent.class;
  	}
}