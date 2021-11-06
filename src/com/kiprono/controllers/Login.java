package com.kiprono.controllers;
import com.kiprono.models.*;
import com.kiprono.utils.*;
import com.kiprono.daos.*;

public class Login {
	private static Customers customer;
	// personal details
	private String firstName;
	private String middleInitial;
	private String lastName;
	
	// contacts
	private String phoneNumber;
	private String address;
	private String city;
	private String state;
	private int zipCode;
	
	// login
	private static String userId;
	private static String passwd;
	private static String encryptedPass, plaintxtPass;
	private static String salt;
	
	private static Keyboard keyboard = Keyboard.getKeyboard();
	private static EncryptDecrypt encoderDecoder = EncryptDecrypt.getEncryptor();
	
	// current customer
	private static LoginDA loginDa = new LoginDA();
	
	
	
	
	// get user id and user name
	public static boolean handleLogin() {
		boolean isint= true, authorised = true, loggedIn = true;
		String uid;
		System.out.println("\nEnter Userid and password");
		

		uid = keyboard.readString("Username: ");
		passwd = keyboard.readString("Password: ");
		
		if(authenticateCustomers(uid, passwd)) {
			// login state
		}else {
			System.out.println("This feature is coming\nTaking you back to main");
			authorised = false;
			
		}
		
		return authorised;
	}
	
	// checks details if they match with the records
	// if password same as account number, then user is first time login, prompt change of password
	private static boolean authenticateCustomers(String userId,String password) {
		boolean authenticated = false;
		boolean firstTimeUser = false;
		int accountNum, passwd=0,savedPass=0;
		
		
		/*
		 * query to fetch all  WHERE userId = userId
		 * update customer variable
		 * 
		 * */
		customer = loginDa.finCustomer(userId);
		accountNum = customer.getAccountNumber();
		String str = customer.getPasswd();
		salt = customer.getKey();
		
		
		
		// check for 1st time login
		try {
			passwd = Integer.valueOf(password);
			//firstTimeUser = true;
			if(passwd == accountNum) {
				firstTimeUser = true;
			}
		}catch(NumberFormatException e) {
			// input is a string
			firstTimeUser = false;
		}	
				
				
		if(firstTimeUser) {
			firstTimeUser = changePassword(passwd);
			authenticated = true;
			// GOTO Login context
		} 
		else {
			try {
				encryptedPass = customer.getPasswd();
				boolean match = PasswordUtils.verifyUserPassword(password, encryptedPass, salt);
				if(match) {
					authenticated = true;
					System.out.println("Success!");
				}
			} catch(NullPointerException s) {
				s.printStackTrace();
				
			} catch (Exception e) {
				e.printStackTrace();
				// this feature is still developing, our customer is null at the moment
			}
		}
		
		return authenticated;
	}
	
	// change password
	private static boolean changePassword(int passwd) {
		boolean changed = false, match = false;
		String newPasswd, confirmPasswd;
		
		// check if that is their password		
		// check if they match
		System.out.println("**********************************\nChange your default password\n");
		do {
			newPasswd = keyboard.readString("Enter new Password: ");
			confirmPasswd = keyboard.readString("Confirm Password: ");
			
			if(newPasswd.equals(confirmPasswd))
				match = true;
			else
				System.out.println("******* Passwords dont match ******");
			
		}while(!match);
		
		// update password and leave
		if(newPasswd.equals(confirmPasswd)) {
			try {
				encryptedPass = PasswordUtils.generateSecurePassword(confirmPasswd, salt);
				customer.setPasswd(encryptedPass);
				loginDa.updateCustomer(customer);
				System.out.println("Password changed!");
			}catch(Exception e){
				e.printStackTrace();
			}
			changed = true;
		}
		
		return changed;
	}
	
	
	
}
