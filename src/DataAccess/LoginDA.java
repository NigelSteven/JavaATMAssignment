package DataAccess;

import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Models.AppDbContext;
import Models.User;

public class LoginDA {
	private String tableName = "users";
	private PreparedStatement stmt;

	public User getUserByName(String userName) {
		String query = "select * from " + tableName + " where userName=?";
		User user = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setString(1, userName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user = new User(rs.getInt("userID"), rs.getString("userName"), rs.getString("fullName"),
						rs.getString("NRIC"), rs.getString("email"), rs.getString("password"), 
						rs.getInt("loginLimit"),rs.getString("userPLP"), rs.getBoolean("isAdmin"), 
						rs.getBoolean("isBan"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return user;
	}

	public boolean updateUserLoginLimit(int id, int errorTime) {

		String query = "UPDATE " + tableName + " SET loginLimit=?,isBan=? Where userID =?";
		boolean update = false;
		AppDbContext.createConnection();

		try {
			boolean ban = false;
			if (errorTime == 0) {
				ban = true;
			}
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, errorTime);
			stmt.setBoolean(2, ban);
			stmt.setInt(3, id);

			update = stmt.executeUpdate() > 0; // update successful = 1, fail = 0
			AppDbContext.disconnect();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return update;
	}

	public User Login(String userName, String password) {
		String query = "select * from " + tableName + " where userName=? and password=?";
		User user = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setString(1, userName);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user = new User(rs.getInt("userID"), rs.getString("userName"), rs.getString("fullName"),
						rs.getString("NRIC"), rs.getString("email"), rs.getString("password"), 
						rs.getInt("loginLimit"),rs.getString("userPLP"), rs.getBoolean("isAdmin"), 
						rs.getBoolean("isBan"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return user;
	}

	public void resetPassword(int id) {
		
		String query = "select * from " + tableName + " where userID=?";
		User user = null;
		AppDbContext.createConnection();

		try {
			stmt = AppDbContext.conn.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				user = new User(rs.getInt("userID"), rs.getString("userName"), rs.getString("fullName"),
						rs.getString("NRIC"), rs.getString("email"), rs.getString("password"), 
						rs.getInt("loginLimit"),rs.getString("userPLP"), rs.getBoolean("isAdmin"), 
						rs.getBoolean("isBan"));
			}

			AppDbContext.disconnect();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		
		if(user == null) {
			System.out.println("Error, Please Try Again");
			return;
		}
		
		String newPass = generatePassword();
		user.setPassword(newPass);
		
		//update new password
		UserDA userDA = new UserDA();
		boolean update = userDA.updateUser(user);
		
		if(!update) {
			System.out.println("Error, Please Try Again");
			return;
		}
		//send new password
		sendPassword(user.getEmail(),newPass);

	}
	
	private void sendPassword(String useremail, String newpassword) {
		final String username = "steveyeoseekee@gmail.com";
		final String password = "wcaj aoug bifz bqej";
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		Session session = Session.getDefaultInstance(props,new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username,password);
			}
			
		});
		session.setDebug(true);
		try {
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(useremail));//get
			message.setSubject("Reset Password");
			message.setText("Your New Password is: " + newpassword);
			Transport.send(message);
			System.out.println("Email sent Successful");
			
			
		}catch(MessagingException e) {
			e.printStackTrace();			
		}
	}
	
	
	//genarate password
	private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static String generatePassword() {
        String password = "";
        SecureRandom random = new SecureRandom();

        // 密码长度介于8到12
        int passwordLength = random.nextInt(5) + 8;

        for (int i = 0; i < passwordLength; i++) {
            // 随机选择字符集
            String charSet = getCharSet(random);
            // 从字符集中随机选择一个字符
            char randomChar = charSet.charAt(random.nextInt(charSet.length()));
            // 将字符添加到密码中
            password += randomChar;
        }

        return password;
    }
	
    private static String getCharSet(SecureRandom random) {
        // 随机选择字符集
        int charSetIndex = random.nextInt(4);
        switch (charSetIndex) {
            case 0:
                return LOWERCASE_CHARS;
            case 1:
                return UPPERCASE_CHARS;
            case 2:
                return DIGITS;
            default:
                return SYMBOLS;
        }
    }
	
	
}
