package com.sp.emailservice.command.rest;

import java.util.Properties;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping("/emails")
public class EamilsCommandController {

	@PostMapping
	public String sendEmail() throws MessagingException {
		String to = "shupachok@gmail.com";
		String subject = "Test Email";
		String body = "This is a test email from Jakarta Mail.";

		sendMail(to, subject, body);

		return "send email";
	}

	public void sendMail(String to, String subject, String body) {
		// provide sender's email ID
		String from = "hi@demomailtrap.com";
		final String username = "api";
		final String password = "49128738cf5f95904242a6e27da2dd6f";
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
