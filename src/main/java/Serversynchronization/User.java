package Serversynchronization;

public class User {
	private int usernumber;
	private String id;
	private String name;

	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public void setUserNumber(Integer num) {
		usernumber=num.intValue();
	}
	public Integer getUserNumber() {
		return new Integer(usernumber);
	}
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		User user = (User) obj;
		if (this.usernumber == user.usernumber) {
			return true;
		}
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

}
