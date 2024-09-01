import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import DataAccess.BankAccountDA;
import DataAccess.LoginDA;
import DataAccess.TransactionDA;
import DataAccess.UserDA;
import Models.BankAccount;
import Models.Transaction;
import Models.User;
import Security.PasswordAuthentication;
import Security.PasswordHasher;
import Security.SendEmail;
import CheckInput.CheckInput;

public class Main {
	private static Scanner scanner = CheckInput.scanner;
	private static boolean loggedIn = false;
	private static User loginUser;

	// Program here
	public static void main(String[] args) {

		// for testing ,must commend
		// loginUser = new User(1, "Admin1", "Admin1", "0987654321", "", "admin!123", 3,
		// null, true, false);
		
		TransactionDA tranDA = new TransactionDA();
		List<Transaction>AllTrans = tranDA.getAllTransactions();
		for (var tran : AllTrans) {
			
			if(!tran.isTransferred()) {
				
				if(tran.getTranType().equals("IBGT")) {
					
					BankAccountDA accDA = new BankAccountDA();
					BankAccount fromAcc = accDA.GetAccount(tran.getFromId());
					BankAccount receiveAcc = accDA.GetAccount(tran.getReceiveId());
					
					if (fromAcc.getBankType().equals(receiveAcc.getBankType())) {
						System.out.println("error");
						return;
					}

					fromAcc.setBalance(fromAcc.getBalance() - tran.getTranAmount() - 0.11);
					receiveAcc.setBalance(receiveAcc.getBalance() - tran.getTranAmount());
					
					tran.setTransferred(true);
					
					accDA.updateAccount(fromAcc);
					accDA.updateAccount(receiveAcc);
					
					tranDA.updateAccount(tran);
					
					AllTrans.clear();
					
				}
				
			}
			
		}
		
		
		
		
		
		
		
		//loggedIn = true;
		//loginUser = new User(9, "user3", "user3", "0987654321", "", "user!123", 3, null, false, false);

		System.out.println("Welcome to Meybank Online Banking");

		while (true) {

			if (!loggedIn) {
				login();
			} else {

				if (loginUser.isAdmin()) {
					adminOPT();
				} else {
					userOPT();
				}

			}
		}
	}

	// Login
	private static void login() {

		LoginDA loginDA = new LoginDA();

		System.out.print("Enter username: ");
		String username = scanner.nextLine();
		if (username.equals("")) {
			return;
		}

		User findUser = loginDA.getUserByName(username);
		if (findUser == null) {
			System.out.println("User not found, please try again");
			return;
		}
		if (findUser.isBan() == true) {
			System.out.println("User is banned, please ask for customer service");
			return;
		}

		boolean cancleLogin = false;
		if (!findUser.isAdmin()) {
			String userPLP = findUser.getUserPLP();
			System.out.println("Is your Personal Login Phrase correct?");
			System.out.println("Your Personal Login Phrase: " + userPLP);
			while (true) {
				System.out.print("Correct(C) Error(E) Cancle(-1): ");
				String opt = scanner.nextLine();
				if (opt.toLowerCase().equals("c")) {
					break;
				} else if (opt.toLowerCase().equals("e")) {
					cancleLogin = true;
					break;
				} else if (opt.equals("-1")) {
					cancleLogin = true;
					break;
				} else {
					System.out.print("Please enter Correct(C), Error(E) or Cancle(-1)");
				}
			}
		}

		if (cancleLogin) {
			return;
		}

		boolean loginLimitReached = false;
		// Password
		while (true) {
			System.out.println("Enter -1 for cancle login, Enter r for Forget Password");
			System.out.println("Enter password: ");
			String password = scanner.nextLine();
			if (password.equals("")) {
				continue;
			}

			if (password.equals("-1")) {
				System.out.println("Login cancled");
				if (findUser.getLoginLimit() != 3) {
					loginDA.updateUserLoginLimit(findUser.getUserID(), findUser.getLoginLimit());
				}
				break;
			}

			// renew password
			if (password.toLowerCase().equals("r")) {
				while (true) {
					System.out.println("Reset password?  Yes(y)/No(n)");
					String YesorNo = scanner.nextLine();
					if (YesorNo.toLowerCase().equals("y")) {
						loginDA.resetPassword(findUser.getUserID());
						break;
					} else if (YesorNo.toLowerCase().equals("n")) {
						break;
					} else {
						System.out.println("Please Enter:  Yes(y)/No(n)");
					}
				}

			}

			PasswordAuthentication passwordAuth = new PasswordAuthentication();
			// if (!PasswordHasher.verifyPassword(password, findUser.getPassword())) {

			if (!passwordAuth.authenticate(password.toCharArray(), findUser.getPassword())) {
				System.out.println("Incorect password");
				int currentLimit = findUser.getLoginLimit();
				findUser.setLoginLimit(currentLimit - 1);
				if (currentLimit - 1 == 0) {
					System.out.println("This account will be banned, Please ask for customer service");
					boolean update = loginDA.updateUserLoginLimit(findUser.getUserID(), findUser.getLoginLimit());

					if (!update) {
						System.out.println("Error");
					}

					loginLimitReached = true;
					break;
				}
			} else {
				if (findUser.getLoginLimit() != 3) {
					loginDA.updateUserLoginLimit(findUser.getUserID(), 3);
				}
				loginUser = findUser;
				loggedIn = true;
				System.out.println("Welcome, " + username + "!");
				break;
			}
		}

		if (loginLimitReached) {
			return;// 如果这里下面没增加其他code，那这句完全是多余的
		}

	}

	// Admin Part
	private static void adminOPT() {
		adminDisplayMenu();
		int choice = 0;
		try {
			choice = scanner.nextInt();
			scanner.nextLine();
		} catch (Exception e) {

		}

		switch (choice) {
		case 1:
			adminManageUser();
			break;
		case 2:
			adminManageAccount();
			break;
		case 3:
			logout();
			break;
		default:
			System.out.println("Invalid Choice");
		}
	}

	private static void adminDisplayMenu() {
		System.out.println("\nMain Menu:");
		System.out.println("1. Manage User ");
		System.out.println("2. Manage Bank Account");
		System.out.println("3. Logout");
		System.out.print("Enter your choice: ");
	}

	// Manage User Start
	private static void adminManageUser() {
		while (true) {
			boolean breakLoop = false;
			manageUserMenu();
			int opt = 0;
			try {
				opt = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
			}

			scanner.nextLine();
			switch (opt) {
			case 1:
				printUsers();
				break;
			case 2:
				addUser();
				break;
			case 3:
				editUser();
				break;
			case 4:
				deleteUser();
				break;
			case 5:
				findUser();
				break;
			case 6:
				breakLoop = true;
				break;
			default:
				System.out.println("Invalid choice");
			}

			if (breakLoop) {
				break;
			}

		}
	}

