package com.wallet.email.task;

import com.wallet.email.core.Email;
import com.wallet.email.dao.EmailConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neo on 5/3/17.
 */
public class EmailTasks {
    private static final Logger logger_ = LoggerFactory.getLogger(EmailTasks.class);

    private static EmailConnector emailConnector = null;

    private static ConcurrentHashMap<String, Email> pendingEmailMap = new ConcurrentHashMap<>();
    private static Lock pendingEmailMapLock = new ReentrantLock();

    public static void init() throws Exception {
        emailConnector = EmailConnector.instance();
    }

    public static class CheckTask extends TimerTask {
        @Override
        public void run() {
            List<Email> newEmails = emailConnector.getByStatus(Email.EMAIL_STATUS.NEW);
            List<Email> sendFailedEmails = emailConnector.getByStatus(Email.EMAIL_STATUS.SEND_FAILED);

            logger_.info("Scan emails for new and send failed emails ...");
            pendingEmailMapLock.lock();
            try {
                for (Email email : newEmails) {
                    if (!pendingEmailMap.containsKey(email.getId())) {
                        pendingEmailMap.put(email.getId(), email);
                    }
                }

                for (Email email : sendFailedEmails) {
                    if (!pendingEmailMap.containsKey(email.getId())) {
                        pendingEmailMap.put(email.getId(), email);
                    }
                }
            } finally {
                pendingEmailMapLock.unlock();
            }
            logger_.info("Scan emails for new and send failed emails ... done");
        }
    }
}
