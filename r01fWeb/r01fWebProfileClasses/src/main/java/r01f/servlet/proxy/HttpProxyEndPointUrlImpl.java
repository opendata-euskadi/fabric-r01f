package r01f.servlet.proxy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.types.url.Url;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class HttpProxyEndPointUrlImpl 
  implements HttpProxyEndPoint {

	private static final long serialVersionUID = 4716073874678808317L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _url;
	
	@Override @SuppressWarnings("unchecked")
	public <E extends HttpProxyEndPoint> E as(final Class<E> type) {
		return (E)this;
	}
}
