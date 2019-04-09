package r01f.notifier;

import r01f.types.contact.ValidatedContactID;

/**
 * Models a notification request to a single destination
 * @param <A>
 */
public interface SingleDestinationNotifierRequest<A extends ValidatedContactID> 
	     extends NotifierRequest<A> {
	
	public A getTo();
	public void setTo(A to);
}
