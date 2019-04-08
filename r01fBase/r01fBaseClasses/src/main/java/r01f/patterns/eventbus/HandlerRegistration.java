package r01f.patterns.eventbus;


/**
 * Registration objects returned when an event handler is bound 
 * (e.g. via {@link SimpleEventBus#addHandler}), used to deregister.
 * <p>
 * A tip: to make a handler deregister itself try something like the following:
 * <pre class='brush:java'>
 * 	new MyHandler() {
 *  	HandlerRegistration reg = MyEvent.register(eventBus,this);
 *  	public void onMyThing(MyEvent event) {
 *    		{@literal /}* do your thing *{@literal /}
 *    		reg.removeHandler();
 *  	}
 * 	};
 * </pre>
 */
public interface HandlerRegistration {

	/**
	 * Deregisters the handler associated with this registration object if the
	 * handler is still attached to the event source. If the handler is no longer
	 * attached to the event source, this is a no-op.
	 */
	void removeHandler();
}
