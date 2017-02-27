package books.service.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryInfo {
	private static final Logger logger_ = LoggerFactory.getLogger(BooksInfo.class);

	// This class take only long and String type.
	// If other type is added, you must update stringToObject.
	private String id;
	private String user_id;
	private String name;
	private String picture_url;
	
	public CategoryInfo(String user_id, String name, String picture_url) {
		if (user_id == null ||
				picture_url == null) {
			logger_.error("Error creating BooksInfo : invalid arguments");
			return;
		}
		
		this.setId(user_id + name);
		this.setUser_id(user_id);
		this.setName(name);
		this.setPicture_url(picture_url);
	}
	
	public CategoryInfo() {
		this.setId("");
		this.setUser_id("");
		this.setName("");
		this.setPicture_url("");
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture_url() {
		return picture_url;
	}

	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
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
	
	public static CategoryInfo stringToObject(String input) {
		CategoryInfo categoryInfo = new CategoryInfo();
		String[] sources = input.substring(1, input.length() - 1).split(",");
		int parsed_fields_cnt = 0;
		
		for (String source : sources) {
			String[] s = source.trim().split("=");
			Field[] fields = categoryInfo.getClass().getDeclaredFields();
			
			if (s.length < 2 || s[1].length() == 0) {
				parsed_fields_cnt++;
				continue;
			}
			
			for (Field field : fields) {
				if (field.getName().equals(s[0]) && !field.getName().equals("logger_")) {
					try {
						if (field.getType().equals(Long.TYPE)) {
							field.setLong(categoryInfo, Long.valueOf(s[1]));
							parsed_fields_cnt++;
						} else if (field.getType().equals(String.class)) {
							field.set(categoryInfo, s[1]);
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
		
		if (parsed_fields_cnt != categoryInfo.getClass().getDeclaredFields().length - 1) {
			logger_.error("Error Parsing String to " + categoryInfo.getClass().getName() + ": " + input);
			logger_.error("Error Parsing String to " + categoryInfo.getClass().getName() + ": has " +
					categoryInfo.getClass().getDeclaredFields().length + " fields, parsed " + parsed_fields_cnt);
		}
		
		return categoryInfo;
	}
	
	// test
		public static void main(String[] args) throws Exception {
			CategoryInfo info = new CategoryInfo("me", "good", "");
			String s = info.toMap().toString();
			CategoryInfo info1 = CategoryInfo.stringToObject(s);
			System.out.println(s);
			System.out.println(info1.toMap());
		}
}
