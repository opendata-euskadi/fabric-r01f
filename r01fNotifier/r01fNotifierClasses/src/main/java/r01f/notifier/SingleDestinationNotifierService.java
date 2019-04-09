package r01f.notifier;

import r01f.types.contact.ValidatedContactID;


/**
 * Models a service that notifies a single destination
 * @param <A>
 */
public interface SingleDestinationNotifierService<A extends ValidatedContactID,
												  REQ extends NotifierRequest<A>,RES extends NotifierResponse<A>> {
	public RES notify(REQ request);
}
