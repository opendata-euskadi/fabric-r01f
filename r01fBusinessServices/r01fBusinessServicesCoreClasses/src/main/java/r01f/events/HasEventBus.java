package r01f.events;

import com.google.common.eventbus.EventBus;

/**
 * Interface for types that contains an {@link EventBus}
 */
public interface HasEventBus {
	/**
	 * @return the {@link EventBus} instance
	 */
	public EventBus getEventBus();
}
