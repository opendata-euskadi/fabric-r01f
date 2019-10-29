package r01f.model;

import r01f.util.types.Strings;

public class ModelObjectExtensionUtils {
	/**
	 * Checks if an extensible model object is extended
	 * @param obj
	 */
	public static void checkExtension(final Object obj) {
		if (obj instanceof ExtendedModelObject) {
			ExtendedModelObject<?> extensible = (ExtendedModelObject<?>)obj;
			if (extensible.getExtension() == null) throw new IllegalStateException(Strings.customized("The extensible model object {} is NOT extended: check that weaving is being taking place!",
																					  				  obj.getClass()));
		} else {
			throw new IllegalStateException(Strings.customized("The object {} is NOT an instance of {}",
															   obj.getClass(),ExtendedModelObject.class));
		}
	}
}
