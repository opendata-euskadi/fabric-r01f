package r01f.aspects.dirtytrack;

import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;

/**
 * Aspecto que convierte una clase que extiende de Map en un mapa trackable (ChangesTrackableMap)
 */
privileged public aspect ChangestTrackableCollectionAspect 
	             extends ChangestTrackableCollectionAspectBase<ChangesTrackableCollection> {
}
