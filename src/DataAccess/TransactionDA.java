package DataAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Models.AppDbContext;
import Models.Transaction;
import Models.User;

import java.time.LocalDateTime;

public class TransactionDA {
	private String tableName = "transactions";
	private PreparedStatement stmt;

	// public BankAccountDA() {
	// AppDbContext.createConnection();
	// }

	public List<Transaction> getAllTransactions() {
		String query = "select * from " + tableName;
		List<Transaction> transactions = new ArrayList<Transaction>();
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int tranID = rs.getInt("tranID");
				int formId = rs.getInt("fromId");
				int receiveId = rs.getInt("receiveId");

				Timestamp timestamp = rs.getTimestamp("tranTime");
				LocalDateTime tranTime = timestamp.toLocalDateTime();

				String tranType = rs.getString("tranType");
				Double tranAmount = rs.getDouble("tranAmount");
				boolean transferred = rs.getBoolean("transferred");

				Transaction tran = new Transaction(tranID, formId, receiveId, tranTime, tranType, tranAmount,
						transferred);
				transactions.add(tran);
				
			}
			AppDbContext.disconnect();
			
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return transactions;
	}

	public void AddTransaction(Transaction tran) {

		String insertTransaction = "INSERT INTO " + tableName + " Values(?,?,?,?,?,?,?)";
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(insertTransaction);
			stmt.setInt(1, tran.getTranID());
			stmt.setInt(2, tran.getFromId());
			stmt.setInt(3, tran.getReceiveId());

			LocalDateTime tranTime = tran.getTranTime();
			Timestamp timestamp = Timestamp.valueOf(tranTime);
			stmt.setTimestamp(4, timestamp);

			stmt.setString(5, tran.getTranType());
			stmt.setDouble(6, tran.getTranAmount());
			stmt.setBoolean(7, tran.isTransferred());

			stmt.executeUpdate();
			AppDbContext.disconnect();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public Transaction GetTransaction(int id) {
		String query = "select * from " + tableName + " where tranID=?";
		Transaction tran = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				Timestamp timestamp = rs.getTimestamp("tranTime");
				LocalDateTime tranTime = timestamp.toLocalDateTime();

				tran = new Transaction(id, rs.getInt("formId"), rs.getInt("receiveId"), tranTime,
						rs.getString("tranType"), rs.getDouble("tranAmount"), rs.getBoolean("transferred"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return tran;
	}

	public boolean updateAccount(Transaction tran) {

		String query = "UPDATE " + tableName
				+ " SET fromId=?,receiveId=?,tranTime=?,tranType=?,tranAmount=?,isTransferred=? Where tranID =?";
		boolean update = false;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(7, tran.getTranID());
			stmt.setInt(1, tran.getFromId());
			stmt.setInt(2, tran.getReceiveId());

			LocalDateTime tranTime = tran.getTranTime();
			Timestamp timestamp = Timestamp.valueOf(tranTime);
			stmt.setTimestamp(3, timestamp);

			stmt.setString(4, tran.getTranType());
			stmt.setDouble(5, tran.getTranAmount());
			stmt.setBoolean(6, tran.isTransferred());

			update = stmt.executeUpdate() > 0; // update successful = 1, fail = 0
			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return update;
	}

	public void DeleteTransaction(int id) {
		String query = "DELETE FROM " + tableName + " where tranID=?";
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

	public List<Transaction> findTransactionByProperty(String targetColumn, String targetValue) {

		List<String> ColumnName = new ArrayList<String>(
				Arrays.asList("tranID", "fromId", "receiveId", "tranTime", "tranType", "tranAmount", "transferred"));

		if (!ColumnName.contains(targetColumn)) {
			return null;
		}

		String query = "SELECT * FROM " + tableName + " WHERE " + targetColumn + " = ?";
		List<Transaction> transactions = new ArrayList<Transaction>();
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setString(1, targetValue);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int tranID = rs.getInt("tranID");
				int formId = rs.getInt("formId");
				int receiveId = rs.getInt("receiveId");

				Timestamp timestamp = rs.getTimestamp("tranTime");
				LocalDateTime tranTime = timestamp.toLocalDateTime();

				String tranType = rs.getString("tranType");
				Double tranAmount = rs.getDouble("tranAmount");
				boolean transferred = rs.getBoolean("transferred");

				Transaction tran = new Transaction(tranID, formId, receiveId, tranTime, tranType, tranAmount,
						transferred);
				transactions.add(tran);

			}
			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return transactions;
	}

	
	//user method
	public List<Transaction> getAllUserTransactions(int accId) {
		String query = "SELECT * FROM " + tableName + " WHERE fromId=? or receiveId=?";
		List<Transaction> transactions = new ArrayList<Transaction>();
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, accId);
			stmt.setInt(2, accId);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int tranID = rs.getInt("tranID");
				int formId = rs.getInt("fromId");
				int receiveId = rs.getInt("receiveId");

				Timestamp timestamp = rs.getTimestamp("tranTime");
				LocalDateTime tranTime = timestamp.toLocalDateTime();

				String tranType = rs.getString("tranType");
				Double tranAmount = rs.getDouble("tranAmount");
				boolean transferred = rs.getBoolean("transferred");

				Transaction tran = new Transaction(tranID, formId, receiveId, tranTime, tranType, tranAmount,
						transferred);
				transactions.add(tran);

			}
			AppDbContext.disconnect();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return transactions;
	}
	
}
