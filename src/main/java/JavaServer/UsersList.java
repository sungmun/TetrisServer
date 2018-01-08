package JavaServer;

import java.util.Vector;

import Serversynchronization.User;

public class UsersList {
	private static Vector<User> list = new Vector<User>();

	private static Object key = new Object();

	public static User[] getList() {
		User[] arr = new User[list.size()];
		arr = list.toArray(arr);
		return arr;
	}

	public static boolean add(User user) {
		synchronized (key) {
			return list.add(user);
		}

	}

	public static boolean delete(User user) {
		synchronized (key) {
			return list.remove(user);
		}
	}

	public static void setList(User[] list) {
		UsersList.list.clear();
		for (User user : list) {
			UsersList.list.addElement(user);
		}
	}

	public static boolean findList(User user) {
		synchronized (key) {
			return (list.indexOf(user)==-1)?false:true;
		}
	}

}
