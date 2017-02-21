package utils;

public class UtilityPrivilege {
	public enum Privilege {        
		GUEST {                                                                                                                                                                                           
			@Override                                                                                                                                                                                   
			public String toString() {                                                                                                                                                                  
				return "Guest";                                                                                                                                                                              
			}                                                                                                                                                                                           
		},
		MEMBER {
			@Override                                                                                                                                                                                   
			public String toString() {                                                                                                                                                                  
				return "Member";                                                                                                                                                                              
			}     
		},
		VIP {
			@Override                                                                                                                                                                                   
			public String toString() {                                                                                                                                                                  
				return "Vip";                                                                                                                                                                              
			}     
		},
		ADMIN {
			@Override                                                                                                                                                                                   
			public String toString() {                                                                                                                                                                  
				return "Admin";                                                                                                                                                                              
			}     
		}
	}
}
