package JavaServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Serversynchronization.User;

public class TetrisRankingDataBase {
	Connection connection = null;
	Statement st = null;
	ResultSet rs = null;

	public TetrisRankingDataBase() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql:localhost:3333/tetrisranking", "tjdans", "dkrak");
			st = connection.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public int insertRankingSQL(User user) throws SQLException {
		String id=user.getID();
		String name=user.getName();
		int score=user.getInfo().getScore();
		int level=user.getInfo().getLevel();
		String sql = "INSERT INTO ranking (id, name, score, level) VALUE ("
				+"'"+ id +"', " 
				+"'"+ name +"', "
				+ score +", "
				+ level
				+ ")";
		rs = st.executeQuery(sql);
		sql="SELECT serialnum FROM ranking desc limit 1;";
		rs=st.executeQuery(sql);
		rs.next();
		int serialnum =rs.getInt("serialnum");
		sql="SELECT serialnum,; id, name, score, level,( @rank := @rank + 1 ) AS rank\r\n" + 
				"FROM ranking AS a, ( SELECT @rank := 0 ) AS b\r\n" + 
				"ORDER BY a.score DESC;";
		rs=st.executeQuery(sql);
		while(rs.next()) {
			if(serialnum==rs.getInt("serialnum")) {
				return rs.getInt("rank");
			}
		}
		return -1;
	}

	public String[] selectChoiceSQL(String colom) throws SQLException {
		String[] users = new String[101];
		int index = 0;
		String sql = "SELECT " + colom + " FROM ranking;";
		rs = st.executeQuery(sql);
		while (rs.next()) {
			users[index++] = rs.getString(colom);
		}
		return users;
	}

	public User[] selectRankingSQL() throws SQLException {
		ArrayList<User> ranklist=new ArrayList<>();
		String sql = "SELECT id, name, score, level FROM ranking;";
		rs = st.executeQuery(sql);
		while (rs.next()) {
			String id = rs.getString("id");
			String name = rs.getString("name");
			int score = rs.getInt("score");
			int level = rs.getInt("level");
			ranklist.add(new User(id, name, level, score));
		}
		User[] users=new User[ranklist.size()];
		int index=0;
		for (User user : ranklist) {
			users[index++]=user;
		}
		return users;
	}
	public User[] selectAllSQL() throws SQLException {
		User[] users = new User[101];
		int index = 0;
		String sql = "SELECT id, name, score, level FROM ranking;";
		rs = st.executeQuery(sql);
		while (rs.next()) {
			String id = rs.getString("id");
			String name = rs.getString("name");
			int score = rs.getInt("score");
			int level = rs.getInt("level");
			users[index++] = new User(id, name, level, score);
		}
		return users;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		rs.close();
		st.close();
		connection.close();
	}
}
