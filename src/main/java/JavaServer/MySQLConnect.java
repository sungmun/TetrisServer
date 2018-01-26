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
	Connection connection=null;
	Statement stat=null;
	public MySQLConnect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection=DriverManager.getConnection("jdbc:mysql:mydirectory.iptime.org:3333","tjdans","dkrak");
			stat=connection.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean insertSQL(String sql) {
		try {
			boolean rs= stat.execute(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updataSQL(String sql) {
		try {
			boolean rs= stat.execute(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean deleteSQL(String sql) {
		try {
			boolean rs= stat.execute(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public LinkedList<LinkedHashMap> selectSQL(String sql) {
		try {
			LinkedList<LinkedHashMap> row=new LinkedList<>();

			ResultSet rs= stat.executeQuery(sql);
			ResultSetMetaData metaData=rs.getMetaData();
			int count=metaData.getColumnCount();
			
			while(rs.next()) {
				LinkedHashMap<String, String> value=new LinkedHashMap<>();
				for(int i=0;i<count;i++) {
					value.put(metaData.getColumnName(i+1), rs.getString(i+1));
				}
				row.push(value);
			}
			
			return row;
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		connection.close();
		stat.close();
	}
}