	private static void manageUserMenu() {
		System.out.println("\nManage User:");
		System.out.println("1. Print All User ");
		System.out.println("2. Add User ");
		System.out.println("3. Edit User ");
		System.out.println("4. Delete User ");
		System.out.println("5. Find User ");
		System.out.println("6. Return to previous level ");
		System.out.print("Enter your choice: ");
	}

	private static void printUsers() {
		UserDA userDA = new UserDA();
		List<User> listofusers = userDA.getAllUsers();
		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(10, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);
		t.addCell("userID");
		t.addCell("userName");
		t.addCell("fullName");
		t.addCell("NRIC");
		t.addCell("email");
		t.addCell("password");
		t.addCell("loginLimit");
		t.addCell("userPLP");
		t.addCell("isAdmin");
		t.addCell("isBan");

		for (var user : listofusers) {

			t.addCell(String.valueOf(user.getUserID()), numberStyle);
			t.addCell(user.getUserName());
			t.addCell(user.getFullName());
			t.addCell(user.getNRIC());
			t.addCell(user.getEmail());
			t.addCell(user.getPassword());
			t.addCell(String.valueOf(user.getLoginLimit()));
			t.addCell(user.getUserPLP());
			t.addCell(String.valueOf(user.isAdmin()));
			t.addCell(String.valueOf(user.isBan()));

		}

		System.out.println(t.render());

	}

	private static void printUsers_PassData(List<User> listofusers) {

		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(10, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);
		t.addCell("userID");
		t.addCell("userName");
		t.addCell("fullName");
		t.addCell("NRIC");
		t.addCell("email");
		t.addCell("password");
		t.addCell("loginLimit");
		t.addCell("userPLP");
		t.addCell("isAdmin");
		t.addCell("isBan");

		for (var user : listofusers) {

			t.addCell(String.valueOf(user.getUserID()), numberStyle);
			t.addCell(user.getUserName());
			t.addCell(user.getFullName());
			t.addCell(user.getNRIC());
			t.addCell(user.getEmail());
			t.addCell(user.getPassword());
			t.addCell(String.valueOf(user.getLoginLimit()));
			t.addCell(user.getUserPLP());
			t.addCell(String.valueOf(user.isAdmin()));
			t.addCell(String.valueOf(user.isBan()));

		}

		System.out.println(t.render());

	}

	private static void printUser(User user) {
		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(2, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);

		t.addCell("userID");
		t.addCell(String.valueOf(user.getUserID()), numberStyle);

		t.addCell("userName");
		t.addCell(user.getUserName());

		t.addCell("fullName");
		t.addCell(user.getFullName());

		t.addCell("NRIC");
		t.addCell(user.getNRIC());

		t.addCell("email");
		t.addCell(user.getEmail());

		t.addCell("password");
		t.addCell(user.getPassword());

		t.addCell("loginLimit");
		t.addCell(String.valueOf(user.getLoginLimit()));

		t.addCell("userPLP");
		t.addCell(user.getUserPLP());

		t.addCell("isAdmin");
		t.addCell(String.valueOf(user.isAdmin()));

		t.addCell("isBan");
		t.addCell(String.valueOf(user.isBan()));

		System.out.println(t.render());

	}

	private static void addUser() {

		String username = null;
		String fullname = null;
		String nric = null;
		String email = null;
		String password = null;
		String PLP = null;

		System.out.println("Enter -1 To Cancle Add New User");

		while (true) {
			System.out.println("Enter User Name:");
			username = scanner.nextLine();
			username = username.trim();
			if (username.equals("-1")) {
				return;
			}
			boolean namecheck = CheckInput.isNameCheck(username);
			if (namecheck) {
				break;
			}
		}

		while (true) {
			System.out.println("Enter Full Name:");
			fullname = scanner.nextLine();
			fullname = fullname.trim();
			if (fullname.equals("-1")) {
				return;
			}
			boolean namecheck = CheckInput.isNameCheck(fullname);
			if (namecheck) {
				break;
			}
		}

		while (true) {
			System.out.println("Enter NRIC:");
			nric = scanner.nextLine();
			nric = nric.trim();
			if (nric.equals("-1")) {
				return;
			}
			boolean nriccheck = CheckInput.isNRICCheck(nric);
			if (nriccheck) {
				break;
			}
		}

		while (true) {
			System.out.println("Enter User Email:");
			email = scanner.nextLine();
			email = email.trim();
			if (email.equals("-1")) {
				return;
			}
			boolean mailcheck = CheckInput.isEmailCheck(email);
			if (mailcheck) {
				break;
			}
		}

		while (true) {
			System.out.println("Enter User Password:");
			password = scanner.nextLine();
			if (password.equals("-1")) {
				return;
			}
			password = password.trim();
			boolean passcheck = CheckInput.isPasswordCheck(password);
			if (passcheck) {
				break;
			}
		}

		int loginLimit = 3;

		while (true) {
			System.out.println("Enter User PLP:");
			PLP = scanner.nextLine();
			if (PLP.equals("-1")) {
				return;
			}
			PLP = PLP.trim();
			boolean PLPcheck = CheckInput.isPLPCheck(PLP);
			if (PLPcheck) {
				break;
			}
		}

		boolean Admin = false;

		while (true) {
			System.out.println("Admin(A) User(U)");
			String IsOrNotAdmin = scanner.nextLine().toLowerCase();
			if (IsOrNotAdmin.equals("-1")) {
				return;
			}
			if (IsOrNotAdmin.equals("a")) {
				Admin = true;
				break;
			} else if (IsOrNotAdmin.equals("u")) {
				break;
			} else {
				System.out.println("Invalid choice");

			}
		}

		boolean Ban = false;

		User user = new User(0, username, fullname, nric, email, password, loginLimit, PLP, Admin, Ban);
		UserDA userDA = new UserDA();
		userDA.AddUser(user);
		System.out.println("Added Successful.");
	}

