package dataBase;

import java.sql.DriverManager;
import java.sql.Statement;

import gitIgnore.PersonalData;

import java.sql.Connection;

public class MySQLDBInitial {
	public static void main(String[] args) {
		try {
			//connect to data base
			System.out.println("Connecting to " + PersonalData.MYSQL_DB_NAME + " Data Base");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(PersonalData.MYSQL_URL);
			if (conn == null) {
				return;
			}
			
			//drop tables if they exist;
			//DROP TABLE IF EXISTS table_name
			Statement statement = conn.createStatement();
			
			String sql = "DROP TABLE IF EXISTS keywords";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);
			//create tables
//			CREATE TABLE table_name (
//			column1 datatype,
//			column2 datatype,
//			column3 datatype,
//		   ....			
//		);
			//items table
			sql = "CREATE TABLE items (" + 
					"item_id VARCHAR(255) NOT NULL," + 
					"name VARCHAR(255)," + 
					"address VARCHAR(255)," +
					"image_url VARCHAR(255)," +
					"url VARCHAR(255)," +
					"PRIMARY KEY (item_id)" +
					")";
			statement.executeUpdate(sql);
			
			//users table
			sql = "CREATE TABLE users (" + 
					"user_id VARCHAR(255) NOT NULL," + 
					"password VARCHAR(255) NOT NULL," + 
					"first_name VARCHAR(255)," +
					"last_name VARCHAR(255)," +
					"PRIMARY KEY (user_id)" +
					")";
			statement.executeUpdate(sql);
			
			//keywords table (items, keywords relation)
			sql = "CREATE TABLE keywords (" + 
					"item_id VARCHAR(255) NOT NULL," + 
					"keyword VARCHAR(255) NOT NULL," + 
					"PRIMARY KEY (item_id, keyword)," +
					"FOREIGN KEY (item_id) REFERENCES items(item_id)" +
					")";
			statement.executeUpdate(sql);
			
			//history table (history of users' favorite items)
			sql = "CREATE TABLE history (" + 
					"user_id VARCHAR(255) NOT NULL," + 
					"item_id VARCHAR(255) NOT NULL," + 
					"last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + 
					"PRIMARY KEY (item_id, user_id)," +
					"FOREIGN KEY (item_id) REFERENCES items(item_id)," +
					"FOREIGN KEY (user_id) REFERENCES users(user_id)" +
					")";
			statement.executeUpdate(sql);
			
			//insert a test user
//			INSERT INTO table_name (column1, column2, column3, ...)
//			VALUES (value1, value2, value3, ...);
			sql = "INSERT INTO users (user_id, password, first_name, last_name)" +
					"VALUES ('9999', '1234', 'test_first_name', 'test_last_name')";
			statement.executeUpdate(sql);
			conn.close();
			System.out.println("Initialization successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
