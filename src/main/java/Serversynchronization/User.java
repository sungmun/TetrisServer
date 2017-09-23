package Serversynchronization;

import java.net.Socket;

public class User {
	private String usernumber;
	private String id;
	private String name;
	private Socket socket;
	public User(Socket socket) {
		this.socket=socket;
	}
	public User(String id, String name) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
	}

	public User(String num,String id, String name, Socket socket) {
		usernumber=num;
		this.id = id;
		this.name = name;
		this.socket = socket;
	}


	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
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
		User user=(User)obj;
		if(this.usernumber==user.usernumber) {
			return true;
		}
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getUserNumber() {
		return usernumber;
	}
	public void setUserNumber(String usernumber) {
		this.usernumber = usernumber;
	}
	

}