	private static void editUser() {
		System.out.println("Edit User ");
		printUsers();

		int chooseUser = 0;

		try {
			System.out.println("Enter User Id: ");
			chooseUser = scanner.nextInt();
		} catch (Exception e) {
			System.out.println("Please Enter User Id.");
			return;
		}

		UserDA userDA = new UserDA();
		User getuser = userDA.GetUser(chooseUser);
		if (getuser == null) {
			System.out.println("User Not Found.");
			return;
		}

		while (true) {

			System.out.println("User Details");
			printUser(getuser);

			System.out.println("Choose The Property To Edit:");
			System.out.println("User Name(1)  | Full Name(2)  | NRIC(3)        | Email(4)         | Cancle Edit(-1)");
			System.out.println("Password(5)   | PLP(6)        | Admin/User(7)  | Block/Unblock(8)                  ");

			boolean breakLoop = false;
			int opt = 0;
			scanner.nextLine();
			try {
				System.out.println("Enter Your Option: ");
				opt = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Invalid choice");
				continue;
			}

			switch (opt) {
			case 1:
				System.out.println("Enter User Name: ");
				String username = scanner.nextLine();
				scanner.nextLine();
				boolean usernamecheck = CheckInput.isNameCheck(username);
				if (!usernamecheck) {
					continue;
				}
				getuser.setUserName(username);
				break;
			case 2:
				System.out.println("Enter User Full Name: ");
				String fullname = scanner.nextLine();
				scanner.nextLine();
				boolean fullnamecheck = CheckInput.isNameCheck(fullname);
				if (!fullnamecheck) {
					continue;
				}
				getuser.setFullName(fullname);
				break;
			case 3:
				System.out.println("Enter User NRIC: ");
				String nric = scanner.nextLine();
				scanner.nextLine();
				boolean nriccheck = CheckInput.isNRICCheck(nric);
				if (!nriccheck) {
					continue;
				}
				getuser.setNRIC(nric);

				break;
			case 4:
				System.out.println("Enter User Email: ");
				String email = scanner.nextLine();
				scanner.nextLine();
				boolean emailcheck = CheckInput.isEmailCheck(email);
				if (!emailcheck) {
					continue;
				}

				getuser.setEmail(email);

				break;
			case 5:
				System.out.println("Enter User Password: ");
				String password = scanner.nextLine();
				scanner.nextLine();
				boolean passwordcheck = CheckInput.isPasswordCheck(password);
				if (!passwordcheck) {
					continue;
				}
				getuser.setPassword(password);

				break;
			case 6:
				System.out.println("Enter User PLP: ");
				String PLP = scanner.nextLine();
				scanner.nextLine();
				boolean plpcheck = CheckInput.isPLPCheck(PLP);
				if (!plpcheck) {
					continue;
				}
				getuser.setUserPLP(PLP);

				break;
			case 7:
				System.out.println("Admin(A) User(U)");
				String IsOrNotAdmin = scanner.nextLine().toLowerCase();
				scanner.nextLine();

				if (IsOrNotAdmin.equals("a")) {
					getuser.setAdmin(true);
				} else if (IsOrNotAdmin.equals("u")) {
					getuser.setAdmin(false);
				} else {
					System.out.println("Invalid choice");
				}
				break;
			case 8:

				if (getuser.isBan()) {
					System.out.println("Unblock This User?  Yes(Y)/No(N)");
					String YesOrNo = scanner.nextLine().toLowerCase();
					if (YesOrNo.equals("y")) {
						getuser.setBan(false);
						getuser.setLoginLimit(3);
					} else if (YesOrNo.equals("n")) {
						getuser.setBan(true);
					} else {
						System.out.println("Invalid choice");
					}
				} else {
					System.out.println("Block This User?  Yes(Y)/No(N)");
					String YesOrNo = scanner.nextLine().toLowerCase();
					if (YesOrNo.equals("y")) {
						getuser.setBan(true);
					} else if (YesOrNo.equals("n")) {
						getuser.setBan(false);
					} else {
						System.out.println("Invalid choice");
					}
				}

				break;
			case -1:
				breakLoop = true;
				break;
			default:
				System.out.println("Invalid choice");
				continue;
			}
			if (breakLoop) {
				break;
			}

			User updateUser = new User(getuser.getUserID(), getuser.getUserName(), getuser.getFullName(),
					getuser.getNRIC(), getuser.getEmail(), getuser.getPassword(), getuser.getLoginLimit(),
					getuser.getUserPLP(), getuser.isAdmin(), getuser.isBan());

			if (userDA.updateUser(updateUser)) {
				System.out.println("Update Successful.");
				getuser = userDA.GetUser(chooseUser);
			}
		}
	}

	private static void deleteUser() {

		System.out.println("Delete User ");
		printUsers();

		int chooseUser = 0;

		try {
			System.out.println("Enter User Id: ");
			chooseUser = scanner.nextInt();
		} catch (Exception e) {
			System.out.println("Please Enter User Id.");
			return;
		}

		UserDA userDA = new UserDA();
		User getuser = userDA.GetUser(chooseUser);
		if (getuser == null) {
			System.out.println("User Not Found.");
			return;
		}
		System.out.println("User Details");
		printUser(getuser);

		System.out.println("Delete This User?  Yes(Y)/No(N)");
		scanner.nextLine();
		String YesOrNo = scanner.nextLine();

		if (YesOrNo.toLowerCase().equals("y")) {
			userDA.DeleteUser(chooseUser);
		} else if (YesOrNo.toLowerCase().equals("n")) {
			System.out.println("Delete Cancle");
			return;
		} else {
			System.out.println("Invalid choice");
			return;
		}

		System.out.println("Delete Successful.");

	}

	private static void findUser() {
		System.out.println("Find By : ");
		System.out.println("UserID(1)  | userName(2)  | fullName(3)  | NRIC(4)            ");
		System.out.println("Email(5)   | Admin(6)     | Ban(7)       | Back To Menu(0) ");

		int opt = 0;
		List<String> ColumnName = new ArrayList<String>(
				Arrays.asList("userID", "userName", "fullName", "NRIC", "email", "isAdmin", "isBan"));

		String target = null;
		try {
			System.out.println("Enter Your Option: ");
			opt = scanner.nextInt();
		} catch (Exception e) {
			System.out.println("Invalid Option");
			return;
		}

		if (opt < 0 || opt > 7) {
			System.out.println("Invalid choice");
			return;
		}
		if (opt == 0) {
			return;
		}

		String stringValue = null;
		boolean isStr = false;

		int intValue = 0;
		boolean isInt = false;

		Boolean booleanValue = null;
		boolean isBool = false;

		while (true) {
			target = ColumnName.get(opt - 1);
			int index = ColumnName.indexOf(target);
			System.out.println("Enter -1 For Exit");
			System.out.println("Enter Search Value: ");
			if (index == 0) {
				// int
				try {
					intValue = scanner.nextInt();
					if (intValue == -1) {
						return;
					}
					isInt = true;
					break;
				} catch (Exception e) {
					System.out.println("All User ID Is Number");
				}

			} else if (index == 1 || index == 2 || index == 3 || index == 4) {
				// string
				stringValue = scanner.nextLine();
				if (stringValue == "-1") {
					return;
				}
				isStr = true;
			} else if (index == 5 || index == 6) {
				// boolean
				if (index == 3) {
					System.out.println("Admin(A) User(U)");
					String IsOrNotAdmin = scanner.nextLine().toLowerCase();
					if (IsOrNotAdmin.equals("a")) {
						booleanValue = true;

					} else if (IsOrNotAdmin.equals("u")) {
						booleanValue = false;

					} else {
						System.out.println("Invalid choice");
					}
				} else {

					System.out.println("User Banned(B) User Not Banned(U)");
					String IsOrNotAdmin = scanner.nextLine().toLowerCase();
					if (IsOrNotAdmin.equals("b")) {
						booleanValue = true;

					} else if (IsOrNotAdmin.equals("u")) {
						booleanValue = false;

					} else {
						System.out.println("Invalid choice");
					}

				}

				isBool = true;
			} else {
				System.out.println("Error");
			}
		}

		UserDA userDA = new UserDA();
		List<User> getusers = null;
		if (isInt) {
			getusers = userDA.findUserByProperty(target, String.valueOf(intValue));
		}
		if (isStr) {
			getusers = userDA.findUserByProperty(target, stringValue);
		}
		if (isBool) {
			getusers = userDA.findUserByProperty(target, String.valueOf(booleanValue));
		}

		if (getusers == null) {
			System.out.println("Student Not Found.");
			return;
		}
		printUsers_PassData(getusers);
	}

