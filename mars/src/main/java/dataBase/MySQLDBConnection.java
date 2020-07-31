package dataBase;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

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

	public void addFavorite(String userId, Item item) {
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		// add item to item table and keywords table first
		addItem(item);
		try {
			// insert user-item into history table
//			INSERT INTO table_name (column1, column2, column3, ...)
//			VALUES (value1, value2, value3, ...);
			String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, item.getItemId());
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Set<String> getFavoriteItemId(String userId) {
		Set<String> itemIds = new HashSet<>();
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return itemIds;
		}
		try {
			// SELECT column_name(s) FROM table_name WHERE column_name operator value
			String sql = "SELECT * FROM history WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				itemIds.add(rs.getString("item_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemIds;
	}

	public Set<Item> getFavoriteItem(String userId) {
		Set<Item> items = new HashSet<>();
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return items;
		}
		Set<String> itemIds = getFavoriteItemId(userId);
		try {
			// SELECT column_name(s) FROM table_name WHERE column_name operator value
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				statement.setString(1, itemId);
				ResultSet rs = statement.executeQuery();
				while (rs.next()) {
					items.add(Item.builder().setName(rs.getString("name")).setAddress(rs.getString("address"))
							.setImageUrl(rs.getString("image_url")).setUrl(rs.getString("url")).setItemId(itemId)
							.setKeywords(getKeywords(itemId)).build());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	private Set<String> getKeywords(String itemId) {
		Set<String> keywords = new HashSet<>();
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return keywords;
		}
		try {
			// SELECT column_name(s) FROM table_name WHERE column_name operator value
			String sql = "SELECT * FROM keywords WHERE item_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while (!rs.next()) {
				keywords.add(rs.getString("keyword"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keywords;
	}

	public void removeFavorite(String user_id, Item item) {
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		// add item to item table and keywords table first
		addItem(item);
		try {
			// insert user-item into history table
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
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		try {
			// insert an item
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
			// insert item-keywords
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

	public String getUserName(String userId) {
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return "";
		}
		try {
			// SELECT column_name(s) FROM table_name WHERE column_name operator value
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getString("last_name") + " " + rs.getString("first_name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// verify if userId and password is correct
	public boolean verifyUser(String userId, String password) {
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
			// SELECT column_name(s) FROM table_name WHERE column_name operator value
			String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// add new user to data base
	public boolean addUser(String userId, String password, String firstName, String lastName) {
		// check if connected to data base
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
//			INSERT INTO table_name (column1, column2, column3, ...)
//			VALUES (value1, value2, value3, ...);
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			statement.setString(3, firstName);
			statement.setString(4, lastName);
			return statement.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// close connection
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
