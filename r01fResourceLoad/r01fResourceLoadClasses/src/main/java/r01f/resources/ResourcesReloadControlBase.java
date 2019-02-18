package r01f.resources;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.common.base.Preconditions;


/**
 * Reload policy implementation
 * @param <C> the component to be reloaded
 */
@Accessors(prefix="_")
abstract class ResourcesReloadControlBase<SELF_TYPE extends ResourcesReloadControlBase<SELF_TYPE>> 
    implements ResourcesReloadControl {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Check interval to see if the reloading should be performed
	 */
	@Getter @Setter ResourcesReloadControlDef _reloadControlDef;
	/**
	 * TimeStamp for the last time the properties reloading was checked
	 * (it's NOT the same as the time between reloads in a periodic reloader)
	 */
	@Getter @Setter transient long _lastReloadCheckTimeStamp = -1;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourcesReloadControlBase() {
		// nothing
	}
	public ResourcesReloadControlBase(final ResourcesReloadControlDef reloadControlDef) {
		Preconditions.checkArgument(reloadControlDef != null,"The definition MUST NOT be null");
		if (!_checkProperties(reloadControlDef.getControlProps())) throw new IllegalArgumentException("The reload control definition has invalid properties!");
		_reloadControlDef = reloadControlDef;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	abstract boolean _checkProperties(final Map<String,String> props);
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * returns true if a check for a possible reload is needed
	 * Do not confuse with the interval between reloads in a periodic reload policy
	 * @return true if a check is needed
	 */
	boolean _hasToCheckIfReloadIsNeeded() {
		boolean outReloadCheckNeeded = false;
		if (_lastReloadCheckTimeStamp < 0) {	// a checking about reloading was never done before
			outReloadCheckNeeded = true;
		} else {
			long currentTimeMillis = System.currentTimeMillis();
			long elapsedTimeSinceLastCheck = currentTimeMillis - _lastReloadCheckTimeStamp;
			outReloadCheckNeeded = elapsedTimeSinceLastCheck > _reloadControlDef.getCheckIntervalMilis();
		}
		if (outReloadCheckNeeded) _lastReloadCheckTimeStamp = System.currentTimeMillis();
		return outReloadCheckNeeded;
	}

	
}
