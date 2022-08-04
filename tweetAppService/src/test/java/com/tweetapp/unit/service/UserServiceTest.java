package com.tweetapp.unit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tweetapp.configuration.KafkaProducer;
import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.User;
import com.tweetapp.repository.UserRepository;
import com.tweetapp.service.UserService;
import com.tweetapp.util.Envelope;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@InjectMocks
	UserService userService;

	@Mock
	MongoOperations mongoperation;
	
	@Mock
	KafkaProducer kafkaProducer;

	@Test
	void registerTest() throws Exception {
		User user = getUser(1, "abc@123", "saini", "Nishu", "abc2@gmail.com", "test", 123456789);
		Mockito.when(userRepository.findByEmailIdName("abc2@gmail.com")).thenReturn(Optional.empty());
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.empty());
		Mockito.when(userRepository.save(user)).thenReturn(user);
		ResponseEntity<Envelope<String>> register = userService.register(user);
		Assertions.assertNotNull(register);
	}
	
	@Test
	void registerTestFailedForEmailId() {
		User user = getUser(1, "abc@123", "saini", "Nishu", "abc2@gmail.com", "test", 123456789);
		Mockito.when(userRepository.findByEmailIdName("abc2@gmail.com")).thenReturn(Optional.of(user));
		TweetAppException exceptionResponse = assertThrows(TweetAppException.class, () -> userService.register(user));
		Assertions.assertEquals("User already present with the same Email Id", exceptionResponse.getData());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
	}
	
	@Test
	void registerTestFailedForUserName() {
		User user = getUser(1, "abc@123", "saini", "Nishu", "abc2@gmail.com", "test", 123456789);
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(user));
		TweetAppException exceptionResponse = assertThrows(TweetAppException.class, () -> userService.register(user));
		Assertions.assertEquals("User already present with the same Username", exceptionResponse.getData());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
	}
	
	@Test
	void loginTest() {
		Mockito.when(userRepository.findByUserNameAndPassword("test", "123456"))
				.thenReturn(Optional.of(new User()));
		ResponseEntity<Envelope<String>> login = userService.login("test", "123456");
		Assertions.assertNotNull(login);
	}

	@Test
	void loginTestFailed() {
		Mockito.when(userRepository.findByUserNameAndPassword("test", "1234"))
				.thenReturn(Optional.empty());
		TweetAppException exceptionResponse = assertThrows(TweetAppException.class,
				() -> userService.login("test", "1234"));
		Assertions.assertEquals("Login Failed: incorrect userName or password", exceptionResponse.getData());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
	}

	@Test
	void forgotPasswordTestUserNotFound() {
		TweetAppException exceptionResponse = assertThrows(TweetAppException.class,
				() -> userService.forgotPassword("abc2@gmail.com", "8212"));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("userName not Present", exceptionResponse.getData());
	}

	@Test
	void forgotPasswordTest() {
		User user = getUser(1, "abc@123", "saini", "Nishu", "abc2@gmail.com", "test", 123456789);
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(user));
		Query query = new Query();
		query.addCriteria(Criteria.where("userName").is("test"));
		Update update = new Update();
		update.set("password", "abc@123");
		Mockito.when(mongoperation.findAndModify(query, update, User.class)).thenReturn(new User());
		ResponseEntity<Envelope<String>> forgotPassword = userService.forgotPassword("test", "abc@123");
		Assertions.assertNotNull(forgotPassword);
	}

	@Test
	void forgotPasswordExceptionTest() {
		User user = getUser(1, "abc@123", "saini", "Nishu", "abc2@gmail.com", "test", 123456789);
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(user));
		Query query = new Query();
		query.addCriteria(Criteria.where("userName").is("test"));
		Update update = new Update();
		update.set("password", "abc@123");
		Mockito.when(mongoperation.findAndModify(query, update, User.class)).thenReturn(null);
		TweetAppException exceptionResponse = assertThrows(TweetAppException.class,
				() -> userService.forgotPassword("test", "abc@123"));
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("Error While Updating Password", exceptionResponse.getData());
	}

	@Test
	void usernameExceptionTest() {
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.empty());
		TweetAppException exceptionResponse = assertThrows(TweetAppException.class,
				() -> userService.username("test"));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("userName not Present", exceptionResponse.getData());
	}

	@Test
	void usernameTest() {
		Mockito.when(userRepository.findByUserName("abc2@gmail.com")).thenReturn(Optional.of(new User()));
		ResponseEntity<Envelope<User>> usernameResponse = userService.username("abc2@gmail.com");
		Assertions.assertNotNull(usernameResponse);
	}

	@Test
	void getAllusersExceptionTest() {
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> userService.getAllusers());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("No Users Found", exceptionResponse.getData());
	}

	@Test
	void getAllusersTest() {
		List<User> users = Arrays.asList(
				new User(1, "abc@123", "saini", "Nishchay", "abc2@gmail.com", "test", 123456789),
				new User(2, "abc@123", "saini", "Nishchay", "abc2@gmail.com", "test", 123456789));
		Mockito.when(userRepository.findAll()).thenReturn(users);
		ResponseEntity<Envelope<List<User>>> allusers = userService.getAllusers();
		Assertions.assertNotNull(allusers);
	}

	private User getUser(int id, String password, String lastName, String firstName, String emailId, String userName,
			long contactNumber) {
		User user = new User();
		user.setUserId(id);
		user.setPassword(password);
		user.setLastName(lastName);
		user.setFirstName(firstName);
		user.setEmailId(emailId);
		user.setUserName(userName);
		user.setContactNumber(contactNumber);
		return user;
	}
}
