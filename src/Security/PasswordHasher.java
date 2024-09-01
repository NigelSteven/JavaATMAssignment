package Security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
	 
    // Hashes a password using BCrypt
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    // Verifies a password against a hashed password
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}