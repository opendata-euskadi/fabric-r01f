package r01f.servlet.proxy;

import java.io.Serializable;

import r01f.types.url.Url;

public interface HttpProxyEndPoint 
		 extends Serializable {
	
	public Url getUrl();
	public <E extends HttpProxyEndPoint> E as(final Class<E> type);
}
