package r01f.patterns.eventbus;

import com.google.common.eventbus.EventBus;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class EventBase<H extends EventHandler> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Type class used to register events with the {@link HandlerManager}.
	 * <p>Type is parameterized by the handler type in order to make the addHandler method type safe.</p>
	 * @param <H> handler type
	 */
	public static class Type<H> 
				extends EventType<H> {
		/* nothing */
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private Object _source;
	private boolean _dead;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public Object getSource() {
		this.assertLive();
		return _source;
	}
	public void setSource(final Object source) {
		_source = source;
	}
	void overrideSource(Object source) {
		_source = source;
	}
	/**
	 * Asserts that the event still should be accessed. All events are considered
	 * to be "dead" after their original handler manager finishes firing them. An
	 * event can be revived by calling {@link EventBase#revive()}.
	 */
	protected void assertLive() {
		assert (!_dead) : "This event has already finished being processed by its original handler manager, so you can no longer access it";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link Type} used to register this event, allowing an
	 * {@link EventBus} to find handlers of the appropriate class.
	 * 
	 * @return the type
	 */
	public abstract Type<H> getAssociatedType();

	/**
	 * Implemented by subclasses to to invoke their handlers in a type safe
	 * manner. Intended to be called by {@link EventBus#fireEvent(EventBase)} or
	 * {@link EventBus#fireEventFromSource(EventBase, Object)}.
	 * @param handler handler
	 * @see EventBus#dispatchEvent(EventBase, Object)
	 */
	protected abstract void dispatch(H handler);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Is the event current live?
	 * @return whether the event is live
	 */
	protected final boolean isLive() {
		return !_dead;
	}
	protected final boolean isDead() {
		return _dead;
	}
	/**
	 * Kill the event. After the event has been killed, users cannot really on its
	 * values or functions being available.
	 */
	protected void kill() {
		_dead = true;
		_source = null;
	}
	/**
	 * Revives the event. Used when recycling event instances.
	 */
	protected void revive() {
		_dead = false;
		_source = null;
	}
}
