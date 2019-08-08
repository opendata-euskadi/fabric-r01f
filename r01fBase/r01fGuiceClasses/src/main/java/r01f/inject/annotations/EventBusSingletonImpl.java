package r01f.inject.annotations;

import java.lang.annotation.Annotation;

import r01f.util.types.Strings;

/**
 * A trick to create an annotation when binding:
 * <pre class='brush:java'>
 *		binder.bind(EventBus.class)
 *			  .annotatedWith(new EventBusSingletonImpl("view"))
 *			  .in(Singleton.class);
 * </pre>
 */
public class EventBusSingletonImpl
  implements EventBusSingleton {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _value;
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public EventBusSingletonImpl(final String value) {
		_value = value;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String usedFor() {
		return _value;
	}
	@Override
	public Class<? extends Annotation> annotationType() {
		return EventBusSingleton.class;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		// This is specified in java.lang.Annotation.
		// BEWARE!! "usedFor".hashCode() <-- usedFor MUST match the annotation property
		return (127 * "usedFor".hashCode()) ^ _value.hashCode();
	}
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof EventBusSingleton)) return false;
		EventBusSingleton other = (EventBusSingleton)o;
		return _value.equals(other.usedFor());
	}
	@Override
	public String toString() {
		return Strings.customized("@{}(usedFor={})",
								  EventBusSingleton.class.getSimpleName(),_value);
	}
}
