package r01f.rest;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class CORSServletFilter 
  implements ContainerResponseFilter {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ContainerResponse filter(final ContainerRequest req,
            						final ContainerResponse res) {
        res.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        res.getHttpHeaders().add("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
        res.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
        res.getHttpHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS, HEAD");

        return res;
    }
}
