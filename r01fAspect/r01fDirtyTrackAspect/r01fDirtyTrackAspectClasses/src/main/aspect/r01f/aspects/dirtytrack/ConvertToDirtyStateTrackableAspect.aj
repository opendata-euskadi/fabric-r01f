package r01f.aspects.dirtytrack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableMap;


/**
 * Injects {@link DirtyStateTrackable} interface and it's behavior to every  {@link ConvertToDirtyStateTrackable} annotated types
 * see {@link DirtyStateTrackableAspect}, {@link ChangestTrackableMapAspect} and {@link ChangesTrackableCollectionAspect} aspects 
 * 
 * In order to make a type implements {@link DirtyStateTrackable} interface, simply annotate it with @ConvertToDirtyStateTrackable 
 */
privileged public aspect ConvertToDirtyStateTrackableAspect {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  INTER-TYPE
/////////////////////////////////////////////////////////////////////////////////////////	
//	declare parents : @ConvertToDirtyStateTrackable * implements DirtyStateTrackable;
	/**
	 * Make every @ConvertToDirtyStateTrackable annotated types that do not extends from Map, List or Set 
	 * implement {@link DirtyStateTrackable} interface (the behavior impl is at {@link DirtyStateTrackableAspect})
	 */
	declare parents : @ConvertToDirtyStateTrackable !(Map+ || List+ || Set+) && !DirtyStateTrackable+ implements DirtyStateTrackable;
	/**
	 * Make every @ConvertToDirtyStateTrackable annotated types that extends from Map 
	 * implement {@link ChangesTrackableMap} interface (the behavior impl is at {@link ChangesTrackableCollectionAspect})
	 */
	declare parents : @ConvertToDirtyStateTrackable Map+ && !ChangesTrackableMap+ implements ChangesTrackableMap;
//  declare parents : @ConvertToDirtyStateTrackable !LanguageTexts+ && Map+ && !ChangesTrackableMap+ implements ChangesTrackableMap;
	/**
	 * Make ever @ConvertToDirtyStateTrackable annotated type that extends from Collection
	 * implement {@link ChangesTrackableCollection} (the behavior impl is at {@link ChangesTrackableCollectionAspect})
	 */
	declare parents : @ConvertToDirtyStateTrackable Collection+ && !ChangesTrackableCollection+ implements ChangesTrackableCollection;
	/**
	 * In the event of aspect collision, Maps and Lists have preference
	 */
	declare precedence : ChangestTrackableMapAspect,ChangestTrackableCollectionAspect,ChangestTrackableListAspect,DirtyStateTrackableAspect;
}
