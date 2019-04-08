package r01f.aspects.core.freezable;


import java.lang.reflect.Field;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

import r01f.aspects.core.util.ObjectsHierarchyModifier;
import r01f.aspects.core.util.ObjectsHierarchyModifier.StateModifierFunction;
import r01f.aspects.interfaces.freezable.Freezable;

/**
 * Utility to freeze an object hierarchy
 */
public class Freezer {
	/**
	 * Predicate used to exclude some fields at modules using changeObjectHierarchyState
	 */
	static final Predicate<Field> _fieldAcceptCriteria = new Predicate<Field>() {
																@Override
																public boolean apply(final Field f) {
																	if (f.getDeclaringClass().getPackage().getName().startsWith("java.lang")) return false;	
																	if (f.getDeclaringClass().getPackage().getName().startsWith("com.google")) return false;
																	if (f.getName().startsWith("ajc$")) return false; 
																	if (f.getName().startsWith("_frozen")) return false;
																	return true;
																}
														};
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static class FreezeStateModifierFunction 
			  implements StateModifierFunction<Freezable> {
		private final boolean _freeze;
		public FreezeStateModifierFunction(final boolean freeze) {
			_freeze = freeze;
		}
		@Override
		public void changeState(Freezable obj) {
			obj.setFrozen(_freeze);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Freezes an object and it's dependent object
	 * @param freezableObj
	 */
	@SuppressWarnings("serial")
	public static void freeze(final Freezable freezableObj) {
		ObjectsHierarchyModifier.<Freezable>changeObjectHierarchyState(freezableObj,new TypeToken<Freezable>() { /* nothing */ },
																	   new FreezeStateModifierFunction(true),
																	   true,		// freeze all object hierarchy
																	   _fieldAcceptCriteria);
	}
	/**
	 * Unfreezes an object and it's dependent objects
	 * @param freezableObj
	 */
	@SuppressWarnings("serial")
	public static void unFreeze(final Freezable freezableObj) {
		ObjectsHierarchyModifier.<Freezable>changeObjectHierarchyState(freezableObj,new TypeToken<Freezable>() { /* nothing */ },
																	   new FreezeStateModifierFunction(false),
																	   true,		// unfreeze all object hierarchy
																	   _fieldAcceptCriteria);
	}
}
