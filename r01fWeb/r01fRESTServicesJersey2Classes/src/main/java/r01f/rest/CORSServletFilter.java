package r01f.rest;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
@CORSFilter
public class CORSServletFilter 
  implements ContainerResponseFilter {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void filter(final ContainerRequestContext  req,
            		   final ContainerResponseContext  res) {
        res.getHeaders().add("Access-Control-Allow-Origin", "*");
        res.getHeaders().add("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
        res.getHeaders().add("Access-Control-Allow-Credentials", "true");
        res.getHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS, HEAD");       
    }
}
