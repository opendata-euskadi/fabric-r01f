package r01f.mail;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import r01f.service.ServiceCanBeDisabled;

     class SpringJavaMailSenderImplDecorator 
   extends JavaMailSenderImpl
implements ServiceCanBeDisabled {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int MAIL_SOCKET_TIMEOUT = 60000;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private boolean _disabled;
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
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Properties createJavaMailProperties() {
		Properties props = new Properties();
		
		// Set a fixed timeout of 60s for all operations - the default timeout is "infinite"
		// see http://www.javacodegeeks.com/2014/06/javamail-can-be-evil-and-force-you-to-restart-your-app-server.html
		props.put("mail.smtp.connectiontimeout",MAIL_SOCKET_TIMEOUT);
		props.put("mail.smtp.timeout", MAIL_SOCKET_TIMEOUT);
		props.put("mail.smtp.writetimeout",MAIL_SOCKET_TIMEOUT);
		
		return props;
	}
}
