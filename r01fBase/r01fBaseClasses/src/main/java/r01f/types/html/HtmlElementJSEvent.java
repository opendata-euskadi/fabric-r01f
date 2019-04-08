package r01f.types.html;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="htmlElementJSEvent")
@Accessors(prefix="_")
public class HtmlElementJSEvent
  implements Serializable {

	private static final long serialVersionUID = -7733169363173544277L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="event",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private LinkJSEvent _event;
	
	@MarshallField(as="jsCode",escape=true)
	@Getter @Setter private String _jsCode;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlElementJSEvent() {
		// defautl no-args constructor
	}
	public HtmlElementJSEvent(final LinkJSEvent event,final String jsCode) {
		_event = event;
		_jsCode = jsCode;
	}
	public HtmlElementJSEvent(final HtmlElementJSEvent other) {
		_event = other.getEvent();
		_jsCode = Strings.isNOTNullOrEmpty(other.getJsCode()) ? new String(other.getJsCode()) : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public enum LinkJSEvent 
	 implements EnumWithCode<String,LinkJSEvent> {
		ON_CLICK("onClick"),
		ON_CHANGE("onChange");
		
		@Getter private final String _code;
		@Getter private final Class<String> _codeType = String.class;
		
		private static final EnumWithCodeWrapper<String,LinkJSEvent> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(LinkJSEvent.class);
		
		@Override
		public boolean isIn(final LinkJSEvent... els) {
			return WRAPPER.isIn(this,els);
		}
		@Override
		public boolean is(final LinkJSEvent el) {
			return WRAPPER.is(this,el);
		}
	}
}
