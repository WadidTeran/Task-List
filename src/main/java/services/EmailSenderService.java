package services;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.util.Properties;

public class EmailSenderService extends JFrame {
  private final String senderEmail;
  private final String SERVICE;
  private final String password;
  private final Session session;
  private final MimeMessage message;

  public EmailSenderService() {
    senderEmail = "alxisM16@gmail.com";
    password = "osobbxvakhymgiut";
    SERVICE = "smtp.gmail.com";

    Properties properties = new Properties();
    properties.put("mail.smtp.host", SERVICE);
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
      FileDataSource file = new FileDataSource(fileDataSource);
      attachFile.setDataHandler(new DataHandler(new FileDataSource(fileDataSource)));
      attachFile.setFileName(file.getName());
      emailElements.addBodyPart(attachFile);

      message.setFrom(new InternetAddress());
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
      message.setSubject(subject);
      message.setContent(emailElements);
      Transport transport = session.getTransport("smtp");
      transport.connect(SERVICE, senderEmail, password);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      System.out.println("Procces completed successfully");
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
      transport.connect(SERVICE, senderEmail, password);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
      System.out.println("Procces completed successfully");
    } catch (MessagingException me) {
      me.printStackTrace();
    }
  }
}
