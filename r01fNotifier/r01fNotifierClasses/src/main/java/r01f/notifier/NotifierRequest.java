package r01f.notifier;

import java.util.Collection;

import r01f.types.contact.ValidatedContactID;

/**
 * Models a notification request 
 * @param <A>
 */
interface NotifierRequest<A extends ValidatedContactID> {
	public A getFrom();
	public void setFrom(A from);
	
	public String getSubject();
	public void setSubject(final String subject);
	
	public String getText();
	public void setText(final String text);
	
	public Collection<NotifierAttachment> getAttachments();
	public void setAttachments(Collection<NotifierAttachment> attachments);
}
