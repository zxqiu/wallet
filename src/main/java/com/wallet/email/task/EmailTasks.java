package com.wallet.email.task;

import com.wallet.email.core.Email;
import com.wallet.email.dao.EmailConnector;
import com.wallet.utils.tools.concurrentHelper.Scheduler;
import org.simplejavamail.mailer.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by neo on 5/3/17.
 */
public class EmailTasks {
    private static final Logger logger_ = LoggerFactory.getLogger(EmailTasks.class);

    private static EmailConnector emailConnector = null;

    private static ConcurrentHashMap<String, Email> pendingEmailMap = null;
    private static Lock pendingEmailMapLock = new ReentrantLock();

    public static final long EMAIL_CHECK_INTERVAL = 1000 * 10; // ms
    public static final long EMAIL_SENDER_HEART_BEAT = 3600; // s
    public static final int EMAIL_SENDER_MAX = 10;

    public void init() throws Exception {
        emailConnector = EmailConnector.instance();
        pendingEmailMap = new ConcurrentHashMap<>();

        logger_.info("Start Email check task on interval of " + EMAIL_CHECK_INTERVAL / 1000 + " seconds.");
        Scheduler.instance().schedule(new EmailTasks.CheckTask(), EMAIL_CHECK_INTERVAL, EMAIL_CHECK_INTERVAL);

        Runnable sendEmailTask = new SendEmailTask();
        Thread sendEmailTaskThread = new Thread(sendEmailTask);
        logger_.info("Start Email send task on thread " + sendEmailTaskThread.getName());
        sendEmailTaskThread.start();
    }

    /*
    this task should only insert to pendingEmailMap
     */
    public static class CheckTask extends TimerTask {
        @Override
        public void run() {
            //logger_.info("Scan emails for new and send failed emails ...");
            List<Email> newEmails = null;
            List<Email> sendFailedEmails = null;
            try {
                newEmails = emailConnector.getByStatus(Email.EMAIL_STATUS.NEW);
                sendFailedEmails = emailConnector.getByStatus(Email.EMAIL_STATUS.SEND_FAILED);
            } catch (Exception e) {
                logger_.error("Error : failed to get pending emails: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            pendingEmailMapLock.lock();
            try {
                addToPendingEmailMap(newEmails);
                addToPendingEmailMap(sendFailedEmails);
                //logger_.info("Scan emails for new and send failed emails ... done. pendingEmailMap size="
                        //+ pendingEmailMap.size());
            } finally {
                pendingEmailMapLock.unlock();
            }
        }

        private static void addToPendingEmailMap(List<Email> pendingEmails) {
            if (pendingEmails == null) {
                return;
            }

            for (Email email : pendingEmails) {
                if (!pendingEmailMap.containsKey(email.getId())) {
                    pendingEmailMap.put(email.getId(), email);
                } else {
                    Email pendingEmail = pendingEmailMap.get(email.getId());
                    if (!pendingEmail.getStatus().equals(email.getStatus())
                            && !pendingEmail.getStatus().equals(Email.EMAIL_STATUS.SENDING)) {
                        try {
                            emailConnector.updateByID(pendingEmailMap.get(email.getId()));
                            if (!isPendingEmail(pendingEmail)) {
                                pendingEmailMap.remove(pendingEmail.getId());
                            }
                        } catch (Exception e) {
                            logger_.error("Error : failed to update email status: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /*
    this task should only remove from pendingEmailMap
     */
    public class SendEmailTask implements Runnable {
        private long start_time;
        private ExecutorService executor = null;

        public SendEmailTask() {
            start_time = System.currentTimeMillis() / 1000;
        }

        @Override
        public void run() {
            while (true) {
                if ((System.currentTimeMillis() / 1000 - start_time) % EMAIL_SENDER_HEART_BEAT == 0) {
                    logger_.info("SendEmailTask still running ...");
                }
                int enqueuedCnt = 0;

                if (pendingEmailMap != null && !pendingEmailMap.isEmpty()) {
                    logger_.info("Email send task found email(s) need to be sent. Try to lock pendingEmailMap..");
                    pendingEmailMapLock.lock();
                    try {
                        if (pendingEmailMap.isEmpty()) {
                            continue;
                        }

                        logger_.info("Email send task found " + pendingEmailMap.size() + " email(s)");
                        for (Email e : pendingEmailMap.values()) {
                            if (!isPendingEmail(e)) {
                                continue;
                            }
                            e.setStatus(Email.EMAIL_STATUS.SENDING);
                            Runnable sender = new emailSender(e);
                            if (executor == null || executor.isTerminated()) {
                                executor = Executors.newFixedThreadPool(EMAIL_SENDER_MAX);
                            }
                            enqueuedCnt++;
                            executor.execute(sender);
                        }
                        logger_.info("Email send task enqueued " + enqueuedCnt + " email(s)");
                    } finally {
                        pendingEmailMapLock.unlock();
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class emailSender implements Runnable {
        private Email in = null;

        public emailSender(Email in) {
            this.in = in;
        }

        @Override
        public void run() {
            logger_.info(this.toString() + " : sender start on thread " + Thread.currentThread().getName());
            this.emailSend(in);
        }

        private void emailSend(Email in) {
            org.simplejavamail.email.Email email = new org.simplejavamail.email.Email();
            email.setFromAddress("walletnote", in.getFrom_address());
            email.addRecipient("", in.getTo_address(), Message.RecipientType.TO);

            if (in.getSubject() != null) {
                email.setSubject(in.getSubject());
            }
            if (in.getHtml() != null && in.getHtml().length() != 0) {
                email.setTextHTML(in.getHtml());
            } else if (in.getText() != null && in.getText().length() != 0) {
                email.setText(in.getText());
            }

            logger_.info(this.toString() + " : sending email " + in.getId() + " from " + in.getFrom_address() + " to " + in.getTo_address());
            try {
                //new Mailer("smtp.gmail.com", 465, "", ""
                 //       , TransportStrategy.SMTP_SSL).sendMail(email);
                new Mailer().sendMail(email);
            } catch (Exception e) {
                in.setStatus(Email.EMAIL_STATUS.SEND_FAILED);
                logger_.error(this.toString() + " : Error : failed to send email + " + in.getId() + " : " + e.getMessage());
                e.printStackTrace();
                return;
            }
            logger_.info(this.toString() + " : email sent ");

            pendingEmailMapLock.lock();
            try {
                if (pendingEmailMap.containsKey(in.getId())) {
                    pendingEmailMap.get(in.getId()).setStatus(Email.EMAIL_STATUS.SENT);
                }
            } finally {
                pendingEmailMapLock.unlock();
            }
        }
    }

    private static boolean isPendingEmail(Email email) {
        return (email.getStatus().equals(Email.EMAIL_STATUS.NEW)
                || email.getStatus().equals(Email.EMAIL_STATUS.SEND_FAILED));
    }
}
