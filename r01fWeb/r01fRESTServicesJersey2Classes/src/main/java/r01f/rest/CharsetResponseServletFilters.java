package r01f.rest;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import lombok.experimental.Accessors;
import r01f.util.types.Strings;

/**
 * To use  CharsetResponseServletFilters:
 * 	 1. Anotate  at REST Resource: @CharsetResponseFilter
 *   2. Register at REST Bootstraping :
 */
public abstract  class CharsetResponseServletFilters {
	
////////////////////////////////////////////////////////////////////////////////////////////////////////
// CharsetResponseServletFilterBase
///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static abstract class CharsetResponseServletFilterBase
				implements ContainerResponseFilter,
						   RESTFilter {
		private final String _charset;

		public CharsetResponseServletFilterBase(final String charset) {
			_charset = charset;
		}
		@Override
		public void filter(ContainerRequestContext request, ContainerResponseContext response) {
			MediaType type = response.getMediaType();
			if (type != null) {
				String contentType = type.toString();
				if (!contentType.contains("charset")) {
					String contentTypeAsString = Strings.customized("{};charset={}", contentType,_charset);
					response.getHeaders().putSingle("Content-Type", contentTypeAsString);
				}
			}
		}
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////
//ISOCharsetResponseServletFilter
///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Provider
	@CharsetResponseFilter
	public static class ISOCharsetResponseServletFilter
				extends CharsetResponseServletFilterBase {
		public ISOCharsetResponseServletFilter() {
			super(StandardCharsets.ISO_8859_1.name());
		}
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////
//UTF8CharsetResponseServletFilter
///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Provider
	@CharsetResponseFilter
	public static class UTF8CharsetResponseServletFilter
				extends CharsetResponseServletFilterBase {
		public UTF8CharsetResponseServletFilter() {
			super(StandardCharsets.UTF_8.name());
		}
	}

}
