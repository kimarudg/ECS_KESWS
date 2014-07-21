package logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage;

import ECSKESWSService.ConfigMapper;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

public class ECSKESWSLogger {

    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;
    static private Formatter formatterHTML;
    static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static public void setup() throws IOException {
         new ConfigMapper();
        final FileHandler fileHTML = new FileHandler(ConfigMapper.getLogFile(), true);
        // Create HTML Formatter

        formatterHTML = new ECSKESWSLogFormater();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);
    }

    static public void Log(String message, String lgLevel) {
        if (lgLevel.contentEquals("finest")) {
            logger.setLevel(Level.FINEST);
            logger.finest(message);
        }
        if (lgLevel.contentEquals("FINER")) {
            logger.setLevel(Level.FINER);
            logger.finer(message);
        }
        if (lgLevel.contentEquals("FINE")) {
            logger.setLevel(Level.FINE);
            logger.fine(message);
        }
        if (lgLevel.contentEquals("INFO")) {
            logger.setLevel(Level.INFO);
            logger.info(message);
        }
        if (lgLevel.contentEquals("WARNING")) {
            logger.setLevel(Level.WARNING);
            logger.warning(message);
        }
        if (lgLevel.contentEquals("SEVERE")) {
            logger.setLevel(Level.SEVERE);
            logger.severe(message);
        }

    }

    static public void maillogs() {
        new ConfigMapper();

        System.out.println("Send message successfully....");

        //2) compose message     
        try {
            String to = ConfigMapper.getEmail1();
            final String user = "bkimutai@kephis.org";//change accordingly  
            final String password = "Kephis@2013";//change accordingly  

            //1) get the session object     
            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.host", "mail.kephis.org");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Message alert");

            //3) create MimeBodyPart object and set your message text     
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText("KESWS ECS ");

            //4) create new MimeBodyPart object and set DataHandler object to this object      
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();

            String filename = ConfigMapper.getLogFile();//change accordingly  
            DataSource source = new FileDataSource(filename);
            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(filename);


            //5) create Multipart object and add MimeBodyPart objects to this object      
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            //6) set the multiplart object to the message object  
            message.setContent(multipart);

            //7) send message  
            Transport.send(message);

            System.out.println("message sent....");
        } catch (Exception ex) {

            ex.printStackTrace();

        }
    }

    static public void mailnotification(String messages) {


        final String username = "bobbykimutai@gmail.com";
        final String password = "";

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("bobbykimutai@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(ConfigMapper.getEmail1()));
            message.setSubject("KESWS-ECS Alerts");
            message.setText(messages);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        /**
         * new ConfigMapper();
         *
         *
         * //2) compose message try{ String to=
         * "bobbykimutai@gmail.com";//ConfigMapper.getEmail1(); final String
         * user="bkimutai@kephis.org";//change accordingly final String
         * password="Kephis@2013";//change accordingly * //1) get the session
         * object Properties properties = System.getProperties();
         * properties.setProperty("mail.smtp.host", "mail.kephis.org");
         * properties.put("mail.smtp.auth", "true"); * Session session =
         * Session.getDefaultInstance(properties, new Authenticator() {
         * protected PasswordAuthentication getPasswordAuthentication() { return
         * new PasswordAuthentication(user,password); } }); MimeMessage message
         * = new MimeMessage(session); message.setFrom(new
         * InternetAddress(user));
         * message.addRecipient(Message.RecipientType.TO,new
         * InternetAddress(to)); message.setSubject("Message alert"); // Now set
         * the actual message message.setText(messages);
         *
         * // Send message Transport.send(message); System.out.println("Sent
         * message successfully...."); //7) send message
         * Transport.send(message); * System.out.println("message sent....");
         * }catch (Exception ex) {
         *
         * ex.printStackTrace();
         *
         * }
         *
         */
    }
}