	// Manage User End

	// Manage Bank Account Start
	private static void adminManageAccount() {

		while (true) {
			boolean breakLoop = false;
			manageAccountMenu();
			int opt = 0;
			try {
				opt = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
			}

			scanner.nextLine();
			switch (opt) {
			case 1:
				printAccounts();
				break;
			case 2:
				addAccount();
				break;
			case 3:
				editAccount();
				break;
			case 4:
				deleteAccount();
				break;
			case 5:
				findAccount(true);
				break;
			case 6:
				breakLoop = true;
				break;
			default:
				System.out.println("Invalid choice");
			}

			if (breakLoop) {
				break;
			}

		}
	}

	private static void manageAccountMenu() {
		System.out.println("\nManage Bank Account:");
		System.out.println("1. Print All Accounts ");
		System.out.println("2. Add Account ");
		System.out.println("3. Edit Account ");
		System.out.println("4. Delete Account ");
		System.out.println("5. Find Account ");
		System.out.println("6. Return to previous level ");
		System.out.print("Enter your choice: ");
	}

	private static void printAccounts() {
		BankAccountDA accDA = new BankAccountDA();
		List<BankAccount> listofaccount = accDA.getAllAccounts();
		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(9, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);
		t.addCell("accID");
		t.addCell("userId");
		t.addCell("accNumber");
		t.addCell("accName");
		t.addCell("accPin");
		t.addCell("balance");
		t.addCell("accType");
		t.addCell("bankType");
		t.addCell("isBan");

		for (var acc : listofaccount) {

			t.addCell(String.valueOf(acc.getAccID()), numberStyle);
			t.addCell(String.valueOf(acc.getUserId()));
			t.addCell(acc.getAccNumber());
			t.addCell(acc.getAccName());
			t.addCell(acc.getAccPin());
			t.addCell(String.valueOf(acc.getBalance()));
			t.addCell(acc.getAccType());
			t.addCell(acc.getBankType());
			t.addCell(String.valueOf(acc.isBan()));

		}
		System.out.println(t.render());

	}

	private static void printAccounts_PassData(List<BankAccount> listofaccount) {

		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(9, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);
		t.addCell("accID");
		t.addCell("userId");
		t.addCell("accNumber");
		t.addCell("accName");
		t.addCell("accPin");
		t.addCell("balance");
		t.addCell("accType");
		t.addCell("bankType");
		t.addCell("isBan");

		for (var acc : listofaccount) {

			t.addCell(String.valueOf(acc.getAccID()), numberStyle);
			t.addCell(String.valueOf(acc.getUserId()));
			t.addCell(acc.getAccNumber());
			t.addCell(acc.getAccName());
			t.addCell(acc.getAccPin());
			t.addCell(String.valueOf(acc.getBalance()));
			t.addCell(acc.getAccType());
			t.addCell(acc.getBankType());
			t.addCell(String.valueOf(acc.isBan()));

		}
		System.out.println(t.render());

	}

	private static void printAccount(BankAccount acc) {
		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(2, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);

		t.addCell("accID");
		t.addCell(String.valueOf(acc.getAccID()), numberStyle);

		t.addCell("userId");
		t.addCell(String.valueOf(acc.getUserId()));

		t.addCell("accNumber");
		t.addCell(acc.getAccNumber());

		t.addCell("accName");
		t.addCell(acc.getAccName());

		t.addCell("accPin");
		t.addCell(acc.getAccPin());

		t.addCell("balance");
		t.addCell(String.valueOf(acc.getBalance()));

		t.addCell("accType");
		t.addCell(acc.getAccType());

		t.addCell("bankType");
		t.addCell(acc.getBankType());

		t.addCell("isBan");
		t.addCell(String.valueOf(acc.isBan()));

		System.out.println(t.render());

	}

	private static void addAccount() {
		int userId = 0;
		String accName = null;
		String accPin = null;
		double balance = 250;
		String accType = null;
		String bankType = null;

		System.out.println("Enter -1 To Cancel Add New Account");

		while (true) {
			try {
				System.out.println("Enter User Id:");
				userId = scanner.nextInt();
				if (userId == -1) {
					return;
				}
				scanner.nextLine(); // 清除缓冲区中的换行符
				boolean userIdCheck = CheckInput.isUserIdCheck(userId);
				if (userIdCheck) {
					break;
				}
			} catch (Exception e) {
				System.out.println("Please Enter Number");
				scanner.nextLine(); // 清除缓冲区中的非数字输入
			}
		}

		// 读取账户名
		while (true) {
			System.out.println("Enter Account Name:");
			accName = scanner.nextLine();
			if (accName.equals("-1")) {
				return;
			}
			accName = accName.trim();
			boolean accNameCheck = CheckInput.isAccNameCheck(accName);
			if (accNameCheck) {
				break;
			}
		}

		// 读取账户PIN
		while (true) {
			System.out.println("Enter Account Pin:");
			accPin = scanner.nextLine();
			if (accPin.equals("-1")) {
				return;
			}
			accPin = accPin.trim();
			boolean accPinCheck = CheckInput.isAccPinCheck(accPin);
			if (accPinCheck) {
				break;
			}
		}

		// 读取账户余额
		while (true) {
			try {
				System.out.println("Enter Account Balance (RM): ");
				balance = scanner.nextDouble();
				if (balance == -1) {
					return;
				}
				boolean balanceCheck = CheckInput.isBalanceCheck(balance);
				if (balanceCheck) {
					break;
				}
			} catch (Exception e) {
				System.out.println("Please Enter Number");
				scanner.nextLine(); // 清除缓冲区中的非数字输入
			}
		}

		// 读取账户类型
		while (true) {
			try {
				System.out.println("Enter Account Type : Savings(1)  Current(2) ");
				int accTypeOpt = scanner.nextInt();
				if (accTypeOpt == -1) {
					return;
				}
				accType = CheckInput.chooseAccType(accTypeOpt);
				if (!accType.isEmpty()) {
					break;
				}
			} catch (Exception e) {
				System.out.println("Please Enter Number");
				scanner.nextLine(); // 清除缓冲区中的非数字输入
			}
		}

		// 读取银行类型
		while (true) {
			try {
				System.out.println("Enter Bank Type : PublicBank(1)  MayBank(2) ");
				int bankTypeOpt = scanner.nextInt();
				if (bankTypeOpt == -1) {
					return;
				}
				bankType = CheckInput.chooseBankType(bankTypeOpt);
				if (!bankType.isEmpty()) {
					break;
				}
			} catch (Exception e) {
				System.out.println("Please Enter Number");
				scanner.nextLine(); // 清除缓冲区中的非数字输入
			}
		}

		BankAccount acc = new BankAccount(userId, accName, accPin, balance, accType, bankType);
		BankAccountDA accDA = new BankAccountDA();
		accDA.AddAccount(acc);

		System.out.println("Added Successful.");
	}

