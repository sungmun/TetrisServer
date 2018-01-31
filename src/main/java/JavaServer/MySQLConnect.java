package JavaServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MySQLConnect {
	Connection connection = null;
	Statement stat = null;

	public MySQLConnect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://mydirectory.iptime.org:3333/TetrisDatabase", "tjdans", "dkrak");
			stat = connection.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean createSQL(String sql) throws SQLException {
		return executeSQL(sql);
	}

	public boolean insertSQL(String sql) throws SQLException {
		return executeSQL(sql);
	}

	public boolean updataSQL(String sql) throws SQLException {
		return executeSQL(sql);
	}

	public boolean deleteSQL(String sql) throws SQLException {
		return executeSQL(sql);
	}

	public LinkedList<LinkedHashMap<String, String>> selectSQL(String sql) throws SQLException {
		return executeQuery(sql);
	}

	private boolean executeSQL(String sql) throws SQLException {
		boolean rs = stat.execute(sql);
		return rs;
	}

	private LinkedList<LinkedHashMap<String, String>> executeQuery(String sql) throws SQLException {
		LinkedList<LinkedHashMap<String, String>> row = new LinkedList<>();

		ResultSet rs = stat.executeQuery(sql);
		ResultSetMetaData metaData = rs.getMetaData();
		int count = metaData.getColumnCount();

		while (rs.next()) {
			LinkedHashMap<String, String> value = new LinkedHashMap<>();
			for (int i = 0; i < count; i++) {
				value.put(metaData.getColumnName(i + 1), rs.getString(i + 1));
			}
			row.push(value);
		}

		return row;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		connection.close();
		stat.close();
	}
}
