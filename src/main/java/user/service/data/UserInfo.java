package user.service.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfo {
private static final Logger logger_ = LoggerFactory.getLogger(UserInfo.class);
	
	// This class take only long and String type.
	// If other type is added, you must update stringToObject.
	private String id;
	private String name;
	private String password;
	private String priority;
	private long create_time;
	private String picture_url;
	
	public UserInfo(String id, String name, String password, UserPriority priority, Long create_time, String picture_url) {
		initUserInfo(id, name, password, priority.name(), create_time, picture_url);
	}
	
	public UserInfo() {
		initUserInfo("", "", "", "", (long)-1, "");
	}

	private void initUserInfo(String id, String name, String password, String priority, Long create_time, String picture_url) {
		if (id == null ||
				name == null ||
				password == null ||
				priority == null ||
				create_time == null ||
				picture_url == null) {
			logger_.error("Error creating UserInfo : invalid arguments");
			return;
		}
		
		setId(id);
		setName(name);
		setPassword(password);
		setPriority(priority);
		setCreate_time(create_time);
		setPicture_url(picture_url);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPicture_url() {
		return picture_url;
	}

	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	public static UserInfo stringToObject(String input) {
		UserInfo UserInfo = new UserInfo();
		String[] sources = input.substring(1, input.length() - 1).split(",");
		int parsed_fields_cnt = 0;
		
		for (String source : sources) {
			String[] s = source.trim().split("=");
			Field[] fields = UserInfo.getClass().getDeclaredFields();
			
			if (s.length < 2 || s[1].length() == 0) {
				parsed_fields_cnt++;
				continue;
			}
			
			for (Field field : fields) {
				if (field.getName().equals(s[0]) && !field.getName().equals("logger_")) {
					try {
						if (field.getType().equals(Long.TYPE)) {
							field.setLong(UserInfo, Long.valueOf(s[1]));
							parsed_fields_cnt++;
						} else if (field.getType().equals(String.class)) {
							field.set(UserInfo, s[1]);
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
		
		if (parsed_fields_cnt != UserInfo.getClass().getDeclaredFields().length - 1) {
			logger_.error("Error Parsing String to " + UserInfo.getClass().getName() + ": " + input);
			logger_.error("Error Parsing String to " + UserInfo.getClass().getName() + ": has " +
								UserInfo.getClass().getDeclaredFields().length + " fields, parsed " + parsed_fields_cnt);
		}
		
		return UserInfo;
	}
	
	// test
	public static void main(String[] args) throws Exception {
		UserInfo UserInfo = new UserInfo("me", "name", "1234", UserPriority.NORMAL, (long) 10, "");
		String userString = UserInfo.toMap().toString();
		UserInfo userNew = stringToObject(userString);
		System.out.println(userString);
		System.out.println(userNew.toMap());
	}
}
