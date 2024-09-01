package DataAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Models.AppDbContext;
import Models.User;
import Security.PasswordAuthentication;
import Security.PasswordHasher;

public class UserDA {
	private String tableName = "users";
	private PreparedStatement stmt;

	//CRUD Start
	public List<User> getAllUsers(){
		String query = "select * from "+ tableName;
		List<User> users = new ArrayList<User>();
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				
				int id = rs.getInt("userID");
				String userName = rs.getString("userName");
				String fullName = rs.getString("fullName");
				String NRIC = rs.getString("NRIC");
				String email = rs.getString("email");
				String password = rs.getString("password");
				int loginLimit = rs.getInt("loginLimit");
				String userPLP = rs.getString("userPLP");
				Boolean isAdmin = rs.getBoolean("isAdmin");
				Boolean isBan = rs.getBoolean("isBan");

				User user  =new User(id,userName,fullName,NRIC,email,password,loginLimit,userPLP,isAdmin,isBan);
				users.add(user);
				
			}
			AppDbContext.disconnect();

		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
				
		return users;
	}

	public void AddUser(User user) {
		
		String insertUser = "INSERT INTO " + tableName + " Values(?,?,?,?,?,?,?,?,?,?)";
		AppDbContext.createConnection();
		
		PasswordAuthentication passwordAuth = new PasswordAuthentication();
        String hashedPassword = passwordAuth.hash(user.getPassword().toCharArray());
		
		try {
			stmt = AppDbContext.conn.prepareStatement(insertUser);
			stmt.setInt(1, user.getUserID());
			stmt.setString(2, user.getUserName());
			stmt.setString(3, user.getFullName());
			stmt.setString(4, user.getNRIC());
			stmt.setString(5, user.getEmail());
			stmt.setString(6, hashedPassword);
			stmt.setInt(7, user.getLoginLimit());//change to 3
			stmt.setString(8, user.getUserPLP());
			stmt.setBoolean(9, user.isAdmin());
			stmt.setBoolean(10, user.isBan());

			stmt.executeUpdate();
			AppDbContext.disconnect();

			
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public User GetUser(int id) {
		String query = "select * from "+ tableName + " where userID=?";
		User user = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				
				user = new User(id,
						rs.getString("userName"),
						rs.getString("fullName"),
						rs.getString("NRIC"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getInt("loginLimit"),
						rs.getString("userPLP"),
						rs.getBoolean("isAdmin"),
						rs.getBoolean("isBan"));
			}
			
			AppDbContext.disconnect();
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return user;
	}
	
	public boolean updateUser(User user){
		
		String query = "UPDATE " + tableName + " SET userName=?,fullName=?,NRIC=?,email=?,password=?,loginLimit=?,userPLP=?,isAdmin=?,isBan=? Where userID =?";
		boolean update = false;
		AppDbContext.createConnection();
		PasswordAuthentication passwordAuth = new PasswordAuthentication();
        String hashedPassword = passwordAuth.hash(user.getPassword().toCharArray());
		//String storedPassword = PasswordHasher.hashPassword(user.getPassword());

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setString(1, user.getUserName());
			stmt.setString(2, user.getFullName());
			stmt.setString(3, user.getNRIC());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, hashedPassword);
			stmt.setInt(6, user.getLoginLimit());
			stmt.setString(7, user.getUserPLP());
			stmt.setBoolean(8, user.isAdmin());
			stmt.setBoolean(9, user.isBan());
			stmt.setInt(10, user.getUserID());

			update = stmt.executeUpdate()>0; // update successful = 1, fail = 0
			AppDbContext.disconnect();
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		
		return update;
	}
	
	public void DeleteUser(int id) {
		String query = "DELETE FROM "+ tableName + " where userID=?";
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			AppDbContext.disconnect();

		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}
	//CRUD end
	
	
	public List<User> findUserByProperty(String targetColumn, String targetValue) {
		
        List<String> ColumnName = new ArrayList<String>(
        		Arrays.asList(
        				"userID", "userName","fullName",
        				"NRIC","email","isAdmin","isBan"
        				)
        		);
        
		if(!ColumnName.contains(targetColumn)) {
			return null;
		}
		
		
	    String query = "SELECT * FROM " + tableName + " WHERE " + targetColumn + " = ?";
	    List<User> users = new ArrayList<>();
	    AppDbContext.createConnection();
	    
	    try {
	        stmt = AppDbContext.conn.prepareStatement(query);
	        stmt.setString(1, targetValue);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            int id = rs.getInt("userID");
	            String userName = rs.getString("userName");
	            String fullName = rs.getString("fullName");
	            String NRIC = rs.getString("NRIC");
	            String email = rs.getString("email");
	            String password = rs.getString("password");
	            int loginLimit = rs.getInt("loginLimit");
	            String userPLP = rs.getString("userPLP");
	            boolean isAdmin = rs.getBoolean("isAdmin");
	            boolean isBan = rs.getBoolean("isBan");

	            User user = new User(id, userName, fullName, NRIC, email, password, loginLimit, userPLP, isAdmin, isBan);
	            users.add(user);
	        }
	        AppDbContext.disconnect();
	    } catch (SQLException ex) {
	        System.out.println(ex.getMessage());
	    }

	    return users;
	}
	
	
	//for user used
	
	public String GetUserName(int id) {
		String query = "select * from "+ tableName + " where userID=?";
		String username = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				username = rs.getString("userName");
			}
			AppDbContext.disconnect();
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return username;
	}
	
	
	
	
}
