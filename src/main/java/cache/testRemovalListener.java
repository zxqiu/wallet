package cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class testRemovalListener implements RemovalListener<String,String>
{
	private static final Logger logger_ = LoggerFactory.getLogger(testRemovalListener.class);
	public void onRemoval(RemovalNotification<String, String> notification) {
		logger_.warn(notification.getKey() + notification.getValue() + notification.getCause());
	}
}