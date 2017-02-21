package MapDBConnector;

import java.io.File;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ConcurrentHelper.Scheduler;

public class MapDBConnector {
	private static Lock createLock_ = new ReentrantLock();
	private static final Logger logger_ = LoggerFactory.getLogger(MapDBConnector.class);
	private static MapDBConnector instance_ = null;
	private static final String FILE_DB_NAME = "FileDB";
	private static DB db = null;

	
	public static long refreshInterval_ = 24 * 60 * 60 * 1000;

	public static MapDBConnector instance() throws MapDBException, JSONException {
		if ( instance_ == null )
		{
			MapDBConnector.createLock_.lock();
			try
			{
				
				instance_ = new MapDBConnector();
			}
			finally
			{
				MapDBConnector.createLock_.unlock();
			}
		}
		
		return instance_;
	}
	
	private MapDBConnector() throws MapDBException{
		db = newFileDB(FILE_DB_NAME);
		if (db == null) {
			throw new MapDBException();
		}
		Scheduler.instance().schedule(new refreshTask(), refreshInterval_, refreshInterval_);
	}
	
	public void refreshAllMapDBs() {
	}
	
	/**
	 * Open existing DB file. Return null if file not found.
	 * @param dbName
	 * @return DB
	 */
	public static DB openFileDB() {
		File file = new File(FILE_DB_NAME);
		
		if (file.exists() == false) {
			return null;
		}
		
		DB newDB = DBMaker.fileDB(file)
				.concurrencyScale(4)
				.fileMmapEnable()
				.deleteFilesAfterClose()
				.closeOnJvmShutdown()
				.make();
		return newDB;
	}
	
	/**
	 * Open or create file DB.
	 * @param dbName
	 * @return DB
	 */
	private static DB newFileDB(String dbName) {
		if (dbName == null || dbName.length() == 0) {
			return null;
		}
		
		// deleteFilesAfterClose() not actually deleted, but closed correctly
		DB newDB = DBMaker.fileDB(new File(dbName))
				.concurrencyScale(4)
				.fileMmapEnable()
				.deleteFilesAfterClose()
				.closeOnJvmShutdown()
				.make();
		
		return newDB;
	}
	
	public void closeDB() {
		db.close();
	}
	
	public HTreeMap<String, String> newMap(String mapName) {
		if (mapName == null || mapName.length() == 0) {
			return null;
		}
		
		HTreeMap<String, String> map = db.hashMap(mapName)
				.keySerializer(Serializer.STRING)
				.valueSerializer(Serializer.STRING)
				.createOrOpen();
		
		return map;
	}
	
	public HTreeMap<String, String> getMap(String mapName)
	{
		HTreeMap<String, String> map = db.hashMap(mapName,
												Serializer.STRING,
												Serializer.STRING)
										.open();
		return map;
	}
	
	public void commit() {
		db.commit();
	}
	
	private class refreshTask extends TimerTask{
		@Override
		public void run() {
			try {
				MapDBConnector.instance().refreshAllMapDBs();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
	}
	
	public static void main(String[] args) throws Exception {
		HTreeMap<String, String> hm = MapDBConnector.instance().newMap("testMap");
		//hm.put("testName0", "testValue0");
		//hm.put("testName1", "testValue1");
		//hm.put("testName2", "testValue2");

		HTreeMap<String, String> map = MapDBConnector.instance().getMap("testMap");
		for(Object key : map.keySet())
		{
			//logger_.warn(key.toString() + ":" + map.get(key).toString());
			Thread.sleep(1000);
			System.out.println(key.toString() + ":" + map.get(key).toString());
		}
		MapDBConnector.instance().closeDB();
	}
}
