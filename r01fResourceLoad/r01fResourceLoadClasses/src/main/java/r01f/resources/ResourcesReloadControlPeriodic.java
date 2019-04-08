package r01f.resources;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.types.TimeLapse;
import r01f.util.types.collections.CollectionUtils;


/**
 * Reload policy implemented by a time interval (<i>ie: every two hours</i>).<br>
 * 
* The params (the {@link Map} provided to the constructor) needs:<br>
 * <ul>
 * <li>The time interval (seconds/minutes/hours).
 * 		<pre>
 * 		ie: 	2s (every 2 seconds)
 * 				5m (every 5 minutes)
 * 				3h (every 3 hours)
 * 		</pre>
 * </li></ul>
 */
@Accessors(prefix="_")
public class ResourcesReloadControlPeriodic 
     extends ResourcesReloadControlBase<ResourcesReloadControlPeriodic> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	static String PERIOD_PROP_KEY = "period";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * TimeStamp for the last reload
	 */
	@Getter private long _lastReloadTimeStamp = -1;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourcesReloadControlPeriodic() {
	}
	public ResourcesReloadControlPeriodic(final ResourcesReloadControlDef resCtrlDef) {
		super(resCtrlDef);
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		boolean outOK = true;
		if (CollectionUtils.isNullOrEmpty(props)) {
			outOK = false;
		} else {
			outOK = CollectionUtils.of(props)
								   .containsAllTheseKeys(PERIOD_PROP_KEY);	
		}
		return outOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the reload period
	 * @param reloadPeriodMillis period in milis
	 */
	public ResourcesReloadControlPeriodic setReloadInterval(final long reloadPeriodMilis) {
		if (this.getReloadControlDef().getControlProps() == null) {
			this.getReloadControlDef()
				.setControlProps(new HashMap<String,String>(1));
		}
		this.getReloadControlDef().getControlProps()
						 		  .put(ResourcesReloadControlPeriodic.PERIOD_PROP_KEY,Long.toString(reloadPeriodMilis));
		return this;
	}
	/**
	 * Sets the reload period than can have the format showed in these samples:
	 * <ul>
	 * 		<li>1h for one hour</li>
	 * 		<li>30m for 30 minutes</li>
	 * 		<li>100s for 100 seconds</li>
	 * </ul>
	 * @param reloadPeriodMillis period in milis
	 */
	public ResourcesReloadControlPeriodic setReloadInterval(final String reloadPeriodSpec) {
		long reloadPeriodMilis = TimeLapse.createFor(reloadPeriodSpec)
									   	  .asMilis();
		return this.setReloadInterval(reloadPeriodMilis);
	}
	/**
	 * Sets the reload period
	 * @param reloadPeriod period 
	 */
	public ResourcesReloadControlPeriodic setReloadInterval(final TimeLapse reloadPeriod) {
		long reloadPeriodMilis = reloadPeriod.asMilis();
		return this.setReloadInterval(reloadPeriodMilis);
	}
	/**
	 * @return the interval between reloads
	 */
	public long getReloadIntervalMilis() {
		return this.getReloadControlDef().getControlProps() != null ? Long.parseLong(this.getReloadControlDef()
																							.getControlProps()
																								.get(ResourcesReloadControlPeriodic.PERIOD_PROP_KEY))
																	: -1;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ResourcesReloadControl
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean needsReload(final String component) {
		boolean outReload = false;
		
		if (_hasToCheckIfReloadIsNeeded()) {	// only do the checking if needed
			if (this.getReloadIntervalMilis() > 0) {
				if (_lastReloadTimeStamp < 0) {
					_lastReloadTimeStamp = System.currentTimeMillis();
					outReload = true;	// it was never loaded... 
				} else {
					long currentTimeMillis = System.currentTimeMillis();
					long elapsedTimeSinceLastReload = currentTimeMillis - _lastReloadTimeStamp;
					if (elapsedTimeSinceLastReload > this.getReloadIntervalMilis()) {
						outReload = true;
						_lastReloadTimeStamp = currentTimeMillis;
					}
				}
			}
		}
		return outReload;
	}
}
