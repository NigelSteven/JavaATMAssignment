package Models;

public class User {
	
	private int userID;
	private String userName;
	private String fullName;
	private String NRIC;
    private String email;
    private String password;
    private int loginLimit;
    private String userPLP;
    private boolean isAdmin;
    private boolean isBan;
    
    
    
    
	public User(int userID, String userName, String fullName, String NRIC, String email, String password,
			int loginLimit, String userPLP, boolean isAdmin, boolean isBan) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.fullName = fullName;
		this.NRIC = NRIC;
		this.email = email;
		this.password = password;
		this.loginLimit = loginLimit;
		this.userPLP = userPLP;
		this.isAdmin = isAdmin;
		this.isBan = isBan;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getNRIC() {
		return NRIC;
	}
	public void setNRIC(String nRIC) {
		NRIC = nRIC;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getLoginLimit() {
		return loginLimit;
	}
	public void setLoginLimit(int loginLimit) {
		this.loginLimit = loginLimit;
	}
	public String getUserPLP() {
		return userPLP;
	}
	public void setUserPLP(String userPLP) {
		this.userPLP = userPLP;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public boolean isBan() {
		return isBan;
	}
	public void setBan(boolean isBan) {
		this.isBan = isBan;
	}
	

    


	
}
