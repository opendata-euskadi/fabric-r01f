package r01f.patterns.eventbus;

import com.google.common.eventbus.EventBus;

/**
 * Type class used to register events with an {@link EventBus}.
 * @param <H> handler type
 */
abstract class EventType<H> {
	private static int _nextHashCode;
	private final int _index;

	public EventType() {
		_index = ++_nextHashCode;
	}
	@Override
	public final int hashCode() {
		return _index;
	}
}