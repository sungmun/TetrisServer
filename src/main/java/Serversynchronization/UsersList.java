package Serversynchronization;

import java.util.HashSet;

public class UsersList {
	private static HashSet<User> list = new HashSet<User>();

	public void add(User user) {
		list.add(user);
	}
	public void delete(User user) {
		list.remove(user);
	}
	public static HashSet<User> getList() {
		return list;
	}

	public static void setList(HashSet<User> list) {
		UsersList.list = list;
	}

}
