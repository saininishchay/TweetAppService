package com.tweetapp.controller;

import static com.tweetapp.util.TweetConstant.ROOT_URL;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.User;
import com.tweetapp.service.UserService;
import com.tweetapp.util.Envelope;

import lombok.Generated;

@RestController
@Generated
@RequestMapping(value = ROOT_URL)
@CrossOrigin(origins = "${client.url}")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;

	@PostMapping(value = "/register")
	public ResponseEntity<Envelope<String>> registerUser(@RequestBody @Valid User user) {
		log.info("Registration for user {} {}", user.getFirstName(), user.getLastName());
		return userService.register(user);
	}

	@GetMapping(value = "/login")
	public ResponseEntity<Envelope<String>> login(@RequestParam("userName") String userName,
			@RequestParam("password") String password) throws TweetAppException {
		log.info("Login for user {} {}", userName, password);
		return userService.login(userName, password);
	}

	@GetMapping(value = "/{userName}/forgot")
	public ResponseEntity<Envelope<String>> forgotPassword(@PathVariable("userName") String userName,
			@RequestParam("newPassword") String password) {
		log.info("forgot password for user {}", userName);
		return userService.forgotPassword(userName, password);
	}

	@GetMapping(value = "/users/all")
	public ResponseEntity<Envelope<List<User>>> users() {
		log.info("Requesting All Users");
		return userService.getAllusers();
	}

	@GetMapping(value = "/users/search")
	public ResponseEntity<Envelope<User>> searchUserName(@RequestParam("userName") String userName) {
		log.info("Search UserName {}", userName);
		return userService.username(userName);
	}

}
