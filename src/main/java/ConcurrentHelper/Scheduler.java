package ConcurrentHelper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler
{
	private static final String schedulerThread_ = "GLOBAL-SCHEDULER";
	private static Scheduler instance_;
	private static Lock createLock_ = new ReentrantLock();
	
	public static Scheduler instance()
	{
		if (instance_ == null)
		{			
			try
			{
				createLock_.lock();
				if (instance_ == null)
				{
					instance_ = new Scheduler();
				}
			}
			finally
			{
				createLock_.unlock();
			}
		}
		return instance_;
	}
	
	private ScheduledExecutorService timer_;
	
	private Scheduler()
	{
		timer_ = new DebuggableScheduledThreadPoolExecutor(1);
	}
	
	/**
	 * Schedule a task for one time execution with a 
	 * specific delay
	 * @param task to be executed
	 * @param delay time after which to execute the task.
	 */
	public void schedule(TimerTask task, long delay)
	{
		timer_.schedule(task, delay, TimeUnit.MILLISECONDS);
	}
	
	public void schedule(TimerTask task)
	{
		timer_.schedule(task, 0, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Schedule a task for one time execution with a 
	 * specific delay and specified periodicity.
	 * @param task to be executed
	 * @param delay time after which to execute the task.
	 * @param period the periodicity of the execution
	 */
	public void schedule(TimerTask task, long delay, long period)
	{
		timer_.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
	}
}
