package r01f.notifier;

import java.io.InputStream;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.contact.ValidatedContactID;

import com.google.common.collect.Lists;

abstract class NotifierServiceBase<A extends ValidatedContactID> {

/////////////////////////////////////////////////////////////////////////////////////////
//  Request
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	protected abstract class NotifierRequestBase
	              implements NotifierRequest<A> {
		@Getter @Setter private A _from;
		@Getter @Setter private String _subject;
		@Getter @Setter private String _text;
		@Getter @Setter private Collection<NotifierAttachment> _attachments;
		
		public void addAttachment(final String name,final String description,
								  final InputStream content) {
			if (_attachments == null) _attachments = Lists.newArrayList();
			_attachments.add(new NotifierAttachment(name,description,
													content));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Response
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	protected abstract class NotifierResponseBase 
	              implements NotifierResponse<A> {
		@Getter @Setter private A _from;
		@Getter @Setter private A _to;
	}
}
