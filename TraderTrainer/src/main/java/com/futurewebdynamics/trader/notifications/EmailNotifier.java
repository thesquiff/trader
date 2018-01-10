package com.futurewebdynamics.trader.notifications;

import org.apache.log4j.Logger;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;

/**
 * Created by Charlie on 02/11/2017.
 */
public class EmailNotifier implements INotifier{

    private String toEmailAddress;
    private String fromEmailAddress;

    private Mailer mailer;

    final static Logger logger = Logger.getLogger(EmailNotifier.class);

    final static int maxAttempts = 5;

    public EmailNotifier(String smtpServer, int port, String username, String password) {
        mailer = new Mailer(smtpServer, 25, username, password);
    }

    public boolean SendNotification(Notification n) {

        Email email = new EmailBuilder()
                .from("Trader App", fromEmailAddress)
                .to("", toEmailAddress)
                .subject(n.getSubject())
                .text(n.getMessage())
                .build();

        for (int i = 1; i <= maxAttempts; i++) {
            try {
                mailer.sendMail(email);
                break;
            } catch (Exception ex) {
                if (i < maxAttempts) {
                    logger.warn("An exception occurred sending a notification to " + toEmailAddress, ex);
                } else {
                    logger.error("An exception occurred sending a notification to " + toEmailAddress + ". Out of retries.", ex);
                    return false;
                }
            }
        }
        return true;
    }

    public void setFromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }
}
