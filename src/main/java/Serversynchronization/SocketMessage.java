package Serversynchronization;

import com.google.gson.Gson;

public class SocketMessage {
	private int messagetype;
	private String message;
	private String ip = null;
	@SuppressWarnings("rawtypes")
	private static Class objecttype;

	public SocketMessage(int msg, Object obj) {
		messagetype = msg;
		setMessage(new Gson().toJson(obj));
		objecttype = obj.getClass();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMessageType() {
		return messagetype;
	}

	public void setMessageType(int messagetype) {
		this.messagetype = messagetype;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@SuppressWarnings("unchecked")
	public static Object transformJSON(String json) {
		return new Gson().fromJson(json, objecttype);

	}
}