	private static void editAccount() {
		System.out.println("--- Edit Account ---");

		printAccounts();

		int chooseAccount = 0;
		boolean needFindUser = false;

		while (true) {
			System.out.println("Need To Find Id?  Yes(Y)/No(N)/-1 to Cancle");
			String YesorNo = scanner.nextLine();
			if (YesorNo.trim().toLowerCase().equals("y")) {
				needFindUser = true;
				break;
			} else if (YesorNo.trim().toLowerCase().equals("n")) {
				break;
			} else if (YesorNo.trim().equals("-1")) {
				return;
			} else {
				System.out.println("Please Enter Y / N / -1");
			}

		}

		while (true) {
			try {
				if (needFindUser) {
					chooseAccount = findAccount(false);
					break;
				} else {
					System.out.println("Enter Account Id: ");
					chooseAccount = scanner.nextInt();
					scanner.nextLine();
					break;
				}
			} catch (Exception e) {
				System.out.println("Please Enter Account Id.");
			}
		}

		BankAccountDA accDA = new BankAccountDA();
		BankAccount findAcc = accDA.GetAccount(chooseAccount);
		if (findAcc == null) {
			System.out.println("User Not Found.");
			return;
		}

		while (true) {

			System.out.println("Account Details");
			printAccount(findAcc);

			System.out.println("Choose The Property To Edit:");
			System.out.println("Account Name(1)  | Balance(2)        | accType(3)  ");
			System.out.println("bankType(4)      | Block/Unblock(5)  | Cancle(-1)  ");
			boolean breakLoop = false;
			int opt = 0;
			scanner.nextLine();
			try {

				System.out.println("Enter Your Option: ");
				opt = scanner.nextInt();
				scanner.nextLine();

			} catch (Exception e) {

				System.out.println("Invalid choice");
				continue;

			}

			switch (opt) {
			case 1:
				System.out.println("Enter Account Name: ");
				String name = scanner.nextLine();

				findAcc.setAccName(name);
				break;
			case 2:
				System.out.println("Enter Account Balance: ");
				double balance = scanner.nextDouble();
				scanner.nextLine();

				findAcc.setBalance(balance);
				break;
			case 3:
				String accType = findAcc.getAccType();
				try {
					System.out.println("Enter Account Type : Savings(1)  Current(2) ");
					int accTypeOpt = scanner.nextInt();
					String returnAccType = CheckInput.chooseAccType(accTypeOpt);
					if (returnAccType.isEmpty()) {
						System.out.println("error");
					} else {
						accType = returnAccType;
					}

				} catch (Exception e) {
					System.out.println("Please Enter Number");
				}
				scanner.nextLine();

				findAcc.setAccType(accType);
				break;
			case 4:
				String bankType = findAcc.getBankType();
				try {
					System.out.println("Enter Bank Type : PublicBank(1)  MayBank(2) ");
					int bankTypeOpt = scanner.nextInt();
					String returnBankType = CheckInput.chooseAccType(bankTypeOpt);
					if (returnBankType.isEmpty()) {
						System.out.println("error");
					} else {
						bankType = returnBankType;
					}

				} catch (Exception e) {
					System.out.println("Please Enter Number");
				}
				scanner.nextLine();

				findAcc.setBankType(bankType);
				break;
			case 5:
				if (findAcc.isBan()) {
					System.out.println("Unblock This Account?  Yes(Y)/No(N)");
					String YesOrNo = scanner.nextLine().toLowerCase();
					if (YesOrNo.equals("y")) {
						findAcc.setBan(false);
					} else if (YesOrNo.equals("n")) {
						findAcc.setBan(true);
					} else {
						System.out.println("Invalid choice");
					}
				} else {
					System.out.println("Block This Account?  Yes(Y)/No(N)");
					String YesOrNo = scanner.nextLine().toLowerCase();
					if (YesOrNo.equals("y")) {
						findAcc.setBan(true);
					} else if (YesOrNo.equals("n")) {
						findAcc.setBan(false);
					} else {
						System.out.println("Invalid choice");
					}
				}
				break;
			case -1:
				breakLoop = true;
				break;
			default:
				System.out.println("Invalid choice");
				break;
			}
			if (breakLoop) {
				break;
			}

			BankAccount updateAcc = new BankAccount(findAcc.getAccID(), findAcc.getUserId(), findAcc.getAccNumber(),
					findAcc.getAccName(), findAcc.getAccPin(), findAcc.getBalance(), findAcc.getAccType(),
					findAcc.getBankType(), findAcc.isBan());

			if (accDA.updateAccount(updateAcc)) {
				System.out.println("Update Successful.");

			}
		}
	}

	private static void deleteAccount() {

		System.out.println("--- Edit Account ---");

		printAccounts();

		int chooseAccount = 0;
		boolean needFindUser = false;

		while (true) {
			System.out.println("Need To Find Id?  Yes(Y)/No(N)/-1 to Cancle");
			String YesorNo = scanner.nextLine();
			if (YesorNo.trim().toLowerCase().equals("y")) {
				needFindUser = true;
				break;
			} else if (YesorNo.trim().toLowerCase().equals("n")) {
				break;
			} else if (YesorNo.trim().equals("-1")) {
				return;
			} else {
				System.out.println("Please Enter Y / N / -1");
			}

		}

		while (true) {
			try {
				if (needFindUser) {
					chooseAccount = findAccount(false);
					break;
				} else {
					System.out.println("Enter Account Id: ");
					chooseAccount = scanner.nextInt();
					scanner.nextLine();
					break;
				}
			} catch (Exception e) {
				System.out.println("Please Enter Account Id.");
			}
		}

		BankAccountDA accDA = new BankAccountDA();
		BankAccount findAcc = accDA.GetAccount(chooseAccount);
		if (findAcc == null) {
			System.out.println("User Not Found.");
			return;
		}

		System.out.println("Account Details");
		printAccount(findAcc);

		System.out.println("Delete This Account?  Yes(Y)/No(N)");
		String YesOrNo = scanner.nextLine();

		if (YesOrNo.toLowerCase().equals("y")) {
			accDA.DeleteAccount(chooseAccount);
		} else if (YesOrNo.toLowerCase().equals("n")) {
			System.out.println("Delete Cancle");
			return;
		} else {
			System.out.println("Invalid choice");
			return;
		}

		System.out.println("Delete Successful.");

	}

