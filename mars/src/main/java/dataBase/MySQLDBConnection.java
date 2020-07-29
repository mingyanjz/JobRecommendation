package dataBase;

import java.sql.DriverManager;
import java.sql.PreparedStatement;


import entity.Item;
import gitIgnore.PersonalData;

import java.sql.Connection;
public class MySQLDBConnection {
	private Connection conn;
	public MySQLDBConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(PersonalData.MYSQL_URL);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addFavorite(String user_id, Item item) {
		//check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		//add item to item table and keywords table first
		addItem(item);
		try {
			//insert user-item into history table
//			INSERT INTO table_name (column1, column2, column3, ...)
//			VALUES (value1, value2, value3, ...);
			String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, item.getItemId());
			statement.executeUpdate();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void removeFavorite(String user_id, Item item) {
		//check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		//add item to item table and keywords table first
		addItem(item);
		try {
			//insert user-item into history table
//			DELETE FROM table_name WHERE condition;
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, item.getItemId());
			statement.executeUpdate();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void addItem(Item item) {
		//check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		try {
			//insert an item
//			INSERT INTO table_name (column1, column2, column3, ...)
//			VALUES (value1, value2, value3, ...);
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getName());
			statement.setString(3, item.getAddress());
			statement.setString(4, item.getImageUrl());
			statement.setString(5, item.getUrl());
			statement.executeUpdate();
			//insert  item-keywords
			sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
			statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			for (String keyword : item.getKeywords()) {
				statement.setNString(2, keyword);
				statement.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
