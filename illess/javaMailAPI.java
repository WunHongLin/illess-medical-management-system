package com.example.illess;

import android.os.AsyncTask;
import android.content.Context;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class javaMailAPI extends AsyncTask<Void,Void,Void> {

    private Context context;
    private Session session;
    private String Email;
    private String Subject;
    private String Content;

    public javaMailAPI(Context context, String email, String subject, String content) {
        this.context = context;
        this.Email = email;
        this.Subject = subject;
        this.Content = content;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(properties,new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(Util.Email,Util.Password);
            }
        });

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(Util.Email));
            message.addRecipients(javax.mail.Message.RecipientType.TO,String.valueOf(new InternetAddress(Email)));
            message.setSubject(Subject);
            message.setText(Content);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
