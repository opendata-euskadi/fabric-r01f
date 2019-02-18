package r01f.locale.services;

import javax.inject.Inject;

import com.google.common.annotations.GwtIncompatible;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import r01f.bundles.ResourceBundleControl;
import r01f.bundles.ResourceBundleControlBuilder;
import r01f.bundles.ResourceBundleMissingKeyBehavior;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.locale.services.I18NMessageBundleService;
import r01f.locale.services.I18NService;
import r01f.locale.services.I18NServiceBuilder;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.resources.ResourcesLoaderDef;
import r01f.resources.ResourcesLoaderDefBuilder;
import r01f.resources.ResourcesLoaderDefLocation;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertyLocation;

/**
 * Handle in charge of listening of Guice events just BEFORE an object is returned
 * It inspects the object to see if there're message bundles to be injected
 * 
 * When a type needs a message bundle:
 * <pre>
 * 	1.- Annotate a {@link I18NService} field with @I18NMessageBundle (see {@link I18NService})
 *  2.- Create a type instance using guice
 * </pre>
 */
@GwtIncompatible
public class I18NMessageAnnotationGuiceHandler 
  implements TypeListener {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Inject 
	private XMLProperties _xmlProperties;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public <I> void hear(final TypeLiteral<I> type,
    					 final TypeEncounter<I> encounter) {
		// See if the injected type has a field annotated with @I18NMessageBundle
        final FieldAnnotated<I18NMessageBundleService>[] fieldsAnnotatedWithI18N = ReflectionUtils.fieldsAnnotated(type.getRawType(),
        																										   I18NMessageBundleService.class);

        if (fieldsAnnotatedWithI18N != null && fieldsAnnotatedWithI18N.length > 0) {      
            // Register a field injector (see I18NGuiceModule): binder.bindListener(Matchers.any(),new I18NMessageAnnotationGuiceHandler());
            // ... any type instance created by Guice is inspected to look after @I18NMessageAnnotationGuiceHandler annotation
            //     and if found, it's injected
            encounter.register(new MembersInjector<I>() {
            	// Annonymous Inner Type of MemberInjector...
                @Override
                public void injectMembers(I instance) {
                	// Inject I18NService fields annotated with @I18NMessageBundle
                    for (FieldAnnotated<I18NMessageBundleService> fieldAnnotatedWitI18N : fieldsAnnotatedWithI18N) {
                    	// Check it's a I18NService field
                        if (!fieldAnnotatedWitI18N.getField().getType().isAssignableFrom(I18NService.class)) {                        	
                        	throw new IllegalStateException(Strings.customized("Field {} of type {} must be of type {} to be injected with a {}",
																   			   fieldAnnotatedWitI18N.getField().getName(),instance.getClass().getCanonicalName(),I18NService.class.getName(),I18NService.class.getName()));
                        }
                    	
                        // Get the name of the bundle to be injected (if it's NOT set a the @I18NMessageBundle, it's taken from the type name)
                        I18NMessageBundleService i18nServiceAnnotation = fieldAnnotatedWitI18N.getAnnotation();
                        String[] theBundleChain = i18nServiceAnnotation.chain();
                        if (theBundleChain != null && theBundleChain.length > 0) {
                        	for (String c : theBundleChain) {
                        		if (c.length() == 0) c = type.getRawType().getName().replace('.', '/');		// default: the type name
                        		if (c.startsWith("/")) c = c.substring(1); 									// ensure it's a relative path (ClassPathLoader is used)                       		
                        	}
                        }
                        if (!_isValidBundleChain(theBundleChain)) throw new IllegalStateException(Strings.customized("Field {} of type {} annotated with {} must have a 'chain' attribute of type String[] with the names of the Bundles of the chain",
                        																				 			 fieldAnnotatedWitI18N.getField().getName(),instance.getClass().getCanonicalName(),I18NService.class.getName()));
                        // Get the resource load/reload definition from a properties file
                        ResourcesLoaderDefLocation resLoadDefLocAnnotation = fieldAnnotatedWitI18N.getField().getAnnotation(ResourcesLoaderDefLocation.class);
                        if (resLoadDefLocAnnotation == null) throw new IllegalStateException(Strings.customized("Field {} of type {} annotated with {} must define the XMLProperties file location of the resources loading/reloading definition using a {} nested-annotation",
        																										fieldAnnotatedWitI18N.getField().getName(),instance.getClass().getCanonicalName(),I18NService.class.getName(),ResourcesLoaderDefLocation.class.getName()));
            			ResourcesLoaderDef resLoadDef = _extractResourcesLoaderDefFromAnnotation(_xmlProperties,
            																					 resLoadDefLocAnnotation);
                        ResourceBundleControl resBundleControl = ResourceBundleControlBuilder.forLoadingDefinition(resLoadDef);
                        
                        // Get the behavior  in case the key is not found
                        ResourceBundleMissingKeyBehavior theMissingKeyBehavior = i18nServiceAnnotation.missingKeyBehavior();
                        
                        // Get an 18NService from the factory and set it 
                        I18NService service = I18NServiceBuilder.createUsing(resBundleControl)
                        										.forBundleChain(theBundleChain)
                        										.usingDefaultClassLoader()
                        										.withMissingKeyBehaviour(theMissingKeyBehavior);
                        ReflectionUtils.setFieldValue(instance,fieldAnnotatedWitI18N.getField(),service,false);
                    }
                }
                
            });
        }
    }
	static ResourcesLoaderDef _extractResourcesLoaderDefFromAnnotation(final XMLProperties xmlProperties,
																	   final ResourcesLoaderDefLocation resLoadDefLocAnnotation) {
        AppCode appCode = AppCode.forId(resLoadDefLocAnnotation.appCode());
        AppComponent component = AppComponent.forId(resLoadDefLocAnnotation.component());
        Path xPath = Path.from(resLoadDefLocAnnotation.xPath());
        
        XMLPropertyLocation xmlPropLoc = XMLPropertyLocation.createFor(appCode,component,xPath);
        ResourcesLoaderDef outDef = ResourcesLoaderDefBuilder.forDefinitionAt(xmlProperties,xmlPropLoc);
        return outDef;
	}
	static final boolean _isValidBundleChain(final String[] theBundleChain) {
		if (theBundleChain == null || theBundleChain.length == 0) return false;
		boolean outValid = true;
		for (String bundle : theBundleChain) {
			if (Strings.isNullOrEmpty(bundle)) {
				outValid = false;
				break;
			}
		}
		return outValid;
	}

}
