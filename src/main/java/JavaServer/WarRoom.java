package JavaServer;

import Serversynchronization.User;

public class WarRoom {
	User user1;
	User user2;
	boolean connencting=true;
	public WarRoom(User user1, User user2) {
		this.user1 = user1;
		this.user2 = user2;
	}

	public User opponentUser(User user) {
		if (user1.equals(user)) {
			return user2;
		} else if (user2.equals(user)) {
			return user1;
		} else {
			return null;
		}
	}
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass()==User.class) {
			return equals((User)obj);
		}
		return super.equals(obj);
	}
	public boolean equals(User user) {
		if (user1.equals(user) || user2.equals(user)) {
			return true;
		}
		return false;
	}
}
