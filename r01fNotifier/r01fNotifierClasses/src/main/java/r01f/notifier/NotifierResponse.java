package r01f.notifier;

import r01f.types.contact.ValidatedContactID;

/**
 * Models a notification response 
 * @param <A>
 */
public interface NotifierResponse<A extends ValidatedContactID> {
	public A getFrom();
	public void setFrom(A from);
	
	public A getTo();
	public void setTo(A to);
	
}
