package r01f.httpclient.loadbalanced;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.http.loadbalance.LoadBalancedBackEndServer;
import r01f.http.loadbalance.LoadBalancedBackendServerStats;
import r01f.http.loadbalance.LoadBalancerManager;
import r01f.http.loadbalance.exception.LoadBalancerNOServerAvailableException;
import r01f.http.loadbalance.exception.LoadBalancerRetriesExceededException;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.types.url.UrlQueryString;

/**
 * Usage
 * <pre class='brush:java'>
 * 		// [1] - Create the load balancer
 *		LoadBalancerManager loadBalancer = LoadBalancerManager.builderForServiceWithName(LoadBalancedServiceID.named("test"))
 *															  .withServers(Url.from("http://www.google.com"),Url.from("http://www.google.es"))
 *															  .withRandomLoadBalancing()
 *															  .usingNewMetricRegistry()
 *															  .withDefaultRefreshInterval()
 *															  .build();
 *		LoadBalancedHttpClient loadBalancedHttpClient = new LoadBalancedHttpClient(loadBalancer,2);
 *
 *		// [2] - Execute load balanced
 *		for (int i=0; i < 10; i++) {
 *			HttpGet httpGet = new HttpGet(Url.from(UrlPath.from("search"),UrlQueryString.fromUrlEncodedParamsString("q=bilbao"))
 *											 .asString());
 *			try {
 *				org.apache.http.HttpResponse resp = loadBalancedHttpClient.executeWithLoadBalancer(httpGet);
 *				System.out.println("=====>" + resp.getStatusLine().getStatusCode());
 *				EntityUtils.consumeQuietly(resp.getEntity());	// the response MUST be consummed in order to avoid exception [httpclient exception “org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection"]
 *			} catch (Throwable th) {
 *				th.printStackTrace();
 *				break;
 *			}
 *		}
 * </pre>
 */
@Slf4j
public class LoadBalancedHttpClient {
/////////////////////////////////////////////////////////////////////////////////////////
//	FILEDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final LoadBalancerManager _loadBalancer;
	private final int _numRetries;
	
	private final HttpClient _httpClient;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("resource")
	public LoadBalancedHttpClient(final LoadBalancerManager loadBalancer,
								  final int numRetries) {
		this(loadBalancer,
			 // create an http client with some timeouts
			 HttpClientBuilder.create()
			 				  .setDefaultRequestConfig(RequestConfig.custom()
																	.setConnectTimeout(5 * 1000)				// the time to establish the connection with the remote host
																	.setConnectionRequestTimeout(10 * 1000)		// the time to wait for a connection from the connection manager/pool
																	.setSocketTimeout(5 * 1000)			// after establishing the connection; maximum time of inactivity between two data packets
																	.build())
			 				  .build(),
			 numRetries);
	}
	public LoadBalancedHttpClient(final LoadBalancerManager loadBalancer,
								  final HttpClient httpClient,
								  final int numRetries) {
		_loadBalancer = loadBalancer;
		_httpClient = httpClient;
		_numRetries = numRetries;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static Url urlFor(final LoadBalancedBackEndServer choosenServer,
					  		 final UrlPath urlPath,final UrlQueryString urlQueryString) {
		// prefix URL with selected server
		Url newUrl = urlPath != null ? choosenServer.getUrl().joinWith(urlPath) : choosenServer.getUrl();
		newUrl = urlQueryString != null ? newUrl.joinWith(urlQueryString) : newUrl;
		return newUrl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpResponse executeWithLoadBalancer(final HttpRequest req) throws LoadBalancerNOServerAvailableException,
																			  LoadBalancerRetriesExceededException {
		return this.executeWithLoadBalancer(req,(HttpContext)null);	// no context
	}
	public HttpResponse executeWithLoadBalancer(final HttpRequest req,
												final HttpContext httpContext) throws LoadBalancerNOServerAvailableException,
																			  		  LoadBalancerRetriesExceededException {
		// ensure that the requests does NOT contains the host part
		Url url = Url.from(req.getRequestLine().getUri());
		if (url.getHost() != null) throw new IllegalArgumentException("The [request uri] MUST NOT contain the host part in order to be load balanced!");
		
		// execute the request
		return _executeWithLoadBalancer(new LoadBalancerHttpRequestExecutor() {
													@Override
													public HttpResponse execute(final LoadBalancedBackEndServer choosenServer) {
														HttpHost host = HttpHost.create(choosenServer.getUrl().getHost().asString());
														try {
															return httpContext != null ? _httpClient.execute(host,req,httpContext)
																					   : _httpClient.execute(host,req);
														} catch (Exception ex) {
															throw Throwables.throwUnchecked(ex);
														}
													}
										 });
	}
	public <T> T executeWithLoadBalancer(final HttpRequest req,
										 final ResponseHandler<? extends T> responseHandler) throws LoadBalancerNOServerAvailableException,
																			   						LoadBalancerRetriesExceededException,
																			   						IOException {
		// get the response
		HttpResponse httpResponse = this.executeWithLoadBalancer(req);
		// run the response handler
		return responseHandler.handleResponse(httpResponse);
	}
	public <T> T executeWithLoadBalancer(final HttpRequest req,
										 final ResponseHandler<? extends T> responseHandler,
										 final HttpContext httpContext) throws LoadBalancerNOServerAvailableException,
																			   LoadBalancerRetriesExceededException,
																			   IOException {
		// get the response
		HttpResponse httpResponse = this.executeWithLoadBalancer(req,
																 httpContext);
		// run the response handler
		return responseHandler.handleResponse(httpResponse);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private interface LoadBalancerHttpRequestExecutor {
		public HttpResponse execute(final LoadBalancedBackEndServer choosenServer); 
	}
	private HttpResponse _executeWithLoadBalancer(final LoadBalancerHttpRequestExecutor executor) throws LoadBalancerNOServerAvailableException,
																										 LoadBalancerRetriesExceededException {
		long retries = _numRetries;
		do {
			// get a load balanced server
			LoadBalancedBackendServerStats chosenServerStats = _loadBalancer.chooseServer();
			if (chosenServerStats == null) throw new LoadBalancerNOServerAvailableException(_loadBalancer.getServiceId());

			// make the http request
			HttpResponse httpResponse = null;
			long latency = -1;
			try {
				// update the server stats
				chosenServerStats.incrementSentMessages();
				chosenServerStats.incrementOpenRequests();
				
				// make the request accounting time 
				long startTime = System.currentTimeMillis();	
				
				httpResponse = executor.execute(chosenServerStats.getServerInstance());
				
				latency = System.currentTimeMillis() - startTime;

				// exit if successful; retry if not
				if (httpResponse == null) {
					throw new TimeoutException("Timed out while waiting for a response.");
				} else if (httpResponse.getStatusLine().getStatusCode() >= 500) {
					throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(),"Unexpected response");
				}
				return httpResponse;
				
			} catch (Exception e) {
				// unexpected exception
				log.error("[load balancer] > unexpected exception: {}, retrying another server",
						  e.getMessage(),e);
				// account errors
				chosenServerStats.incrementErrors();
			} finally {
				// update the server stats
				chosenServerStats.decrementOpenRequests();
				if (latency > 0) chosenServerStats.recordLatency(latency);
			}
			retries = retries - 1;
		} while (retries >= 0);
		throw new LoadBalancerRetriesExceededException(_loadBalancer.getServiceId(),_numRetries);
	}
}
