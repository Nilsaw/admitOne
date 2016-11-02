package com.admitone.domain.objects;

/**
 * The customer id is the primary key. 
 * @author Nilesh Sawant
 *
 */
public class CustomerInfo {
	private String id;
	private String fName;
	private String lName;
	private String email;
	
	public CustomerInfo(String id, String fName, String lName, String email){
		this.id = id;
		this.fName = fName;
		this.lName = lName;
		this.email = email;
	}
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	
}
