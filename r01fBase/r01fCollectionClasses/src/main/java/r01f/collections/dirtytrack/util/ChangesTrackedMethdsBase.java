package r01f.collections.dirtytrack.util;

import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;

@Slf4j
abstract class ChangesTrackedMethdsBase {
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Adapts a {@link DirtyStateTrackable} object
	 * @param 
	 * @return
	 */
	protected static <T> DirtyStateTrackable _adaptToDirtyStateTrackable(final T object) {
		try {
			return (DirtyStateTrackable)object;
		} catch(ClassCastException ccEx) {
			log.error("{} canot be cast-ed to {}: maybe it's not annotated as @{} or maybe weaving is not in use, add -javaagent:aspectjweaver.jar to the VM start command",
					  object.getClass(),DirtyStateTrackable.class,ConvertToDirtyStateTrackable.class.getName());
			throw ccEx;
		}
	}
}
