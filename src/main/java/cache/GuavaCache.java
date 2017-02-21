package cache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableMap;

public class GuavaCache<K,V> { 
	private static final Logger logger_ = LoggerFactory.getLogger(GuavaCache.class);
	private LoadingCache<K,V> cache_ = null;

	public  GuavaCache(int threadNum, long expireTime, TimeUnit tu, int initCapicity, int maximumSize, CacheLoader<K,V> loadable, RemovalListener<K, V> removelistener) 
	{
		cache_ =    CacheBuilder.newBuilder()
				.concurrencyLevel(threadNum)
				.expireAfterAccess(expireTime, tu)
				.initialCapacity(initCapicity)
				.maximumSize(maximumSize)
				.recordStats()
				.removalListener(removelistener)
				.build(loadable);
	}
	
	public CacheStats getStats()
	{
		return cache_.stats();
	}
	
	public long getSize()
	{
		return cache_.size();
	}
	
	public void invalidAll()
	{
		cache_.invalidateAll();
	}
	
	public void invalidate(K key)
	{
		cache_.invalidate(key);
	}
	
	public void put(K key, V value)
	{
		cache_.put(key, value);
	}
	
	public ImmutableMap<K, V> getAllPresent(Iterable<K> keys)
	{
		return cache_.getAllPresent(keys);
	}
	
	public V getIfPresent(K key) throws ExecutionException
	{
		return cache_.get(key);
	}
	
	public V get(K key) throws ExecutionException
	{
		return cache_.get(key);
	}
	
	public void cleanUp()
	{
		cache_.cleanUp();
	}
	
	public static void main(String[] args) throws ExecutionException {
		CacheLoader<String,String> tl = new testloadable();
		RemovalListener<String, String> trl = new testRemovalListener();
		GuavaCache <String,String> gc = new GuavaCache<String,String>(1, 1, TimeUnit.SECONDS, 10, 100, tl, trl); 
		for(int i =0 ; i<101;i++)
		{
			gc.get("KEY" + i);
		}
		CacheStats cs = gc.getStats();
		System.out.println(cs.toString());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(cs.toString());
		
		System.out.println(gc.get("KEY"+1));
		System.out.println(cs.toString());
	}
}