	private static int findAccount(boolean NoneReturn) {

		int opt = 0;

		List<String> ColumnName = new ArrayList<String>(
				Arrays.asList("accID", "userId", "accNumber", "AccName", "accType", "bankType", "isBan"));

		String target = null;

		while (true) {
			System.out.println("Find By : ");
			System.out.println("accID(1)    | userId(2)    | accNumber(3)  | AccName(4)");
			System.out.println("accType(5)  | bankType(6)  | Ban(7)        | Back To Menu(0) ");

			try {
				System.out.println("Enter Your Option: ");
				opt = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Invalid Option");
				continue;
			}

			if (opt < 0 || opt > 7) {
				System.out.println("Invalid choice");
				continue;
			}
			if (opt == 0) {
				return 0;
			}

			break;
		}

		String stringValue = null;
		boolean isStr = false;

		int intValue = 0;
		boolean isInt = false;

		Boolean booleanValue = null;
		boolean isBool = false;

		while (true) {
			target = ColumnName.get(opt - 1);
			int index = ColumnName.indexOf(target);
			System.out.println("Enter -1 For Exit");
			System.out.println("Enter Search Value: ");
			if (index == 0 || index == 1) {
				// int
				try {
					intValue = scanner.nextInt();
					scanner.nextLine();
					if (intValue == -1) {
						return 0;
					}
					isInt = true;
					break;
				} catch (Exception e) {
					System.out.println("All User ID Is Number");
				}

			} else if (index == 2 || index == 3 || index == 4 || index == 5) {
				// string
				stringValue = scanner.nextLine();
				if (stringValue == "-1") {
					return 0;
				}
				isStr = true;
			} else if (index == 6) {
				// boolean
				System.out.println("User Banned(B) User Not Banned(U)");
				String IsOrNotAdmin = scanner.nextLine().toLowerCase();
				if (IsOrNotAdmin.equals("b")) {
					booleanValue = true;

				} else if (IsOrNotAdmin.equals("u")) {
					booleanValue = false;

				} else {
					System.out.println("Invalid choice");
				}

				isBool = true;
			} else {
				System.out.println("Error");
			}
		}

		BankAccountDA accDA = new BankAccountDA();
		List<BankAccount> getaccounts = null;
		if (isInt) {
			getaccounts = accDA.findAccountByProperty(target, String.valueOf(intValue));
		}
		if (isStr) {
			getaccounts = accDA.findAccountByProperty(target, stringValue);
		}
		if (isBool) {
			getaccounts = accDA.findAccountByProperty(target, String.valueOf(booleanValue));
		}

		if (getaccounts == null) {
			System.out.println("Accound Not Found.");
			return -1;
		}

		if (NoneReturn == true) {
			return 0;
		}

		while (true) {
			printAccounts_PassData(getaccounts);
			System.out.println("Enter -1 To Cancle");
			System.out.println("Enter Account Id : ");
			try {
				int accId = scanner.nextInt();
				scanner.nextLine();
				if (accId == -1) {
					return 0;
				}
				BankAccount findAcc = accDA.GetAccount(accId);
				if (findAcc == null) {
					System.out.println("Account Not Found, Please Enter Valid Id");
				} else {
					return accId;
				}

			} catch (Exception e) {
				System.out.println("Please Enter Account Id");
				continue;
			}

		}

	}

	private static int findTransaction(boolean NoneReturn) {

		int opt = 0;
		List<String> ColumnName = new ArrayList<String>(
				Arrays.asList("tranID", "fromId", "receiveId", "tranTime", "tranType", "tranAmount", "transferred"));

		String target = null;

		while (true) {
			System.out.println("Find By : ");
			System.out.println("tranID(1)    | fromId(2)     | receiveId(3)   | tranTime(4)");
			System.out.println("tranType(5)  | tranAmount(6) | transferred(7) | Back To Menu(0)");

			try {
				System.out.println("Enter Your Option: ");
				opt = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Invalid Option");
				continue;
			}

			if (opt < 0 || opt > 5) {
				System.out.println("Invalid choice");
				continue;
			}
			if (opt == 0) {
				return 0;
			}

			break;
		}

		String stringValue = null;
		boolean isStr = false;

		int intValue = 0;
		boolean isInt = false;

		Boolean booleanValue = null;
		boolean isBool = false;

		while (true) {
			target = ColumnName.get(opt - 1);
			int index = ColumnName.indexOf(target);
			System.out.println("Enter -1 For Exit");
			System.out.println("Enter Search Value: ");
			if (index == 0 || index == 1) {
				// int
				try {
					intValue = scanner.nextInt();
					scanner.nextLine();
					if (intValue == -1) {
						return 0;
					}
					isInt = true;
					break;
				} catch (Exception e) {
					System.out.println("All User ID Is Number");
				}

			} else if (index == 2 || index == 3) {
				// string
				stringValue = scanner.nextLine();
				if (stringValue == "-1") {
					return 0;
				}
				isStr = true;
			} else if (index == 4) {
				// boolean
				System.out.println("User Banned(B) User Not Banned(U)");
				String IsOrNotAdmin = scanner.nextLine().toLowerCase();
				if (IsOrNotAdmin.equals("b")) {
					booleanValue = true;

				} else if (IsOrNotAdmin.equals("u")) {
					booleanValue = false;

				} else {
					System.out.println("Invalid choice");
				}

				isBool = true;
			} else {
				System.out.println("Error");
			}
		}

		BankAccountDA accDA = new BankAccountDA();
		List<BankAccount> getaccounts = null;
		if (isInt) {
			getaccounts = accDA.findAccountByProperty(target, String.valueOf(intValue));
		}
		if (isStr) {
			getaccounts = accDA.findAccountByProperty(target, stringValue);
		}
		if (isBool) {
			getaccounts = accDA.findAccountByProperty(target, String.valueOf(booleanValue));
		}

		if (getaccounts == null) {
			System.out.println("Accound Not Found.");
			return -1;
		}

		if (NoneReturn == true) {
			return 0;
		}

		while (true) {
			printAccounts_PassData(getaccounts);
			System.out.println("Enter -1 To Cancle");
			System.out.println("Enter Account Id : ");
			try {
				int accId = scanner.nextInt();
				scanner.nextLine();
				if (accId == -1) {
					return 0;
				}
				BankAccount findAcc = accDA.GetAccount(accId);
				if (findAcc == null) {
					System.out.println("Account Not Found, Please Enter Valid Id");
				} else {
					return accId;
				}

			} catch (Exception e) {
				System.out.println("Please Enter Account Id");
				continue;
			}

		}

	}

	// User Part
	private static void userOPT() {
		userDisplayMenu();
		int choice = scanner.nextInt();
		scanner.nextLine();
		switch (choice) {
		case 1:
			printUserAccountDetails();
			break;
		case 2:
			userChangePassword();
			break;
		case 3:
			userChangeEmail();
			break;
		case 4:
			userOnlineTransfer();
			break;
		case 5:
			printUserTransaction();
			break;
		case 6:
			logout();
			break;
		default:
			System.out.println("Invalid choice!");
		}

	}

