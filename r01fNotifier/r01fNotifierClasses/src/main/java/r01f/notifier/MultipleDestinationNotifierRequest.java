package r01f.notifier;

import java.util.Collection;

import r01f.types.contact.ValidatedContactID;

/**
 * Models a notification request to multiple destinations
 * @param <A>
 */
public interface MultipleDestinationNotifierRequest<A extends ValidatedContactID> 
	     extends NotifierRequest<A> {
	
	public Collection<A> getTo();
	public void setTo(Collection<A> to);
}
