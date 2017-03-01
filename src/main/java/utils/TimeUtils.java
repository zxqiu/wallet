package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {
	private static final Logger logger_ = LoggerFactory.getLogger(TimeUtils.class);
	
	private static final AtomicLong TIMESTAMP_LAST_TIME_MS = new AtomicLong();
	
	/**
	 * 
	 * @return get Beijing time
	 */
	public static String getBeijingTime() {
		TimeZone local = TimeZone.getDefault();
		String dateString = null;
		
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateString = dateFormat.format(date);
        TimeZone.setDefault(local);
        
        return dateString;
	}
	
	
	public static String getUniqueTimeStampInDateTime() {
		Long timeStampMS = TimeUtils.getUniqueTimeStampInMS();
		String ret = TimeUtils.getBeijingTime() + timeStampMS.toString().substring(timeStampMS.toString().length() - 3);
		
		return ret;
	}
	
	/**
	 * 
	 * @return get a unique time stamp
	 */
	public static long getUniqueTimeStampInMS() {
	    long now = System.currentTimeMillis();
	    while(true) {
	        long lastTime = TIMESTAMP_LAST_TIME_MS.get();
	        if (lastTime >= now)
	            now = lastTime+1;
	        if (TIMESTAMP_LAST_TIME_MS.compareAndSet(lastTime, now))
	            return now;
	    }
	}
	
	public static String formatISOToUS(String iso) {
		return iso.substring(5, 7) + "/" + iso.substring(8, 10) + "/" + iso.substring(0, 4);
	}
	
	public static String formatUSToISO(String us) {
		return us.substring(6, 10) + "-" + us.substring(3, 5) + "-" + us.substring(0, 2); 
	}
	
	// test
	public static void main(String[] arg) throws Exception
	{
        logger_.warn(TimeUtils.getBeijingTime());
	}
}
