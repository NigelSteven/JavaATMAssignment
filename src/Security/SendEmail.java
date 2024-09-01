package Security;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

	public static void ChangePasswordNotification(String email) {
		final String username = "steveyeoseekee@gmail.com";
		final String password = "wcaj aoug bifz bqej";
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		Session session = Session.getDefaultInstance(props,new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username,password);
			}
			
		});
		session.setDebug(true);
		try {
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));//get
			message.setSubject("Security Alert ");
			message.setText("Your password has been changed");
			Transport.send(message);
			System.out.println("Email sent Successful");
			
			
		}catch(MessagingException e) {
			e.printStackTrace();			
		}
	}
}
