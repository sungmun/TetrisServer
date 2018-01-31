package JavaServer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

import JavaServer.SQLCreate.Insert.Value;
import JavaServer.SQLCreate.Where.Sort;
import Serversynchronization.User;

public class Ranking {

	SQLCreate SQL = null;
	MySQLConnect DataBaseConnect = null;
	ObjectFileConnect FileConnect = null;
	LinkedList<User> DailyRanking = null;
	LinkedList<LinkedHashMap<String, String>> BestOfBestRanking = null;

	final String BOBTableName = "BestOfBest";
	String DailyTableName = null;

	public Ranking() {
		SQL = new SQLCreate();
		DataBaseConnect = new MySQLConnect();
		FileConnect = new ObjectFileConnect();
		searchRanking();
	}

	public void insertRanking(final User user) {

		boolean isBOBRank = isBOBRankingRegistration(user.getInfo().getScore());
		if (isBOBRank) {
			BOBRankingRegistration(user);
		}
		DailyRankingLocalRegistration(user);
	}

	public boolean isBOBRankingRegistration(final int score) {
		String sql = null;
		String where = null;

		where = SQL.where.sort(new Sort("score", true)) + " " + SQL.where.limit(1);
		sql = SQL.select.getSelect("*", "BestOfBest") + " " + where;

		try {
			Iterator<LinkedHashMap<String, String>> iter = DataBaseConnect.selectSQL(sql).iterator();
			if (!iter.hasNext() || Integer.parseInt(iter.next().get("score")) > score) {
				throw new SQLException();
			}
			return true;
		} catch (SQLException e) {
			System.out.println(sql);
			return false;
		}
	}

	public void DailyRankingLocalRegistration(final User user) {
		DailyRanking.add(user);
		Collections.sort(DailyRanking);
	}

	public void DailyRankingRegistration() {
		// 랭킹을 정렬하여 순서대로 저장을 해준다
		Collections.sort(DailyRanking);

		DailyTableName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년MM월DD일"));

		FileConnect.write(DailyRanking, DailyTableName + ".obj");

	}

	public void BOBRankingFinalUserDelete() {
		String sql = null;
		String where = null;

		where = SQL.where.sort(new Sort("score", true)) + " " + SQL.where.limit(1);
		sql = SQL.delete.getDelete(BOBTableName) + " " + where;

		try {
			DataBaseConnect.deleteSQL(sql);
		} catch (SQLException e) {
			System.out.println(sql);
			return;
		}

	}

	public void BOBRankingRegistration(final User user) {
		String sql = null;
		try {
			sql = SQL.select.getSelect("Count(*)", BOBTableName);
			String temp = DataBaseConnect.selectSQL(sql).get(0).get("Count(*)");
			if (Integer.parseInt(temp) >= 100) {
				BOBRankingFinalUserDelete();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		RankingRegistration(BOBTableName, user);
	}

	public void RankingRegistration(final String tablename, final User user) {
		// 유저의 정보와 유저의 정보를 저장할 테이블의 이름을 받고 테이블에 유저의 정보를 저장한다
		String sql = null;

		ArrayList<Value> value = new ArrayList<>();
		value.add(new Value("serialnumber", "'" + user.getUuid().toString() + "'"));
		value.add(new Value("id", "'" + user.getID() + "'"));
		value.add(new Value("name", "'" + user.getName() + "'"));
		value.add(new Value("score", Integer.toString(user.getInfo().getScore())));
		value.add(new Value("level", Integer.toString(user.getInfo().getLevel())));

		sql = SQL.insert.getInsert(tablename, value.toArray(new Value[value.size()]));

		try {
			DataBaseConnect.insertSQL(sql);
		} catch (SQLException e) {
			System.out.println(sql);
			return;
		}
	}


	public void searchRanking() {
		searchDailyRanking();
		searchBOBRanking();
	}

	public void searchDailyRanking() {
		DailyTableName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년MM월DD일"));

		Object obj = FileConnect.read(DailyTableName + ".obj");
		if(DailyRanking==null) {
			DailyRanking = ((obj instanceof LinkedList<?>) ? (LinkedList<User>) obj : new LinkedList<User>());
		}else {
			DailyRanking= ((obj instanceof LinkedList<?>) ? DailyRanking : new LinkedList<User>());
		}

	}

	public void searchBOBRanking() {
		String sql = null;

		sql = SQL.select.getSelect(BOBTableName);
		try {
			BestOfBestRanking = DataBaseConnect.selectSQL(sql);
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
	}

	public int searchBOBRanking(Object serialnumber) {
		if (!(serialnumber instanceof UUID)) {
			return -1;
		}
		Iterator<LinkedHashMap<String, String>> iter = BestOfBestRanking.iterator();
		for (int i = 1; iter.hasNext(); i++) {
			if (iter.next().get("serialnumber").equals(serialnumber.toString())) {
				return i;
			}
		}
		return -1;
	}

	public int searchDailyRanking(Object serialnumber) {
		if (!(serialnumber instanceof UUID)) {
			return -1;
		}

		Collections.sort(DailyRanking);

		Iterator<User> iter = DailyRanking.iterator();
		for (int i = 1; iter.hasNext(); i++) {
			if (iter.next().getUuid() == serialnumber) {
				return i;
			}
		}
		return -1;
	}

}
