package Serversynchronization;

import com.google.gson.Gson;

public class SocketMessage {
	private int messagetype;
	private String message;

	public SocketMessage(int msg, Object obj) {
		messagetype = msg;
		setMessage(obj);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		Gson gson=new Gson();
		this.message = gson.toJson(message);
	}

	public int getMessageType() {
		return messagetype;
	}

	public void setMessageType(int messagetype) {
		this.messagetype = messagetype;
	}

}
