package JavaServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

import Serversynchronization.User;

public class TetrisRanking {
	static TetrisDataBase database = new TetrisDataBase();
	static User[] bestofbest = new User[100];
	static HashMap<Integer, User> dailyrank = new HashMap<>();
	static int index = 0;

	public TetrisRanking() {
		bestofbest = database.BestOfBestSort();
		database.dailyTablecheck();
		dailyrank = database.selectDailyBest();
	}

	public User insertRanking(User user) {
		LocalDateTime time = LocalDateTime.now();
		int h = time.getHour();
		int m = time.getMinute();
		int s = time.getSecond();
		int n = time.getNano();
		int timestamp = h + m + s + n;
		database.dailyTablecheck();
		database.insertDailyBest(user, timestamp);
		dailyrank = database.selectDailyBest();
		InsertBestOfBest(user,timestamp);
		return dailyrank.get(new Integer(timestamp));
	}

	public User InsertBestOfBest(User users,int serialnumber) {
		if (!isInsertBestOfBest(users))
			return null;
		bestofbest = database.insertBestOfBest(users, serialnumber);
		index++;
		User temp = null;
		for(int i=0;i<index;i++) {
			if (bestofbest[i].getUserNumber().equals(new Integer(serialnumber))) {
				temp = bestofbest[i];
			}
		}
		return temp;
	}

	private boolean isInsertBestOfBest(User users) {
		if (index == 0 || users.getInfo().getScore() > bestofbest[index].getInfo().getScore()) {
			return true;
		} else {
			return false;
		}
	}
}
