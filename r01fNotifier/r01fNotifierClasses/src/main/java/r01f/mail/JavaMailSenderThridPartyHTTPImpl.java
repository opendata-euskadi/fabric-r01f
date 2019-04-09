package r01f.mail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpRequestFormParameter;
import r01f.httpclient.HttpRequestFormParameterForText;
import r01f.httpclient.HttpRequestPayload;
import r01f.httpclient.HttpResponse;
import r01f.httpclient.HttpResponseCode;
import r01f.service.ServiceCanBeDisabled;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.util.types.Strings;

@Slf4j
public class JavaMailSenderThridPartyHTTPImpl
  implements JavaMailSender,
			 ServiceCanBeDisabled {
	
/////////////////////////////////////////////////////////////////////////////////////////
//FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private Properties _javaMailProperties = new Properties();
	@Getter private final HttpClientProxySettings _proxySettings;
	@Getter private final Url _thirdPartyProviderUrl;
	@Getter private final boolean _supportsMimeMessage;
	private boolean _disabled;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderThridPartyHTTPImpl(final Url thirdPartyProviderUrl ,
											final HttpClientProxySettings proxySettings ,
											final boolean supportsMimeMessage) {
		_thirdPartyProviderUrl = thirdPartyProviderUrl;
		_proxySettings = proxySettings;
		_supportsMimeMessage = supportsMimeMessage;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//	ServiceCanBeDisabled
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
//JavaMailSender
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void send(final SimpleMailMessage... simpleMessages) throws MailException {
		for (SimpleMailMessage s : simpleMessages ) {
			send(s);
		}
	}
	@Override
	public void send(final SimpleMailMessage simpleMessage) throws MailException {
		_doSend(simpleMessage);
	}
	@Override
	public void send(MimeMessage mimeMessage) throws MailException {
		  try {
			_doSend(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
		}
	}
	@Override
	public void send(MimeMessage... mimeMessages) throws MailException {
		for (MimeMessage s : mimeMessages) {
			send(s);
		}
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
// UNSOPPORTED METHODS(...yet)
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public MimeMessage createMimeMessage() {
		throw new UnsupportedOperationException("JavaMailSenderThridPartyHTTPImpl mime not supported");
	}

	@Override
	public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
		throw new UnsupportedOperationException("JavaMailSenderThridPartyHTTPImpl mime not supported");
	}
	@Override
	public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
		throw new UnsupportedOperationException("JavaMailSenderThridPartyHTTPImpl mime not supported");
	}

	@Override
	public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
		throw new UnsupportedOperationException("JavaMailSenderThridPartyHTTPImpl mime not supported");
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//	SEND
/////////////////////////////////////////////////////////////////////////////////////////
	private void _doSend(final SimpleMailMessage simpleMessage) {
		String[] to = simpleMessage.getTo();
		String from = simpleMessage.getFrom();
		String text = simpleMessage.getText();
		String subject = simpleMessage.getSubject();
		_doSend(from, to, subject, text);
	}
	
	private void _doSend(final String from,
						 final String[] to,
						 final String subject,
						 final String text) {
		List<HttpRequestFormParameter> parameters = new ArrayList<HttpRequestFormParameter>();
		if (from != null) {
			parameters.add(HttpRequestFormParameterForText.of(from).withName("from"));
		}
		parameters.add(HttpRequestFormParameterForText.of(to[0]).withName("to"));
		parameters.add(HttpRequestFormParameterForText.of(subject).withName("subject"));
		parameters.add(HttpRequestFormParameterForText.of(text).withName("messageText"));
		HttpResponse response = null;
		try {
			if (_proxySettings != null) {
				log.debug(">>>>>>>>>>>>>>>> HTTP VIA PROXY");;
				response = HttpClient.forUrl(_thirdPartyProviderUrl)
									 .POSTForm()
									 .withPOSTFormParameters(parameters)
									 .getResponse()
									 .usingProxy(_proxySettings).withoutTimeOut().noAuth();
			} else {
				log.debug(">>>>>>>>>>>>>>>> HTTP DIRECT");;
				
				log.debug("URL {}" , _thirdPartyProviderUrl);
				response = HttpClient.forUrl(_thirdPartyProviderUrl)
									 .POSTForm()
									 .withPOSTFormParameters(parameters)
									 .getResponse()
									 .notUsingProxy().withoutTimeOut().noAuth();
			}
			
			if (!response.getCode().isIn(HttpResponseCode.OK)) {
				log.warn("> HTTP Mail Response Code : {}",response.getCode(),response.loadAsString());
				throw new JavaMailSenderThridPartyHTTPImplException(Strings.customized("Remote Server Error int HTTP Mail Service {}",
																					   response.loadAsString()));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
		}
	}
	
	private void _doSend(final MimeMessage mimeMessage) throws MessagingException, 
															   IOException {
		Address[] toAddress =  mimeMessage.getRecipients(RecipientType.TO);
		Address[] fromAddress = mimeMessage.getFrom();
		String[] to = new String[toAddress.length];
	    String[] from = new String[fromAddress.length];
		int i = 0;
		for (Address a : toAddress) {
			to[i++] = a.toString();
		}
		int j = 0;
		for (Address f : fromAddress) {
			from[j++] = f.toString();
		}
		String subject = mimeMessage.getSubject();
		if (_supportsMimeMessage) {
			log.debug(">>>>>>>>>> MIME MESSAGE SUPPORTED");
			InputStream is = mimeMessage.getInputStream();
			try {
				Url url = _thirdPartyProviderUrl.joinWith(UrlQueryString.fromParams(UrlQueryStringParam.of("to",to[0]),
																					UrlQueryStringParam.of("from",from[0]),
																					UrlQueryStringParam.of("subject",subject)));
				HttpResponse response = null;
				if (_proxySettings != null) {
					 response = HttpClient.forUrl(url)
										      .POST()
										           .withPayload(HttpRequestPayload.wrap(is))
										       .getResponse()
										       		.usingProxy(_proxySettings).withoutTimeOut().noAuth();
				} else {
					response = HttpClient.forUrl(_thirdPartyProviderUrl)
						      .POST()
						           .withPayload(HttpRequestPayload.wrap(is))
						       .getResponse()
						       		.notUsingProxy().withoutTimeOut().noAuth();
				}
				if (!response.getCode().isIn(HttpResponseCode.OK)) {
					log.warn("HTTP Mail Response Code {}",response.getCode(),response.loadAsString() );
					throw new JavaMailSenderThridPartyHTTPImplException(Strings.customized("Remote Server Error int HTTP Mail Service {}",response.loadAsString()));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
			} catch (IOException e) {
				e.printStackTrace();
				throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
			} catch (Throwable e) {
					e.printStackTrace();
				throw new JavaMailSenderThridPartyHTTPImplException(e.getLocalizedMessage());
			}
		} else {
			System.out.println(">>>>>>>>>> MIME MESSAGE NOT SUPPORTED");
			_doSend(from[0], to, subject, 
					_getMimeMessageAsPlainText(mimeMessage));
		}
	}
	
	private String _getMimeMessageAsPlainText(final MimeMessage mimeMessage) {
		MimeMessageParser parser = new MimeMessageParser(mimeMessage);
		try {
			parser.parse();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (parser.hasHtmlContent()) {
			return parser.getHtmlContent();
		} else {
			return parser.getPlainContent();
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// INNER CLASSES
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static class JavaMailSenderThridPartyHTTPImplException extends MailException {
		private static final long serialVersionUID = -8313498571229772866L;
		public JavaMailSenderThridPartyHTTPImplException(final String msg) {
			super(msg);
		}
	}
}
