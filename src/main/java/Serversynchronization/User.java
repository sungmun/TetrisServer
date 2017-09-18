package Serversynchronization;

public class User {
	private String id;
	private String ip;
	private String name;

	public User(String id, String name) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
	}

	public User(String id, String name, String ip) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.ip = ip;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getIP() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
