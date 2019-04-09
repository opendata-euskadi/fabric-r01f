package r01f.mail;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import r01f.types.url.Host;
import r01f.util.types.Strings;


/**
 * Uses Microsoft Exchange SMTP service to send mail
 */
public class MicrosoftExchangeSMTPMailSender {
	/**
	 * Creates a {@link JavaMailSender} to send an email using Microsoft Exchange SMTP
	 * @param host
	 * @param port
	 * @return
	 */
	public static JavaMailSender create(final Host host) {
		if (host == null || Strings.isNullOrEmpty(host.asString())) throw new IllegalArgumentException("Invalid Microsoft Exchange HOST");
		Properties javaMailProps = SpringJavaMailSenderImplDecorator.createJavaMailProperties();
		javaMailProps.put("mail.smtp.host",host.asString());
		
		JavaMailSenderImpl outMailSender = new SpringJavaMailSenderImplDecorator();
		outMailSender.setHost(host.asString());
		outMailSender.setJavaMailProperties(javaMailProps);
		
		return outMailSender;
	}

}
