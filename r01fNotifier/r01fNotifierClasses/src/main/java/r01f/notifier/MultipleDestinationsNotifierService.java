package r01f.notifier;

import java.util.Collection;

import r01f.types.contact.ValidatedContactID;


public interface MultipleDestinationsNotifierService<A extends ValidatedContactID,
													 REQ extends NotifierRequest<A>,RES extends NotifierResponse<A>>
		 extends SingleDestinationNotifierService<A,REQ,RES> {
	
	public Collection<RES> notifyAll(Collection<REQ> requests);
}
