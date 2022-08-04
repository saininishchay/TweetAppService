package com.tweetapp.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Document(collection = "User")
public class User {
	
	@Transient
	public static final String SEQUENCE_NAME = "users_sequence";
	
	@Id
	@Min(1)
	public int userId;

	@NotBlank(message = "firstName cannot be null")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid firstName")
	public String firstName;

	@NotBlank(message = "lastName cannot be null")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid LastName")
	public String lastName;
	
	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid EmailId")
	@NotBlank(message = "Email Id cannot be null")
	@Indexed(unique = true)
	public String emailId;
	
	@NotBlank(message = "userName cannot be null")
	@Indexed(unique = true)
	public String userName;

	@NotBlank(message = "password cannot be null")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password Should Atleast contain one lowerCase,one UpperCase, one Special Char, one digit and length should be greater than 8.")
	public String password;
	
	public long contactNumber;

	public User(@Min(1) int userId,
			@NotBlank(message = "firstName cannot be null") @Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid firstName") String firstName,
			@NotBlank(message = "lastName cannot be null") @Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid LastName") String lastName,
			@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid EmailId") @NotBlank(message = "Email Id cannot be null") String emailId,
			@NotBlank(message = "userName cannot be null") String userName,
			@NotBlank(message = "password cannot be null") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password Should Atleast contain one lowerCase,one UpperCase, one Special Char, one digit and length should be greater than 8.") String password,
			long contactNumber) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailId = emailId;
		this.userName = userName;
		this.password = password;
		this.contactNumber = contactNumber;
	}

	public User() {
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(long contactNumber) {
		this.contactNumber = contactNumber;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId
				+ ", userName=" + userName + ", password=" + password + ", contactNumber=" + contactNumber + "]";
	}
	
}
