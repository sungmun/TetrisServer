package JavaServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import Serversynchronization.User;

public class TetrisDataBase {
	Connection connection = null;
	Statement st = null;
	ResultSet rs = null;

	public TetrisDataBase() {
		openDataBase();
	}

	public void insertDailyBest(User user, long timestamp) {
		String id = user.getID();
		String name = user.getName();
		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+"day";

		int score = user.getInfo().getScore();
		int level = user.getInfo().getLevel();
		try {
			String sql = "INSERT INTO " + today + " (serialnumber, id, name, score, level) VALUE (" + timestamp + ",'"
					+ id + "', " + "'" + name + "', " + score + ", " + level + ")";
			System.out.println(sql);
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.insertDailyBest()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
	}

	public HashMap<Integer, User> selectDailyBest() {
		HashMap<Integer, User> users = new HashMap<>();
		try {
			openQuery();
			String sql = "SELECT serialnumber, id, name, score, level FROM "
					+ LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "day ORDER BY level DESC, score DESC;";
			rs = st.executeQuery(sql);
			int rank = 0;
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				int score = rs.getInt("score");
				int level = rs.getInt("level");
				User temp = new User(id, name, level, score);
				temp.setUserNumber(new Integer(rank++));
				users.put(new Integer(rs.getInt("serialnumber")), temp);
			}
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.selectDailyBest()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
		return users;
	}

	public void dailyTablecheck() {

		String droptables = null;
		int iscreatetables = 0;
		try {
			openQuery();
			String sql = "SHOW TABLES;";

			rs = st.executeQuery(sql);
			while (rs.next()) {
				String day = rs.getNString(1);
				if (day.equals("BestOfBest")) continue;
				if (!day.equals(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+"day")) {
					droptables = day;
				} else {
					iscreatetables = -1;
				}
			}
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.dailyTablecheck()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
		dailyTableDrop(droptables);
		dailyTableCreate(iscreatetables);
	}

	public void dailyTableCreate(int create) {
		if (create < 0)
			return;
		try {
			openQuery();
			String sql = "CREATE TABLE " + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "day ("
					+ " serialnumber int(30) unsigned NOT NULL," + " id varchar(20) NOT NULL," + " name varchar(10) NOT NULL,"
					+ " score int(11) NOT NULL," + " level int(11) NOT NULL," + " PRIMARY KEY (`serialnumber`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.dailyTableCreate()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
	}

	public void dailyTableDrop(String drop) {
		if (drop == null)
			return;
		try {
			openQuery();
			String sql = "DROP TABLE " + drop + ";";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.dailyTableDrop()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
	}

	public void delectBestOfBest(long serialnumber) {
		try {
			openQuery();
			String sql = "DELETE FROM BestOfBest WHERE serialnumber=" + serialnumber + ";";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.delectBestOfBest()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
	}

	public synchronized User[] insertBestOfBest(User user, int timestamp) {
		String id = user.getID();
		String name = user.getName();
		int score = user.getInfo().getScore();
		int level = user.getInfo().getLevel();
		try {
			openQuery();
			String sql = "INSERT INTO BestOfBest (serialnumber, id, name, score, level) VALUE (" + timestamp + ", '"
					+ id + "', " + "'" + name + "', " + score + ", " + level + ")";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getSQLState());
			System.out.println(e.getErrorCode());
			System.out.println("TetrisDataBase.insertBestOfBest()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
		return BestOfBestSort();
	}

	public User[] BestOfBestSort() {
		User[] users = new User[101];
		String sql;
		int index = 0, rank = 0;
		try {
			openQuery();
			sql = "SELECT serialnumber, id, name, score, level FROM BestOfBest ORDER BY level DESC, score DESC;";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				int score = rs.getInt("score");
				int level = rs.getInt("level");
				users[rank] = new User(id, name, level, score);
				users[rank].setUserNumber(new Integer(rank++));
				index = rs.getInt("serialnumber");
			}

		} catch (SQLException e) {
			System.out.println("TetrisDataBase.BestOfBestSort()");
			System.err.println("Query 오류");
		} finally {
			closeQuery();
		}
		if (rank >= 100) {
			delectBestOfBest(index);
			users[index] = null;
		}
		return users;
	}

	private void openDataBase() {
		try {
			if (connection == null) {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://mydirectory.iptime.org:3333/TetrisDatabase",
						"tjdans", "dkrak");
			}
			if (st == null) {
				st = connection.createStatement();
			}
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.openDataBase()");
			System.err.println("Close 오류");
		} catch (ClassNotFoundException e) {
			System.out.println("TetrisDataBase.openDataBase()");
			System.err.println("Class 오류");
		}
	}

	private void closeDataBase() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.err.println("Close 오류");
		}
	}

	private void openQuery() {
		try {
			if (st == null) {
				st = connection.createStatement();
			}
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.openQuery()");
			System.err.println("Close 오류");
		}
	}

	private void closeQuery() {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("TetrisDataBase.closeQuery()");
			System.err.println("Close 오류");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeDataBase();
	}
}
