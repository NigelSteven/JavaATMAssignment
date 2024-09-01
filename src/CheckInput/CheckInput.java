package CheckInput;

import java.util.Scanner;
import java.util.regex.Pattern;

import DataAccess.UserDA;
import Models.User;


public class CheckInput {
	
	
    public static Scanner scanner = new Scanner(System.in);
	
    
    
	//user
    public static boolean isNameCheck(String name) {
    	
        if (name != null && name.trim().length() > 4) {
            return true;
        } else {
            System.out.println("Invalid name. Name must Contain At Least Five Letters.");
            return false;
        }
        
    }
    
    public static boolean isNRICCheck(String nric) {
    	
        String NRICPattern = "\\d{6}\\-\\d{2}\\-\\d{4}";
        return Pattern.matches(NRICPattern, nric);
        
    }
    
    public static boolean isEmailCheck(String email) {
        // 邮箱验证正则表达式
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // 使用正则表达式验证邮箱格式
        if(!Pattern.matches(emailPattern, email)) {
            System.out.println("Invalid Email Format");
        }
        return Pattern.matches(emailPattern, email);

    }

    public static boolean isPasswordCheck(String password) {
        if (password.length() > 5) {
            return password.matches(".*[a-zA-Z]+.*");
        } else {
            System.out.println("Invalid password. Password must be at least 6 characters long and contain at least one letter.");
            return false;
        }
    }

    public static boolean isPLPCheck(String PLP) {
        if (PLP.length() >= 3) {
            return true;
        } else {
            System.out.println("Invalid PLP. PLP must contain at least 3 letters.");
            return false;
        }
    }
    
    
    //account
    public static boolean isUserIdCheck(int userId) {
    	
    	UserDA userDA = new UserDA();
    	User findUser = userDA.GetUser(userId);
    	if(findUser == null) {
            System.out.println("User Not Found");
    		return false;
    	}
    	return true;
    	
    	
    }

    public static boolean isAccNameCheck(String accName) {
        if (accName != null && accName.length() >= 3) {
            return true;
        } else {
            System.out.println("Invalid account name. Account name must contain at least 3 letters.");
            return false;
        }
    }
    
    public static boolean isAccPinCheck(String pin) {
        String pinPattern = "^\\d{6}$";
        return Pattern.matches(pinPattern, pin);
    }
    
    public static boolean isBalanceCheck(double balance) {
        if (balance >= 250) {
            return true;
        } else {
            System.out.println("Invalid Balance. For Create New Account Balance Must Be At Least RM 250.");
            return false;
        }
    }
    public static String chooseAccType(int opt) {
        if (opt == 1) {
            return "Savings";
        } 
        else if(opt == 2) {
            return "Current";
        }
        return "";
    }
    
    public static String chooseBankType(int opt) {
        if (opt == 1) {
            return "PublicBank";
        } 
        else if(opt == 2) {
            return "MayBank";
        }
        return "";
    }
    
    public static boolean isAmountCheck(double amount) {
        if (amount >= 10) {
            return true;
        } else {
            System.out.println("Invalid Amount. Amount Cannot Least Then RM 10.");
            return false;
        }
    }
}
