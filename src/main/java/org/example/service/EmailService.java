package org.example.service;

import org.example.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private Session session;
    private String emailUsername;
    private String emailPassword;
    
    public EmailService() {
        initializeSession();
    }
    
    private void initializeSession() {
        emailUsername = ConfigUtil.getEmailUsername();
        emailPassword = ConfigUtil.getEmailPassword();
        
        Properties props = new Properties();
        props.put("mail.smtp.host", ConfigUtil.getEmailHost());
        props.put("mail.smtp.port", ConfigUtil.getEmailPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });
    }
    
    public boolean envoyerEmail(String destinataire, String sujet, String message) {
        try {
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(emailUsername));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            email.setSubject(sujet);
            email.setText(message);
            
            Transport.send(email);
            logger.info("Email envoyé à: {}", destinataire);
            return true;
            
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à: " + destinataire, e);
            return false;
        }
    }
    
    public boolean envoyerEmailAvecHTML(String destinataire, String sujet, String messageHTML) {
        try {
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(emailUsername));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            email.setSubject(sujet);
            email.setContent(messageHTML, "text/html; charset=utf-8");
            
            Transport.send(email);
            logger.info("Email HTML envoyé à: {}", destinataire);
            return true;
            
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email HTML à: " + destinataire, e);
            return false;
        }
    }
    
    public boolean testerConnexion() {
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(ConfigUtil.getEmailHost(), emailUsername, emailPassword);
            transport.close();
            logger.info("Connexion SMTP réussie");
            return true;
        } catch (MessagingException e) {
            logger.error("Échec de la connexion SMTP", e);
            return false;
        }
    }
}