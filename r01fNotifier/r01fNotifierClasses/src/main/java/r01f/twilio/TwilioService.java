package r01f.twilio;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Call;
import com.twilio.sdk.resource.instance.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.OIDBaseImmutable;
import r01f.httpclient.HttpClientProxySettings;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.service.ServiceCanBeDisabled;
import r01f.types.contact.Phone;
import r01f.types.url.Url;

/**
 * Encapsulates twilio message & call sending
 * Sample usage:
 * <pre class='brush:java'>
 *		TwilioService twilioService = new TwilioService(new TwilioAPIData(TwilioAPIClientID.of("xxx"),Password.forId("yyy"),
 *																		  Phone.of("+34510000341"),	
 *																		  Phone.of("+34510000341")));	// same twilio number for sms and voice
 *		Call call = twilioService.makeCall(Phone.of("+34688671967"),
 *										   SerializedURL.of("http://demo.twilio.com/docs/voice.xml"));
 *		Message sms = twilioService.sendSMS(Phone.of("+34688671967"),
 *											"Hello world");
 * </pre>
 */
@SuppressWarnings("deprecation")
@Singleton
@Slf4j
public class TwilioService 
  implements ServiceCanBeDisabled {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final TwilioAPIData _apiData;
	private final HttpClientProxySettings _proxySettings;	// TODO enable twilio with proxy
	private boolean _disabled;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TwilioService(final TwilioConfig config) {
		this(config.getApiData(),
			 config.getProxySettings());		
	}
	public TwilioService(final TwilioAPIData apiData) {
		this(apiData,
			 null);	// proxy settings
	}
	public TwilioService(final TwilioAPIData apiData,
						 final HttpClientProxySettings proxySettings) {
		_apiData = apiData;
		_proxySettings = proxySettings;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ServiceCanBeDisabled
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isEnabled() {
		return !_disabled;
	}
	@Override
	public boolean isDisabled() {
		return _disabled;
	}
	@Override
	public void setEnabled() {
		_disabled = false;
	}
	@Override
	public void setDisabled() {
		_disabled = true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TWILIO
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Makes a twilio outgoing phone call
	 * @param toPhone the target phone number
	 * @param twmlUrl the twml to execute (ie "http://demo.twilio.com/docs/voice.xml")
	 * @return
	 */
	public Call makeCall(final Phone toPhone,
						 final Url twmlUrl) throws TwilioRestException {
		Preconditions.checkArgument(toPhone != null,"The destination phone must NOT be null!");
		Preconditions.checkArgument(twmlUrl != null,"A twml URL is needed!");
		Preconditions.checkState(_apiData.existsAccountData() && _apiData.canMakeVoicePhoneCalls(),"The API is NOT configured properly to make phone calls");
		
        TwilioRestClient client = _createTwilioRESTClient();
        
        List<NameValuePair> params = new ArrayList<NameValuePair>(); 
		params.add(new BasicNameValuePair("To",toPhone.asString())); 
		params.add(new BasicNameValuePair("From",_apiData.getVoicePhone().asString()));   
		params.add(new BasicNameValuePair("Method","GET"));  
		params.add(new BasicNameValuePair("FallbackMethod","GET"));  
		params.add(new BasicNameValuePair("StatusCallbackMethod","GET"));    
		params.add(new BasicNameValuePair("Record","false")); 
		params.add(new BasicNameValuePair("Url",twmlUrl.asString()));
		
        // Make the call
        CallFactory callFactory = client.getAccount().getCallFactory(); 
        Call call = callFactory.create(params); 
        log.info("Call stablished with id={}",call.getSid());
        return call;
	}
	/**
	 * Sends a twilio SMS
	 * @param toPhone
	 * @param text
	 * @return
	 * @throws TwilioRestException
	 */
	public Message sendSMS(final Phone toPhone,
						   final String text) throws TwilioRestException {
		Preconditions.checkArgument(toPhone != null,"The destination phone must NOT be null!");
		Preconditions.checkArgument(text != null,"A text is needed for the sms message!");
		Preconditions.checkState(_apiData.existsAccountData() && _apiData.canSendMessages(),"The API is NOT configured properly to send messages");
		
        TwilioRestClient client = new TwilioRestClient(_apiData.getAccountSID().asString(),
        											   _apiData.getAccountToken().asString());
		// Build a filter for the MessageList
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Body",text));
		params.add(new BasicNameValuePair("To",toPhone.asString()));
		params.add(new BasicNameValuePair("From",_apiData.getMessagingPhone().asString()));

		MessageFactory messageFactory = client.getAccount().getMessageFactory();
		Message message = messageFactory.create(params);
		log.info("SMS Message sent with id={}",message.getSid());
		return message;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor @AllArgsConstructor
	public static class TwilioAPIData {
		@Getter private final TwilioAPIClientID _accountSID;
		@Getter private final Password _accountToken;
		@Getter private Phone _voicePhone;			// (a twilio number) +34518880365
		@Getter private Phone _messagingPhone;		// (a twilio number) +34518880365
		
		public boolean existsAccountData() {
			return _accountSID != null && _accountToken != null;
		}
		public boolean canMakeVoicePhoneCalls() {
			return _voicePhone != null;
		}
		public boolean canSendMessages() {
			return _messagingPhone != null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="twilioAPIClientID")
	public static class TwilioAPIClientID 
				extends OIDBaseImmutable<String> {
		private static final long serialVersionUID = -5867457273405673410L;
		private TwilioAPIClientID(final String id) {
			super(id);
		}
		public static TwilioAPIClientID of(final String id) {
			return new TwilioAPIClientID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "resource"})
	private TwilioRestClient _createTwilioRESTClient() {
        TwilioRestClient outClient = new TwilioRestClient(_apiData.getAccountSID().asString(),
        											      _apiData.getAccountToken().asString());
		if (_proxySettings != null && _proxySettings.isEnabled()) {
			log.info("Connecting to twilio through {}:{}",_proxySettings.getProxyHost(), 
														  _proxySettings.getProxyPort());
			// Get the twilio api underlying http client
			DefaultHttpClient httpClient = (DefaultHttpClient)outClient.getHttpClient();
			
			// Set proxy details
			HttpHost proxy = new HttpHost(_proxySettings.getProxyHost().asString(),_proxySettings.getProxyPort(),
										  "http");
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
												proxy);
			httpClient.getCredentialsProvider().setCredentials(new AuthScope(_proxySettings.getProxyHost().asString(), 
															 				 _proxySettings.getProxyPort()),
															   new UsernamePasswordCredentials(_proxySettings.getUser().asString(),
																	   						   _proxySettings.getPassword().asString()));
			httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF,
												Lists.newArrayList(AuthPolicy.BASIC));
												
		} 
		return outClient;
	}
}
