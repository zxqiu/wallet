package books.service.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooksInfo {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksInfo.class);
	
	// This class take only long and String type.
	// If other type is added, you must update stringToObject.
	private String id;
	private String user_id;
	private String category;
	private String event_date;
	private long amount;
	private String note;
	private String picture_url;
	private long edit_time;
	
	public BooksInfo(Long create_time, String user_id, String category, String event_date, Long amount, String note, String picture_url) {
		initBooksInfo(user_id + create_time, create_time, user_id, category, event_date, amount, note, picture_url);
	}
	
	public BooksInfo(String id, Long edit_time, String user_id, String category, String event_date, Long amount, String note, String picture_url) {
		initBooksInfo(id, edit_time, user_id, category, event_date, amount, note, picture_url);
	}
	
	public BooksInfo() {
		initBooksInfo("", (long)-1, "", "", "", (long)-1, "", "");
	}

	private void initBooksInfo(String id, Long edit_time, String user_id, String category, String event_date, Long amount, String note, String picture_url) {
		if (id == null ||
				user_id == null ||
				category == null ||
				note == null ||
				picture_url == null) {
			logger_.error("Error creating BooksInfo : invalid arguments");
			return;
		}
		
		setId(id);
		setUser_id(user_id);
		setCategory(category);
		setEvent_time(event_date);
		setAmount(amount);
		setNote(note);
		setPicture_url(picture_url);
		setEdit_time(edit_time);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_time(String event_date) {
		this.event_date = event_date;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPicture_url() {
		return picture_url;
	}

	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}

	public long getEdit_time() {
		return edit_time;
	}

	public void setEdit_time(long edit_time) {
		this.edit_time = edit_time;
	}
	
	
	public Map<String,Object> toMap(){
	    Map<String,Object> map = new HashMap<String, Object>();
	    Field[] fields = this.getClass().getDeclaredFields();
	    for (Field field : fields) {
	        Object obj;
	        try {
	            obj = field.get(this);
	            if(obj!=null && !field.getName().equals("logger_")) {
	                map.put(field.getName(), obj);
	            }
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	    }
	    return map;
	}
	
	public static BooksInfo stringToObject(String input) {
		BooksInfo booksInfo = new BooksInfo();
		String[] sources = input.substring(1, input.length() - 1).split(",");
		int parsed_fields_cnt = 0;
		
		for (String source : sources) {
			String[] s = source.trim().split("=");
			Field[] fields = booksInfo.getClass().getDeclaredFields();
			
			if (s.length < 2 || s[1].length() == 0) {
				parsed_fields_cnt++;
				continue;
			}
			
			for (Field field : fields) {
				if (field.getName().equals(s[0]) && !field.getName().equals("logger_")) {
					try {
						if (field.getType().equals(Long.TYPE)) {
							field.setLong(booksInfo, Long.valueOf(s[1]));
							parsed_fields_cnt++;
						} else if (field.getType().equals(String.class)) {
							field.set(booksInfo, s[1]);
							parsed_fields_cnt++;
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		if (parsed_fields_cnt != booksInfo.getClass().getDeclaredFields().length - 1) {
			logger_.error("Error Parsing String to " + booksInfo.getClass().getName() + ": " + input);
			logger_.error("Error Parsing String to " + booksInfo.getClass().getName() + ": has " +
								booksInfo.getClass().getDeclaredFields().length + " fields, parsed " + parsed_fields_cnt);
		}
		
		return booksInfo;
	}
	
	// test
	public static void main(String[] args) throws Exception {
		BooksInfo booksInfo = new BooksInfo((long) 1, "me", "good", "1990-11-15", (long) 10, "note", "");
		String bookString = booksInfo.toMap().toString();
		BooksInfo orderNew = BooksInfo.stringToObject(bookString);
		System.out.println(bookString);
		System.out.println(orderNew.toMap());
	}
}
