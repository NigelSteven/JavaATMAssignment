package DataAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Models.AppDbContext;
import Models.BankAccount;
import Models.User;

public class BankAccountDA {
	private String tableName = "bank_accounts";
	private PreparedStatement stmt;

	// public BankAccountDA() {
	// AppDbContext.createConnection();
	// }

	public List<BankAccount> getAllAccounts() {
		String query = "select * from " + tableName;
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int accID = rs.getInt("accID");
				int userId = rs.getInt("userId");
				String accNumber = rs.getString("accNumber");
				String accName = rs.getString("accName");
				String accPin = rs.getString("accPin");
				Double balance = rs.getDouble("balance");
				String accType = rs.getString("accType");
				String bankType = rs.getString("bankType");
				Boolean isBan = rs.getBoolean("isBan");

				BankAccount account = 
						new BankAccount(
								accID, userId, accNumber,
								accName,accPin, balance, 
								accType,bankType, isBan
								);

				accounts.add(account);

			}
			AppDbContext.disconnect();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return accounts;
	}

	public void AddAccount(BankAccount account) {

		String insertAccount = "INSERT INTO " + tableName + " Values(?,?,?,?,?,?,?,?,?)";
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(insertAccount);
			stmt.setInt(1, account.getAccID());
			stmt.setInt(2, account.getUserId());
			stmt.setString(3, account.getAccNumber());
			stmt.setString(4, account.getAccName());
			stmt.setString(5, account.getAccPin());
			stmt.setDouble(6, account.getBalance());
			stmt.setString(7, account.getAccType());
			stmt.setString(8, account.getBankType());
			stmt.setBoolean(9, account.isBan());

			stmt.executeUpdate();
			AppDbContext.disconnect();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public BankAccount GetAccount(int id) {
		String query = "select * from " + tableName + " where accID=?";
		BankAccount account = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				account = 
						new BankAccount(
								id, rs.getInt("userId"), rs.getString("accNumber"), 
								rs.getString("accName"),rs.getString("accPin"),rs.getDouble("balance"), 
								rs.getString("accType"),rs.getString("bankType"),rs.getBoolean("isBan"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return account;
	}

	public boolean updateAccount(BankAccount account) {

		String query = "UPDATE " + tableName + " SET userId=?,accNumber=?,accName=?,accPin=?,balance=?,accType=?,bankType=?,isBan=? Where accID =?";
		boolean update = false;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, account.getUserId());
			stmt.setString(2, account.getAccNumber());
			stmt.setString(3, account.getAccName());
			stmt.setString(4, account.getAccPin());
			stmt.setDouble(5, account.getBalance());
			stmt.setString(6, account.getAccType());
			stmt.setString(7, account.getBankType());
			stmt.setBoolean(8, account.isBan());
			stmt.setInt(9, account.getAccID());

			update = stmt.executeUpdate() > 0; // update successful = 1, fail = 0
			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return update;
	}

	public void DeleteAccount(int id) {
		String query = "DELETE FROM " + tableName + " where accID=?";
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			AppDbContext.disconnect();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public List<BankAccount> findAccountByProperty(String targetColumn, String targetValue) {

		List<String> ColumnName = new ArrayList<String>(
				Arrays.asList("accID", "userId", "accNumber", 
						"AccName", "accType", "bankType", "isBan"));

		if (!ColumnName.contains(targetColumn)) {
			return null;
		}

		String query = "SELECT * FROM " + tableName + " WHERE " + targetColumn + " = ?";
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
	        stmt.setString(1, targetValue);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int accID = rs.getInt("accID");
				int userId = rs.getInt("userId");
				String accNumber = rs.getString("accNumber");
				String accName = rs.getString("accName");
				String accPin = rs.getString("accPin");
				Double balance = rs.getDouble("balance");
				String accType = rs.getString("accType");
				String bankType = rs.getString("bankType");
				Boolean isBan = rs.getBoolean("isBan");

				BankAccount account = 
						new BankAccount(
								accID, userId, accNumber,
								accName,accPin, balance, 
								accType,bankType, isBan
								);

				accounts.add(account);

			}
			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return accounts;
	}

	
	
	
	//for user used
	
	public List<BankAccount> GetAccountsByUserId(int user_Id) {
		String query = "select * from " + tableName + " where userId=?";
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		AppDbContext.createConnection();
		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, user_Id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int accID = rs.getInt("accID");
				int userId = rs.getInt("userId");
				String accNumber = rs.getString("accNumber");
				String accName = rs.getString("accName");
				String accPin = rs.getString("accPin");
				Double balance = rs.getDouble("balance");
				String accType = rs.getString("accType");
				String bankType = rs.getString("bankType");
				Boolean isBan = rs.getBoolean("isBan");

				BankAccount account = 
						new BankAccount(
								accID, userId, accNumber,
								accName,accPin, balance, 
								accType,bankType, isBan
								);

				accounts.add(account);

			}
			AppDbContext.disconnect();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return accounts;
	}
	
	public BankAccount getUserAccount(int userId, int accID) {
		
		String query = "select * from " + tableName + " where userId=? and accID=?";
		BankAccount account = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, userId);
			stmt.setInt(2, accID);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				account = 
						new BankAccount(
								accID, rs.getInt("userId"), rs.getString("accNumber"), 
								rs.getString("accName"),rs.getString("accPin"),rs.getDouble("balance"), 
								rs.getString("accType"),rs.getString("bankType"),rs.getBoolean("isBan"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return account;
		
	}

	public BankAccount getAccountByAccNumber(String accNumber, String bankType) {
		
		String query = "select * from " + tableName + " where accNumber=? and bankType=?";
		BankAccount account = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setString(1, accNumber);
			stmt.setString(2, bankType);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				account = 
						new BankAccount(
								rs.getInt("accID"), rs.getInt("userId"), rs.getString("accNumber"), 
								rs.getString("accName"),rs.getString("accPin"),rs.getDouble("balance"), 
								rs.getString("accType"),rs.getString("bankType"),rs.getBoolean("isBan"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return account;
		
	}
	
	public BankAccount getAccountByAccNo(String accNumber) {
		
		String query = "select * from " + tableName + " where accNumber=?";
		BankAccount account = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setString(1, accNumber);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				account = 
						new BankAccount(
								rs.getInt("accID"), rs.getInt("userId"), rs.getString("accNumber"), 
								rs.getString("accName"),rs.getString("accPin"),rs.getDouble("balance"), 
								rs.getString("accType"),rs.getString("bankType"),rs.getBoolean("isBan"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return account;
		
	}
	
	
	
	public String getAccountNumber(int id) {
		
		String query = "select * from " + tableName + " where accID=?";
		String accountNumber = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				accountNumber = rs.getString("accNumber");
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return accountNumber;
		
	}
	
	
	
	
	
	
	
	
}
