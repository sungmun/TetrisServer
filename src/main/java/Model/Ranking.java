package Model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

import FileInputOutput.MySQLConnect;
import FileInputOutput.ObjectFileConnect;
import Model.SQLCreate.Insert.Value;
import Model.SQLCreate.Where.Sort;
import Serversynchronization.User;

public class Ranking {
	public class DailyRank {
		public LinkedList<User> Ranking = null;

		public DailyRank() {
			searchRanking();
		}

		public void RankingLocalRegistration(final User user) {
			Ranking.add(user);
			Collections.sort(Ranking);
		}

		public void searchRanking() {
			TableName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년MM월DD일"));

			Object obj = FileConnect.read(TableName + ".obj");
			if (obj instanceof LinkedList<?>) {
				Ranking = (Ranking == null) ? (LinkedList<User>) obj : Ranking;
			} else {
				Ranking = new LinkedList<User>();
			}
		}

		public int searchRanking(Object serialnumber) {
			if (!(serialnumber instanceof UUID)) {
				return -1;
			}

			Collections.sort(Ranking);

			Iterator<User> iter = Ranking.iterator();
			for (int i = 1; iter.hasNext(); i++) {
				if (iter.next().getUuid() == serialnumber) {
					return i;
				}
			}
			return -1;
		}

		public void RankingRegistration() {
			// 랭킹을 정렬하여 순서대로 저장을 해준다
			Collections.sort(Ranking);

			TableName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년MM월DD일"));

			FileConnect.write(Ranking, TableName + ".obj");

		}
	}

	public class BestOfBestRank {
		public LinkedList<LinkedHashMap<String, String>> rank = new LinkedList<>();
		final String TableName = "BestOfBest";

		public BestOfBestRank() {
			searchRanking();
		}

		public void searchRanking() {
			String sql = null;

			sql = SQL.select.getSelect(TableName);
			try {
				rank = DataBaseConnect.selectSQL(sql);
			} catch (SQLException e) {
				System.out.println(sql);
				e.printStackTrace();
			}
		}

		public int searchRanking(Object serialnumber) {
			if (!(serialnumber instanceof UUID)) {
				return -1;
			}
			searchRanking();
			Iterator<LinkedHashMap<String, String>> iter = rank.iterator();
			for (int i = 1; iter.hasNext(); i++) {
				if (iter.next().get("serialnumber").equals(serialnumber.toString())) {
					return i;
				}
			}
			return -1;
		}

		public boolean isRankingRegistration(int score) {
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

		public void RankingFinalUserDelete() {
			String sql = null;
			String where = null;

			where = SQL.where.sort(new Sort("score", true)) + " " + SQL.where.limit(1);
			sql = SQL.delete.getDelete(TableName) + " " + where;

			try {
				DataBaseConnect.deleteSQL(sql);
			} catch (SQLException e) {
				System.out.println(sql);
				return;
			}

		}

		public void RankingRegistration(final User user) {
			String sql = null;
			try {
				sql = SQL.select.getSelect("Count(*)", TableName);
				LinkedList<LinkedHashMap<String, String>> templist=DataBaseConnect.selectSQL(sql);
				
				String temp =templist.getFirst().get("Count(*)");
				
				if (Integer.parseInt(temp) >= 100) {
					RankingFinalUserDelete();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			Ranking.this.RankingRegistration(TableName, user);
		}
	}

	SQLCreate SQL = null;
	MySQLConnect DataBaseConnect = null;
	ObjectFileConnect FileConnect = null;

	public BestOfBestRank bestOfBestRank = null;
	public DailyRank dailyRank = null;

	String TableName = null;

	public Ranking() {
		SQL = new SQLCreate();
		DataBaseConnect = new MySQLConnect();
		FileConnect = new ObjectFileConnect();
		bestOfBestRank = new BestOfBestRank();
		dailyRank = new DailyRank();
	}

	public void insertRanking(User user) {

		boolean isBOBRank = bestOfBestRank.isRankingRegistration(user.getInfo().getScore());
		if (isBOBRank) {
			bestOfBestRank.RankingRegistration(user);
		}
		dailyRank.RankingLocalRegistration(user);
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
		dailyRank.searchRanking();
		bestOfBestRank.searchRanking();
	}

}
