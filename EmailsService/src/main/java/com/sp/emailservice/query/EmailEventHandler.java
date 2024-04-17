package com.sp.emailservice.query;

import java.util.Properties;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.sp.core.events.EmailDispatchedEvent;
import com.sp.core.model.User;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailEventHandler {

	@EventHandler
	public void on(EmailDispatchedEvent emailDispatchedEvent) {
		
		User user = emailDispatchedEvent.getUser();
		String to = user.getEmail();
		String orderId = emailDispatchedEvent.getOrderId();
		String name = user.getFirstName()+"  "+user.getLastName();
		String subject = String.format("Order # [%s] - Thanks for your purchase",orderId);
		String body = String.format("""
				Hi %1$s,

				Thanks for your order!.

				This email confirms your order [%2$s] has been approved.
				""",name,orderId);

		sendMail(to, subject, body);

	}
	
	public void sendMail(String to, String subject, String body) {
		// provide sender's email ID
		String from = "hi@demomailtrap.com";
		final String username = "api";
		final String password = "1234";
		// provide Mailtrap's host address
		String host = "live.smtp.mailtrap.io";
		// configure Mailtrap's SMTP server details
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
		// create the Session object
		Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};
		Session session = Session.getInstance(props, authenticator);

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(body);
			
			Transport.send(message);
			
			System.out.println("Email Message Sent Successfully");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}

	public void sendGmail(String to, String subject, String body) throws MessagingException {
		// Set up mail server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		// Set up OAuth2 properties
		properties.put("mail.smtp.auth.mechanisms", "XOAUTH2");
		properties.put("mail.smtp.auth.xoauth2.disable", "false");

		// Create a mail session
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("demo@gmail.com", "token");
			}
		});

		try {
			// Create a default MimeMessage object
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header
			message.setFrom(new InternetAddress("demo@gmail.com"));

			// Set To: header field of the header
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(body);

			// Send message
			Transport.send(message);
			System.out.println("Email sent successfully...");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

}
