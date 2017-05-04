package com.wallet.email.task;

import com.wallet.email.core.Email;
import com.wallet.email.dao.EmailConnector;
import com.wallet.utils.tools.concurrentHelper.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.wallet.email.dao.EmailConnector.EMAIL_CHECK_INTERVAL;

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
        logger_.info("Start Email check task on interval of " + EMAIL_CHECK_INTERVAL / 1000 + " seconds.");
        Scheduler.instance().schedule(new EmailTasks.CheckTask(), EMAIL_CHECK_INTERVAL, EMAIL_CHECK_INTERVAL);
    }

    public static class CheckTask extends TimerTask {
        @Override
        public void run() {
            logger_.info("Scan emails for new and send failed emails ...");
            List<Email> newEmails = null;
            List<Email> sendFailedEmails = null;
            try {
                newEmails = emailConnector.getByStatus(Email.EMAIL_STATUS.NEW);
                sendFailedEmails = emailConnector.getByStatus(Email.EMAIL_STATUS.SEND_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
            }

            pendingEmailMapLock.lock();
            try {
                if (newEmails != null) {
                    for (Email email : newEmails) {
                        if (!pendingEmailMap.containsKey(email.getId())) {
                            pendingEmailMap.put(email.getId(), email);
                        }
                    }
                }

                if (sendFailedEmails != null) {
                    for (Email email : sendFailedEmails) {
                        if (!pendingEmailMap.containsKey(email.getId())) {
                            pendingEmailMap.put(email.getId(), email);
                        }
                    }
                }
            } finally {
                logger_.info("Scan emails for new and send failed emails ... done. pendingEmailMap size="
                        + pendingEmailMap.size());
                pendingEmailMapLock.unlock();
            }
        }
    }
}
