package Models;

import java.util.Random;

public class BankAccount {
	
    private int accID;
	private int userId;
    private String accNumber;
    private String accName;
    private String accPin;
    private double balance;
    private String accType;
    private String bankType;
    private boolean isBan;
    
    //for create new
	public BankAccount(int userId, String accName, String accPin, 
			double balance, String accType, String bankType) {
		super();
		this.userId = userId;
		this.accNumber = generateAccountNumber();
		this.accName = accName;
		this.accPin = accPin;
		this.balance = balance;
		this.accType = accType;
		this.bankType = bankType;
		this.isBan = false;
	}

    
	public BankAccount(int accID, int userId, String accNumber, 
			String accName, String accPin, double balance,
			String accType, String bankType, boolean isBan) {
		super();
		this.accID = accID;
		this.userId = userId;
		this.accNumber = accNumber;
		this.accName = accName;
		this.accPin = accPin;
		this.balance = balance;
		this.accType = accType;
		this.bankType = bankType;
		this.isBan = isBan;
	}
	
	private String generateAccountNumber() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        // 生成16位随机数字
        for (int i = 0; i < 16; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
	
	public int getAccID() {
		return accID;
	}

	public void setAccID(int accID) {
		this.accID = accID;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getAccPin() {
		return accPin;
	}

	public void setAccPin(String accPin) {
		this.accPin = accPin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public boolean isBan() {
		return isBan;
	}

	public void setBan(boolean isBan) {
		this.isBan = isBan;
	}

	
	

}
