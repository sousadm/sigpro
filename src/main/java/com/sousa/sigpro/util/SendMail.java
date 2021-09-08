package com.sousa.sigpro.util;

import java.io.IOException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Email;

public class SendMail {

	ParametroMail param;

	public SendMail(ParametroMail param) {
		this.param = param;
	}

	public void enviar(Email email) throws IOException {

		Properties props = System.getProperties();
		props.put("mail.smtp.host", param.getSmtpHostMail());
		props.put("mail.smtp.user", param.getUserMail());
		props.put("mail.smtp.password", param.getPasword());
		props.put("mail.smtp.port", param.getSmtpPortMail());

		if (param.getSmtpAuth())
			props.put("mail.smtp.auth", "true");

		if (param.getSmtpStarttls())
			props.put("mail.smtp.starttls.enable", "true");

		if (param.getSmtpStartSsl()) {
			props.setProperty("mail.smtp.**ssl.enable", "true");
			props.setProperty("mail.smtp.**ssl.required", "true");
		}

		// Get the default Session object.
		Session session = Session.getDefaultInstance(props);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();

			htmlPart.setContent(email.getConteudo() != null ? email.getConteudo() : "", "text/html; charset=utf-8");
			multipart.addBodyPart(htmlPart);

			if (email.getAnexo() != null) {
				MimeBodyPart anexo = new MimeBodyPart();
				FileDataSource source = new FileDataSource(email.getAnexo());
				anexo.setDataHandler(new DataHandler(source));
				anexo.setFileName(source.getName());
				multipart.addBodyPart(anexo);
			}

			message.setContent(multipart);
			message.setSentDate(new Date());
			message.setSubject(email.getAssunto() != null ? email.getAssunto() : "");// Assunto
			message.setFrom(new InternetAddress(param.getUserMail(), email.getRemetenteNome())); // Remetente
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getDestinatarioMail()));

			// Send message
			Transport transport = session.getTransport("smtp");
			transport.connect(param.getSmtpHostMail(), param.getUserMail(), param.getPasword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}