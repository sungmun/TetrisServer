package Serversynchronization;

public class User {
	private int usernumber;
	private String id;
	private String name;
	private PlayerInformation info;
	public User(String id, String name,int level,int score) {
		this.id=id;
		this.name=name;
		info=new PlayerInformation(level, score);
	}
	public User(String id, String name, Integer num) {
		this.usernumber = num.intValue();
		this.id = id;
		this.name = name;
	}

	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public User() {
	}
	public void setUserNumber(Integer num) {
		usernumber = num.intValue();
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

	public boolean equals(User user) {
		if (this.usernumber == user.usernumber) {
			return true;
		}
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerInformation getInfo() {
		return info;
	}

	public void setInfo(PlayerInformation info) {
		this.info = info;
	}

}
