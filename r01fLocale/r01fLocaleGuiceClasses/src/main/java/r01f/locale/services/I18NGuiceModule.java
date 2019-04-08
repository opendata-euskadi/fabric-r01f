package r01f.locale.services;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;

import r01f.locale.services.I18NLocalized;

/**
 * Guice module for the I18N 
 */
public class I18NGuiceModule 
  implements Module {
	@Override
	public void configure(final Binder binder) {
        // When a @I18NLocalized annotated type is injected, all members of the types are inspected to find 
        // the ones of I18NService type and @I18NMessageBundleService annotated; if it's the case, the member is injected
        // with the bundle
        binder.bindListener(new I18NLocalizedTypeAnnotationMatcher(),
        					new I18NMessageAnnotationGuiceHandler());	// @I18NMessageBundle annotation handling
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	Guice Matcher used to link a listener when the type is annotated with @I18NLocalized
//	the link injects every member annotated with @I18NMessageBundleService
/////////////////////////////////////////////////////////////////////////////////////////
	class I18NLocalizedTypeAnnotationMatcher 
  extends AbstractMatcher<TypeLiteral<?>> {
		@Override
		public boolean matches(final TypeLiteral<?> typeLiteral) {
			return typeLiteral.getRawType().getAnnotation(I18NLocalized.class) != null;
		}
	}
}
