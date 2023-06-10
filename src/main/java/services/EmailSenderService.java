package services;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class EmailSenderService {
  private final String senderEmail;
  private final String service;
  private final String password;
  private final Session session;
  private final MimeMessage message;

  public EmailSenderService() {
    senderEmail = "listtaskapp@gmail.com";
    password = "siwrggjduvteeqkb";
    service = "smtp.gmail.com";

    Properties properties = new Properties();
    properties.put("mail.smtp.host", service);
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.port", 587);
    properties.put("mail.smtp.user", senderEmail);
    properties.put("mail.smtp.clave", password);
    properties.put("mail.smtp.auth", "true");

    session = Session.getDefaultInstance(properties);
    message = new MimeMessage(session);
  }

  public void sendEmailWithFile(
          String subject, String textBody, String fileDataSource, String receiverEmail) {

    try {

      MimeMultipart emailElements = new MimeMultipart();
      MimeBodyPart emailBody = new MimeBodyPart();

      emailBody.setContent(textBody, "text/html; charset=utf-8");
      emailElements.addBodyPart(emailBody);

      MimeBodyPart attachFile = new MimeBodyPart();
      File file = new File(fileDataSource);
      attachFile.setDataHandler(new DataHandler(new FileDataSource(fileDataSource)));
      attachFile.setFileName(file.getName());
      emailElements.addBodyPart(attachFile);

      message.setFrom(new InternetAddress());
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
      message.setSubject(subject);
      message.setContent(emailElements);
      Transport transport = session.getTransport("smtp");
      transport.connect(service, senderEmail, password);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      System.out.println("Procces completed successfully");
      file.deleteOnExit();
    } catch (MessagingException me) {
      me.printStackTrace();
    }
  }

  public void sendEmail(String subject, String textBody, String receiverEmail) {

    try {
      message.setFrom(new InternetAddress());
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
      message.setSubject(subject);
      message.setText(textBody);
      Transport transport = session.getTransport("smtp");
      transport.connect(service, senderEmail, password);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      System.out.println("Procces completed successfully");
    } catch (MessagingException me) {
      me.printStackTrace();
    }
  }
}
