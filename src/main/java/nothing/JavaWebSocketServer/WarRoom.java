package nothing.JavaWebSocketServer;

import Serversynchronization.User;

public class WarRoom {
	User user1;
	User user2;

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
		if (user1.equals(obj) || user2.equals(obj)) {
			return true;
		}
		return false;
	}
}