	private static void userDisplayMenu() {
		System.out.println("\nMain Menu:");
		System.out.println("1. Print Accounts");
		System.out.println("2. Change Password");
		System.out.println("3. Change Email");
		System.out.println("4. Online Transfer");
		System.out.println("5. Print Transaction");
		System.out.println("6. Logout");
		System.out.print("Enter your choice: ");
	}

	private static void printUserAccountDetails() {
		System.out.println("-- Accounts Details --");

		BankAccountDA accDA = new BankAccountDA();
		List<BankAccount> listofaccount = accDA.GetAccountsByUserId(loginUser.getUserID());

		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(7, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);

		t.addCell("accID");
		t.addCell("accNumber");
		t.addCell("accName");
		t.addCell("balance");
		t.addCell("accType");
		t.addCell("bankType");
		t.addCell("isBan");

		for (var acc : listofaccount) {

			t.addCell(String.valueOf(acc.getAccID()), numberStyle);
			t.addCell(acc.getAccNumber());
			t.addCell(acc.getAccName());
			t.addCell(String.valueOf(acc.getBalance()));
			t.addCell(acc.getAccType());
			t.addCell(acc.getBankType());
			t.addCell(String.valueOf(acc.isBan()));

		}
		System.out.println(t.render());

	}

	private static void userChangePassword() {
		System.out.println("-- Change Password --");
		System.out.println("Enter Your Current Password: ");
		String password = scanner.nextLine();

		//if (!password.equals(loginUser.getPassword())) {
			//System.out.println("Password Incorrect");
			//return;
		//}
		while (true) {
			System.out.println("Enter -1 To Exit");
			System.out.println("Enter Your New Password: ");
			String newPassword = scanner.nextLine();
			if (newPassword.equals("-1")) {
				return;
			}
			if (!CheckInput.isPasswordCheck(newPassword)) {
				continue;
			}
			System.out.println("Enter -1 To Exit");
			System.out.println("Enter Your New Password Again: ");
			String confirmPassword = scanner.nextLine();
			if (confirmPassword.equals("-1")) {
				return;
			}
			if (newPassword.equals(confirmPassword)) {
				loginUser.setPassword(newPassword);

				UserDA userDA = new UserDA();
				boolean update = userDA.updateUser(loginUser);
				if (update) {
					System.out.println("Update Successful");
					SendEmail.ChangePasswordNotification(loginUser.getEmail());
				}

			}
			break;
		}

	}

	private static void userChangeEmail() {
		System.out.println("-- Change Email --");
		while (true) {
			System.out.println("Enter -1 To Exit");
			System.out.println("Enter Your New Email: ");
			String newEmail = scanner.nextLine();
			if (newEmail.equals("-1")) {
				return;
			}
			if (!CheckInput.isEmailCheck(newEmail)) {
				continue;
			}
			System.out.println("Enter -1 To Exit");
			
			break;
		}

	}

	// transfer start
	private static void userOnlineTransfer() {
		System.out.println("-- Online Transfer --");
		System.out.println("1. Third party transfer");
		System.out.println("2. Interbank Giro");
		System.out.println("3. Instant Transfer");
		System.out.println("4. Back");

		int choice = scanner.nextInt();
		scanner.nextLine();
		switch (choice) {
		case 1:
			thirdPartyTransfer();
			break;
		case 2:
			interbankGiro();
			break;
		case 3:
			instantTransfer();
			break;
		case 4:
			return;
		default:
			System.out.println("Invalid choice!");
		}

	}

	private static void thirdPartyTransfer() {

		System.out.println("-- Third party transfer --");
		printUserAccountDetails();
		BankAccountDA accDA = new BankAccountDA();
		TransactionDA tranDA = new TransactionDA();
		BankAccount getOwnAcc = null;
		BankAccount getTargetAcc = null;
		double tranAmount = 0;

		// check this user acc
		while (true) {

			int chooseAccount = 0;
			try {
				System.out.println("Enter -1 To Cancle");
				System.out.println("Enter Your Account Id: ");
				chooseAccount = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Please Enter Valid Account Id.");
				continue;
			}

			if (chooseAccount == -1) {
				return;
			}

			getOwnAcc = accDA.getUserAccount(loginUser.getUserID(), chooseAccount);

			if (getOwnAcc == null) {
				System.out.println("Account Not Found, Please Enter Valid Account ");
				continue;
			} else {
				break;
			}
		}

		// check target user acc
		while (true) {

			String targetAccNo = null;
			System.out.println("Enter -1 To Cancle");
			System.out.println("Enter Target Account Number: ");
			targetAccNo = scanner.nextLine();

			if (targetAccNo == "-1") {
				return;
			}
			if (targetAccNo.equals(getOwnAcc.getAccNumber())) {
				System.out.println("Cannot Tranfer To Own Account.");
				return;
			}

			getTargetAcc = accDA.getAccountByAccNumber(targetAccNo, getOwnAcc.getBankType());

			if (getTargetAcc == null) {
				System.out.println("Account Not Found, Please Enter Valid Account ");
				continue;
			} else {
				break;
			}
		}
		
		printAccDetails(getTargetAcc);
		
		while(true) {
			try {
				System.out.println("Enter -1 To Cancle");
				System.out.println("Enter Transfer Amount: ");
				tranAmount = scanner.nextDouble();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Please Enter Valid Amount");
				continue;
			}
			if (tranAmount == -1) {
				return;
			}
			
			if(tranAmount < 20 || tranAmount > 30000) {
				System.out.println("Amount Cannot Less than 20 and Cannot More than 30000");
				continue;
			}
			
			// Assuming the transaction type is "IBGT" for interbank transfer
	        String tranType = "Third Party Transfer";
	        Transaction transaction = new Transaction(getOwnAcc.getAccID(), getTargetAcc.getAccID(), tranType, tranAmount);
	        
	        tranDA.AddTransaction(transaction);
	        break; // Exit the loop once the transaction is successfully added
			
		}
		
		
		
	}

	private static void printAccDetails(BankAccount acc) {

		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(2, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);
		
		UserDA userDA = new UserDA();
		String userName = userDA.GetUserName(acc.getUserId());
		
		
		t.addCell("accID");
		t.addCell(String.valueOf(acc.getAccID()), numberStyle);
		
		t.addCell("UserName");
		t.addCell(userName);

		t.addCell("accNumber");
		t.addCell(acc.getAccNumber());

		t.addCell("accName");
		t.addCell(acc.getAccName());

		t.addCell("accType");
		t.addCell(acc.getAccType());

		t.addCell("bankType");
		t.addCell(acc.getBankType());

		t.addCell("isBan");
		t.addCell(String.valueOf(acc.isBan()));

		
		
		System.out.println(t.render());
	}

	private static void interbankGiro() {
		System.out.println("-- Interbank Giro --");
		printUserAccountDetails();
		BankAccountDA accDA = new BankAccountDA();
		TransactionDA tranDA = new TransactionDA();
		BankAccount getOwnAcc = null;
		BankAccount getTargetAcc = null;
		double tranAmount = 0;

		// check this user acc
		while (true) {

			int chooseAccount = 0;
			try {
				System.out.println("Enter -1 To Cancle");
				System.out.println("Enter Your Account Id: ");
				chooseAccount = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Please Enter Valid Account Id.");
				continue;
			}

			if (chooseAccount == -1) {
				return;
			}

			getOwnAcc = accDA.getUserAccount(loginUser.getUserID(), chooseAccount);

			if (getOwnAcc == null) {
				System.out.println("Account Not Found, Please Enter Valid Account ");
				continue;
			} else {
				break;
			}
		}

		// check target user acc
		while (true) {

			String targetAccNo = null;
			System.out.println("Enter -1 To Cancle");
			System.out.println("Enter Target Account Number: ");
			targetAccNo = scanner.nextLine();

			if (targetAccNo == "-1") {
				return;
			}
			if (targetAccNo.equals(getOwnAcc.getAccNumber())) {
				System.out.println("Cannot Tranfer To Own Account.");
				return;
			}

			getTargetAcc = accDA.getAccountByAccNo(targetAccNo);

			if (getTargetAcc == null) {
				System.out.println("Account Not Found, Please Enter Valid Account ");
				continue;
			} else {
				
				if(getTargetAcc.getBankType().equals(getOwnAcc.getBankType()))
				{
					System.out.println("Please Change To Third Party Transfer");
					return;
				}
				
				
				break;
			}
		}
		
		printAccDetails(getTargetAcc);
		
		while(true) {
			try {
				System.out.println("Enter -1 To Cancle");
				System.out.println("Enter Transfer Amount: ");
				tranAmount = scanner.nextDouble();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Please Enter Valid Amount");
				continue;
			}
			if (tranAmount == -1) {
				return;
			}
			
			if(tranAmount < 20 || tranAmount > 30000) {
				System.out.println("Amount Cannot Less than 20 and Cannot More than 30000");
				continue;
			}
			
			// Assuming the transaction type is "IBGT" for interbank transfer
	        String tranType = "Third Party Transfer";
	        Transaction transaction = new Transaction(getOwnAcc.getAccID(), getTargetAcc.getAccID(), tranType, tranAmount);
	        
	        tranDA.AddTransaction(transaction);
	        break; // Exit the loop once the transaction is successfully added
			
		}
		
	}

	private static void instantTransfer() {
		System.out.println("-- Instant Transfer --");
		printUserAccountDetails();
		BankAccountDA accDA = new BankAccountDA();
		TransactionDA tranDA = new TransactionDA();
		BankAccount getOwnAcc = null;
		BankAccount getTargetAcc = null;
		double tranAmount = 0;

		// check this user acc
		while (true) {

			int chooseAccount = 0;
			try {
				System.out.println("Enter -1 To Cancle");
				System.out.println("Enter Your Account Id: ");
				chooseAccount = scanner.nextInt();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Please Enter Valid Account Id.");
				continue;
			}

			if (chooseAccount == -1) {
				return;
			}

			getOwnAcc = accDA.getUserAccount(loginUser.getUserID(), chooseAccount);

			if (getOwnAcc == null) {
				System.out.println("Account Not Found, Please Enter Valid Account ");
				continue;
			} else {
				break;
			}
		}

		// check target user acc
		while (true) {

			String targetAccNo = null;
			System.out.println("Enter -1 To Cancle");
			System.out.println("Enter Target Account Number: ");
			targetAccNo = scanner.nextLine();

			if (targetAccNo == "-1") {
				return;
			}
			if (targetAccNo.equals(getOwnAcc.getAccNumber())) {
				System.out.println("Cannot Tranfer To Own Account.");
				return;
			}

			getTargetAcc = accDA.getAccountByAccNumber(targetAccNo, getOwnAcc.getBankType());

			if (getTargetAcc == null) {
				System.out.println("Account Not Found, Please Enter Valid Account ");
				continue;
			} else {
				break;
			}
		}
		
		printAccDetails(getTargetAcc);
		
		while(true) {
			try {
				System.out.println("Enter -1 To Cancle");
				System.out.println("Enter Transfer Amount: ");
				tranAmount = scanner.nextDouble();
				scanner.nextLine();
			} catch (Exception e) {
				System.out.println("Please Enter Valid Amount");
				continue;
			}
			if (tranAmount == -1) {
				return;
			}
			
			if(tranAmount < 20 || tranAmount > 30000) {
				System.out.println("Amount Cannot Less than 20 and Cannot More than 30000");
				continue;
			}
			
			// Assuming the transaction type is "IBGT" for interbank transfer
	        String tranType = "Third Party Transfer";
	        Transaction transaction = new Transaction(getOwnAcc.getAccID(), getTargetAcc.getAccID(), tranType, tranAmount);
	        
	        tranDA.AddTransaction(transaction);
	        break; // Exit the loop once the transaction is successfully added
			
		}
		
	}
	// transfer end

	private static void printUserTransaction() {

		System.out.println("-- Print Transaction --");
		printUserAccountDetails();

		int chooseAccount = 0;
		try {
			System.out.println("Enter -1 For Cancle");
			System.out.println("Enter Account Id: ");
			chooseAccount = scanner.nextInt();
			scanner.nextLine();
			
			if(chooseAccount == -1) {
				return;
			}
			
			
		} catch (Exception e) {
			System.out.println("Please Enter Account Id.");
			return;
		}
		
		BankAccountDA accDA = new BankAccountDA();
		BankAccount getAcc = accDA.getUserAccount(loginUser.getUserID(), chooseAccount);

		if (getAcc == null) {
			System.out.println("Account Not Found");
			return;
		}
		
		TransactionDA tranDA = new TransactionDA();
		List<Transaction> thisAccTrans = tranDA.getAllUserTransactions(getAcc.getAccID());
		
		CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.center);
		Table t = new Table(7, BorderStyle.CLASSIC_WIDE, ShownBorders.ALL);
		t.addCell("Transaction ID");
		t.addCell("From Account Number");
		t.addCell("Receive Account Number");
		t.addCell("Transaction Time");
		t.addCell("Transaction Type");
		t.addCell("Transaction Amount");
		t.addCell("Transferred");

		for (var tran : thisAccTrans) {
			
			t.addCell(String.valueOf(tran.getTranID()), numberStyle);
			t.addCell(accDA.getAccountNumber(tran.getFromId()));
			t.addCell(accDA.getAccountNumber(tran.getReceiveId()));
			t.addCell(String.valueOf(tran.getTranTime()));
			t.addCell(tran.getTranType());
			t.addCell(String.valueOf(tran.getTranAmount()));
			t.addCell(String.valueOf(tran.isTransferred()));

		}
		System.out.println(t.render());

	}

	// Logout
	private static void logout() {
		loggedIn = false;
		loginUser = null;
		System.out.println("Logged out successfully.");
	}

}
